import React, {useEffect, useState} from 'react';

import Jumbotron from "react-bootstrap/Jumbotron";
import Button from "react-bootstrap/Button";
import useError from "./useError";
import {get} from './BasicAuth.js';

function Home() {

    const [info, setInfo] = useState({users:{count:0}});

    const { addError } = useError();

    let fetchData2 = () => {
        get('/actuator/info')
            .then(count => setInfo(count))
            .catch(error => addError("Trouble getting actuator info: " + error.message));
    };

    // // this works too
    // async function fetchData() {
    //     const res = await fetch('/actuator/info');
    //     const json = await res.json();
    //     setInfo(json);
    // }

    // When React's Suspense feature with fetch is ready, that'll be the preferred way to fetch data
    useEffect(fetchData2, [setInfo]);

    return (
        <Jumbotron>
            <h1>Zero Downtime Demo<i className="fas fa-home"></i></h1>
            <p>
                This is a sample app that manages users. It is a starting point for an app
                and a demonstration of doing zero downtime deployment.
            </p>
            <p>
                There are {info.users.count} users in the system.
            </p>
            <p>
                <Button variant="primary" onClick={fetchData2}>Refresh User Count</Button>
            </p>
        </Jumbotron>
    );
}

export default Home;
