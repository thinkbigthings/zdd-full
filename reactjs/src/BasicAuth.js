
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

    // If the server returns a 401 status code and includes one or more WWW-Authenticate headers, then
    // the browser pops up an authentication dialog asking for the username and password
    // Including X-Requested-With by the client signals the server to not respond with that header
    const requestMeta = {
        headers: {
            'Authorization': 'Basic ' + encoded,
            'X-Requested-With': 'XMLHttpRequest'
        }
    };

    return fetch(url, requestMeta)
        .then(throwOnBadResponse)
        .then(httpResponse => httpResponse.json());
}

export {getWithCreds, basicHeader}