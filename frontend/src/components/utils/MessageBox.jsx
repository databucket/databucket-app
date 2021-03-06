import React from 'react';
import Snackbar from '@material-ui/core/Snackbar';
import {Alert, AlertTitle} from '@material-ui/lab';
import {makeStyles} from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
    root: {
        width: '100%',
        '& > * + *': {
            marginTop: theme.spacing(2),
        },
    },
}));

export const MessageBox = (props) => {
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Snackbar
                anchorOrigin={{
                    vertical: 'top',
                    horizontal: 'center',
                }}
                open={props.config.open}
                onClose={props.onClose}
                autoHideDuration={7000}
            >
                <Alert severity={props.config.severity}>
                    <AlertTitle>{props.config.title}</AlertTitle>
                    {props.config.message}
                </Alert>
            </Snackbar>
        </div>
    );
}