import React, {useContext, useState} from 'react';

import {UserForm} from './UserFormReducer.js';
import ResetPasswordModal from "./ResetPasswordModal.js";

import Toast from "react-bootstrap/Toast";

import {put, post, get, useAuthHeader} from './BasicAuth.js';
import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import {UserContext} from "./UserContext";

function EditUser({history, match}) {

    const { params: { username } } = match;

    const userEndpoint = '/user/' + username;
    const updatePasswordEndpoint = userEndpoint + '/password/update'

    const userContext = useContext(UserContext);

    const headers = useAuthHeader();

    const loadUserPromise = get(userEndpoint, headers);

    const [toast, setToast] = useState(false);
    const [saveSuccess, setSaveSuccess] = useState(false);
    const [showResetPassword, setShowResetPassword] = useState(false);

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

    const onResetPassword = (plainTextPassword) => {
        post(updatePasswordEndpoint, plainTextPassword, headers)
            .then(result => {
                if(result.status !== 200) {
                    console.log("ERROR SAVING PASSWORD");
                    console.log(result);
                    setSaveSuccess(false);
                    setToast(true);
                }
                else {
                    const user = userContext.getCurrentUser();
                    if(user.username === username) {
                        const updatedUser = {...user, password: plainTextPassword}
                        userContext.setCurrentUser(updatedUser);
                    }
                    setShowResetPassword(false);
                }
            });
    }

    // onClose={toggleSuccessToast}
    const toastMessage = saveSuccess ? "Save Successful" : "Save Failed";
    const toastStyle = saveSuccess ? "text-success" : "text-danger";
    const toastHeader = saveSuccess ? "Info" : "Error";

    return (
        <div className="container mt-3">
            <h1>User Profile</h1>

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

            <Button variant="warning" className="ml-0" onClick={() => setShowResetPassword(true)}>
                <i className="fa fa-key" aria-hidden="true" />   Reset Password
            </Button>

            <ResetPasswordModal show={showResetPassword} onConfirm={onResetPassword} onHide={() => setShowResetPassword(false)} />

            <Container className="pl-0 pr-0">
                <UserForm loadUserPromise={loadUserPromise} onSave={onSave} onCancel={history.goBack}/>
            </Container>
        </div>

    );
}

export default EditUser;
