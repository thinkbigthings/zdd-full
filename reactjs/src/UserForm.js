import React, {useState} from 'react';
// import 'bootstrap/dist/css/bootstrap.min.css';
import Button from "react-bootstrap/Button";


function CreateUser(props) {

    const {initialUser, onSave} = props;

    console.log(initialUser);

    const [username, setUsername] = useState(initialUser.username);
    const [displayName, setDisplayName] = useState(initialUser.displayName);
    const [email, setEmail] = useState(initialUser.email);
    const [heightCm, setHeightCm] = useState(initialUser.heightCm);
    const [phoneNumber, setPhoneNumber] = useState(initialUser.phoneNumber);

    const buildUserData = () => {
        return {
            username: username,
            displayName: displayName,
            email: email,
            heightCm: heightCm,
            phoneNumber: phoneNumber,
        }
    }

    const onClickSave = () => {
        const formUserData = buildUserData();
        console.log(formUserData);
        onSave(formUserData);
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

                <Button variant="primary" onClick={onClickSave}>Save</Button>

            </form>
        </div>

    );
}

export default CreateUser;
