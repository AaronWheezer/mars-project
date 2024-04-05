function centraMap() {
  const locations = new Array();
  allContainers.forEach((container) => {
    locations.push([container.location.longitude, container.location.latitude]);
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
        src: "assets/media/icons/marker.png",
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

  const ImageLayer = new ol.layer.Image({
    source: new ol.source.ImageStatic({
      attributions: '© <a href="https://dustydepot.be">dusty depot</a>',
      url: "http://127.0.0.1:5500/src/assets/media/mars.png",
      imageExtent: extent,
      projection: projection,
    }),
  });

  const URL = "https://cartocdn-gusc.global.ssl.fastly.net/opmbuilder/api/v1/map/named/opm-mars-basemap-v0-2/all/{z}/{x}/{y}.png"
  //https://www.openplanetary.org/opm-basemaps/opm-mars-basemap-v0-2
  const map = new ol.Map({
    target: "centra-map",
    layers: [
        new ol.layer.Tile({
            source: new ol.source.OSM({
                url: URL,
            }),
        }),
        markerLayer],
    view: new ol.View({
      center: ol.proj.fromLonLat([4.34878, 50.85045]),
      zoom: 0,
    }),
  });
  const bounds = markerVectors.getExtent();
  map.getView();
}
