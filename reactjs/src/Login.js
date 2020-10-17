import React, {useState} from 'react';
import Button from "react-bootstrap/Button";

import {getWithCreds} from './BasicAuth.js';
import useCurrentUser from "./useCurrentUser";
import useError from "./useError";
import {recoveryActions} from "./ErrorContext";

// login needs to be a component in the router for history to be passed here
function Login({history}) {

    const { addError } = useError();

    // local form state
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const {onLogin} = useCurrentUser();

    // call the callback function if the enter key was pressed in the event
    function callOnEnter(event, callback) {
        if(event.key === 'Enter') {
            callback();
        }
    }

    const onClickLogin = () => {

        const credentials = {
            username: username,
            password: password
        }

        const loginUrl = '/login';
        getWithCreds(loginUrl, credentials)
            .then(retrievedUser => {
                const loggedInUser = {...retrievedUser, isLoggedIn: true}
                onLogin(loggedInUser);
                history.push("/");
            })
            .catch(error => {
                console.log(error.message);
                addError("Login failed.", recoveryActions.NONE);
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
                    <input type="password" className="form-control" id="inputPassword" placeholder="Password"
                           value={password}
                           onChange={e => setPassword(e.target.value) }
                           onKeyPress={e => callOnEnter(e, onClickLogin) }
                    />
                </div>

                <Button variant="success" onClick={() => onClickLogin() }>Login</Button>
            </form>

        </div>
    );
}

export default Login;
