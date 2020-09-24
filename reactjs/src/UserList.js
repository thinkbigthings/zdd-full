import React, {useContext, useEffect, useState} from 'react';

import { Link } from 'react-router-dom';

import ButtonGroup  from 'react-bootstrap/ButtonGroup';
import Button       from "react-bootstrap/Button";
import Container    from 'react-bootstrap/Container';
import Row          from 'react-bootstrap/Row';
import Col          from 'react-bootstrap/Col';
//
// import Spinner      from 'react-bootstrap/Spinner';
// import Jumbotron from "react-bootstrap/Jumbotron";
//

import copy from './Copier.js';
import {useAuthHeader, get, post} from "./BasicAuth";
import CreateUserModal from "./CreateUserModal";
import useError from "./useError";
import useApi from "./useApi";

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

    const [showCreateUser, setShowCreateUser] = useState(false);

    const {error, addError} = useError();
    const headers = useAuthHeader();

    const {setUrl, isLoading, hasError, fetchedData, isLongRequest} = useApi('/user?page=0&size=10', initialPage);


    let fetchRecentUsers = (pageable) => {
        setUrl('/user?' + pageQuery(pageable));
    };

    const onCreate = (userData) => {
        setShowCreateUser(false);

        const registrationRequest = {
            username: userData.username,
            plainTextPassword: userData.password,
            email: userData.email
        }
        post('/registration', registrationRequest, headers)
            .then(result => fetchRecentUsers(initialPage.pageable))
            .catch(error => addError("Trouble saving user: " + error.message));
    }

    const pageQuery = (pageable) => {
        return 'page=' + pageable.pageNumber + '&size=' + pageable.pageSize;
    }

    function movePage(amount) {
        let pageable = copy(fetchedData.pageable);
        pageable.pageNumber = pageable.pageNumber + amount;
        fetchRecentUsers(pageable);
    }

    const firstElementInPage = fetchedData.pageable.offset + 1;
    const lastElementInPage = fetchedData.pageable.offset + fetchedData.numberOfElements;
    const currentPage = firstElementInPage + "-" + lastElementInPage + " of " + fetchedData.totalElements;

    if(isLoading && ! isLongRequest) {
        console.log(isLoading + ' ' + isLongRequest);
        return (
            <div />
         );
    }

    if(isLoading && isLongRequest) {
        return (
            <Container>
                <Row className="text-center">
                    <Col xs="12" className="pt-5">
                        {/*//         <i className="fa fa-cog fa-spin fa-3x fa-fw"></i>*/}
                        {/*//         <span className="sr-only">Loading...</span>*/}
                        <div className="d-flex justify-content-center">
                            <div className="spinner-border text-secondary" role="status">
                                <span className="sr-only">Loading...</span>
                            </div>
                        </div>
                    </Col>
                </Row>
            </Container>
        );
    }

    return (
        <>

            <div className="container mt-3">
                <h1>User Management</h1>

                <Button variant="success" onClick={() => setShowCreateUser(true)}>Create User</Button>
                <CreateUserModal show={showCreateUser} onConfirm={onCreate} onHide={() => setShowCreateUser(false)} />

                <Container className="container mt-3">
                    {fetchedData.content.map(user =>
                        <Row key={user.personalInfo.displayName} className="pt-2 pb-2 border-bottom border-top ">
                            <Col >{user.personalInfo.displayName}</Col>
                            <Col xs={2}>
                                <Link to={"/users/" + user.username + "/edit" } className="btn btn-primary">
                                    <i className="mr-2 fas fa-user-edit" />Edit
                                </Link>
                            </Col>
                        </Row>
                    )}
                </Container>

                <ButtonGroup className="mt-2">
                    <Button variant="primary" disabled={fetchedData.first} className={"btn btn-primary "} onClick={ () => movePage(-1) }>
                        <i className="mr-2 fas fa-caret-left" />Previous
                    </Button>
                    <div className="page-item disabled"><span className="page-link">{currentPage}</span></div>
                    <Button variant="primary" disabled={fetchedData.last} className={"btn btn-primary "} onClick={ () => movePage(1) }>
                        <i className="mr-2 fas fa-caret-right" />Next
                    </Button>
                </ButtonGroup>
            </div>
        </>
    );
}

export default UserList;
