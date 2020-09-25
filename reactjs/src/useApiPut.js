
import useError from "./useError";
import useAuthHeader from "./useAuthHeader";
import {throwOnBadResponse} from './HttpResponseFilter';


const useApiPut = () => {

    const requestHeaders = useAuthHeader();
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
                addError("The app encountered an error: " + error.message);
            });
    }

    return put;
};

export default useApiPut;