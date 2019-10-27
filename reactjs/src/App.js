import React, { useState, useEffect } from 'react';

import logo from './logo.svg';
import './App.css';

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

    // When React's Suspense feature with fetch is ready, that’ll be the preferred way to fetch data
    useEffect(fetchData2, [setInfo]);

    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>
                <h1 className="App-title">{info.users.count}</h1>
                <button onClick={fetchData2}>
                    Click me
                </button>
            </header>

        </div>
    );
}

export default App;
