const origin = window.location.origin;
// const origin = 'http://localhost:8080';

export const getDataUrl = (bucket) => {
    return `${origin}/api/bucket/${bucket.name}`;
}

export const getDataReserveUrl = (bucket, number, random) => {
    return random ? `${origin}/api/bucket/${bucket.name}/reserve?limit=${number}&sort=random` : `${origin}/api/bucket/${bucket.name}/data/reserve?limit=${number}`;
}

export const getDataByIdUrl = (bucket, id) => {
    return `${origin}/api/bucket/${bucket.name}/${id}`;
}

export const getDataHistoryUrl = (bucket, id) => {
    return `${origin}/api/bucket/${bucket.name}/${id}/history`;
}

export const getDataHistoryPropertiesUrl = (bucket, id, idA, idB) => {
    return `${origin}/api/bucket/${bucket.name}/${id}/history/${idA},${idB}`;
}

export const getBaseUrl = (endpoint) => {
    return `${origin}/api/${endpoint}`;
}

export const getSessionUrl = (endpoint) => {
    return `${origin}/api/session/${endpoint}`;
}

export const getSessionUrlWithIds = (endpoint, ids) => {
    return `${origin}/api/session/${endpoint}/${ids}`;
}

export const getSwaggerDocPath = () => {
    return `/swagger-ui/#/`;
}