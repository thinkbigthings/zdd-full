import React, {useState} from 'react';

// import 'bootstrap/dist/css/bootstrap.min.css';
import Button from "react-bootstrap/Button";
import {UserForm} from './UserForm.js';
import Toast from "react-bootstrap/Toast";


function EditUser({history, match}) {

    const { params: { username } } = match;

    const userApi = '/user/' + username;

    const loadUserPromise = fetch(userApi).then(httpResponse => httpResponse.json());

    const [saveSuccess, setSaveSuccess] = useState(false);

    const toggleSuccessToast = () => setSaveSuccess(!saveSuccess);

    const onSave = (userData) => {
        fetch(userApi, {
            method: 'PUT',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData),
        }).then(
            r => {setSaveSuccess(true);}
        );
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
