import React, {useState} from 'react';

// import 'bootstrap/dist/css/bootstrap.min.css';
import Button from "react-bootstrap/Button";
import UserForm from './UserForm.js';


function CreateUser({history}) {

    const initialUser = {
        username: '',
        displayName: '',
        email: '',
        heightCm: 0,
        phoneNumber: '',
    }

    let onSave = (userData) => {
        fetch('/user', {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData),
        }).then(r => {
            alert("SUCCESS!!!");
            history.push("/users");
        });
    }

    return (
        <UserForm initialUser={initialUser} onSave={onSave}/>
    );
}

export default CreateUser;
