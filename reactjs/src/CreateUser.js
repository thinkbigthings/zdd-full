import React, {useState} from 'react';

import {UserForm, blankUser} from './UserForm.js';
import Toast from "react-bootstrap/Toast";
import {post, useAuthHeader} from "./BasicAuth";

function CreateUser({history}) {

    const loadUserPromise = new Promise(function(resolve, reject) {
        resolve(blankUser);
    });

    const [saveError, setSaveError] = useState(false);

    const toggleErrorToast = () => setSaveError(!saveError);

    const headers = useAuthHeader();

    const onSave = (userData) => {

        // TODO could pass in success/failure callbacks?
        // then fetch/post would work more similarly
        // and we could share the toast around

        post('/user', userData, headers)
            .then(result => {
                console.log(result);
                if(result.status !== 200) {
                    console.log(result);
                    setSaveError(true);
                }
                else {
                    history.push("/users");
                }
            });
    }

    return (
        <div>
            <Toast show={saveError} onClose={toggleErrorToast} animation={true} autohide={true} delay={3000}
                   style={{
                       position: 'absolute',
                       top: 60,
                       right: 0,
                       width: 250
                   }}>
                <Toast.Header>
                    <strong className="mr-auto text-danger">Error</strong>
                </Toast.Header>
                <Toast.Body>Save Failed</Toast.Body>
            </Toast>
            <UserForm loadUserPromise={loadUserPromise} onSave={onSave} isUsernameEditable={true}/>
        </div>
    );
}

export default CreateUser;
