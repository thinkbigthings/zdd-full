import React, {useEffect, useState} from 'react';

// import 'bootstrap/dist/css/bootstrap.min.css';
import ListGroup from 'react-bootstrap/ListGroup';
import { Link } from 'react-router-dom';

function UserList() {

    const [userList, setUserList] = useState([]);

    let fetchRecentUsers = () => {
        fetch('/user')
            .then(httpResponse => httpResponse.json())
            .then(u => setUserList(u));
    };

    // When React's Suspense feature with fetch is ready, that'll be the preferred way to fetch data
    useEffect(fetchRecentUsers, [setUserList]);

    return (
        <div>
            <Link to={"/users/create"} className="btn btn-primary" >Create User</Link>
            <ListGroup>
                {userList.map(user =>
                    <ListGroup.Item key={user.username}>
                        {user.displayName}
                        <Link to={"/users/" + user.username + "/details" } className="btn btn-primary">Details</Link>
                        <Link to={"/users/" + user.username + "/edit" } className="btn btn-primary">Edit</Link>
                    </ListGroup.Item>)
                }
            </ListGroup>
        </div>
    );
}

export default UserList;
