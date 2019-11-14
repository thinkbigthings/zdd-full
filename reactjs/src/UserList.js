import React, {useEffect, useState} from 'react';

import { Link } from 'react-router-dom';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

function UserList() {

    const [userList, setUserList] = useState([]);

    let fetchRecentUsers = () => {
        fetch('/user')
            .then(httpResponse => httpResponse.json())
            .then(page => page.content)
            .then(u => setUserList(u));
    };

    // When React's Suspense feature with fetch is ready, that'll be the preferred way to fetch data
    useEffect(fetchRecentUsers, [setUserList]);

    return (
        <div className="container mt-3">
            <h1>User Management</h1>

            <Link to={"/users/create"} className="btn btn-success" ><i className="mr-2 fas fa-user-plus" />Create User</Link>
            <Container className="container mt-3">
                {userList.map(user =>
                    <Row key={user.displayName} className="pt-2 pb-2 border-bottom border-top ">
                        <Col >{user.displayName}</Col>
                        <Col xs={2}>
                            <Link to={"/users/" + user.username + "/edit" } className="btn btn-primary">
                                <i className="mr-2 fas fa-user-edit" />Edit
                            </Link>
                        </Col>
                    </Row>
                )}
            </Container>
        </div>
    );
}

export default UserList;
