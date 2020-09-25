
import useCurrentUser from "./useCurrentUser";

import {basicAuthHeader} from "./BasicAuth";

const useAuthHeader = () => {

    const {currentUser} = useCurrentUser();

    if( ! currentUser.isLoggedIn) {
        throw new Error("user is not logged in");
    }

    return basicAuthHeader(currentUser.username, currentUser.password);
};

export default useAuthHeader;