import React, {useState} from 'react';
import Button from "react-bootstrap/Button";

import useCurrentUser from "./useCurrentUser";
import useError from "./useError";
import {recoveryActions} from "./ErrorContext";
import {REACT_APP_API_VERSION, throwOnBadResponse, VERSION_HEADER} from "./HttpResponseFilter";

function getWithCreds(url, credentials) {

    const encoded = btoa(credentials.username + ":" + credentials.password);

    // If the server returns a 401 status code and includes one or more WWW-Authenticate headers, then
    // the browser pops up an authentication dialog asking for the username and password
    // Including X-Requested-With by the client signals the server to not respond with that header
    const requestMeta = {
        headers: {
            'Authorization': 'Basic ' + encoded,
            'X-Requested-With': 'XMLHttpRequest'
        }
    };

    return fetch(url, requestMeta);
}

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

    const loginUrl = '/login';

    const onClickLogin = () => {

        getWithCreds(loginUrl, { username, password })
            .then(throwOnBadResponse)
            .then(response =>
                response.json().then(data => ({
                    user: data,
                    status: response.status,
                    headers: response.headers
                }))
            )
            .then(response => {

                onLogin(response.user);
                history.push("/");

                // Check current client version and if out of date do a hard refresh.
                // If someone logs out and attempts to log in later, this gives us a good boundary to update the client.
                const clientApiVersion = REACT_APP_API_VERSION;
                const serverApiVersion = response.headers.get(VERSION_HEADER);
                if(clientApiVersion !== serverApiVersion) {
                    window.location.reload(true);
                }
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
