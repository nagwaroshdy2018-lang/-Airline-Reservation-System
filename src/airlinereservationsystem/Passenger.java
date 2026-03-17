/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airlinereservationsystem;
public class Passenger {
    private String passengerName;
    private String passengerID;
    private String flightNumber;
    public Passenger(String passengerName, String passengerID, String flightNumber){
        this.passengerName=passengerName;
        this.passengerID=passengerID;
        this.flightNumber=flightNumber;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getPassengerID() {
        return passengerID;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getFlightNumber() {
        return flightNumber;
    }
    public void displayPassenger(){
        System.out.println("Passenger: " + passengerName + " | ID: " + passengerID +
                " | Flight: " + flightNumber);
    }
}
