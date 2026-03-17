/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airlinereservationsystem;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.nio.file.*;

public class FlightManager {
    private ArrayList<Flight> flights;
    private ArrayList<Passenger>  passengers;
    public FlightManager (){
        flights=new ArrayList<>();
        passengers=new ArrayList<>();
    }

    public ArrayList<Flight> getFlights() { return flights; }
    public ArrayList<Passenger> getPassengers() { return passengers; }

    public String addDomesticFlight(String flightNumber, String departure, String destination,
                                    String departureTime, String arrivalTime, int seats, String region) {
        flights.add(new DomesticFlight(flightNumber, departure, destination, departureTime, arrivalTime, seats, region));
        return "Flight " + flightNumber + " added successfully!";
    }

    public String addInternationalFlight(String flightNumber, String departure, String destination,
                                         String departureTime, String arrivalTime, int seats,
                                         String country, boolean visaRequired) {
        flights.add(new InternationalFlight(flightNumber, departure, destination, departureTime, arrivalTime, seats, country, visaRequired));
        return "Flight " + flightNumber + " added successfully!";
    }

    public String bookSeat(String flightNumber, String name, String id) {
        Flight targetflight = findFlight(flightNumber);
        if (targetflight == null) return "Flight not found.";
        if (targetflight.getAvailableSeats() <= 0) return "No available seats on this flight.";
        for (Passenger p : passengers) {
            if (p.getPassengerID().equals(id)) return "This passenger ID already has a reservation.";
        }
        passengers.add(new Passenger(name, id, flightNumber));
        targetflight.setAvailableSeats(targetflight.getAvailableSeats() - 1);
        return "Seat booked successfully for " + name + " on flight " + flightNumber + "!";
    }

    public String cancelReservation(String id) {
        Passenger toRemove = null;
        for (Passenger p : passengers) {
            if (p.getPassengerID().equals(id)) { toRemove = p; break; }
        }
        if (toRemove == null) return "No reservation found for this passenger ID.";
        Flight flight = findFlight(toRemove.getFlightNumber());
        if (flight != null) flight.setAvailableSeats(flight.getAvailableSeats() + 1);
        String name = toRemove.getPassengerName();
        passengers.remove(toRemove);
        return "Reservation canceled successfully for passenger " + name + "!";
    }

    public void addFlight(Scanner scanner){
        System.out.println("\nAdding Flight:");
        System.out.print("Enter flight number: ");
        String flightNumber= scanner.nextLine();
        System.out.print("Enter departure location: ");
        String departure = scanner.nextLine();
        System.out.print("Enter destination: ");
        String destination= scanner.nextLine();
        System.out.print("Enter departure time (HH:MM): ");
        String departureTime = scanner.nextLine();
        System.out.print("Enter arrival time (HH:MM): ");
        String arrivalTime = scanner.nextLine();
        System.out.print("Enter available seats: ");
        int seats= Integer.parseInt(scanner.nextLine());
        System.out.print("Flight type (1 = Domestic, 2 = International): ");
        int type = Integer.parseInt(scanner.nextLine());
        if (type == 1){
            System.out.println("Enter region: ");
            String region= scanner.nextLine();
            flights.add(new DomesticFlight(flightNumber, departure,destination,departureTime,arrivalTime,seats,region));
        }else{
            System.out.println("Enter Country: ");
            String country= scanner.nextLine();
            System.out.println("Visa Required?  (true/false): ");
            boolean visa=Boolean.parseBoolean(scanner.nextLine());
            flights.add(new InternationalFlight(flightNumber,departure,destination,departureTime,arrivalTime,seats,country,visa));
        }
        System.out.println("\nFlight added successfully!");
    }
    public void viewFlights(){
        System.out.println("\nViewing Flights: ");
        if (flights.isEmpty()){ System.out.println("No flights available."); }
        int i=1;
        for (Flight f : flights){ System.out.println(i++ +"."); f.displayFlight(); }
    }
    public void bookSeat(Scanner scanner){
        System.out.println("\nBooking Seat:");
        System.out.print("Enter flight number: ");
        String flightNumber = scanner.nextLine();
        Flight targetflight=findFlight(flightNumber);
        if (targetflight==null){ System.out.println("Flight not found."); return; }
        if (targetflight.getAvailableSeats()<=0){ System.out.println("No available seats on this flight."); return; }
        System.out.print("Enter passenger name: ");
        String name = scanner.nextLine();
        System.out.print("Enter passenger ID: ");
        String id = scanner.nextLine();
        for (Passenger p : passengers){
            if (p.getPassengerID().equals(id)){ System.out.println("This passenger ID already has a reservation."); return; }
        }
        passengers.add(new Passenger(name, id, flightNumber));
        targetflight.setAvailableSeats(targetflight.getAvailableSeats()-1);
        System.out.println("\nSeat booked successfully for " + name + " on flight " + flightNumber + "!");
    }
    public void cancelReservation(Scanner scanner){
        System.out.println("\nCanceling Reservation");
        System.out.print("Enter passenger ID: ");
        String id = scanner.nextLine();
        Passenger toRemove=null;
        for (Passenger p : passengers){
            if (p.getPassengerID().equals(id)) { toRemove = p; break; }
        }
        if (toRemove==null){ System.out.println("No reservation found for this passenger ID."); return; }
        Flight flight=findFlight(toRemove.getFlightNumber());
        if (flight!= null){ flight.setAvailableSeats(flight.getAvailableSeats()+1); }
        System.out.println("\nReservation canceled successfully for passenger " + toRemove.getPassengerName() + "!");
        passengers.remove(toRemove);
    }
    private Flight findFlight(String flightNumber){
        for (Flight f : flights){
         if (f.getFlightNumber().equalsIgnoreCase(flightNumber))
             return f;
        }
        return null;
    }

    // ── File Database ────────────────────────────────────────────────────────────
    private static final String DB_DIR      = System.getProperty("user.home") + "/AirlineDB/";
    private static final String FLIGHTS_FILE    = DB_DIR + "flights.txt";
    private static final String PASSENGERS_FILE = DB_DIR + "passengers.txt";

    public void saveToFile() {
        try {
            Files.createDirectories(Paths.get(DB_DIR));
            try (PrintWriter pw = new PrintWriter(new FileWriter(FLIGHTS_FILE))) {
                for (Flight f : flights) {
                    if (f instanceof DomesticFlight) {
                        pw.println("D|" + f.getFlightNumber() + "|" + f.getDeparture() + "|" +
                            f.getDestination() + "|" + f.getDepartureTime() + "|" +
                            f.getArrivalTime() + "|" + f.getAvailableSeats() + "|" +
                            ((DomesticFlight) f).getRegion());
                    } else if (f instanceof InternationalFlight) {
                        InternationalFlight i = (InternationalFlight) f;
                        pw.println("I|" + f.getFlightNumber() + "|" + f.getDeparture() + "|" +
                            f.getDestination() + "|" + f.getDepartureTime() + "|" +
                            f.getArrivalTime() + "|" + f.getAvailableSeats() + "|" +
                            i.getCountry() + "|" + i.isVisaRequired());
                    }
                }
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(PASSENGERS_FILE))) {
                for (Passenger p : passengers) {
                    pw.println(p.getPassengerName() + "|" + p.getPassengerID() + "|" + p.getFlightNumber());
                }
            }
        } catch (IOException e) {
            System.err.println("Save failed: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        File ff = new File(FLIGHTS_FILE);
        if (ff.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(ff))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split("\\|", -1);
                    if (p[0].equals("D") && p.length == 8) {
                        flights.add(new DomesticFlight(p[1], p[2], p[3], p[4], p[5], Integer.parseInt(p[6]), p[7]));
                    } else if (p[0].equals("I") && p.length == 9) {
                        flights.add(new InternationalFlight(p[1], p[2], p[3], p[4], p[5], Integer.parseInt(p[6]), p[7], Boolean.parseBoolean(p[8])));
                    }
                }
            } catch (IOException e) {
                System.err.println("Load flights failed: " + e.getMessage());
            }
        }
        File pf = new File(PASSENGERS_FILE);
        if (pf.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(pf))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split("\\|", -1);
                    if (p.length == 3) passengers.add(new Passenger(p[0], p[1], p[2]));
                }
            } catch (IOException e) {
                System.err.println("Load passengers failed: " + e.getMessage());
            }
        }
    }

}

