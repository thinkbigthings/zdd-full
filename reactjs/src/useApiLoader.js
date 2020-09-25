import { useState, useEffect } from 'react';

import useError from "./useError";
import useAuthHeader from "./useAuthHeader";

import {throwOnBadResponse} from './HttpResponseFilter';

// this is more of a data loader for a page, which is useful as a hook
// methods in callbacks can't be hooks, so it makes sense to use fetch()
const useApiLoader = (initialUrl, initialData) => {

    const [url, setUrl] = useState(initialUrl);
    const [isLoading, setLoading] = useState(true);
    const [isLongRequest, setLongRequest] = useState(false);
    const [fetchedData, setFetchedData] = useState(initialData);

    const requestHeaders = useAuthHeader();

    const { addError } = useError();

    const longLoadTimeMs = 1000;

    // the caller can choose to only show a spinner if it takes a long time using the isLongRequest flag
    // otherwise it's a poor UX to flash a spinner for a fraction of a second
    let longRequestTimer = setTimeout(() => {}, longLoadTimeMs);

    useEffect(() => {

        const handleFetchResponse = response => {
            clearTimeout(longRequestTimer);
            setLoading(false);
            setLongRequest(false);

            return response.ok && response.json ? response.json() : initialData;
        };

        // default is GET
        const fetchData = () => {

            // TODO Assignments to the 'longRequestTimer' variable from inside Reach Hook useEffect will be lost after each render.
            // To preserve the value over time, store it in a useRef Hook and keep the mutable value in the '.current' property.
            // Otherwise, you can move this variable directly inside useEffect react-hooks/exhaustive-deps

            clearTimeout(longRequestTimer);
            longRequestTimer = setTimeout(() => setLongRequest(true), longLoadTimeMs);
            setLoading(true);
            setLongRequest(false);

            console.log(JSON.stringify(requestHeaders));

            let request = { headers: requestHeaders };

            return fetch(url, request)
                .then(throwOnBadResponse)
                .then(handleFetchResponse)
                .catch(error => {
                    addError("The app encountered an error: " + error.message);
                    return initialData;
                });
        };

        if(url) {
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
        fetchedData
    }
};

export default useApiLoader;