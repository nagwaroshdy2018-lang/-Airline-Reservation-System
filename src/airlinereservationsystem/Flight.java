/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airlinereservationsystem;
public class Flight {
    private final String flightNumber;
    private String departure;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private int availableSeats;

    public Flight (String flightNumber, String departure, String destination, String departureTime, String arrivalTime, int availableSeats){
        this.flightNumber=flightNumber;
        this.departure=departure;
        this.destination=destination;
        this.departureTime=departureTime;
        this.arrivalTime=arrivalTime;
        this.availableSeats=availableSeats;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int seats) {
        this.availableSeats = seats;
    }
    public void displayFlight(){
        System.out.println(flightNumber + "-" + departure + "to" + destination + "-" + "to" + arrivalTime + "-" + availableSeats +" Seats available");
    }
}
