import React, {useState} from 'react';

import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";


function ErrorModal(props) {

    return (
        <Modal show={props.show} onHide={props.onHide} backdrop="static" centered>
            <Modal.Header closeButton>
                <Modal.Title>
                    Warning
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Alert variant='warning'>
                    Page is out of date: please reload the page
                </Alert></Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={props.onHide}>
                    Ignore
                </Button>
                <Button variant="primary" onClick={ () => window.location.reload(true)}>
                    Reload
                </Button>
            </Modal.Footer>
        </Modal>
    );

}

export default ErrorModal;
