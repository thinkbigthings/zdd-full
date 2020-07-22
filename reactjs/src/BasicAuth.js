import React, {useContext, useState} from 'react';

import {UserContext} from './UserContext.js';

function basicAuthHeader(username, password) {
    const encoded = btoa(username + ":" + password);
    return {
        'Authorization': 'Basic ' + encoded,
        "Content-Type": "application/json"
    };
}

function useCredentials() {

    // context shared from the top level
    const [user, setUser] = useContext(UserContext);

    const [username, setUsername] = useState(user.username);
    const [password, setPassword] = useState(user.password);

    return {username, password};
}

function usePutWithAuth(url, userData) {

    const [user, setUser] = useContext(UserContext);

    if( ! user.isLoggedIn) {
        throw 'user is not logged in';
    }

    let requestHeaders = basicAuthHeader(user.username, user.password);

    const requestMeta = {
        headers: requestHeaders,
        method: 'PUT',
        body: JSON.stringify(userData),
    };

    return fetch(url, requestMeta);
}

function usePostWithAuth(url, userData) {

    const [user, setUser] = useContext(UserContext);

    if( ! user.isLoggedIn) {
        throw 'user is not logged in';
    }

    let requestHeaders = basicAuthHeader(user.username, user.password);

    const requestMeta = {
        headers: requestHeaders,
        method: 'POST',
        body: JSON.stringify(userData),
    };

    return fetch(url, requestMeta);
}

function useGetWithAuth(url) {

    const [user, setUser] = useContext(UserContext);

    if( ! user.isLoggedIn) {
        throw 'user is not logged in';
    }

    let requestHeaders = basicAuthHeader(user.username, user.password);

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

function putWithAuth(url, userData) {

    const username = localStorage.getItem('username');
    const password = localStorage.getItem('password');

    let requestHeaders = basicAuthHeader(username, password);

    const requestMeta = {
        headers: requestHeaders,
        method: 'PUT',
        body: JSON.stringify(userData),
    };

    return fetch(url, requestMeta);
}

function postWithAuth(url, userData) {

    const username = localStorage.getItem('username');
    const password = localStorage.getItem('password');

    let requestHeaders = basicAuthHeader(username, password);

    const requestMeta = {
        headers: requestHeaders,
        method: 'POST',
        body: JSON.stringify(userData),
    };

    return fetch(url, requestMeta);
}

function fetchWithAuth(url) {

    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    if( ! currentUser.isLoggedIn) {
        throw 'user is not logged in';
    }

    return fetchWithCreds(url, currentUser);
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

export {fetchWithAuth, postWithAuth, putWithAuth, fetchWithCreds, usePutWithAuth, usePostWithAuth, useGetWithAuth}