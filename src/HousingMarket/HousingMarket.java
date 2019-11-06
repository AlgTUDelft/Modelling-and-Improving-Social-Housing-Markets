package HousingMarket;

public class HousingMarket {

    private int year;
    private double freeSpace;

    public HousingMarket(int year, double freeSpace) throws FreeSpaceException {
        this.year = year;
        if (0 <= freeSpace && freeSpace <= 100) {
            this.freeSpace = freeSpace;
        }
        else { throw new FreeSpaceException("Error: Free space has to be a percentage between 0 and 100.");}
    }

    public int getYear() {
        return this.year;
    }

    public double getFreeSpace() {
        return this.freeSpace;
    }

    public class FreeSpaceException extends Exception {
        public FreeSpaceException(String errorMessage) {
            super(errorMessage);
        }
    }

}
