import React, {useEffect, useState} from 'react';

import { Link } from 'react-router-dom';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

import copy from './Copier.js';
import Button from "react-bootstrap/Button";

import {fetchWithAuth} from './BasicAuth.js';

const blankPage = {
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

    const [userPage, setUserPage] = useState(blankPage);

    const pageQuery = (pageable) => {
        return 'page='+pageable.pageNumber + "&"
            + 'size='+pageable.pageSize;
    }

    function movePage(amount) {
        let pageable = copy(userPage.pageable);
        pageable.pageNumber = pageable.pageNumber + amount;
        fetchRecentUsers(pageable);
    }

    let fetchRecentUsers = (pageable) => {
        fetchWithAuth('/user?' + pageQuery(pageable))
            .then(page => setUserPage(page));
    };

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
        <div className="container mt-3">
            <h1>User Management</h1>

            <Link to={"/users/create"} className="btn btn-success" ><i className="mr-2 fas fa-user-plus" />Create User</Link>
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
                <nav aria-label="Page navigation">
                    <ul className="pagination">
                        <li onClick={ () => movePage(-1) } className="page-item ">
                            <Button variant="primary" className={"btn btn-primary " + styleFirst} >
                                <i className="mr-2 fas fa-caret-left" />Previous
                            </Button>
                        </li>
                        <li className="page-item disabled"><span className="page-link">{currentPage}</span></li>
                        <li onClick={ () => movePage(1) } className="page-item ">
                            <Button variant="primary" className={"btn btn-primary " + styleLast} >
                                <i className="mr-2 fas fa-caret-right" />Next
                            </Button>
                        </li>
                    </ul>
                </nav>
            </Container>
        </div>
    );
}

export default UserList;
