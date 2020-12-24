import React from 'react';

import ButtonGroup  from 'react-bootstrap/ButtonGroup';
import Button       from "react-bootstrap/Button";
import Container    from 'react-bootstrap/Container';
import Row          from 'react-bootstrap/Row';
import Col          from 'react-bootstrap/Col';

import copy from './Copier.js';
import useApiLoader from "./useApiLoader";
import CenteredSpinner from "./CenteredSpinner";
import {Table} from "react-bootstrap";

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

function ItemList() {

    const {setUrl, isLoading, isLongRequest, fetchedData} = useApiLoader('/item?page=0&size=10', initialPage);

    let fetchItems = (pageable) => {
        setUrl('/item?' + pageQuery(pageable));
    };

    const pageQuery = (pageable) => {
        return 'page=' + pageable.pageNumber + '&size=' + pageable.pageSize;
    }

    function movePage(amount) {
        let pageable = copy(fetchedData.pageable);
        pageable.pageNumber = pageable.pageNumber + amount;
        fetchItems(pageable);
    }

    const firstElementInPage = fetchedData.pageable.offset + 1;
    const lastElementInPage = fetchedData.pageable.offset + fetchedData.numberOfElements;
    const currentPage = firstElementInPage + "-" + lastElementInPage + " of " + fetchedData.totalElements;

    if(isLoading && ! isLongRequest) { return <div />; }

    if(isLoading && isLongRequest) {   return <CenteredSpinner /> ; }

    return (
        <>

            <div className="container mt-3">
                <h1>Items</h1>

                <Table striped bordered hover>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Type</th>
                        <th>THC%</th>
                        <th>CBD%</th>
                        <th>Weight (g)</th>
                        <th>Price ($)</th>
                    </tr>
                    </thead>
                    <tbody>

                    {fetchedData.content.map(item =>
                        <tr>
                            <td>{item.strain}</td>
                            <td>{item.subspecies}</td>
                            <td>{item.thc}</td>
                            <td>{item.cbd}</td>
                            <td>{item.weightGrams}</td>
                            <td>{item.priceDollars}</td>
                        </tr>
                    )}
                    {/*
                    List<TerpeneAmount> terpeneAmounts
                    String vendor
                    */}

                    </tbody>
                </Table>

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

export default ItemList;
