import React, {useState} from 'react';

import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";

const blankFormData = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
}

// expected props: show, onHide, onConfirm
// https://react-bootstrap.github.io/components/modal/
function CreateUserModal(props) {

    const [user, setUser] = useState(blankFormData);

    function updateUser(updateValues) {
        setUser( {...user, ...updateValues});
    }

    function onHide() {
        setUser(blankFormData);
        props.onHide();
    }

    function onConfirm() {
        setUser(blankFormData);
        props.onConfirm({...user, displayName: user.username});
    }

    const passwordReady = user.password === user.confirmPassword && user.password !== '';

    return (

        <Modal show={props.show} onHide={props.onHide} >
            <Modal.Header closeButton>
                <Modal.Title>Create User</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="form-group">
                    <label htmlFor="username">Username</label>
                    <input type="text" className="form-control" id="username" placeholder="Username"
                           value={user.username}
                           onChange={e => updateUser({username : e.target.value })} />
                </div>
                <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input type="text" className="form-control" id="email" placeholder="Email"
                           value={user.email}
                           onChange={e => updateUser({email : e.target.value })} />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input type="text" className="form-control" id="password" placeholder="Password"
                           value={user.password}
                           onChange={e => updateUser({password : e.target.value })} />
                </div>
                <div className="form-group">
                    <label htmlFor="confirmPassword">Confirm Password</label>
                    <input type="text" className="form-control" id="confirmPassword" placeholder="Confirm Password"
                           value={user.confirmPassword}
                           onChange={e => updateUser({confirmPassword : e.target.value })} />
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onHide}>Close</Button>
                <Button variant="primary" onClick={onConfirm} disabled={!passwordReady}>Save</Button>
            </Modal.Footer>
        </Modal>

    );
}

export default CreateUserModal;
