import React, {useContext, useState} from 'react';

import {UserForm} from './UserForm.js';
import ResetPasswordModal from "./ResetPasswordModal.js";

import Toast from "react-bootstrap/Toast";

import {put, post, get, useAuthHeader} from './BasicAuth.js';
import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import {UserContext} from "./UserContext";
import useError from "./useError";

function EditUser({history, match}) {

    const { params: { username } } = match;

    const userEndpoint = '/user/' + username;
    const userInfoEndpoint = userEndpoint + '/personalInfo';
    const updatePasswordEndpoint = userEndpoint + '/password/update'

    const userContext = useContext(UserContext);

    const headers = useAuthHeader();
    const { addError } = useError();

    const loadUserPromise = get(userEndpoint, headers)
        .catch(error => addError("Trouble loading user: " + error.message));


    // TODO use a different error display if it's a version mismatch vs server error
    // (e.g. there could be a constraint violation and we don't want to exit this component for that)

    const [toast, setToast] = useState(false);
    const [saveSuccess, setSaveSuccess] = useState(false);
    const [showResetPassword, setShowResetPassword] = useState(false);

    const onSave = (personalInfo) => {

        // // rebuild because incoming object has extra info
        // const personalInfo = {
        //     email: userData.email,
        //     displayName: userData.displayName,
        //     phoneNumber: userData.phoneNumber,
        //     heightCm: userData.heightCm,
        //     addresses: userData.addresses,
        // }

        put(userInfoEndpoint, personalInfo, headers)
            .then(result => history.goBack() )
            .catch(error => addError("Trouble saving user: " + error.message));
    }

    const onResetPassword = (plainTextPassword) => {
        post(updatePasswordEndpoint, plainTextPassword, headers)
            .then(result => {
                const user = userContext.getCurrentUser();
                if(user.username === username) {
                    const updatedUser = {...user, password: plainTextPassword}
                    userContext.setCurrentUser(updatedUser);
                }
                setShowResetPassword(false);
            })
            .catch(error => addError("Trouble Resetting password: " + error.message));
        ;
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
