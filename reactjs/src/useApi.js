import { useState, useEffect } from 'react';


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

const useApi = (initialUrl, initialData) => {

    const [url, setUrl] = useState(initialUrl);
    const [isLoading, setLoading] = useState(true);
    const [hasError, setError] = useState(false);
    const [fetchedData, setFetchedData] = useState(initialData);

    const[isLongRequest, setLongRequest] = useState(false);

    const requestHeaders = useAuthHeader();

    const longLoadTimeMs = 1000;

    let longLoadTimer = setTimeout(() => {}, longLoadTimeMs);

    useEffect(() => {

        // the caller can choose to only show a spinner if it takes a long time
        // otherwise it's a poor UX to flash a spinner for a fraction of a second
        // let longLoadTimer = setTimeout(() => setLongRequest(isLoading), 500);

        const handleFetchResponse = response => {
            clearTimeout(longLoadTimer);
            setError(!response.ok);
            setLoading(false);
            setLongRequest(false);
            return response.ok && response.json ? response.json() : initialData;
        };

        // default is GET
        const fetchData = () => {
            clearTimeout(longLoadTimer);
            longLoadTimer = setTimeout(() => setLongRequest(true), longLoadTimeMs);
            setLoading(true);
            setLongRequest(false);
            return fetch(url, { headers: requestHeaders })
                .then(handleFetchResponse)
                .catch(handleFetchResponse);
        };

        if(initialUrl) {
            fetchData().then(data => setFetchedData(data));
        }

        // // might need to set the cleanup function if any flags need to be reset for subsequent calls
        // return () => {
        //    // cleanup would go here...
        // }

    }, [url]);



    return {
        setUrl,
        isLoading,
        isLongRequest,
        hasError,
        fetchedData
    }
};

export default useApi;