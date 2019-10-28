import React, { useState, useEffect } from 'react';

import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Button from 'react-bootstrap/Button';
import NavBar from 'react-bootstrap/NavBar';
import Nav from 'react-bootstrap/Nav';
import Form from 'react-bootstrap/Form';
import FormControl from 'react-bootstrap/FormControl';
import Jumbotron from 'react-bootstrap/Jumbotron';

import UserList from './UserList.js';

function App() {

    const [info, setInfo] = useState({users:{count:0}});
    const [userList, setUserList] = useState([]);

    let fetchRecentUsers = () => {
        fetch('/user')
            .then(httpResponse => httpResponse.json())
            .then(u => setUserList(u));
    };

    let fetchData2 = () => {
        fetch('/actuator/info')
            .then(httpResponse => httpResponse.json())
            .then(count => setInfo(count));
    };

    // // this works too
    // async function fetchData() {
    //     const res = await fetch('/actuator/info');
    //     const json = await res.json();
    //     setInfo(json);
    // }

    // When React's Suspense feature with fetch is ready, that'll be the preferred way to fetch data
    useEffect(fetchData2, [setInfo]);
    useEffect(fetchRecentUsers, [setUserList]);

    return (
        <div className="App">
            <NavBar bg="dark" variant="dark">
                <NavBar.Brand href="#home">Navbar</NavBar.Brand>
                <Nav className="mr-auto">
                    <Nav.Link href="#home">Home</Nav.Link>
                    <Nav.Link href="#features">Features</Nav.Link>
                    <Nav.Link href="#pricing">Pricing</Nav.Link>
                </Nav>
                <Form inline>
                    <FormControl type="text" placeholder="Search" className="mr-sm-2" />
                    <Button variant="outline-info">Search</Button>
                </Form>
            </NavBar>
            <Jumbotron>
                <h1>Hello, world!</h1>
                <p>
                    This is a simple hero unit, a simple jumbotron-style component for calling
                    extra attention to featured content or information.
                    There are {info.users.count} users in the system.
                </p>
                <p>
                    <Button variant="primary"  onClick={fetchData2}>Refresh User Count</Button>
                </p>
            </Jumbotron>
            <UserList users={userList} />
        </div>
    );
}

export default App;
