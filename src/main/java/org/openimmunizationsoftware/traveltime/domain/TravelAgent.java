package org.openimmunizationsoftware.traveltime.domain;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.List;

public class TravelAgent implements Comparable<TravelAgent> {

  private static final String GOOGLE_API_KEY = "AIzaSyCA9R-LNskrJvC1oV7uiXRMvnWtWBn_qk4";

  private List<Trip> tripList;
  private float totalTravelTime = 0;
  private DataStore dataStore = null;
  private String generation = "1";
  private String signature = "";

  public String getSignature() {
    return signature;
  }

  @Override
  public int compareTo(TravelAgent ta) {
    if (this.totalTravelTime < ta.totalTravelTime) {
      return -1;
    } else if (this.totalTravelTime > ta.totalTravelTime) {
      return 1;
    }
    int c = generation.compareTo(ta.generation);
    if (c == 0) {
      c = signature.compareTo(ta.signature);
    }
    return c;
  }

  public String getGeneration() {
    return generation;
  }

  public void setGeneration(String generation) {
    this.generation = generation;
  }

  public float getTotalTravelTime() {
    return totalTravelTime;
  }

  public List<Trip> getTripList() {
    return tripList;
  }

  public boolean areSignaturesEqual(TravelAgent otherAgent) {
    return otherAgent.signature.equals(signature);
  }

  public TravelAgent(DataStore dataStore, List<Destination> destinationList) {
    this.dataStore = dataStore;
    tripList = dataStore.getTripBuilder().makeTrip(dataStore, destinationList);
    calculateTotalTime();
  }

  public TravelAgent(DataStore dataStore) {
    this.dataStore = dataStore;
    tripList = dataStore.getTripBuilder().makeTrip(dataStore);
    calculateTotalTime();
  }

  public TravelAgent(TravelAgent travelAgent) {
    this.dataStore = travelAgent.dataStore;
    tripList = dataStore.getTripBuilder().clone(travelAgent);
    calculateTotalTime();
  }

  private void addToSignature(Trip trip) {
    if (signature.length() > 0) {
      signature += " ";
    }
    boolean first = true;
    for (TripStop tripStop : trip.getTripStopList()) {
      if (first) {
        signature += tripStop.getDestination().getShortName();
      } else {
        signature += "-" + tripStop.getDestination().getShortName();
      }
      first = false;
    }
  }

  public TravelAgent(TravelAgent mom, TravelAgent dad, String name) {
    this.dataStore = mom.dataStore;
    this.generation = mom.getGeneration() + "." + dataStore.getCurrentGeneration();
    // this.name = mom.name + "." + name;
    tripList = dataStore.getTripBuilder().makeNewTripList(mom.getTripList(), dad.getTripList(), dataStore, generation);
    calculateTotalTime();
  }

  private void calculateTotalTime() {
    for (Trip trip : tripList) {
      addToSignature(trip);
      totalTravelTime += trip.getTotalTime();
    }
  }

  public void printSchedule(PrintWriter out) {
    out.println("<p>Total Travel Time = " + totalTravelTime + "<br/>Average Travel Time: "
        + (totalTravelTime / dataStore.getDestinationMap().size()) + "</p>");
    out.println("<table>");
    out.println("  <tr>");
    out.println("    <th>Week</th>");
    out.println("    <th>Day</th>");
    out.println("    <th>Time</th>");
    out.println("    <th>Site</th>");
    out.println("    <th>City</th>");
    out.println("    <th>Travel Time</th>");
    out.println("    <th>Description</th>");
    out.println("  </tr>");
    int tripId = 0;
    for (Trip trip : tripList) {
      tripId++;
      if (trip.getTripStopList().size() > 0) {
        for (TripStop tripStop : trip.getTripStopList()) {
          out.println("  <tr>");
          out.println("    <td>" + tripId + "</td>");
          out.println("    <td>" + tripStop.getDay() + "</td>");
          out.println("    <td>" + tripStop.getHourDisplay() + "</td>");
          out.println("    <td>" + tripStop.getDestination().getShortName() + "</td>");
          out.println("    <td>" + tripStop.getDestination().getCityName() + "</td>");
          if (tripStop.getTravelTime() == null) {
            out.println("    <td>-</td>");
          } else {
            out.println("    <td>" + tripStop.getTravelTime().getTime() + "</td>");
          }
          out.println("    <td>" + tripStop.getDescription() + "</td>");
          out.println("  </tr>");
        }
      }
    }
    out.println("</table>");
  }
}
