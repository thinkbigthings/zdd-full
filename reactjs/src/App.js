import React from 'react';

import './App.css';
// import 'bootstrap/dist/css/bootstrap.min.css';
import NavBar from 'react-bootstrap/NavBar';
import Nav from 'react-bootstrap/Nav';

import { HashRouter, Route } from 'react-router-dom';

import Home from './Home.js';
import UserList from './UserList.js';
import About from './About.js';
import CreateUser from './CreateUser.js';
import EditUser from './EditUser.js';
import Login from './Login.js';

function App() {

    return (
        <div className="App">
            <HashRouter>
                <NavBar bg="dark" variant="dark">
                    <NavBar.Brand>ZDD Demo</NavBar.Brand>
                    <Nav className="mr-auto">
                        <Nav.Link href="/">Home</Nav.Link>
                        <Nav.Link href="#users">Users</Nav.Link>
                        <Nav.Link href="#about">About</Nav.Link>
                    </Nav>
                </NavBar>
                <Route exact path="/" render={() => <Home />} />
                <Route exact path="/about" render={() => <About />} />
                <Route exact path="/users" render={() => <UserList />} />
                <Route exact path="/login" component={Login} />
                <Route exact path="/users/create" component={CreateUser} />
                <Route exact path="/users/:username/edit" component={EditUser} />
            </HashRouter>
        </div>
    );
}

export default App;
