"use strict";

let barcodeDetector;
let localStream;
let interval;


function scanInit(){
    const camera = document.querySelector(".camera");
    camera.addEventListener("loadeddata", startDecoding);
    loadDevices();
    initBarcodeDetector();
}

function loadDevices(){
    const constraints = {video: true, audio: false};
    navigator.mediaDevices.getUserMedia(constraints).then(stream => {
        localStream = stream;
        navigator.mediaDevices.enumerateDevices().then( (devices) => {
                let cameraDevices = [];
                devices.forEach(device => {
                    if (device.kind === 'videoinput'){
                        cameraDevices.push(device);
                    }
                });
                if (cameraDevices){
                    play(cameraDevices[0].deviceId);
                }
            }
        );
    });
}

function play(deviceId) {
    stop();
    let constraints;

    if (!!deviceId) {
        constraints = {video: {deviceId: deviceId}, audio: false};
    }

    navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
        localStream = stream;
        const camera = document.querySelector(".camera");
        camera.srcObject = stream;
    }).catch((err) => {
        console.error('getUserMediaError', err, err.stack);
    });
}

function stop(){
    clearInterval(interval);
    try{
        localStream.getTracks().forEach(track => track.stop());
    } catch (e){
        console.error(e);
    }
}

async function initBarcodeDetector() {
    let barcodeDetectorUsable = false;
    if ('BarcodeDetector' in window) {
        const formats = await window.BarcodeDetector.getSupportedFormats();
        if (formats.length > 0) {
            barcodeDetectorUsable = true;
        }
    }

    if (barcodeDetectorUsable === false){
        Dynamsoft.DBR.BarcodeReader.license = 'DLS2eyJoYW5kc2hha2VDb2RlIjoiMTAxNTQ1NTYzLVRYbFhaV0pRY205cVgyUmljZyIsIm9yZ2FuaXphdGlvbklEIjoiMTAxNTQ1NTYzIiwiY2hlY2tDb2RlIjotMTQyNDI0MzkxNX0=';
        const reader = await BarcodeDetectorPolyfill.init();
        console.log(reader);
        window.BarcodeDetector = BarcodeDetectorPolyfill;
    }

    barcodeDetector = new window.BarcodeDetector();

}

function startDecoding(){
    clearInterval(interval);
    interval = setInterval(decode, 100);
}

async function decode(){
    const video = document.querySelector(".camera");
    const barcodes = await barcodeDetector.detect(video);

        if(barcodes.length > 0) {
            await clearInterval(interval);
            checkValue(barcodes[0].rawValue);
        }
}

async function checkValue(value) {
    if (parseInt(value) && value.length === 8) {
        let result = await checkContainerOrPallet(value);
        if (result) {
            showScannerPopup(result);
        } else {
            showFailurePopup("Container or Pallet not found");
        }
    }else{
        showFailurePopup("Barcode not valid");
    }
}

async function checkContainerOrPallet(ID){
    let pallet, container;
    await getPalletByPalletID(ID).then(palletRes => {
       pallet = palletRes;
    });
    await getContainerByContainerID(ID).then(containerRes =>{
        container = containerRes;
    });
    return pallet || container;
}

function showScannerPopup(container){
    const template = document.querySelector("#scanner-popup").content.firstElementChild.cloneNode(true);
    template.querySelector("#scanned-vessel-id").innerText = `ID: ${container.id}`;
    template.querySelector("#scanned-vessel-owner").innerText = `Owner: ${container.owner}`;

    template.querySelector("#scanned-vessel-comment").innerHTML = `<p><b>Comment(s):</b></p>`;
    container.comments.forEach( comment => {
        template.querySelector("#scanned-vessel-content").innerHTML += `<p>${comment}</p>`;
    });

    template.querySelector("#scanned-vessel-content").innerHTML += `<p><b>Contents:</b></p>`;
    for (const [key, value] of Object.entries(container.contents)) {
        template.querySelector("#scanned-vessel-content").innerHTML += `<p>${key}, ${value}</p>`;
    }

    document.querySelector(".qr-code").insertAdjacentHTML('beforeend', template.outerHTML);
    document.querySelector(".qr-code .cancel").addEventListener("click", hidePopup);
    document.querySelector(".popup-background").classList.add("visible");
}

function showFailurePopup(message){
    const template = document.querySelector("#scanner-popup").content.firstElementChild.cloneNode(true);
    template.querySelector("#scanned-vessel-content").innerText = message;

    document.querySelector(".qr-code").insertAdjacentHTML('beforeend', template.outerHTML);
    document.querySelector(".qr-code .cancel").addEventListener("click", hidePopup);
    document.querySelector(".popup-background").classList.add("visible");
}


function hidePopup(){
    document.querySelector(".qr-code .pop-up").remove();
    document.querySelector(".popup-background").classList.remove("visible");
    loadDevices();
    initBarcodeDetector();
}
