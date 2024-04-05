
async function addListenersToReportPage(){
    document.querySelector(".damage-report-form").addEventListener("submit", submitReportForm);
    await getAllReports();
}

function submitReportForm(e){
    e.preventDefault();
    const containerOrPallet = document.querySelector("#select-sort").selectedOptions[0].value;
    const ID = document.querySelector("#container-id-report").value;
    const description = document.querySelector("#description").value;
    const backgroundPopup = document.querySelector(".popup-background");

    if(ID !== "" && description !== ""){
        executeFormData(containerOrPallet, ID, description);
        backgroundPopup.classList.add("visible");
        document.querySelector("#added-report").classList.remove("hidden");
        addClickEventToPopUp();
    }
    else{
        document.querySelector(".errormessage").innerText = "Please, fill in all the fields.";
    }
}

function executeFormData(containerOrPallet, ID, description){
    const comment = { "comment": `${description}` };
    if (containerOrPallet === "container"){
        putCommentsToContainer(ID, comment);
    }
    else{
        putCommentsToPallet(ID, comment);
    }
}

function addClickEventToPopUp(){
    document.querySelector("#added-report .close").addEventListener("click", closeConfirmationPopUp);
}

function closeConfirmationPopUp(){
    backgroundPopup.classList.remove("visible");
    document.querySelector("#added-report").classList.add("hidden");
    resetFormData();
}

function resetFormData(){
    document.querySelector("#container-id-report").value = "";
    document.querySelector("#description").value = "";
}


async function getAllReports(){
    document.querySelector(".report-location").innerHTML = "";
    await getAllContainerReports();
    await getAllPalletReports();
}

async function getAllContainerReports(){
    const allContainers = await getAllContainers();
    allContainers.forEach(container => {
        if (Object.keys(container.comments).length !== 0){
            if (Object.keys(container.comments).length > 1 ){
                container.comments.forEach(comment => {
                    addReportToList(container, "container", comment);
                });
            }
            else{
                addReportToList(container, "container", container.comments);
            }
        }
    });
}

async function getAllPalletReports(){
    const allPallets = await getAllPallets();
    allPallets.forEach(pallet => {
        if (Object.keys(pallet.comments).length !== 0){
            if (Object.keys(pallet.comments).length > 1 ){
                pallet.comments.forEach(comment => {
                    addReportToList(pallet, "pallet", comment);
                });
            }
            else{
                addReportToList(pallet, "pallet", pallet.comments);
            }
        }
    });
}

function addReportToList(entity, containerOrPallet, comment){
    const insertLocation = document.querySelector(".report-location");
    insertLocation.innerHTML += htmlToAddReport(entity, comment, containerOrPallet);
}

function htmlToAddReport(entity, comment, containerOrPallet){
    return `<ul class="table-rows ${containerOrPallet}">
                <li>#${entity.id}</li>
                <li>${containerOrPallet}</li>
                <li class="comment">${comment}</li>
                <li>${entity.owner}</li>
            </ul>`;
}
