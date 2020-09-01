import React from 'react';
import Jumbotron from "react-bootstrap/Jumbotron";

class ErrorBoundary extends React.Component {
    state = {
        hasError: false,
        error: { message: '', stack: '' },
        info: { componentStack: '' }
    };

    static getDerivedStateFromError = error => {
        return { hasError: true };
    };

    componentDidCatch = (error, info) => {
        this.setState({ error, info });
    };

    render() {
        const { hasError, error, info } = this.state;
        const { children } = this.props;
        if(hasError) {
            console.log("error is " + JSON.stringify(error));
            console.log("info is " + JSON.stringify(info));
        }
        return hasError
            ? <Jumbotron>
                <h1>
                    <i className="fas fa-exclamation-triangle"></i>
                    Something went wrong
                </h1>
            </Jumbotron>

            : children;
    }
}

export default ErrorBoundary;