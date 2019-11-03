import React, {useState} from 'react';

// import 'bootstrap/dist/css/bootstrap.min.css';
import Button from "react-bootstrap/Button";
import UserForm from './UserForm.js';


function CreateUser({history}) {

    const promiseLoadUser = new Promise(function(resolve, reject) {
        resolve({
            username: 'Fetched Username!',
            displayName: '',
            email: '',
            heightCm: 160,
            phoneNumber: '',
        });
    });

    const onSave = (userData) => {
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
        <UserForm promiseLoadUser={promiseLoadUser} onSave={onSave} usernameEditable={true}/>
    );
}

export default CreateUser;
