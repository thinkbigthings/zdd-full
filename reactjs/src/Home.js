import React from 'react';

import Jumbotron from "react-bootstrap/Jumbotron";
import CenteredSpinner from "./CenteredSpinner";
import useApi from "./useApi";

const initialData = {
    users: {
        count:0
    }
};

function Home() {

    const {isLoading, isLongRequest, fetchedData} = useApi('/actuator/info', initialData);

    if(isLoading && ! isLongRequest) { return <div />; }

    if(isLoading && isLongRequest) {   return <CenteredSpinner /> ; }

    return (
        <Jumbotron>
            <h1>Zero Downtime Demo<i className="fas fa-home"></i></h1>
            <p>
                This is a sample app that manages users. It is a starting point for an app
                and a demonstration of doing zero downtime deployment.
            </p>
            <p>
                There are {fetchedData.users.count} users in the system.
            </p>
        </Jumbotron>
    );
}

export default Home;
