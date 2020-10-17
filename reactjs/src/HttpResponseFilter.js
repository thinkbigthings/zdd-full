
const VERSION_HEADER = 'X-Version';

// picks up from .env file in build
const { REACT_APP_API_VERSION } = process.env;


const throwOnBadResponse = function(httpResponse) {

    if(httpResponse.status !== 200) {
        console.log('Received response code ' + httpResponse.status);
    }

    if(httpResponse.status === 401) {
        const message = 'You are not logged in.';
        const userAction = 'Login to the app.';
        throw Error(message + " " + userAction);
    }

    if(httpResponse.status === 403) {
        const message = 'The action was forbidden.';
        const userAction = 'Contact your administrator if you required additional privileges.';
        throw Error(message + " " + userAction);
    }

    if(httpResponse.status >= 400) {
        const message = 'There was an input error.';
        const userAction = 'Try again.';
        throw Error(message + " " + userAction);
    }

    if(httpResponse.status >= 500) {
        const message = 'There was a server error.';
        const userAction = 'Try again, if the error continues contact the administrator.';
        throw Error(message + " " + userAction);
    }

    const serverApiVersion = httpResponse.headers.get(VERSION_HEADER);
    if(serverApiVersion !== null && serverApiVersion !== REACT_APP_API_VERSION) {
        const message = 'Your app is version ' + REACT_APP_API_VERSION + ' and the server is version ' + serverApiVersion + ".";
        const userAction = 'Try reloading the page.';
        throw Error(message + " " + userAction);
    }

    return httpResponse;
}

export {throwOnBadResponse, VERSION_HEADER, REACT_APP_API_VERSION};