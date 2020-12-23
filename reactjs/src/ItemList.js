import React from 'react';

import ButtonGroup  from 'react-bootstrap/ButtonGroup';
import Button       from "react-bootstrap/Button";
import Container    from 'react-bootstrap/Container';
import Row          from 'react-bootstrap/Row';
import Col          from 'react-bootstrap/Col';

import copy from './Copier.js';
import useApiLoader from "./useApiLoader";
import CenteredSpinner from "./CenteredSpinner";

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

                <Container className="container mt-3">
                    {fetchedData.content.map(item =>
                        <Row key={item.strain + item.weightGrams} className="pt-2 pb-2 border-bottom border-top ">
                            <Col >{item.strain}</Col>
                            <Col >{item.subspecies}</Col>
                            <Col >{item.thc}</Col>
                            <Col >{item.cbd}</Col>

                            {/*Subspecies subspecies, String strain,*/}
                            {/*BigDecimal thc, BigDecimal cbd, List<TerpeneAmount> terpeneAmounts,*/}
                            {/*BigDecimal weightGrams, Long priceDollars, String vendor*/}

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

export default ItemList;
