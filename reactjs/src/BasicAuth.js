import {useContext} from 'react';

import {UserContext} from './UserContext.js';

const VERSION_HEADER = 'X-Version';

// picks up from .env file in build
const { REACT_APP_API_VERSION } = process.env;

const httpStatusFilter = function(httpResponse) {

    if(httpResponse.status !== 200) {
        console.log('Received ' + httpResponse);
    }

    if(httpResponse.status === 401 || httpResponse.status === 403) {
        console.log('TODO redirect to /login');
    }
    else if(httpResponse.headers.get(VERSION_HEADER) !== REACT_APP_API_VERSION) {
        const serverApi = httpResponse.headers.get(VERSION_HEADER);
        const clientApi= REACT_APP_API_VERSION;
        const message = 'client is version ' + clientApi + ' and server is version ' + serverApi;
        throw Error(message);
    }

    return httpResponse;
}


function basicAuthHeader(username, password) {

    const encoded = btoa(username + ":" + password);
    return {
        'Authorization': 'Basic ' + encoded,
        "Content-Type": "application/json",
        VERSION_HEADER: REACT_APP_API_VERSION
    };
}

function useAuthHeader() {

    // context shared from the top level
    const userContext = useContext(UserContext);
    const user = userContext.getCurrentUser();
    if( ! user.isLoggedIn) {
        throw new Error("user is not logged in");
    }

    return basicAuthHeader(user.username, user.password);
}

function put(url, userData, requestHeaders) {

    const requestMeta = {
        headers: requestHeaders,
        method: 'PUT',
        body: JSON.stringify(userData),
    };

    return fetch(url, requestMeta).then(httpStatusFilter);
}

function post(url, userData, requestHeaders) {

    const body = typeof userData === 'string' ? userData : JSON.stringify(userData);

    const requestMeta = {
        headers: requestHeaders,
        method: 'POST',
        body: body
    };

    return fetch(url, requestMeta).then(httpStatusFilter);
}

function get(url, requestHeaders) {

    const requestMeta = {
        headers: requestHeaders
    };

    return fetch(url, requestMeta)
        .then(httpStatusFilter)
        .then(httpResponse => httpResponse.json());
}

function fetchWithCreds(url, credentials) {

    const requestMeta = {
        headers: basicAuthHeader(credentials.username, credentials.password)
    };

    return fetch(url, requestMeta)
        .then(httpStatusFilter)
        .then(httpResponse => httpResponse.json());
}

export {fetchWithCreds, put, post, get, useAuthHeader}