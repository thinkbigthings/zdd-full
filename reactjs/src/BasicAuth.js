
import {REACT_APP_API_VERSION, VERSION_HEADER} from "./HttpResponseFilter";

function basicHeader() {

    let headers = {
        "Content-Type": "application/json"
    };
    headers[VERSION_HEADER] = REACT_APP_API_VERSION;
    return headers;
}

export {basicHeader}