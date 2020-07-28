import React, {useState} from 'react';

import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";


function CreateUserModal(props) {

    const {open, onConfirm, onCancel} = props;

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [email, setEmail] = useState('');

    function clearForm() {
        setUsername('');
        setEmail('');
        setPassword('');
        setConfirmPassword('');

        onCancel();
    }

    function save() {
        setUsername('');
        setEmail('');
        setPassword('');
        setConfirmPassword('');

        onConfirm( { username, email, plainTextPassword: password, displayName: username } );
    }

    // https://react-bootstrap.github.io/components/modal/

    const passwordReady = password === confirmPassword && password !== '';

    return (

        <Modal show={open} onHide={() => clearForm()}>
            <Modal.Header closeButton>
                <Modal.Title>Create User</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="form-group">
                    <label htmlFor="username">Username</label>
                    <input type="text" className="form-control" id="username" placeholder="Username"
                           value={username}
                           onChange={e => setUsername(e.target.value)} />
                </div>
                <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input type="text" className="form-control" id="email" placeholder="Email"
                           value={email}
                           onChange={e => setEmail(e.target.value)} />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input type="text" className="form-control" id="password" placeholder="Password"
                           value={password}
                           onChange={e => setPassword(e.target.value)} />
                </div>
                <div className="form-group">
                    <label htmlFor="confirmPassword">Confirm Password</label>
                    <input type="text" className="form-control" id="confirmPassword" placeholder="Confirm Password"
                           value={confirmPassword}
                           onChange={e => setConfirmPassword(e.target.value)} />
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={clearForm} >Cancel</Button>
                <Button variant="primary" onClick={save} disabled={!passwordReady}>Save</Button>
            </Modal.Footer>
        </Modal>

    );
}

export default CreateUserModal;
