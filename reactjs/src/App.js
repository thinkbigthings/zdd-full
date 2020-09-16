import React, {useContext} from 'react';

import './App.css';
import NavBar from 'react-bootstrap/NavBar';
import Nav from 'react-bootstrap/Nav';
import Form from 'react-bootstrap/Form';

import { HashRouter, Route } from 'react-router-dom';

import Home from './Home.js';
import UserList from './UserList.js';
import About from './About.js';
import EditUser from './EditUser.js';
import Login from './Login.js';
import ErrorBoundary from './ErrorBoundary.js';

import {ErrorProvider} from './ErrorContext.js';
import ErrorModal from "./ErrorModal";
import useCurrentUser from "./useCurrentUser";
import {CurrentUserContext, CurrentUserProvider} from "./CurrentUserContext";

function App() {

    return (
        <div className="App">
            <ErrorBoundary>
                <ErrorProvider>
                    <ErrorModal />
                    <CurrentUserProvider>
                        <CurrentUserContext.Consumer>
                            { value => value[0].isLoggedIn ? <AuthenticatedApp /> : <UnauthenticatedApp /> }
                        </CurrentUserContext.Consumer>
                    </CurrentUserProvider>
                </ErrorProvider>
            </ErrorBoundary>
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


function AuthenticatedApp() {

    const {currentUser, hasAdmin, onLogout} = useCurrentUser();

    const userUrl = "#/users/"+currentUser.username+"/edit";

    if(hasAdmin()) {
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
                        <Nav.Link onClick={onLogout}>Logout</Nav.Link>
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
                        <Nav.Link onClick={onLogout}>Logout</Nav.Link>
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
