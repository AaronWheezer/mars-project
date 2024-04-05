"use strict";

function addNavigationClickEvents(){
    document.querySelectorAll(".navigation ul li").forEach( e => {
        e.addEventListener("click", checkCurrentScreen);
    });
}

function checkCurrentScreen(e){
    document.querySelector(".selected").classList.remove('selected');
    e.target.classList.add("selected");
    showRightScreen(e);
}

function showRightScreen(e){
    const clickedID = e.target.id;

    document.querySelector('.previous-selected').classList.add('hidden');
    document.querySelector('.previous-selected').classList.remove('previous-selected');

    document.querySelector(`.${clickedID}`).classList.remove("hidden");
    document.querySelector(`.${clickedID}`).classList.add("previous-selected");

    if (clickedID === "qr-code") {
        scanInit();
    }
    else if(localStream){
        stop();
    }
}
