import React, {useState} from "react";
import "./LoginPage.css";
import Button from "@material-ui/core/Button";
import Logo from "../../images/logo.png";
import LogoDark from "../../images/logo_dark.png";
import {
    Avatar, Input, InputAdornment, InputLabel,
    ListItem,
    ListItemAvatar,
    ListItemText,
    Paper
} from "@material-ui/core";
import ManageAccountsIcon from "@material-ui/icons/Settings";
import ActiveProjectIcon from "@material-ui/icons/FolderSpecial";
import DisabledProjectIcon from "@material-ui/icons/NotInterested";
import ExpiredProjectIcon from "@material-ui/icons/EventBusy";
import Typography from "@material-ui/core/Typography";
import List from "@material-ui/core/List";
import {handleErrors} from "../../utils/FetchHelper";
import {
    isLogin,
    setToken,
    setProjectId,
    setProjectName,
    setRoles,
    hasRole, getThemeName, setUsername
} from '../../utils/ConfigurationStorage';
import {Link, Redirect} from 'react-router-dom';
import FormControl from "@material-ui/core/FormControl";
import IconButton from "@material-ui/core/IconButton";
import {Visibility, VisibilityOff} from "@material-ui/icons";
import Grid from "@material-ui/core/Grid";
import {MessageBox} from "../MessageBox";
import {sortByKey} from "../../utils/JsonHelper";

const initialState = {
    username: "",
    password: "",
    projects: null,
    changePassword: false,
    showPassword: false
};

export default function LoginPage() {

    const [{username, password, projects, changePassword, showPassword}, setState] = useState(initialState);
    const [messageBox, setMessageBox] = useState({open: false, severity: 'error', title: '', message: ''});

    const onChange = e => {
        const {name, value} = e.target;
        setState(prevState => ({...prevState, [name]: value}));
    };

    const handleClickShowPassword = () => {
        setState(prevState => ({...prevState, showPassword: !showPassword}));
    }

    const handleMouseDownPassword = (event) => {
        event.preventDefault();
    };

    const handleSignIn = () => {
        signIn(username, password, null);
    }

    const signIn = (username, password, projectId) => {
        fetch(`${window.apiURL}/public/signin`, {
            method: 'POST',
            body: JSON.stringify(projectId == null ? {username, password} : {username, password, projectId}),
            headers: {'Content-Type': 'application/json'}
        })
            .then(handleErrors)
            .then(data => {
                setUsername(username);
                if (data.changePassword != null && data.changePassword === true) {
                    setToken(data.token);
                    setState(prevState => ({...prevState, changePassword: true}));
                } else if (data.projects != null) {
                    // for SUPER user only
                    if (data.token != null)
                        setToken(data.token);

                    setRoles(data.roles);
                    let sortedProjects = sortByKey(data.projects, 'id');
                    setState(prevState => ({...prevState, projects: sortedProjects, changePassword: false}));
                } else if (data.token != null) {
                    setToken(data.token);
                    setProjectId(data.project.id);
                    setProjectName(data.project.name);
                    setRoles(data.roles);
                    setState(prevState => ({...prevState, projects: null, changePassword: false}));
                }
            }).catch(error => {
                setMessageBox({open: true, severity: 'error', title: 'Login failed', message: error});
            }
        );
    };


    const selectProject = (id) => {
        signIn(username, password, id);
    }

    const getLoginPaper = () => {
        return (
            <Paper className="PaperClass" elevation={3}>
                <Typography className="Title" variant="h5">
                    Login
                </Typography>
                <FormControl className="LoginInputText">
                    <InputLabel htmlFor="standard-adornment-username">Username</InputLabel>
                    <Input
                        id="standard-adornment-username"
                        name="username"
                        type='text'
                        value={username}
                        onChange={onChange}
                    />
                </FormControl>
                <FormControl className="LoginInputText">
                    <InputLabel htmlFor="standard-adornment-password">Password</InputLabel>
                    <Input
                        id="standard-adornment-password"
                        name="password"
                        type={showPassword ? 'text' : 'password'}
                        value={password}
                        onChange={onChange}
                        endAdornment={
                            <InputAdornment position="end">
                                <IconButton
                                    aria-label="toggle password visibility"
                                    onClick={handleClickShowPassword}
                                    onMouseDown={handleMouseDownPassword}
                                >
                                    {showPassword ? <Visibility/> : <VisibilityOff/>}
                                </IconButton>
                            </InputAdornment>
                        }
                    />
                </FormControl>
                <div className="Button">
                    <Button
                        variant="contained"
                        color="primary"
                        disabled={!(username.length > 0 && password.length > 0)}
                        onClick={() => {
                            handleSignIn();
                        }}
                    >
                        Submit
                    </Button>
                </div>
            </Paper>
        );
    }

    const getProjectsPaper = () => {
        return (
            <Paper className="PaperClass" elevation={3}>
                <Grid className="TitleGrid" container direction="row" justify="center" alignItems="center" spacing={1}>
                    <Grid item>
                        <Typography variant="h5">
                            Select project
                        </Typography>
                    </Grid>
                    {hasRole("SUPER") ? (
                        <Grid item>
                            <IconButton component={Link} to={'/management'}>
                                <ManageAccountsIcon/>
                            </IconButton>
                        </Grid>
                    ) : (
                        <div/>
                    )}

                </Grid>

                <List component="nav" className="ProjectsList">
                    {projects.map(({id, name, description, enabled, expired}) => {
                        return (
                            <div key={id}>
                                <ListItem button onClick={() => selectProject(id)}>
                                    <ListItemAvatar>
                                        <Avatar>
                                            {enabled !== true ? <DisabledProjectIcon /> : expired === true ?
                                                <ExpiredProjectIcon color='error' /> : <ActiveProjectIcon color='secondary'/>}
                                        </Avatar>
                                    </ListItemAvatar>
                                    <ListItemText primary={name} secondary={description}/>
                                </ListItem>
                            </div>
                        );
                    })}
                </List>
            </Paper>
        );
    }

    const redirectTo = (pagePath) => {
        return (<Redirect to={pagePath}/>);
    }

    const getSwitchParam = () => {
        if (changePassword === true) {
            return 3;
        } else if (projects != null) {
            return 2;
        } else if (isLogin()) {
            return 1;
        } else {
            return 0;
        }
    }

    const paper = () => {
        switch (getSwitchParam()) {
            case 3:
                return redirectTo("/change-password");
            case 2:
                return getProjectsPaper();
            case 1:
                return redirectTo("/project/");
            default:
                return getLoginPaper();
        }
    }

    return (
        <div className="ContainerClass">
            {getThemeName() === 'light' ? <img src={Logo} alt=''/> : <img src={LogoDark} alt=''/>}
            {paper()}
            <MessageBox
                config={messageBox}
                onClose={() => setMessageBox({...messageBox, open: false})}
            />
        </div>
    );
}