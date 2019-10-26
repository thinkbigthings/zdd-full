import React, { useState, useEffect } from 'react';

import logo from './logo.svg';
import './App.css';

function App() {

    const [info, setInfo] = useState("");

    useEffect(() => {
        fetch('/actuator/info')
            .then(response => response.text())
            .then(message =>  setInfo(message));
    });

    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>
                <h1 className="App-title">{info}</h1>
            </header>
        </div>
    );
}

export default App;
