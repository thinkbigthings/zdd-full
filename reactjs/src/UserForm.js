import React, {useEffect, useState} from 'react';

import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import {Link} from "react-router-dom";

import copy from './Copier.js';

const blankUser = {
    username: '',
    displayName: '',
    email: '',
    heightCm: 0,
    phoneNumber: '',
    registrationTime: '',
    addresses: []
}

const addressToString = (address) => {
    return address.line1 + ', ' + address.city ; // + ' ' + address.state + ' ' + address.zip;
}

function UserForm(props) {

    const {loadUserPromise, onSave, isUsernameEditable} = props;

    const [user, setUser] = useState(blankUser);

    // When React's Suspense feature with fetch is ready, that'll be the preferred way to fetch data
    useEffect(() => { loadUserPromise.then(u => setUser(u)) },[loadUserPromise, setUser]);

    function setUserValue(fieldName, fieldValue) {
        let userCopy = copy(user);
        userCopy[ fieldName ] = fieldValue;
        setUser(userCopy);
    }

    const displayRegistrationStyle = user.registrationTime === '' ? "d-none" : "";

    const [show, setShow] = useState(false);

    return (
        <div className="container mt-5">

            <form>

                <div className="form-group">
                    <label htmlFor="inputUserName">User Name</label>
                    <input type="text" className="form-control" id="inputUserName" placeholder="User Name"
                           disabled = {isUsernameEditable ? "" : "disabled"}
                           value={user.username}
                           onChange={e => setUserValue("username", e.target.value) }/>
                </div>

                <div className={"form-group " + displayRegistrationStyle}>
                    <label htmlFor="registrationTime">Registration Time</label>
                    <input type="text" className="form-control" id="inputRegistrationTime" placeholder="Registration Time"
                           disabled = "disabled"
                           value={user.registrationTime}/>
                </div>

                <div className="form-group">
                    <label htmlFor="inputDisplayName">Display Name</label>
                    <input type="text" className="form-control" id="inputDisplayName" placeholder="Display Name"
                           value={user.displayName}
                           onChange={e => { setUserValue("displayName", e.target.value) }}/>
                </div>

                <div className="form-group">
                    <label htmlFor="exampleInputEmail1">Email address</label>
                    <input type="email" className="form-control" id="exampleInputEmail1" aria-describedby="emailHelp"
                           placeholder="Enter email"
                           value={user.email}
                           onChange={e => setUserValue("email", e.target.value) }/>
                    <small id="emailHelp" className="form-text text-muted">We'll never share your email with anyone
                        else.</small>
                </div>

                <div className="form-group">
                    <label htmlFor="inputHeight">Height (cm)</label>
                    <input type="number" className="form-control" id="inputHeight" placeholder="Height"
                           value={user.heightCm}
                           onChange={e => setUserValue("heightCm", e.target.value) }/>
                </div>

                <div className="form-group">
                    <label htmlFor="inputPhone">Phone Number</label>
                    <input type="text" className="form-control" id="inputPhone" placeholder="Phone Number"
                           value={user.phoneNumber}
                           onChange={e => setUserValue("phoneNumber", e.target.value) }/>
                </div>

                <div className="form-group">

                    {user.addresses.map(a => addressToString(a))}
                    <Button variant="primary" onClick={() => setShow(true)}>
                        Edit Address
                    </Button>

                    <Modal show={show} onHide={() => setShow(false)}>
                        <Modal.Header closeButton>
                            <Modal.Title>Edit Address</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>

                            <div className="form-group">
                                <label htmlFor="line1">Street Address</label>
                                <input type="text" className="form-control" id="line1" placeholder="Street Address"
                                       value={ "address goes here" }
                                       onChange={e => setUserValue("phoneNumber", e.target.value) }/>
                            </div>

                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={() => setShow(false)}>Cancel</Button>
                            <Button variant="primary" onClick={() => setShow(false)}>OK</Button>
                        </Modal.Footer>
                    </Modal>
                </div>

                <Button variant="outline-success" onClick={() => { onSave(user); }}>Save</Button>
                <Link to={"/users" } className="btn btn-light ml-3">Cancel</Link>
            </form>

        </div>

    );
}

export {UserForm, blankUser};
