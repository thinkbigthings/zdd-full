import React from 'react';

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
        return hasError ? <h1>Something went wrong.</h1> : children;
    }
}

export default ErrorBoundary;