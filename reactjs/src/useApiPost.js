
import useError from "./useError";
import {basicHeader} from "./BasicAuth";
import {throwOnBadResponse} from './HttpResponseFilter';
import {recoveryActions} from "./ErrorContext";


const useApiPost = () => {

    const requestHeaders = basicHeader();
    const { addError } = useError();

    function post(url, body) {

        const bodyString = typeof body === 'string' ? body : JSON.stringify(body);

        const requestMeta = {
            headers: requestHeaders,
            method: 'POST',
            body: bodyString
        };

        return fetch(url, requestMeta)
            .then(throwOnBadResponse)
            .catch(error => {
                addError("The app encountered an error: " + error.message, recoveryActions.RELOAD);
            });
    }

    return post;
};

export default useApiPost;