"use strict";

let backgroundPopup;
let popupConfirm;


function addListenersToContainerPage(){
    sortByID();
    loadAllContainersAndPallets();
    backgroundPopup = document.querySelector(".popup-background");
    popupConfirm = document.querySelector("#container-pallet-popup-confirmation");

    document.querySelector(".add-new-container").addEventListener("submit", clickAddContainerOrPallet);
    document.querySelector(".add-new-pallet").addEventListener("submit", clickAddContainerOrPallet);

    document.querySelector(".rented-containers .sort-by").addEventListener("click", clickedSortBy);
    document.querySelector(".rented-pallets .sort-by").addEventListener("click", clickedSortBy);
}


/*SORTING FUNCTIONS*/
function clickedSortBy(e){
    const whatToSort = e.target.closest('div');
    if (whatToSort.classList.contains("rented-pallets")){
        showRightFieldsInSortByPopUp(" Pallet");
        document.querySelectorAll(".sort-size").forEach(use => {
            use.classList.add("hidden");
        });
    }
    else{
        showRightFieldsInSortByPopUp(" Container");
        document.querySelectorAll(".sort-size").forEach(use => {
            use.classList.remove("hidden");
        });
    }

    const containerSortBy = document.querySelector("#container-sort-by");
    containerSortBy.classList.remove("hidden");
    backgroundPopup.classList.add("visible");

    document.querySelector(`#container-sort-by .cancel`).addEventListener("click", function (){
        containerSortBy.classList.add("hidden");
        backgroundPopup.classList.remove("visible");
    });

    document.querySelector(`#container-sort-by .select`).addEventListener("click", checkWhatToSort);
}

function showRightFieldsInSortByPopUp(containerPallet){
    document.querySelector("#container-sort-by .text-area span").innerText = containerPallet;
    document.querySelector("#container-sort-by label span").innerText = containerPallet;

}

function checkWhatToSort(){
    const checkRadio = document.querySelector('input[name="sorting-by"]:checked');

    if (checkRadio.value === "ID"){
        sortByID();
        loadAllContainersAndPallets();
    }
    else if (checkRadio.value === "locations"){
        sortByLocation();
        loadAllContainersAndPallets();
    }
    else if (checkRadio.value === "owners"){
        sortByOwner();
        loadAllContainersAndPallets();
    }
    else if (checkRadio.value === "size"){
        sortByDimension();
        loadAllContainersAndPallets();
    }
    document.querySelector("#container-sort-by").classList.add("hidden");
    backgroundPopup.classList.remove("visible");
}

function sortByID(){
    allContainers.sort(function (container1, container2) {
        return container1.id - container2.id;
    });
    allPallets.sort(function (pallet1, pallet2) {
        return pallet1.id - pallet2.id;
    });
}
function sortByOwner(){
    allContainers.sort(function (container1, container2) {
        return container1.owner.localeCompare(container2.owner);
    });
    allPallets.sort(function (pallet1, pallet2) {
        return pallet1.owner.localeCompare(pallet2.owner);
    });
}
function sortByDimension(){
    allContainers.sort(function (container1, container2) {
        return container1.dimensions.depth - container2.dimensions.depth;
    });
    allPallets.sort(function (pallet1, pallet2) {
        return pallet1.dimensions.depth - pallet2.dimensions.depth;
    });
}
function sortByLocation(){
    allContainers.sort(function (container1, container2) {
        return container1.location.locationName.localeCompare(container2.location.locationName);
    });
    allPallets.sort(function (pallet1, pallet2) {
        return pallet1.location.locationName.localeCompare(pallet2.location.locationName);
    });
}

/*CONTAINER LOADERS*/
function loadAllContainersAndPallets(){
    const locationContainers = document.querySelector(".import-table-containers");
    const locationPallets = document.querySelector(".import-table-pallets");
    locationContainers.innerHTML = "";
    locationPallets.innerHTML = "";
    allPallets.forEach(pallet => {
        insertHTML(locationPallets, pallet);
    });
    allContainers.forEach(container => {
        insertHTML(locationContainers, container);
    });
    document.querySelectorAll(".table-container").forEach(row => {
        row.addEventListener("click", clickContainer);
    });
}

function clickContainer(e){
    // TODO nog redirecten naar andere pagina...
    console.log(e.target.closest('.table-rows'));
}

function clickAddContainerOrPallet(e){
    e.preventDefault();
    const currentSelectedScreen = document.querySelector(".selected").innerText;
    addClickEventsToPopUps();
    insertPopUpInformation(currentSelectedScreen);
    popupConfirm.classList.remove("hidden");
    backgroundPopup.classList.add("visible");

    if(currentSelectedScreen.toLowerCase() === "containers"){
        const owner = document.querySelector("#owner").value;
        const location = document.querySelector("#location").selectedOptions[0].innerHTML;
        const container = document.querySelector("#select-container").selectedOptions[0].innerHTML;
        addDetailsToPopUp(owner, location, container);
        document.querySelector("#confirm").addEventListener("click", async function (){
            await addContainerOrPallet(owner, location, container);
        });
    }
    else{
        const owner = document.querySelector("#company-owner").value;
        const location = document.querySelector("#pallet-location").selectedOptions[0].innerHTML;
        removeDetailsFromPopUp();
        document.querySelector("#confirm").addEventListener("click", async function (){
            await addContainerOrPallet(owner, location);
        });
    }
}

function addClickEventsToPopUps(){
    document.querySelector("#container-pallet-popup-confirmation .cancel").addEventListener("click", function (){
        popupConfirm.classList.add("hidden");
        backgroundPopup.classList.remove("visible");
    });
    document.querySelector("#container-succes .close").addEventListener("click", function (){
        document.querySelector("#container-succes").classList.add("hidden");
        backgroundPopup.classList.remove("visible");
    });
}

function addDetailsToPopUp(owner, location, container){
    const htmlLocationOfPopupDetails = document.querySelector(".container-details");
    htmlLocationOfPopupDetails.innerHTML = "";
    htmlLocationOfPopupDetails.insertAdjacentHTML("beforeend", htmlToAddToConfirmationPopUp(owner, location, container));
}
function removeDetailsFromPopUp(){
    const htmlLocationOfPopupDetails = document.querySelector(".container-details");
    htmlLocationOfPopupDetails.innerHTML = "";
}

async function addContainerOrPallet(owner, location, containerOrPallet){
    const latLongLocation = { "longitude": 15.5735, "latitude": -63.13982 };
    let response;
    if (containerOrPallet !== undefined){
        response = await postNewContainer(containerOrPallet);
    }
    else{
        response = await postNewPallet();
    }
    if (containerOrPallet !== undefined){
        await putNewOwnerToContainer(owner, response.id);
        if(await putNewLocationToContainer(location, response.id, latLongLocation)){
            resetFormContainerPallet();
            getCompanyOwners(await getAllContainers());
        }
    }
    else{
        await putNewOwnerToPallet(owner, response.id);
        if(await putNewLocationToPallet(location, response.id, latLongLocation)){
            resetFormContainerPallet();
            getCompanyOwners(await getAllContainers());
            loadAllContainersAndPallets();
        }
    }
    await resetCharts();
}

function resetFormContainerPallet(){
    popupConfirm.classList.add("hidden");
    document.querySelector("#container-succes").classList.remove("hidden");
    document.querySelector("#owner").value = "";
    document.querySelector("#company-owner").value = "";
}

/*HTML INSERTING FUNCTIONS*/
function insertHTML(location, container){
    location.insertAdjacentHTML('beforeend', htmlToInsert(container));
}

function htmlToInsert(container){
    if (container.dimensions.depth === 1.2){
        return ` <ul class="table-rows table-container">
                <li>#${container.id}</li>
                <li>${container.location.locationName}</li>
                <li>${container.owner}</li>
            </ul>`;
    }
    else{
        return` <ul class="table-rows table-container">
                    <li>#${container.id}</li>
                    <li>${container.location.locationName}</li>
                    <li class="${checkContainerSize(container.dimensions)}-container"></li>
                    <li>€${container.rentprice}</li>
                    <li>${container.owner}</li>
                </ul>`;
    }
}

function insertPopUpInformation(value){
    const textAreaConfirm = document.querySelector("#container-pallet-popup-confirmation .text-area span");
    const textAreaSucceed = document.querySelector("#container-succes .text-area span");
    const button = document.querySelector("#container-pallet-popup-confirmation #confirm span");

    textAreaConfirm.innerText = " " + value;
    textAreaSucceed.innerText = " " + value;
    button.innerText = " " + value;
}

function htmlToAddToConfirmationPopUp(owner, location, container){
    return `
        <ul>
            <li>${container}</li>
            <li>${owner}</li>
            <li>${location}</li>
        </ul>
    `;
}

/*EXTRA FUNCTIONS*/
function checkContainerSize(containerSize){
    if(containerSize.depth === 6){
        return "small";
    }
    else if(containerSize.depth === 9){
        return "medium";
    }
    else{
        return "large";
    }
}
