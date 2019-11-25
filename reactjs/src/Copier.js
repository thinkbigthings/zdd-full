
function copy(obj) {
    return JSON.parse(JSON.stringify(obj)); // deep copy but not methods
}

export default copy;
