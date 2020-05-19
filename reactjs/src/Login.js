import React, {useContext, useState} from 'react';
import Button from "react-bootstrap/Button";

import {defaultUserContext, UserContext} from './UserContext.js';
import {fetchWithAuth} from './BasicAuth.js';

function logout() {
    localStorage.removeItem('username');
    localStorage.removeItem('password');
}

function login(username, password) {
    localStorage.setItem('username', username);
    localStorage.setItem('password', password);
}


// login needs to be a component in the router for history to be passed here
function Login({history}) {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const currentUserContext = useContext(UserContext);

    // call the callback function if the enter key was pressed in the event
    function callOnEnter(event, callback) {
        if(event.key === 'Enter') {
            callback();
        }
    }

    const onLogin = () => {

        // TODO read from local storage to initialize the top level context on page load

        // TODO maybe pass in response handlers: map of response code to callbacks
        // and a default callback (so one for 200 and one for other errors as you can fill it in)

        login(username, password);

        fetchWithAuth('/user/' + username)
            .then(user => {

                    // TODO retrieve a real user to assign to the context
                    // user is being retrieved but can't be assigned
                    // need to learn more about context, maybe reducer hook?

                    // currentUserContext.login(user);
                    history.push("/");
                });
    }

    return (
        <div className="container mt-5">

            <form>

                <div className="form-group">
                    <label htmlFor="inputUsername">Username</label>
                    <input type="text" className="form-control" id="inputUsername" placeholder="Username"
                           value={username}
                           onChange={e => setUsername(e.target.value) }/>
                </div>

                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input type="text" className="form-control" id="inputPassword" placeholder="Password"
                           value={password}
                           onChange={e => setPassword(e.target.value) }
                           onKeyPress={e => callOnEnter(e, onLogin) }
                    />
                </div>

                <Button variant="success" onClick={() => onLogin() }>Login</Button>
            </form>

        </div>
    );
}

export default Login;
