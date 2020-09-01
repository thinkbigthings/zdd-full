import React, {useEffect, useState} from 'react';
import Jumbotron from "react-bootstrap/Jumbotron";
import Button from "react-bootstrap/Button";

// picks up from .env file in build
const { REACT_APP_API_VERSION } = process.env;

const styleByStatus = {
    "UP" : "text-success",
    "DOWN" : "text-danger",
    "UNKNOWN" : "text-warning"
};

function About() {

    const [serverStatus, setServerStatus] = useState("UNKNOWN");

    function handleResponse(httpResponse) {
        if (httpResponse.status !== 200) {
            console.log(httpResponse);
            setServerStatus("DOWN");
        } else {
            setServerStatus("UP");
        }
    }

    let fetchData = () => {
        fetch('/actuator/health')
            .then(handleResponse)
            .catch(handleResponse);
    }

    useEffect(fetchData, [setServerStatus]);

    return (
        <Jumbotron>
            <h1>About</h1>
            <p>
                Client API Compatibility Version {REACT_APP_API_VERSION}
            </p>
            <p><span className={styleByStatus[serverStatus]}>
                {"Server is "+ serverStatus}</span>
            </p>
            <p>
                <Button variant="primary" onClick={fetchData}><i className="mr-2 fas fa-redo" />Refresh Status</Button>
            </p>
        </Jumbotron>
    );

}

export default About;
