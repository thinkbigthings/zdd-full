import React, {useState} from 'react';

import {UserForm} from './UserFormReducer.js';
import Toast from "react-bootstrap/Toast";

import {put, get, useAuthHeader} from './BasicAuth.js';

function EditUser({history, match}) {

    const { params: { username } } = match;

    const userEndpoint = '/user/' + username;

    const headers = useAuthHeader();

    const loadUserPromise = get(userEndpoint, headers);

    const [toast, setToast] = useState(false);
    const [saveSuccess, setSaveSuccess] = useState(false);

    const onSave = (userData) => {
        put(userEndpoint, userData, headers)
            .then(result => {
                if(result.status !== 200) {
                    console.log("ERROR SAVING USER");
                    console.log(result);
                    setSaveSuccess(false);
                    setToast(true);
                }
                else {
                    history.goBack();
                }
            });
    }

    // onClose={toggleSuccessToast}
    const toastMessage = saveSuccess ? "Save Successful" : "Save Failed";
    const toastStyle = saveSuccess ? "text-success" : "text-danger";
    const toastHeader = saveSuccess ? "Info" : "Error";

    return (
        <div>
            <Toast show={toast} animation={true} autohide={true} onClose={() => setToast(false)} delay={3000}
                   style={{
                       position: 'absolute',
                       top: 60,
                       right: 0,
                       width: 250
                   }}>
                <Toast.Header>
                    <strong className={"mr-auto " + toastStyle}>{toastHeader}</strong>
                </Toast.Header>

                <Toast.Body>{toastMessage}</Toast.Body>
            </Toast>
            <UserForm loadUserPromise={loadUserPromise} onSave={onSave} onCancel={history.goBack}/>
        </div>
    );
}

export default EditUser;
