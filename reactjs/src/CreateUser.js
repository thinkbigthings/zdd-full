import React, {useState} from 'react';

// import 'bootstrap/dist/css/bootstrap.min.css';
import Button from "react-bootstrap/Button";


function CreateUser() {

    const [username, setUsername] = useState('');
    const [displayName, setDisplayName] = useState('');
    const [email, setEmail] = useState('');
    const [heightCm, setHeightCm] = useState(0);
    const [phoneNumber, setPhoneNumber] = useState('');


    let buildUserData = () => {
        return {
            username: username,
            displayName: displayName,
            email: email,
            heightCm: heightCm,
            phoneNumber: phoneNumber,
        }
    }

    let saveForm = () => {

        const userData = buildUserData();

        console.log(userData);

        fetch('/user', {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData),
        }).then(r => alert("SUCCESS!!!"));
    }

    return (
        <div className="container mt-5">
            <form>

                <div className="form-group">
                    <label htmlFor="inputUserName">User Name</label>
                    <input type="text" className="form-control" id="inputUserName" placeholder="User Name"
                           onChange={e => setUsername(e.target.value)}/>
                </div>

                <div className="form-group">
                    <label htmlFor="inputDisplayName">Display Name</label>
                    <input type="text" className="form-control" id="inputDisplayName" placeholder="Display Name"
                           onChange={e => setDisplayName(e.target.value)}/>
                </div>

                <div className="form-group">
                    <label htmlFor="exampleInputEmail1">Email address</label>
                    <input type="email" className="form-control" id="exampleInputEmail1" aria-describedby="emailHelp"
                           placeholder="Enter email"
                           onChange={e => setEmail(e.target.value)} />
                    <small id="emailHelp" className="form-text text-muted">We'll never share your email with anyone
                        else.</small>
                </div>

                <div className="form-group">
                    <label htmlFor="inputHeight">Height (cm)</label>
                    <input type="number" className="form-control" id="inputHeight" placeholder="Height"
                           onChange={e => setHeightCm(e.target.value)}/>
                </div>

                <div className="form-group">
                    <label htmlFor="inputPhone">Phone Number</label>
                    <input type="text" className="form-control" id="inputPhone" placeholder="Phone Number"
                           onChange={e => setPhoneNumber(e.target.value)}/>
                </div>

                <Button variant="primary" onClick={saveForm}>Save</Button>

            </form>
        </div>

    );
}

export default CreateUser;
