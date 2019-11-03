import React, {useState} from 'react';

// import 'bootstrap/dist/css/bootstrap.min.css';
import Button from "react-bootstrap/Button";
import {UserForm} from './UserForm.js';


function EditUser({history, match}) {

    const { params: { username } } = match;

    const userApi = '/user/' + username;

    const loadUserPromise = fetch(userApi).then(httpResponse => httpResponse.json());

    const onSave = (userData) => {
        fetch(userApi, {
            method: 'PUT',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData),
        }).then(r => {
            history.push('/users');
        });
    }

    return (
        <UserForm loadUserPromise={loadUserPromise} onSave={onSave} isUsernameEditable={false}/>
    );
}

export default EditUser;
