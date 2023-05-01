"use strict";

let marsMap;
const MAP_URL="https://cartocdn-gusc.global.ssl.fastly.net/opmbuilder/api/v1/map/named/opm-mars-basemap-v0-2/all/{z}/{x}/{y}.png";
//source: https://www.openplanetary.org/opm-basemaps/opm-mars-basemap-v0-2

const officeLocation = [68, -25];

function loadMap() {
    marsMap = new ol.Map({
        target: 'marsMap',
        layers: [
            new ol.layer.Tile({
                source: new ol.source.XYZ({
                    url: MAP_URL
                })
            })
        ],
        interactions: getInteractions(),
        control: [],
        view: new ol.View({
            center: ol.proj.fromLonLat([0, 10]),
            zoom: 2
        })
    });

    marsMap.addOverlay(createOfficeMarker());

    geolocate();
}

function getInteractions() {
    return ol.interaction.defaults.defaults(
        {
            mouseWheelZoom: false,
            zoomInClassName: false,
            zoomOutClassName: false,
            dragPan: false
        }
    );
}

function geolocate() {
    navigator.geolocation.getCurrentPosition(addMarker, () => {
        displayPopupAlert("ERROR", "You need to share your location to receive our geolocation.", "Ok");
    });
}

function addMarker(currentPosition) {
    const long = currentPosition.coords.longitude;
    const lat = currentPosition.coords.latitude;

    const location = ol.proj.fromLonLat([long, lat]);

    marsMap.addOverlay(createMarker(location, "user"));
    marsMap.addOverlay(createOfficeMarker());
    drawLine(location);
}

function createMarker(position, iconElement) {
    return new ol.Overlay({
        position: position,
        element: document.getElementById(iconElement),
        positioning: "bottom-center"
    });
}

function createOfficeMarker() {
    const location = ol.proj.fromLonLat(officeLocation);
    return createMarker(location, "building");
}

function drawLine(userLocation) {
    const lonlat = userLocation;
    const location2 = ol.proj.fromLonLat(officeLocation);

    const linie2style = [
        // linestring
        new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: '#342E37',
                width: 5
            })
        })
    ];

    const linie2 = new ol.layer.Vector({
        source: new ol.source.Vector({
            features: [new ol.Feature({
                geometry: new ol.geom.LineString([lonlat, location2]),
                name: 'Line',
            })]
        })
    });

    linie2.setStyle(linie2style);
    marsMap.addLayer(linie2);
}
