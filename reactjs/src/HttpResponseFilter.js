
const VERSION_HEADER = 'X-Version';

// picks up from .env file in build
const { REACT_APP_API_VERSION } = process.env;

const throwOnBadResponse = function(httpResponse) {

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

    const serverApiVersion = httpResponse.headers.get(VERSION_HEADER);
    if(serverApiVersion !== null && serverApiVersion !== REACT_APP_API_VERSION) {
        const message = 'client is version ' + REACT_APP_API_VERSION + ' and server is version ' + serverApiVersion;
        const userAction = 'Try reloading the page';
        throw Error(message + " ... " + userAction);
    }

    return httpResponse;
}

export {throwOnBadResponse, VERSION_HEADER, REACT_APP_API_VERSION};