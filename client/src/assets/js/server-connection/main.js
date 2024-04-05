let api;

async function loadConfig(location) {
    await fetch(location)
        .then(resp => resp.json())
        .then(config => {
            api = `${config.host ? config.host + '/': ''}${config.group ? config.group + '/' : ''}api/`;
        });
}

function requestApi(uri, method, body) {
    const request = new Request(api + uri, {
        method: method,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    });
    return fetch(request)
            .then(response => response.json())
            .then(data => {
                return data;
            });
}

function requestApiWithouthFeedback(uri, method, body) {
    const request = new Request(api + uri, {
        method: method,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    });
    fetch(request);
}


/*CONTAINERS*/
function getAllContainers() {
    return requestApi("containers", "GET");
}
function getContainerByContainerID(containerID){
    return requestApi(`containers/${containerID}`, "GET");
}
function postNewContainer(size){
    return requestApi(`containers/${size}`, "POST");
}
function putCommentsToContainer(containerID, description){
    requestApiWithouthFeedback(`containers/${containerID}/comments`, "PUT", description)
}
function deleteContainer(containerID){
    return requestApi(`containers/${containerID}`, "DELETE");
}


/*PALLETS*/
function getAllPallets(){
    return requestApi("pallets", "GET");
}
function postNewPallet(){
    return requestApi(`pallets/universalsize`, "POST");
}
function putCommentsToPallet(palletID, description){
    requestApiWithouthFeedback(`pallets/${palletID}/comments`, "PUT", description)
}

/*BUSINESSES*/
function getContainersByOwner(owner){
    return requestApi(`business/${owner}/containers`, "GET");
}
function putNewOwnerToContainer(owner, containerID){
    return requestApi(`business/${owner}/containers/${containerID}`, "PUT");
}
function putNewOwnerToPallet(owner, palletID){
    return requestApi(`business/${owner}/pallets/${palletID}`, "PUT");
}


/*LOCATIONS*/
function getContainersByLocation(location){
    return requestApi(`location/${location}/containers`, "GET");
}
function putNewLocationToContainer(location, containerID, body){
    return requestApi(`location/${location}/containers/${containerID}`, "PUT", body);
}
function putNewLocationToPallet(location, palletID, body){
    return requestApi(`location/${location}/pallets/${palletID}`, "PUT", body);
}
function deleteContainer(containerID){
    return requestApi(`containers/${containerID}`, "DELETE");
}
function getPalletByPalletID(palletID){
    return requestApi(`pallets/${palletID}`, "GET");
}

