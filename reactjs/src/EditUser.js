import React, {useState} from 'react';

import {UserForm} from './UserForm.js';
import ResetPasswordModal from "./ResetPasswordModal.js";

import Toast from "react-bootstrap/Toast";

import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import {get, useAuthHeader} from './BasicAuth.js';
import useError from "./useError";
import useCurrentUser from "./useCurrentUser";
import useApiPost from "./useApiPost";
import useApiPut from "./useApiPut";


function EditUser({history, match}) {

    const { params: { username } } = match;

    const userEndpoint = '/user/' + username;
    const userInfoEndpoint = userEndpoint + '/personalInfo';
    const updatePasswordEndpoint = userEndpoint + '/password/update'

    const {currentUser, onLogin} = useCurrentUser();

    const headers = useAuthHeader();
    const { addError } = useError();

    const post = useApiPost();
    const put = useApiPut();

    const loadUserPromise = get(userEndpoint, headers)
        .catch(error => addError("Trouble loading user: " + error.message));


    // TODO use a different error display if it's a version mismatch vs server error
    // (e.g. there could be a constraint violation and we don't want to exit this component for that)

    const [toast, setToast] = useState(false);
    const [saveSuccess, setSaveSuccess] = useState(false);
    const [showResetPassword, setShowResetPassword] = useState(false);

    const onSave = (personalInfo) => {
        put(userInfoEndpoint, personalInfo)
            .then(result => history.goBack());
    }

    const onResetPassword = (plainTextPassword) => {
        post(updatePasswordEndpoint, plainTextPassword)
            .then(result => {
                if(currentUser.username === username) {
                    onLogin({...currentUser, password: plainTextPassword});
                }
                setShowResetPassword(false);
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
