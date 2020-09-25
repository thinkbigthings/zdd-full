
import useError from "./useError";
import useAuthHeader from "./useAuthHeader";
import {throwOnBadResponse} from './HttpResponseFilter';


const useApiPost = () => {

    const requestHeaders = useAuthHeader();
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
                addError("The app encountered an error: " + error.message);
            });
    }

    return post;
};

export default useApiPost;