import { useState, useEffect } from 'react';


import useCurrentUser from "./useCurrentUser";

const VERSION_HEADER = 'X-Version';

// picks up from .env file in build
const { REACT_APP_API_VERSION } = process.env;

const checkResponseCode = function(httpResponse) {

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

const useApi = (initialUrl, initialData) => {

    const [url, setUrl] = useState(initialUrl);
    const [isLoading, setLoading] = useState(true);
    const [isLongRequest, setLongRequest] = useState(false);
    const [hasError, setError] = useState(false);
    const [fetchedData, setFetchedData] = useState(initialData);

    const {currentUser} = useCurrentUser();
    const encoded = btoa(currentUser.username + ":" + currentUser.password);
    const requestHeaders = {
        'Authorization': 'Basic ' + encoded,
        "Content-Type": "application/json",
        VERSION_HEADER: REACT_APP_API_VERSION
    };

    const longLoadTimeMs = 1000;

    let longRequestTimer = setTimeout(() => {}, longLoadTimeMs);

    useEffect(() => {

        // the caller can choose to only show a spinner if it takes a long time using the isLongRequest flag
        // otherwise it's a poor UX to flash a spinner for a fraction of a second
        // let longLoadTimer = setTimeout(() => setLongRequest(isLoading), 500);

        const handleFetchResponse = response => {
            clearTimeout(longRequestTimer);
            setError(!response.ok);
            setLoading(false);
            setLongRequest(false);

            return response.ok && response.json ? response.json() : initialData;
        };

        // default is GET
        const fetchData = () => {
            clearTimeout(longRequestTimer);
            longRequestTimer = setTimeout(() => setLongRequest(true), longLoadTimeMs);
            setLoading(true);
            setLongRequest(false);
            return fetch(url, { headers: requestHeaders })
                // .then(checkResponseCode )
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