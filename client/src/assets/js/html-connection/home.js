"use strict";

let owners = [];

let doughnutChart;
let barChart;

function addClickEventsToHomePage() {
  document
    .querySelector(".all-warehouses-btn")
    .addEventListener("click", showAllWarehouses);
  document
    .querySelector(".view-all")
    .addEventListener("click", showAllCompanies);
  addClickEventsToCompanies();
  generateBarCharts();
  doughnut();
}

function addClickEventsToCompanies() {
  document.querySelectorAll(".company").forEach((company) => {
    company.addEventListener("click", checkClickedCompany);
  });
}

function showAllWarehouses() {
  const gridHome = document.querySelector(".grid-home");
  const fullViewWarehouses = "full-view-warehouses";
  const text =  document.querySelector(".all-warehouses-btn");
  if (gridHome.classList.contains(fullViewWarehouses)) {
    gridHome.classList.remove(fullViewWarehouses);
    text.innerHTML = "Show all warehouses";
  } else {
    gridHome.classList.add(fullViewWarehouses);
    text.innerHTML = "Show less warehouses";
  }
}

function showAllCompanies() {
  showAllWarehouses();
  const gridHome = document.querySelector(".grid-home");

  gridHome.classList.remove("full-view-warehouses");

  const viewAllCompanies = "view-all-companies";
  if (gridHome.classList.contains(viewAllCompanies)) {
    gridHome.classList.remove(viewAllCompanies);
  } else {
    gridHome.classList.add(viewAllCompanies);
  }
}

function checkClickedCompany(e) {
    sessionStorage.setItem("company", e.target.innerHTML);

    //change location of the window
    let url = window.location.href;
    if(url.includes("mars-19")){
        window.location.href = window.location.href.concat("pages/company.html");
    }
    else{
      url = url.concat("pages/company.html");
      window.location.href = url;
    }
}

function getCompanyOwners(_containers) {
  owners = [];

  const companyLocation = document.querySelector(".company-container ul");

  //if the container object is empty, keep dummy data from html...
  // TODO change to: als leeg is, dan gwn zeggen in html da het leeg is en niets van dummy data tonen
  if (Object.keys(_containers).length !== 0) {
    companyLocation.innerHTML = "";
  }

  _containers.forEach((company) => {
    if (!owners.includes(company.owner) && company.owner !== null) {
      owners.push(company.owner);
      companyLocation.innerHTML += `<li><p class="company">${company.owner}</p></li>`;
      addClickEventsToCompanies();
    }
  });
}

function getLocationContainerAmountObject() {
  const locations = {};

  allContainers.forEach((container) => {
    if (!Object.keys(locations).includes(container.location.locationName)) {
      const key = container.location.locationName;
      locations[key] = 1;
    } else {
      locations[container.location.locationName]++;
    }
  });
  return locations;
}

async function getAmountOfContainersPerLocation(location) {
  const containers = await getContainersByLocation(location);
  return containers.length;
}

async function generateBarCharts() {
  const ctx = document.querySelector("#bar-chart").getContext("2d");
  const data = await getAllContainers();
  const amountContainersPerLocation = getLocationContainerAmountObject();
  const configuration = {
    type: "bar",
    data: {
      datasets: [
        {
          data: amountContainersPerLocation,
        },
      ],
    },
    options: {
      backgroundColor: "#9C27B0",
      plugins: {
        title: {
          display: true,
          text: "Containers per location",
          font: {
            size: 15,
          },
        },
        legend: {
          display: false,
        },
      },
      scales: {
        x: {
          display: true,
          title: {
            display: true,
            text: "wharehouse",
            font: {
              weight: "bold",
            },
          },
        },
        y: {
          display: true,
          title: {
            display: true,
            text: "#containers",
            font: {
              weight: "bold",
            },
          },
          max: data.length,
        },
      },
    },
  };
  barChart = new Chart(ctx, configuration);
}

function getStorageTypesObject(){
    const storageTypes ={
        "small" : 0,
        "medium" : 0,
        "large" : 0,
        "pallets" :0
    };

    allContainers.forEach((container) => {
        const key = checkContainerSize(container.dimensions);
        storageTypes[key]++;
    });

    storageTypes["pallets"] = allPallets.length;
    return storageTypes;
}

async function doughnut() {
  const data = getStorageTypesObject();
  const ctx = document.querySelector("#doughnut-chart").getContext("2d");
  const configuration = {
    type: "doughnut",
    data: {
      labels: Object.keys(data),
      datasets: [
        {
          data: Object.values(data),
          backgroundColor: ["#FF5722", "#FF9800", "#9C27B0", "#4CAF50"],
        },
      ],
    },
    options: {
      plugins: {
        title: {
          display: true,
          text: "storage types of all warehouses",
          font: {
            size: 24,
          },
        },
      },
    },
  };
  doughnutChart = new Chart(ctx, configuration);
}

async function resetCharts(){
  allContainers = await getAllContainers();
  allPallets = await getAllPallets();

  const dataDoughnut = getStorageTypesObject();
  doughnutChart.data.datasets[0].data = Object.values(dataDoughnut);
  doughnutChart.reset();
  doughnutChart.update();

  barChart.data.datasets[0].data = getLocationContainerAmountObject();
  barChart.reset();
  barChart.update();

  sessionStorage.clear();
}
