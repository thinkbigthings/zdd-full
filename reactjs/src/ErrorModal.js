import React from 'react';

import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";

import useError from "./useError";

function ErrorModal(props) {

    const { error, clearError } = useError();

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
                <Button variant="primary" onClick={ () => window.location.reload(true)}>
                    Reload
                </Button>
            </Modal.Footer>
        </Modal>
    );

}

export default ErrorModal;
