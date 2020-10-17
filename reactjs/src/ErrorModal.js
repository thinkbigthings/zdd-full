import React from 'react';

import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";

import useError from "./useError";
import {recoveryActions} from "./ErrorContext";

function ErrorModal(props) {

    const { error, clearError } = useError();

    const displayReload = error.recoveryAction === recoveryActions.RELOAD;
    const displayReloadStyle = displayReload ? '' : 'd-none';

    return (
        <Modal show={error.hasError} onHide={clearError} backdrop="static" centered>
            <Modal.Header closeButton>
                <Modal.Title>
                    Warning
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Alert variant='warning'>
                    {error.message}
                </Alert></Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={clearError}>
                    Cancel
                </Button>
                <Button className={displayReloadStyle} variant="primary" onClick={ () => window.location.reload(true)}>
                    Reload
                </Button>
            </Modal.Footer>
        </Modal>
    );

}

export default ErrorModal;
