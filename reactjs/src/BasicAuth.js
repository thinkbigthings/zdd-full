
import {REACT_APP_API_VERSION, VERSION_HEADER, throwOnBadResponse} from "./HttpResponseFilter";

function basicAuthHeader(username, password) {

    const encoded = btoa(username + ":" + password);
    let headers = {
        'Authorization': 'Basic ' + encoded,
        "Content-Type": "application/json"
    };
    headers[VERSION_HEADER] = REACT_APP_API_VERSION;
    return headers;
}

function getWithCreds(url, credentials) {

    const requestMeta = {
        headers: basicAuthHeader(credentials.username, credentials.password)
    };

    return fetch(url, requestMeta)
        .then(throwOnBadResponse)
        .then(httpResponse => httpResponse.json());
}

export {getWithCreds, basicAuthHeader}