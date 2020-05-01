import React, {useState} from 'react';
import Button from "react-bootstrap/Button";

// login needs to be a component in the router for history to be passed here
function Login({history}) {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    // call the callback function if the enter key was pressed in the event
    function callOnEnter(event, callback) {
        if(event.key === 'Enter') {
            console.log("calling callback");
            callback();
        }
    }

    const onLogin = () => {
        // later this will be a call to /authenticate and we'll store a token
        localStorage.setItem("username", username);
        localStorage.setItem("password", password);
        localStorage.setItem('authToken', '');
        console.log("saving to localStorage " + username + " " + password);
        history.push("/");
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
