import React, { useState, useEffect } from 'react';

import logo from './logo.svg';
import './App.css';

function App() {

    const [info, setInfo] = useState({users:{count:0}});

    let callForInfo = () => {
        fetch('/actuator/info')
            .then(httpResponse => httpResponse.json())
            .then(count => setInfo(count));
    };

    // TODO populate with real data from server on page load
    // useEffect() ?
    // callForInfo() ?
    // call first and pass in as initial value to useState() ?

    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>
                <h1 className="App-title">{info.users.count}</h1>
                <button onClick={callForInfo}>
                    Click me
                </button>
            </header>

        </div>
    );
}

export default App;
