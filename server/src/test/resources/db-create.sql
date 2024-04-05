DROP TABLE if EXISTS containers;
CREATE TABLE containers
(
    containerID int,
    rentprice float,
    width float,
    height float,
    depth float,
    owner varchar(255),
    locationName varchar(255),
    longitude float,
    latitude float
);
DROP TABLE if EXISTS pallets;
CREATE TABLE pallets
(
    palletID int,
    width float,
    height float,
    depth float,
    owner varchar(255),
    locationName varchar(255),
    longitude float,
    latitude float
);
DROP TABLE if EXISTS containerContents;
CREATE TABLE containerContents
(
    containerID int,
    content varchar(255),
    amount int
);
DROP TABLE if EXISTS palletContents;
CREATE TABLE palletContents
(
    palletID int,
    content varchar(255),
    amount int
);
DROP TABLE if EXISTS containerComments;
CREATE TABLE containerComments
(
    containerID int,
    comment varchar(255)
);
DROP TABLE if EXISTS palletComments;
CREATE TABLE palletComments
(
    palletID int,
    comment varchar(255)
);