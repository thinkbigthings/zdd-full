import React from 'react';

import Button from "react-bootstrap/Button";

import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

const addressToString = (address) => {
    return address.line1 + ', ' + address.city + ' ' + address.state + ' ' + address.zip;
}

function AddressRow(props) {

    const {currentAddress, onEdit, onDelete} = props;

    return (

        <Row key={addressToString(currentAddress)} className="pt-2 pb-2 border-bottom border-top ">
            <Col xs="9">
                {addressToString(currentAddress)}
            </Col>
            <Col xs="3">
                <Button variant="primary" className="mr-2" onClick={onEdit}>
                    <i className="mr-2 fas fa-edit" />Edit
                </Button>
                <Button variant="danger"  onClick={onDelete}>
                    <i className="mr-2 fas fa-trash" />Delete
                </Button>
            </Col>
        </Row>
    );
}

export default AddressRow;
