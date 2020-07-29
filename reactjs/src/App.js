import React, {useContext, useState} from 'react';

import './App.css';
// import 'bootstrap/dist/css/bootstrap.min.css';
import NavBar from 'react-bootstrap/NavBar';
import Nav from 'react-bootstrap/Nav';
import Form from 'react-bootstrap/Form';

import { HashRouter, Route } from 'react-router-dom';

import Home from './Home.js';
import UserList from './UserList.js';
import About from './About.js';
import EditUser from './EditUser.js';
import Login from './Login.js';

import {UserProvider, UserContext, defaultUser} from './UserContext.js';

function App() {

    console.log('rendering App');

    return (
        <div className="App">
            <UserProvider>
                <UserContext.Consumer>
                    { value => value[0].isLoggedIn ? <AuthenticatedApp /> : <UnauthenticatedApp /> }
                </UserContext.Consumer>
            </UserProvider>
        </div>
    );
}

function UnauthenticatedApp() {
    return (
        <HashRouter>
            <NavBar bg="dark" variant="dark">
                <NavBar.Brand>ZDD Demo</NavBar.Brand>
                <Form inline>
                    <Nav.Link href="#login">Login</Nav.Link>
                </Form>
            </NavBar>
            <Route exact path="/login" component={Login} />
        </HashRouter>
    );
}

function hasRole(user, roleName) {
    return user.roles.find(role => role === roleName) !== undefined;
}

const isAdmin = user => hasRole(user, 'ADMIN');

function AuthenticatedApp() {

    const [user, setUser] = useContext(UserContext);
    function logout() {
        localStorage.removeItem("currentUser");
        setUser(defaultUser);
    }
    const admin = isAdmin(user);

    const userUrl = "#/users/"+user.username+"/edit";

    if(admin) {
        return (
            <HashRouter>
                <NavBar bg="dark" variant="dark">
                    <NavBar.Brand>ZDD Demo</NavBar.Brand>
                    <Nav className="mr-auto">
                        <Nav.Link href="/">Home</Nav.Link>
                        <Nav.Link href="#users">Users</Nav.Link>
                        <Nav.Link href={userUrl}>Profile</Nav.Link>
                        <Nav.Link href="#about">About</Nav.Link>
                    </Nav>
                    <Form inline>
                        <Nav.Link onClick={logout}>Logout</Nav.Link>
                    </Form>
                </NavBar>
                <Route exact path="/" render={() => <Home/>}/>
                <Route exact path="/about" render={() => <About/>}/>
                <Route exact path="/users" render={() => <UserList/>}/>
                <Route exact path="/users/:username/edit" component={EditUser}/>
            </HashRouter>
        );
    }
    else {
        return (
            <HashRouter>
                <NavBar bg="dark" variant="dark">
                    <NavBar.Brand>ZDD Demo</NavBar.Brand>
                    <Nav className="mr-auto">
                        <Nav.Link href="/">Home</Nav.Link>
                        <Nav.Link href="#about">About</Nav.Link>
                        <Nav.Link href={userUrl}>Profile</Nav.Link>
                    </Nav>
                    <Form inline>
                        <Nav.Link onClick={logout}>Logout</Nav.Link>
                    </Form>
                </NavBar>
                <Route exact path="/" render={() => <Home/>}/>
                <Route exact path="/about" render={() => <About/>}/>
                <Route exact path="/users/:username/edit" component={EditUser}/>
            </HashRouter>
        );
    }

}

export default App;
