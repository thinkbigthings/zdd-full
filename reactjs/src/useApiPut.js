
import useError from "./useError";
import {basicHeader} from "./BasicAuth";
import {throwOnBadResponse} from './HttpResponseFilter';
import {recoveryActions} from "./ErrorContext";


const useApiPut = () => {

    const requestHeaders = basicHeader();
    const { addError } = useError();

    function put(url, body) {

        const bodyString = typeof body === 'string' ? body : JSON.stringify(body);

        const requestMeta = {
            headers: requestHeaders,
            method: 'PUT',
            body: bodyString
        };

        return fetch(url, requestMeta)
            .then(throwOnBadResponse)
            .catch(error => {
                addError("The app encountered an error: " + error.message, recoveryActions.RELOAD);
            });
    }

    return put;
};

export default useApiPut;