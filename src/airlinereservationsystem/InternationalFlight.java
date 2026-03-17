
package airlinereservationsystem;

public class InternationalFlight extends Flight {
    private  String country;
    private boolean visaRequired;
    public InternationalFlight(String flightNumber, String departure, String destination, String departureTime, String arrivalTime, int availableSeats, String country, boolean visaRequired){
        super(flightNumber,departure,destination,departureTime,arrivalTime,availableSeats);
        this.country=country;
        this.visaRequired=visaRequired;
    }

    public String getCountry() {
        return country;
    }

    public boolean isVisaRequired() {
        return visaRequired;
    }

    @Override
    public void displayFlight() {
        super.displayFlight();
        System.out.println("   [International - Country: " + country + " | Visa Required: " + (visaRequired ? "Yes" : "No") + "]");
    }
}
