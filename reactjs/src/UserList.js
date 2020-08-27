import React, {useEffect, useState} from 'react';

import { Link } from 'react-router-dom';

import ButtonGroup  from 'react-bootstrap/ButtonGroup';
import Button       from "react-bootstrap/Button";
import Container    from 'react-bootstrap/Container';
import Row          from 'react-bootstrap/Row';
import Col          from 'react-bootstrap/Col';

import copy from './Copier.js';
import {useAuthHeader, get, post} from "./BasicAuth";
import CreateUserModal from "./CreateUserModal";
import ErrorModal from "./ErrorModal";

const initialPage = {
    content: [],
    first: true,
    last: true,
    totalElements: 0,
    pageable: {
        offset: 0,
        pageNumber: 0,
        pageSize: 10,
    },
    numberOfElements: 0,
}

function UserList() {

    const [userPage, setUserPage] = useState(initialPage);
    const [showCreateUser, setShowCreateUser] = useState(false);
    const [showErrorModal, setShowErrorModal] = useState(false);

    // calling this from a callback allows the error boundary parent to catch the error
    const [/* state */, setError] = useState(null);

    let fetchRecentUsers = (pageable) => {
        get('/user?' + pageQuery(pageable), headers)
            .then(page => setUserPage(page))
            .catch(error => setError(() => { throw error }));
    };

    const headers = useAuthHeader();
    const onSave = (userData) => {
        setShowCreateUser(false);
        post('/user', userData, headers)
            .then(result => fetchRecentUsers(initialPage.pageable))
            .catch(error => setError(() => { throw error }));
    }



    const pageQuery = (pageable) => {
        return 'page=' + pageable.pageNumber + '&size=' + pageable.pageSize;
    }

    function movePage(amount) {
        let pageable = copy(userPage.pageable);
        pageable.pageNumber = pageable.pageNumber + amount;
        fetchRecentUsers(pageable);
    }

    // useEffect didn't seem to like this being defined inline
    let getCurrentList = () => {
        fetchRecentUsers(userPage.pageable);
    }


    // When React's Suspense feature with fetch is ready, that'll be the preferred way to fetch data
    useEffect(getCurrentList, [setUserPage]);

    const styleFirst = userPage.first ? "disabled" : "";
    const styleLast = userPage.last ? "disabled" : "";
    const firstElementInPage = userPage.pageable.offset + 1;
    const lastElementInPage = userPage.pageable.offset + userPage.numberOfElements;
    const currentPage = firstElementInPage + "-" + lastElementInPage + " of " + userPage.totalElements;


    return (
        <>
            <ErrorModal show={showErrorModal} onHide={() => setShowErrorModal(false)}/>

            <div className="container mt-3">
                <h1>User Management</h1>

                <Button variant="success" onClick={() => setShowCreateUser(true)}>Create User</Button>
                <CreateUserModal show={showCreateUser} onConfirm={onSave} onHide={() => setShowCreateUser(false)} />

                <Container className="container mt-3">
                    {userPage.content.map(user =>
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

                <ButtonGroup className="mt-2">
                    <Button variant="primary" className={"btn btn-primary " + styleFirst} onClick={ () => movePage(-1) }>
                        <i className="mr-2 fas fa-caret-left" />Previous
                    </Button>
                    <div className="page-item disabled"><span className="page-link">{currentPage}</span></div>
                    <Button variant="primary" className={"btn btn-primary " + styleLast} onClick={ () => movePage(1) }>
                        <i className="mr-2 fas fa-caret-right" />Next
                    </Button>
                </ButtonGroup>
            </div>
        </>
    );
}

export default UserList;
