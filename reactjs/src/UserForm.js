import React, {useEffect, useState} from 'react';

import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import {Link} from "react-router-dom";

import copy from './Copier.js';
import Container from "react-bootstrap/Container";

import AddressRow from './AddressRow.js';

const blankUser = {
    username: '',
    displayName: '',
    email: '',
    heightCm: 0,
    phoneNumber: '',
    registrationTime: '',
    addresses: [],
    editAddressIndex: -1
}

const blankAddress = {
    line1: '',
    city: '',
    state: '',
    zip: ''
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

    const [addressIndex, setAddressIndex] = useState(-1);
    const[address, setAddress] = useState(blankAddress);

    function setAddressValue(fieldName, fieldValue) {
        let addressCopy = copy(address);
        addressCopy[ fieldName ] = fieldValue;
        setAddress(addressCopy);
    }

    // https://stackoverflow.com/questions/33613728/what-happens-when-using-this-setstate-multiple-times-in-react-component
    function setUserAddress(address, index) {
        let userCopy = copy(user);
        userCopy.addresses[index] = address;
        setUser(userCopy);
        clearAddressForm();
    }

    function deleteAddress(index) {
        let userCopy = copy(user);
        userCopy.addresses.splice(index, 1);
        setUser(userCopy);
        clearAddressForm();
    }

    function clearAddressForm() {
        resetAddressForm(blankAddress, -1);
    }

    function resetAddressForm(addressValues, index) {
        setAddress(addressValues);
        setAddressIndex(index);
    }

    const displayRegistrationStyle = user.registrationTime === '' ? "d-none" : "";

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

                <hr />

                <div className="container mt-3">

                    <span className="font-weight-bold mr-2">Addresses</span>

                    <Button variant="success"  onClick={() => setAddressIndex(user.addresses.length)}>
                        <i className="mr-2 fas fa-plus-circle" />Add Address
                    </Button>

                    <Container className="container mt-3">
                        {user.addresses.map( (address, index) =>
                            <AddressRow key={index} currentAddress={address}
                                        onEdit={() => resetAddressForm(address, index)}
                                        onDelete={() => deleteAddress(index)} />
                        )}
                    </Container>
                </div>

                <div className="form-group">

                    <Modal show={addressIndex >= 0} onHide={() => clearAddressForm()}>
                        <Modal.Header closeButton>
                            <Modal.Title>Edit Address</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>

                            <div className="form-group">
                                <label htmlFor="line1">Street Address</label>
                                <input type="text" className="form-control" id="line1" placeholder="Street Address"
                                       value={address.line1}
                                       onChange={e => setAddressValue('line1', e.target.value)}  />
                            </div>
                            <div className="form-group">
                                <label htmlFor="city">City</label>
                                <input type="text" className="form-control" id="city" placeholder="City"
                                       value={address.city}
                                       onChange={e => setAddressValue('city', e.target.value)}  />
                            </div>
                            <div className="form-group">
                                <label htmlFor="state">State</label>
                                <input type="text" className="form-control" id="state" placeholder="State"
                                       value={address.state}
                                       onChange={e => setAddressValue('state', e.target.value)}  />
                            </div>
                            <div className="form-group">
                                <label htmlFor="zip">Zip</label>
                                <input type="text" className="form-control" id="zip" placeholder="Zip"
                                       value={address.zip}
                                       onChange={e => setAddressValue('zip', e.target.value)}  />
                            </div>

                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={ () => clearAddressForm()} >Cancel</Button>
                            <Button variant="primary" onClick={ () => setUserAddress(address, addressIndex)}>OK</Button>
                        </Modal.Footer>
                    </Modal>
                </div>

                <Button variant="success" onClick={() => { onSave(user); }}>Save</Button>
                <Link to={"/users" } className="btn btn-light ml-3">Cancel</Link>
            </form>

        </div>

    );
}

export {UserForm, blankUser};
