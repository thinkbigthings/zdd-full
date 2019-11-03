import React, {useState} from 'react';

// import 'bootstrap/dist/css/bootstrap.min.css';
import Button from "react-bootstrap/Button";
import {UserForm, blankUser} from './UserForm.js';

function CreateUser({history}) {

    const loadUserPromise = new Promise(function(resolve, reject) {
        resolve(blankUser);
    });

    const onSave = (userData) => {
        fetch('/user', {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData),
        }).then(r => {
            history.push("/users");
        });
    }

    return (
        <UserForm loadUserPromise={loadUserPromise} onSave={onSave} isUsernameEditable={true}/>
    );
}

export default CreateUser;
