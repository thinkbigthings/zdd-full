import React, {useEffect, useReducer, useState} from 'react';

const defaultUser = {
    displayName: '',
    username: '',
    password: '',
    roles: [],
    isLoggedIn: false,
}

const UserContext = React.createContext([{}, ({}) => {}]);
UserContext.displayName = 'UserContext';

const UserProvider = (props) => {

    // code for pre-loading the user's information if we have their token in
    // localStorage goes here
    let currentUserStr = localStorage.getItem('currentUser');
    let currentUser = currentUserStr !== null
        ? JSON.parse(currentUserStr)
        : defaultUser;

    const [state, setState] = useState(currentUser);

    return (
        <UserContext.Provider value={[state, setState]}>
            {props.children}
        </UserContext.Provider>
        // <AuthContext.Provider value={{data, login, logout, register}} {...props} />
    );
}

export {UserContext, UserProvider, defaultUser};
