import React from "react";

const defaultUser = {
    username: '',
    displayName: '',
    password: '',
    tokenAuth: '',
    roles: []
}

const defaultUserContext = {
    user: defaultUser,
    isLoggedIn: false,
}

const UserContext = React.createContext(defaultUserContext);

export {defaultUserContext, UserContext};
