
import useCurrentUser from "./useCurrentUser";

import {VERSION_HEADER, REACT_APP_API_VERSION} from './HttpResponseFilter';

const useAuthHeader = () => {

    const {currentUser} = useCurrentUser();

    if( ! currentUser.isLoggedIn) {
        throw new Error("user is not logged in");
    }

    const encoded = btoa(currentUser.username + ":" + currentUser.password);
    const requestHeaders = {
        'Authorization': 'Basic ' + encoded,
        "Content-Type": "application/json",
        VERSION_HEADER: REACT_APP_API_VERSION
    };

    return requestHeaders;
};

export default useAuthHeader;