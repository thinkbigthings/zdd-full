
function basicAuthHeader(username, password) {
    const encoded = btoa(username + ":" + password);
    return {'Authorization': 'Basic ' + encoded };
}


function putWithAuth(url, userData) {

    const username = localStorage.getItem('username');
    const password = localStorage.getItem('password');

    let requestHeaders = basicAuthHeader(username, password);
    requestHeaders["Content-Type"] = "application/json";

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
    requestHeaders["Content-Type"] = "application/json";

    const requestMeta = {
        headers: requestHeaders,
        method: 'POST',
        body: JSON.stringify(userData),
    };

    return fetch(url, requestMeta);
}

function fetchWithAuth(url) {

    const username = localStorage.getItem('username');
    const password = localStorage.getItem('password');

    const requestMeta = {
        headers: basicAuthHeader(username, password)
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

export {fetchWithAuth, postWithAuth, putWithAuth}