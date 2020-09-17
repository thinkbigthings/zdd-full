
import useCurrentUser from "./useCurrentUser";

const VERSION_HEADER = 'X-Version';

// picks up from .env file in build
const { REACT_APP_API_VERSION } = process.env;

const httpStatusFilter = function(httpResponse) {

    const serverApiVersion = httpResponse.headers.get(VERSION_HEADER);

    if(httpResponse.status !== 200) {
        console.log('Received ' + httpResponse);
    }

    if(httpResponse.status === 401 || httpResponse.status === 403) {
        console.log('TODO redirect to /login');
    }

    if(httpResponse.status >= 400) {
        const message = 'There was an input error';
        const userAction = 'Try again';
        throw Error(message + " ... " + userAction);
    }

    if(httpResponse.status >= 500) {
        const message = 'There was a server error';
        const userAction = 'Try reloading the page';
        throw Error(message + " ... " + userAction);
    }

    if(serverApiVersion !== null && serverApiVersion !== REACT_APP_API_VERSION)
    {
        const serverApi = httpResponse.headers.get(VERSION_HEADER);
        const clientApi= REACT_APP_API_VERSION;
        const message = 'client is version ' + clientApi + ' and server is version ' + serverApi;
        const userAction = 'Try reloading the page';
        throw Error(message + " ... " + userAction);
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

    const {currentUser} = useCurrentUser();

    if( ! currentUser.isLoggedIn) {
        throw new Error("user is not logged in");
    }

    return basicAuthHeader(currentUser.username, currentUser.password);
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