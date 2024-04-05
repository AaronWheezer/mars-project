"use strict";

let allContainers;
let allPallets;

document.addEventListener("DOMContentLoaded", init);

async function init() {
    const path = window.location.pathname;
    const page = path.split("/").pop();

    if (page === "company.html") {
        await loadConfig("../config.json");

        companyPageClickEvents();
    }
    else{
        await loadConfig("config.json");
        allContainers = await getAllContainers();
        allPallets = await getAllPallets();

        getCompanyOwners(allContainers);
        addNavigationClickEvents();
        centraMap();
        addClickEventsToHomePage();
        addListenersToContainerPage();
        addListenersToReportPage();
    }
}
