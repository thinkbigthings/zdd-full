import React, {useEffect, useState} from 'react';

// import 'bootstrap/dist/css/bootstrap.min.css';
import Jumbotron from "react-bootstrap/Jumbotron";
import Button from "react-bootstrap/Button";


function UserDetail({ match, location }) {

    const { params: { username } } = match;

    const [user, setUser] = useState({username:'', displayName:''});

    let fetchUser = () => {
        fetch('/user/' + username)
            .then(httpResponse => httpResponse.json())
            .then(details => setUser(details));
    };

    // When React's Suspense feature with fetch is ready, that'll be the preferred way to fetch data
    useEffect(fetchUser, [setUser]);

    return (
        <Jumbotron>
            <h1>{user.displayName}</h1>
            <p>
                {user.username}
            </p>
        </Jumbotron>
    );
}

export default UserDetail;
