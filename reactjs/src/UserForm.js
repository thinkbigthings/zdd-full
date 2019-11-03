import React, {useEffect, useState} from 'react';
// import 'bootstrap/dist/css/bootstrap.min.css';
import Button from "react-bootstrap/Button";


function UserForm(props) {

    const {promiseLoadUser, onSave, usernameEditable} = props;

    const [username, setUsername] = useState('');
    const [displayName, setDisplayName] = useState('');
    const [email, setEmail] = useState('');
    const [heightCm, setHeightCm] = useState(0);
    const [phoneNumber, setPhoneNumber] = useState('');

    const onUserLoaded = (userData) => {
        setUsername(userData.username);
        setDisplayName(userData.displayName);
        setEmail(userData.email);
        setHeightCm(userData.heightCm);
        setPhoneNumber(userData.phoneNumber);
    }

    // When React's Suspense feature with fetch is ready, that'll be the preferred way to fetch data
    useEffect(() => { promiseLoadUser.then(u => onUserLoaded(u)) },
        [setUsername, setDisplayName, setEmail, setHeightCm, setPhoneNumber]);

    const onClickSave = () => {
        onSave({
            username: username,
            displayName: displayName,
            email: email,
            heightCm: heightCm,
            phoneNumber: phoneNumber,
        });
    }

    return (
        <div className="container mt-5">
            <form>

                <div className="form-group">
                    <label htmlFor="inputUserName">User Name</label>
                    <input type="text" className="form-control" id="inputUserName" placeholder="User Name"
                           disabled = {usernameEditable ? "" : "disabled"}
                           value={username}
                           onChange={e => setUsername(e.target.value)}/>
                </div>

                <div className="form-group">
                    <label htmlFor="inputDisplayName">Display Name</label>
                    <input type="text" className="form-control" id="inputDisplayName" placeholder="Display Name"
                           value={displayName}
                           onChange={e => setDisplayName(e.target.value)}/>
                </div>

                <div className="form-group">
                    <label htmlFor="exampleInputEmail1">Email address</label>
                    <input type="email" className="form-control" id="exampleInputEmail1" aria-describedby="emailHelp"
                           placeholder="Enter email"
                           value={email}
                           onChange={e => setEmail(e.target.value)} />
                    <small id="emailHelp" className="form-text text-muted">We'll never share your email with anyone
                        else.</small>
                </div>

                <div className="form-group">
                    <label htmlFor="inputHeight">Height (cm)</label>
                    <input type="number" className="form-control" id="inputHeight" placeholder="Height"
                           value={heightCm}
                           onChange={e => setHeightCm(e.target.value)}/>
                </div>

                <div className="form-group">
                    <label htmlFor="inputPhone">Phone Number</label>
                    <input type="text" className="form-control" id="inputPhone" placeholder="Phone Number"
                           value={phoneNumber}
                           onChange={e => setPhoneNumber(e.target.value)}/>
                </div>

                <Button variant="primary" onClick={onClickSave}>Save</Button>

            </form>
        </div>

    );
}

export default UserForm;
