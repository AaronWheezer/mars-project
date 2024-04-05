package be.howest.ti.mars.logic.domain;

public class RentalControl {
    public Container createContainer(String size){
        switch (size){
            case "large":
                return createLargeContainer();
            case "medium":
                return  createMediumContainer();
            case "small":
                return createSmallContainer();
            default:
                throw new IllegalArgumentException("This size doesn't exist");
        }

    }
    public Container createLargeContainer(){return new Container(126, 4, 2.5, 12, createStandardLocation());}

    public Container createMediumContainer(){return new Container(112, 2, 2.5, 9, createStandardLocation());}

    public Container createSmallContainer(){return new Container(73, 2, 2.5, 6, createStandardLocation());}

    public Pallet createPalletUniversalSize(){return new Pallet(-1,0.8, 0.14, 1.2, createStandardLocation());}

    public Location createStandardLocation(){return new Location("Dusty Depot");}

}
