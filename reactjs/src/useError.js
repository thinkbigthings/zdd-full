import { useContext } from 'react';
import { ErrorContext, noErrors } from "./ErrorContext";

const useError = () => {

    const [error, setError] = useContext(ErrorContext);

    function addError(message) {
        setError({message, hasError: true});
    }

    function clearError() {
        setError(noErrors);
    }

    return {
        error,
        addError,
        clearError,
    }
};

export default useError;