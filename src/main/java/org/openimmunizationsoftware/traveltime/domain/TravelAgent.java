package org.openimmunizationsoftware.traveltime.domain;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.List;

import org.openimmunizationsoftware.traveltime.logic.TripBuilder;

public class TravelAgent implements Comparable<TravelAgent> {

  private static final String GOOGLE_API_KEY = "AIzaSyCA9R-LNskrJvC1oV7uiXRMvnWtWBn_qk4";

  private List<Trip> tripList;
  private int totalTravelTime = 0;
  private DataStore dataStore = null;
  private String generation = "1";
  private String signature = "";
  private String name = "";

  public String getName() {
    return name;
  }

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

  public int getTotalTravelTime() {
    return totalTravelTime;
  }

  public List<Trip> getTripList() {
    return tripList;
  }

  public boolean areSignaturesEqual(TravelAgent otherAgent) {
    return otherAgent.signature.equals(signature);
  }

  public TravelAgent(DataStore dataStore, String name) {
    this.dataStore = dataStore;
    this.name = name;
    tripList = TripBuilder.makeTrip(dataStore);
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
      }
      {
        signature += "-" + tripStop.getDestination().getShortName();
      }
    }
  }

  public TravelAgent(TravelAgent mom, TravelAgent dad, String name) {
    this.dataStore = mom.dataStore;
    this.generation = mom.getGeneration() + "." + dataStore.getCurrentGeneration();
    this.name = mom.name + "." + name;
    tripList = TripBuilder.makeNewTripList(mom.getTripList(), dad.getTripList(), dataStore, generation);
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
    int tripId = 0;
    for (Trip trip : tripList) {
      tripId++;
      if (trip.getTripStopList().size() > 0) {
        {
          TripStop tripStop = trip.getTripStopList().get(0);
          out.println("<h3>" + tripStop.getDestination() + "</h3>");
        }
        out.println("<table>");
        out.println("  <tr>");
        out.println("    <th>Day</th>");
        out.println("    <th>Time</th>");
        out.println("    <th>Site</th>");
        out.println("    <th>City</th>");
        out.println("    <th>Travel Time</th>");
        out.println("  </tr>");
        for (TripStop tripStop : trip.getTripStopList()) {
          out.println("  <tr>");
          out.println("    <td>" + tripStop.getDay() + "</td>");
          out.println("    <td>" + tripStop.getHourDisplay() + "</td>");
          out.println("    <td>" + tripStop.getDestination().getShortName() + "</td>");
          out.println("    <td>" + tripStop.getDestination().getCityName() + "</td>");
          out.println("    <td>" + tripStop.getTravelTime().getTime() + "</td>");
          out.println("  </tr>");
        }
        out.println("  <tr>");
        out.println("    <td colspan=\"3\"></td>");
        out.println("    <td>Home</td>");
        out.println("    <td>7</td>");
        out.println("  </tr>");
        out.println("  <tr>");
        out.println("    <td colspan=\"3\"></td>");
        out.println("    <td>Total Travel Time</td>");
        out.println("    <td>" + trip.getTotalTime() + "</td>");
        out.println("  </tr>");
        out.println("  <tr>");
        out.println("    <td colspan=\"3\"></td>");
        out.println("    <td>Travel Score</td>");
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        out.println("    <td>" + numberFormat.format(trip.getTravelScore()) + "</td>");
        out.println("  </tr>");
        out.println("  <tr>");
        out.println("    <td colspan=\"3\"></td>");
        out.println("    <td>Generation</td>");
        out.println("    <td>" + trip.getGeneration() + "</td>");
        out.println("  </tr>");
        out.println("</table>");
        if (false) {
          out.println("<div id=\"map" + tripId
              + "\" style=\"height=100px; width=100px; border-size=\"1\" border-style: solid; border-color: black;\">Hello</div>Goodbye");
          out.println("<script>");
          out.println("function initMap" + tripId + "() {");
          out.println("  var myLatLng = {lat: 32.72, lng: -65.370};");

          out.println("  // Create a map object and specify the DOM element for display.");
          out.println("  var map = new google.maps.Map(document.getElementById('map" + tripId + "'), {");
          out.println("    center: myLatLng,");
          out.println("    scrollwheel: false,");
          out.println("    zoom: 3");
          out.println("  });");

          out.println("  // Create a marker and set its position.");
          out.println("  var marker = new google.maps.Marker({");
          out.println("    map: map,");
          out.println("    position: myLatLng,");
          out.println("    title: 'Hello World!'");
          out.println("  });");
          out.println("}");
          out.println("</script>");
          try {
            out.println("<script async defer src=\"https://maps.googleapis.com/maps/api/js?key="
                + URLEncoder.encode(GOOGLE_API_KEY, "UTF-8") + "&callback=initMap" + tripId
                + "\" type=\"text/javascript\"></script>");
          } catch (UnsupportedEncodingException use) {
            // do nothing
          }
        }

      }
    }
  }
}
