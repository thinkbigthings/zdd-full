
import {REACT_APP_API_VERSION, VERSION_HEADER, throwOnBadResponse} from "./HttpResponseFilter";

function basicHeader() {

    let headers = {
        "Content-Type": "application/json"
    };
    headers[VERSION_HEADER] = REACT_APP_API_VERSION;
    return headers;
}

function getWithCreds(url, credentials) {

    const encoded = btoa(credentials.username + ":" + credentials.password);

    const requestMeta = {
        headers: {
            'Authorization': 'Basic ' + encoded
        }
    };

    return fetch(url, requestMeta)
        .then(throwOnBadResponse)
        .then(httpResponse => httpResponse.json());
}

export {getWithCreds, basicHeader}