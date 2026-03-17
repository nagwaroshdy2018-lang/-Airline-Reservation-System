
package airlinereservationsystem;

public class DomesticFlight extends Flight{
    private String region;
    public DomesticFlight(String flightNumber, String departure, String destination, String departureTime, String arrivalTime, int availableSeats, String region){
        super(flightNumber,departure,destination,departureTime,arrivalTime,availableSeats);
        this.region=region;
    }

    @Override
    public void displayFlight() {
        super.displayFlight();
        System.out.println("      [Domestic - Region:  " + region + "]");
    }

    public String getRegion() {
        return region;
    }
}
