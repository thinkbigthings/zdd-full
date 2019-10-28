import React, { useState, useEffect } from 'react';

import logo from './logo.svg';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Button from 'react-bootstrap/Button';
import NavBar from 'react-bootstrap/NavBar';
import Nav from 'react-bootstrap/Nav';
import Form from 'react-bootstrap/Form';
import FormControl from 'react-bootstrap/FormControl';


function App() {

    const [info, setInfo] = useState({users:{count:0}});

    let fetchData2 = () => {
        fetch('/actuator/info')
            .then(httpResponse => httpResponse.json())
            .then(count => setInfo(count));
    };

    // this works too
    async function fetchData() {
        const res = await fetch('/actuator/info');
        const json = await res.json();
        setInfo(json);
    }

    // When React's Suspense feature with fetch is ready, that'll be the preferred way to fetch data
    useEffect(fetchData2, [setInfo]);

    return (
        <div className="App">
            <link
                rel="stylesheet"
                href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
                integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
                crossOrigin="anonymous"
            />
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
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>
                <h1 className="App-title">{info.users.count}</h1>
                <Button variant="primary"  onClick={fetchData2}>Update</Button>
            </header>

        </div>
    );
}

export default App;
