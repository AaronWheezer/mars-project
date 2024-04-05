"use strict";

const _apiMS = "https://project-ii.ti.howest.be/mars-15/api/";
const _companyID = "DUSTYDEPOT";
const _sizes = {"S":"small","M":"medium","L":"large"};
const _warehouseLocation = {"longitude": 13.2692,"latitude": -38.9541};

function getOrdersMS(){
    const request = new Request(_apiMS + `${_companyID}/orders`, {
        method: 'GET'
    });

    call(request, transferContainerMS, logError);
}


function transferContainerMS(response){
    response.json().then(
        orders => {
            orders.forEach(order => {
                for (let i = 0; i < order.containerAmount; i++) {
                    postNewContainer(_sizes[order.containerSize]).then(
                        c => {
                            putNewOwnerToContainer("marShip", c.id);
                            putNewLocationToContainer("Warehouse Space Station", c.id, _warehouseLocation);
                        }
                    );
                }
                updateStatusMS(order);
            });
        }
    );
}

function updateStatusMS(order){
    const body = {"status": "WAITING_FOR_DELIVERY"};
    const request = new Request(_apiMS + `${order.companyId}/orders/${order.orderId}`, {
        method: 'PATCH',
        headers: {
            'Content-type': 'application/json;'
        },
        body: JSON.stringify(body)
    });

    call(request, logJson, logError);
}
