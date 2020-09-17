import React, {useEffect, useReducer} from 'react';

import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";

import copy from './Copier.js';
import Container from "react-bootstrap/Container";

import AddressRow from './AddressRow.js';

const blankAddress = {
    line1: '',
    city: '',
    state: '',
    zip: ''
}

const blankUser = {
    username: '',
    displayName: '',
    email: '',
    heightCm: 0,
    phoneNumber: '',
    registrationTime: '',
    addresses: [],
    roles: []
}

const blankEditableAddress = {
    isEditing: false,
    address: blankAddress,
    originatingIndex: 0
}

const blankFormState = {
    user: blankUser,
    editableAddress: blankEditableAddress
}


function UserForm(props) {

    const {loadUserPromise, onSave, onCancel} = props;

    // return new state based on current state and action
    // reducer itself should not cause side effects, it should be called FROM a side effect
    // possibly combined into a custom hook
    const formReducer = (formState, action) => {

        let newState = copy(formState);
        switch(action.type) {
            case 'START_ADDRESS_EDIT': {
                newState.editableAddress.isEditing = true;
                newState.editableAddress.originatingIndex = action.payload;
                newState.editableAddress.address = newState.user.addresses[action.payload];
                return newState;
            }
            case 'UPDATE_ADDRESS_EDIT': {
                newState.editableAddress.address = {...newState.editableAddress.address, ...action.payload};
                return newState;
            }
            case 'KEEP_ADDRESS_EDIT': {
                const index = newState.editableAddress.originatingIndex;
                const updatedAddress = newState.editableAddress.address;
                newState.editableAddress.isEditing = false;
                newState.user.addresses[index] = {...updatedAddress};
                return newState;
            }
            case 'DISCARD_ADDRESS_EDIT': {
                newState.editableAddress.isEditing = false;
                return newState;
            }
            case 'ADD_ADDRESS': {
                newState.editableAddress.isEditing = true;
                newState.editableAddress.originatingIndex = newState.user.addresses.length;
                newState.editableAddress.address = blankAddress;
                return newState;
            }
            case 'DELETE_ADDRESS': {
                newState.user.addresses.splice(action.payload, 1);
                return newState;
            }
            case 'LOAD_USER': {
                newState.user = action.payload
                return newState;
            }
            case 'UPDATE_USER': {
                newState.user = {...newState.user, ...action.payload};
                return newState;
            }
            default: {
                throw new Error('Unhandled action type ' + action.type);
            }
        }
    }

    const [formState, dispatch] = useReducer(formReducer, blankFormState);

    // TODO do we need the deps when using the reducer? Seems to function the same with/without the dispatch dependency
    // TODO braces around the method body of the effect seem to be necessary when it's a promise, not sure why
    // TODO could the load function be the third parameter to the reducer hook? to lazy initialize the full state
    useEffect(() => { loadUserPromise.then(u => dispatch({type:'LOAD_USER', payload: u}))}, [loadUserPromise, dispatch]);

    const address = formState.editableAddress.address;

    return (
        <Container className="mt-3 pt-3 pl-0 pr-0 border-top border-light" >

            <form>

                <div className="form-group">
                    <label htmlFor="inputUserName">User Name</label>
                    <input type="text" className="form-control" id="inputUserName" placeholder="User Name"
                           disabled = "disabled"
                           value={formState.user.username}/>
                </div>

                <div className={"form-group "}>
                    <label htmlFor="registrationTime">Registration Time</label>
                    <input type="text" className="form-control" id="inputRegistrationTime" placeholder="Registration Time"
                           disabled = "disabled"
                           value={formState.user.registrationTime}/>
                </div>

                <div className={"form-group "}>
                    <label htmlFor="roles">Roles</label>
                    <input type="text" className="form-control" id="roles" placeholder="Roles"
                           disabled = "disabled"
                           value={formState.user.roles}/>
                </div>

                <div className="form-group">
                    <label htmlFor="inputDisplayName">Display Name</label>
                    <input type="text" className="form-control" id="inputDisplayName" placeholder="Display Name"
                           value={formState.user.displayName}
                           onChange={e => dispatch({type:'UPDATE_USER', payload: {displayName: e.target.value }})}/>
                </div>

                <div className="form-group">
                    <label htmlFor="exampleInputEmail1">Email address</label>
                    <input type="email" className="form-control" id="exampleInputEmail1" aria-describedby="emailHelp"
                           placeholder="Enter email"
                           value={formState.user.email}
                           onChange={e => dispatch({type:'UPDATE_USER', payload: {email: e.target.value }})}/>
                    <small id="emailHelp" className="form-text text-muted">We'll never share your email with anyone
                        else.</small>
                </div>

                <div className="form-group">
                    <label htmlFor="inputHeight">Height (cm)</label>
                    <input type="number" className="form-control" id="inputHeight" placeholder="Height"
                           value={formState.user.heightCm}
                           onChange={e => dispatch({type:'UPDATE_USER', payload: {heightCm: e.target.value }})}/>
                </div>

                <div className="form-group">
                    <label htmlFor="inputPhone">Phone Number</label>
                    <input type="text" className="form-control" id="inputPhone" placeholder="Phone Number"
                           value={formState.user.phoneNumber}
                           onChange={e => dispatch({type:'UPDATE_USER', payload: {phoneNumber: e.target.value }})}/>
                </div>

                <hr />

                <div className="container mt-3">

                    <span className="font-weight-bold mr-2">Addresses</span>

                    <Button variant="success"  onClick={() => dispatch({type:'ADD_ADDRESS'})}>
                        <i className="mr-2 fas fa-plus-circle" />Add Address
                    </Button>

                    <Container className="container mt-3">
                        {formState.user.addresses.map( (address, index) =>
                            <AddressRow key={index} currentAddress={address}
                                        onEdit={() => dispatch({type:'START_ADDRESS_EDIT', payload: index})}
                                        onDelete={() => dispatch({type:'DELETE_ADDRESS', payload: index})} />
                        )}
                    </Container>
                </div>

                <div className="form-group">

                    <Modal show={formState.editableAddress.isEditing} onHide={() => dispatch({type:'DISCARD_ADDRESS_EDIT'})}>
                        <Modal.Header closeButton>
                            <Modal.Title>Edit Address</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>

                            <div className="form-group">
                                <label htmlFor="line1">Street Address</label>
                                <input type="text" className="form-control" id="line1" placeholder="Street Address"
                                       value={address.line1}
                                       onChange={e => dispatch({type:'UPDATE_ADDRESS_EDIT', payload: {line1: e.target.value }})}/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="city">City</label>
                                <input type="text" className="form-control" id="city" placeholder="City"
                                       value={address.city}
                                       onChange={e => dispatch({type:'UPDATE_ADDRESS_EDIT', payload: {city: e.target.value }})}/>

                            </div>
                            <div className="form-group">
                                <label htmlFor="state">State</label>
                                <input type="text" className="form-control" id="state" placeholder="State"
                                       value={address.state}
                                       onChange={e => dispatch({type:'UPDATE_ADDRESS_EDIT', payload: {state: e.target.value }})}/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="zip">Zip</label>
                                <input type="text" className="form-control" id="zip" placeholder="Zip"
                                       value={address.zip}
                                       onChange={e => dispatch({type:'UPDATE_ADDRESS_EDIT', payload: {zip: e.target.value }})}/>
                            </div>

                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={ () =>  dispatch({type:'DISCARD_ADDRESS_EDIT'})}>Cancel</Button>
                            <Button variant="primary" onClick={ () =>  dispatch({type:'KEEP_ADDRESS_EDIT'})}>OK</Button>
                        </Modal.Footer>
                    </Modal>
                </div>

                <Button variant="success" onClick={() => { onSave(formState.user); }}>Save</Button>
                {/*<Link to={"/users" } className="btn btn-light ml-3">Cancel</Link>*/}
                <Button variant="light" onClick={onCancel}>Cancel</Button>
            </form>

        </Container>

    );
}

export {UserForm};
