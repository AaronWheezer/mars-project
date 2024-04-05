"use strict";
//customer-map

async function customerMap() {
  const locations = new Array();
  let allContainers = await getAllContainers();
  let name = document.querySelector("h3 span").innerText;
  console.log(name);
  allContainers.forEach((container) => {
    if (name == container.owner) {
      locations.push([
        container.location.longitude,
        container.location.latitude,
      ]);
    }
  });

  const markers = locations.map((location) => {
    return new ol.Feature({
      geometry: new ol.geom.Point(
        ol.proj.fromLonLat([location[0], location[1]])
      ),
    });
  });
  const markerVectors = new ol.source.Vector({
    features: markers,
  });
  const markerLayer = new ol.layer.Vector({
    source: markerVectors,
    style: new ol.style.Style({
      image: new ol.style.Icon({
        src: "../assets/media/icons/marker.png",
        anchor: [0.5, 1],
        scale: 0.1,
      }),
    }),
  });
  const extent = [0, 0, 1024, 968];
  const projection = new ol.proj.Projection({
    code: "xkcd-image",
    units: "pixels",
    extent: extent,
  });

  const URL =
    "https://cartocdn-gusc.global.ssl.fastly.net/opmbuilder/api/v1/map/named/opm-mars-basemap-v0-2/all/{z}/{x}/{y}.png";
  //https://www.openplanetary.org/opm-basemaps/opm-mars-basemap-v0-2
  const map = new ol.Map({
    target: "customer-map",
    layers: [
      new ol.layer.Tile({
        source: new ol.source.OSM({
          url: URL,
        }),
      }),
      markerLayer,
    ],
    view: new ol.View({
      center: ol.proj.fromLonLat([4.34878, 50.85045]),
      zoom: 0,
    }),
  });
  const bounds = markerVectors.getExtent();
  map.getView();
}

async function getLocationContainerAmountObject(owner, containers) {
  const locations = {};
  let allContainers = await getAllContainers();
  allContainers.forEach((container) => {
    if (container.owner == owner) {
      console.log("test");
      if (!Object.keys(locations).includes(container.location.locationName)) {
        const key = container.location.locationName;
        locations[key] = 1;
      } else {
        locations[container.location.locationName]++;
      }
    }
  });

  return locations;
}

async function generateBarChartsCustomer() {
  const ctx = document.querySelector("#bar-chart-company").getContext("2d");
  const owner = document.querySelector("h3 span").innerText;
  const data = await getContainersByOwner(owner);
  const amountContainersPerLocation = await getLocationContainerAmountObject(
    owner,
    data
  );
  console.log(amountContainersPerLocation);
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
      backgroundColor: "#f18f01",
      plugins: {
        title: {
          display: true,
          text: "Content per location",
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
          max: data.length + 2,
        },
      },
    },
  };
  new Chart(ctx, configuration);
}
