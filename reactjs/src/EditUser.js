import React, {useState} from 'react';

import {UserForm} from './UserForm.js';
import Toast from "react-bootstrap/Toast";

import {fetchWithAuth, putWithAuth} from './BasicAuth.js';

function EditUser({history, match}) {

    const { params: { username } } = match;

    const userEndpoint = '/user/' + username;

    const loadUserPromise = fetchWithAuth(userEndpoint);

    const [saveSuccess, setSaveSuccess] = useState(false);

    const toggleSuccessToast = () => setSaveSuccess(!saveSuccess);

    const onSave = (userData) => {
        putWithAuth(userEndpoint, userData)
            .then(result => {
                if(result.status !== 200) {
                    console.log("ERROR EDITING USER");
                    console.log(result);
                }
                else {
                    history.push('/users');
                }
            });
    }

    return (
        <div>
            <Toast show={saveSuccess} onClose={toggleSuccessToast} animation={true} autohide={true} delay={3000}
                   style={{
                       position: 'absolute',
                       top: 60,
                       right: 0,
                       width: 250
                   }}>
                <Toast.Header>
                    <strong className="mr-auto text-success">Info</strong>
                </Toast.Header>
                <Toast.Body>Save Successful for {username}</Toast.Body>
            </Toast>
            <UserForm loadUserPromise={loadUserPromise} onSave={onSave} isUsernameEditable={false}/>
        </div>
    );
}

export default EditUser;
