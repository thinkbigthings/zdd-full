import React, {useContext} from 'react';

import {UserContext} from './UserContext.js';

function basicAuthHeader(username, password) {
    const encoded = btoa(username + ":" + password);
    return {
        'Authorization': 'Basic ' + encoded,
        "Content-Type": "application/json"
    };
}

function useAuthHeader() {

    // context shared from the top level
    const userContext = useContext(UserContext);
    const user = userContext.getCurrentUser();
    if( ! user.isLoggedIn) {
        throw 'user is not logged in';
    }

    return basicAuthHeader(user.username, user.password);
}

function put(url, userData, requestHeaders) {

    const requestMeta = {
        headers: requestHeaders,
        method: 'PUT',
        body: JSON.stringify(userData),
    };

    return fetch(url, requestMeta);
}

function post(url, userData, requestHeaders) {

    const body = typeof userData === 'string'
                    ? userData
                    : JSON.stringify(userData);

    const requestMeta = {
        headers: requestHeaders,
        method: 'POST',
        body: body
    };

    return fetch(url, requestMeta);
}

function get(url, requestHeaders) {

    const requestMeta = {
        headers: requestHeaders
    };

    return fetch(url, requestMeta)
        .then(function(httpResponse) {
            if(httpResponse.status !== 200) {
                console.log('Called ' + url + ' and received ' + httpResponse);
            }
            if(httpResponse.status === 401 || httpResponse.status === 403) {
                console.log('TODO push /login to history');
            }
            return httpResponse;
        })
        .then(httpResponse => httpResponse.json());
}

function fetchWithCreds(url, credentials) {

    const requestMeta = {
        headers: basicAuthHeader(credentials.username, credentials.password)
    };

    return fetch(url, requestMeta)
        .then(function(httpResponse) {
            if(httpResponse.status !== 200) {
                console.log('Called ' + url + ' and received ' + httpResponse);
            }
            if(httpResponse.status === 401 || httpResponse.status === 403) {
                console.log('TODO push /login to history');
            }
            return httpResponse;
        })
        .then(httpResponse => httpResponse.json());
}

export {fetchWithCreds, put, post, get, useAuthHeader}