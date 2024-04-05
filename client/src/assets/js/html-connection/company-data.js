"use strict";

let containersByOwner;
let checkFullView = false;
let warehouse;

let popupRemove;
let popupSucceed;
let backgroundPopup;

function companyPageClickEvents(e){
    showCompanyData();
    customerMap();
    generateBarChartsCustomer();
    popupRemove = document.querySelector("#container-remove-confirmation");
    popupSucceed = document.querySelector("#container-remove-success");
    backgroundPopup = document.querySelector(".popup-background");

    warehouse = document.querySelector("#warehouses");
    warehouse.addEventListener("change", function(){
        return (checkFullView ? showFullWarehouseData() : showWarehouseData());
    });
    document.querySelector(".warehouse-content .view-all").addEventListener("click", hideAllElements);
    document.querySelector(".all-warehouse-content .close-all").addEventListener("click", function (){
        document.querySelector(".warehouse-content").classList.remove("hidden");
        document.querySelector(".all-warehouse-content").classList.add("hidden");
        checkFullView = false;
        showCompanyData();
    });
}

function hideAllElements(){
    document.querySelector(".warehouse-content").classList.add("hidden");
    document.querySelector(".customer-chart").classList.add("hidden");
    document.querySelector(".customer-map").classList.add("hidden");
    document.querySelector(".all-warehouse-content").classList.remove("hidden");
    checkFullView = true;
    showCompanyData();
}

async function showCompanyData(){
    const location = document.querySelector(".customer .customer-data h3 span");
    const title = document.querySelector("title");
    const company = sessionStorage.getItem("company");
    title.innerText = company;
    location.innerText = company;
    containersByOwner = await getContainersByOwner(company);
    return (checkFullView ? showFullWarehouseData() : showWarehouseData());
}

function addClickEventsToContainerCards(){
    document.querySelectorAll(".container-overview section").forEach(card => {
        card.addEventListener("click", openContainer);
    });

    document.querySelectorAll(".remove-container").forEach( btn => {
        btn.addEventListener("click", removeContainer);
    });
    document.querySelectorAll(".update-container").forEach( btn => {
        btn.addEventListener("click", updateContainer);
    });
}

async function openContainer(e){
    const container = e.target.closest("section");
    const containerID = container.querySelector("h5").innerText;
    console.log(containerID);
    console.log(await getContainerByContainerID(containerID));


}

/*REMOVE CONTAINER*/
function removeContainer(e){
    e.preventDefault();
    const selectedContainer = e.target.closest('.container-overview');
    const containerID = selectedContainer.querySelector("h5").innerText;
    sessionStorage.setItem("containerID", containerID);
    showRemovePopUp();
}

function showRemovePopUp(){
    popupRemove.classList.remove("hidden");
    backgroundPopup.classList.add("visible");
    if(!popupRemove.classList.contains("hidden")){
        document.querySelector(".cancel").addEventListener("click", closeRemovePopup);
        document.querySelector("#confirm").addEventListener("click", removeCurrentContainer);
    }
}

function closeRemovePopup(){
    popupRemove.classList.add("hidden");
    backgroundPopup.classList.remove("visible");
}

async function removeCurrentContainer(){
    const containerID = sessionStorage.getItem("containerID");
    closeRemovePopup();
    if(await deleteContainer(containerID)){
        backgroundPopup.classList.add("visible");
        popupSucceed.classList.remove("hidden");
        if(!popupSucceed.classList.contains("hidden")){
            document.querySelector("#container-remove-success .close").addEventListener("click", closeRemoveSucceedPopup);
            await showCompanyData();
        }
    }
}

function closeRemoveSucceedPopup(){
    backgroundPopup.classList.remove("visible");
    popupSucceed.classList.add("hidden");
}
/*END OF REMOVE CONTAINER*/


/*UPDATE CONTAINER*/
function updateContainer(e){
    e.preventDefault();
    const selectedContainer = e.target.closest('.container-overview');
    const containerID = selectedContainer.querySelector("h5").innerText;
    sessionStorage.setItem("containerID", containerID);
    showUpdatePopUp();
}

function showUpdatePopUp(){
    const popupUpdate = document.querySelector("#container-update-form");
    popupUpdate.classList.remove("hidden");
    backgroundPopup.classList.add("visible");

    if(!popupUpdate.classList.contains("hidden")){
        document.querySelector(".cancel-update").addEventListener("click", closeUpdatePopup);
        document.querySelector("#confirm-update").addEventListener("click", updateCurrentContainer);
    }
}

function closeUpdatePopup(){
    document.querySelector("#container-update-form").classList.add("hidden");
    backgroundPopup.classList.remove("visible");

    document.querySelector("#new-location").value = "";
    document.querySelector("#new-lat").value = "";
    document.querySelector("#new-long").value = "";
}

async function updateCurrentContainer(e){
    e.preventDefault();
    const popupUpdateSucceed = document.querySelector("#container-update-success");
    const containerID = sessionStorage.getItem("containerID");
    const newLocation = document.querySelector("#new-location").value;
    const lat = document.querySelector("#new-lat").value;
    const long = document.querySelector("#new-long").value;
    const coordinates = {"longitude": parseFloat(long), "latitude": parseFloat(lat)};

    if(newLocation === "" || lat === "" || long === ""){
        document.querySelector("#container-update-form .errormessage").innerHTML = "You need to fill in all the fields";
    }
    else{
        closeUpdatePopup();
        if(await putNewLocationToContainer(newLocation, containerID, coordinates)){
            backgroundPopup.classList.add("visible");
            popupUpdateSucceed.classList.remove("hidden");
            if(!popupUpdateSucceed.classList.contains("hidden")){
                document.querySelector("#container-update-success .close").addEventListener("click", closeUpdateSucceedPopup);
                await showCompanyData();
            }
        }
    }
}

function closeUpdateSucceedPopup(){
    backgroundPopup.classList.remove("visible");
    document.querySelector("#container-update-success").classList.add("hidden");

}
/*END OF UPDATE CONTAINER*/


//TRY TO CLEAN UP THIS FUNCTION BY COMBINING IT WITH showWarehouseData();
function showFullWarehouseData(){
    const location = document.querySelector(".all-warehouse-content .warehouse-container-location");
    document.querySelector(".all-warehouse-content h4").innerHTML = warehouse.selectedOptions[0].innerHTML;
    location.innerHTML = "";
    if (!checkOnEmptyContainer()){
        containersByOwner.forEach(container => {
            if(container.location.locationName === warehouse.selectedOptions[0].innerHTML){
                injectViewableHTML(container, location);
            }
        });
        addClickEventsToContainerCards();
    }
    else{
        location.innerHTML = `<p>There are no containers on this location</p>`;
    }
}

function showWarehouseData(){
    const location = document.querySelector(".warehouse-content .warehouse-container-location");
    document.querySelector("h4").innerHTML = warehouse.selectedOptions[0].innerHTML;
    location.innerHTML = "";
    let counter = 0;
    if (!checkOnEmptyContainer()){
        containersByOwner.forEach(container => {
            if(container.location.locationName === warehouse.selectedOptions[0].innerHTML && counter !== 2){
                injectViewableHTML(container, location);
                counter += 1;
            }
        });
        addClickEventsToContainerCards();
    }
    else{
        location.innerHTML = `<p>There are no containers on this location</p>`;
    }
}

function checkOnEmptyContainer(){
    return Object.keys(containersByOwner).length === 0;
}

function injectViewableHTML(container, location){
    location.innerHTML += htmlToAddContainerOverview(container);
}

function htmlToAddContainerOverview(container){
    return `<div class="container-overview">
                <section>
                    <h5>${container.id}</h5>
                    <div>
                        <p>Dimensions: </p>
                        <p>${container.dimensions.width} - ${container.dimensions.height} - ${container.dimensions.depth}</p>                  
                    </div>
                    <div>
                        <p>Location: </p>
                        <ul>
                            <li>LAT: <span>${container.location.latitude}</span></li>
                            <li>LON: <span>${container.location.longitude}</span></li>
                        </ul>
                    </div>
                    <div>
                        <p>Rent Price:</p>
                        <p>€ ${container.rentprice}</p>
                    </div>
                </section>
                ${addButtonsToContainerOverview()}
            </div>`;
}

function addButtonsToContainerOverview(){
    return `<div>
                <a class="remove-container" href="#">Remove</a>
                <a class="update-container" href="#">Update</a>
            </div>`;
}
