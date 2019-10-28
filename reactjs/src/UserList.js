import React from 'react';

// import 'bootstrap/dist/css/bootstrap.min.css';
import ListGroup from 'react-bootstrap/ListGroup';

function UserList(props) {

    let users = props.users;

    return (
        <ListGroup>
            {users.map(user =>
                <ListGroup.Item key={user.username}>{user.displayName}</ListGroup.Item>)
            }
        </ListGroup>
    );
}

export default UserList;
