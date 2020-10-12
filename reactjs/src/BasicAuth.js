
import {REACT_APP_API_VERSION, VERSION_HEADER, throwOnBadResponse} from "./HttpResponseFilter";

function basicHeader() {

    let headers = {
        "Content-Type": "application/json"
    };
    headers[VERSION_HEADER] = REACT_APP_API_VERSION;
    return headers;
}

function getWithCreds(url, credentials) {

    const requestMeta = {
        headers: basicHeader(credentials.username, credentials.password),
        // credentials: 'same-origin'
    };

    return fetch(url, requestMeta)
        .then(throwOnBadResponse)
        .then(httpResponse => httpResponse.json());
}

export {getWithCreds, basicHeader}