package org.openimmunizationsoftware.traveltime.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.openimmunizationsoftware.traveltime.domain.DataStore;
import org.openimmunizationsoftware.traveltime.domain.Destination;
import org.openimmunizationsoftware.traveltime.domain.TravelAgent;
import org.openimmunizationsoftware.traveltime.domain.TravelTime;
import org.openimmunizationsoftware.traveltime.domain.Trip;
import org.openimmunizationsoftware.traveltime.domain.TripStop;

public class TripBuilderContinuous implements TripBuilderInterface {
  public List<Trip> makeTrip(DataStore dataStore) {
    List<Trip> tripList = new ArrayList<Trip>();
    Random random = new Random();
    List<Destination> destinationsNotVisitedList = new ArrayList<Destination>(dataStore.getDestinationMap().values());
    setupTrips(tripList, random, destinationsNotVisitedList, "1");
    return tripList;
  }

  private static List<Trip> setupTrips(List<Trip> tripList, Random random, List<Destination> destinationsNotVisitedList,
      String generation) {
    Trip trip = null;
    TripStop tsp = null;
    while (destinationsNotVisitedList.size() > 0) {
      Destination d2 = getNextDestinationNotVisited(random, destinationsNotVisitedList);
      if (trip == null) {
        trip = createTrip(tripList, generation);
        tsp = new TripStop();
        tsp.setDestination(d2);
        TravelTime tt = new TravelTime();
        tt.setTime(0);
        tsp.setDay("Tuesday");
        tsp.setHour(9);
        tsp.setTravelTime(tt);
        tsp.setDescription("Leave home Monday and arrive at " + tsp.getDestination().getCityName() + " by "
            + tsp.getHourDisplay() + ".");
      } else {
        TripStop tspPrev = tsp;
        tsp = new TripStop();
        tsp.setDestination(d2);
        tsp.setDay(tspPrev.getDay());
        tsp.setHour(tspPrev.getHour());
        tsp.addTime(5);
        String description = "Leave " + tspPrev.getDestination().getCityName() + " by " + tsp.getHourDisplay() + ". ";
        TravelTime tt = tspPrev.getDestination().getTravelTimeMap().get(d2);
        if (tt == null) {
          tt = new TravelTime();
          tt.setDestination1(tspPrev.getDestination());
          tt.setDestination2(d2);
          tt.setTime(8);
          description += "Take travel day. ";
        } else {
          description += "Expect " + tt.getTime() + " hours of travel. ";
        }
        tsp.setTravelTime(tt);
        tsp.addTime(tt.getTime());
        tsp.setToNextWorkingDay();
        description += "Arrive in " + tsp.getDestination().getCityName() + " and meet with "
            + tsp.getDestination().getShortName() + " " + tsp.getHourDisplay() + ".";
        tsp.setDescription(description);
        if (tsp.weekAfter(tspPrev)) {
          trip = createTrip(tripList, generation);
        }
        trip.addTotalTime(tsp.getTravelTime().getTime());
      }
      trip.add(tsp);

    }
    TravelTime tt = new TravelTime();
    tt.setDestination1(trip.getTripStopList().get(0).getDestination());
    tt.setDestination2(tsp.getDestination());

    trip.addTotalTime(tt.getTime());
    //trip.addTotalTime(7); // Time to get home
    return tripList;
  }

  private static Trip createTrip(List<Trip> tripList, String generation) {
    Trip trip;
    trip = new Trip();
    trip.setGeneration(generation);
    tripList.add(trip);
    return trip;
  }

  private static Destination getNextDestinationNotVisited(Random random, List<Destination> destinationsNotVisitedList) {
    int position = random.nextInt(destinationsNotVisitedList.size());
    Destination d1 = destinationsNotVisitedList.get(position);
    destinationsNotVisitedList.remove(position);
    return d1;
  }

  public List<Trip> makeNewTripList(List<Trip> tripList1, List<Trip> tripList2, DataStore dataStore,
      String generation) {
    List<Destination> destinationList1 = new ArrayList<Destination>();
    for (Trip trip : tripList1) {
      for (TripStop tripStop : trip.getTripStopList()) {
        destinationList1.add(tripStop.getDestination());
      }
    }
    List<Destination> destinationList2 = new ArrayList<Destination>();
    for (Trip trip : tripList2) {
      for (TripStop tripStop : trip.getTripStopList()) {
        destinationList2.add(tripStop.getDestination());
      }
    }

    Random random = new Random();

    List<Destination> destinationsVisitedList = new ArrayList<Destination>();

    List<Trip> tripList = new ArrayList<Trip>();

    Trip trip = null;
    TripStop tsp = null;
    boolean useDestination1 = random.nextBoolean();
    int cutSize = random.nextInt(10);
    int currentPosition = 0;
    List<Destination> destinationList = useDestination1 ? destinationList1 : destinationList2;
    while (destinationsVisitedList.size() < destinationList1.size()) {
      Destination d2 = destinationList.get(currentPosition);
      destinationsVisitedList.add(d2);

      if (trip == null) {
        trip = createTrip(tripList, generation);
        tsp = new TripStop();
        tsp.setDestination(d2);
        TravelTime tt = new TravelTime();
        tt.setTime(8);
        tsp.setDay("Tuesday");
        tsp.setHour(9);
        tsp.setTravelTime(tt);
        tsp.setDescription("Leave home Monday and arrive at " + tsp.getDestination().getCityName() + " by "
            + tsp.getHourDisplay() + ".");
      } else {
        TripStop tspPrev = tsp;
        tsp = new TripStop();
        tsp.setDestination(d2);
        tsp.setDay(tspPrev.getDay());
        tsp.setHour(tspPrev.getHour());
        tsp.addTime(5);
        String description = "Leave " + tspPrev.getDestination().getCityName() + " by " + tsp.getHourDisplay() + ". ";
        TravelTime tt = tspPrev.getDestination().getTravelTimeMap().get(d2);
        if (tt == null) {
          tt = new TravelTime();
          tt.setDestination1(tspPrev.getDestination());
          tt.setDestination2(d2);
          tt.setTime(8);
          description += "Take travel day. ";
        } else {
          description += "Expect " + tt.getTime() + " hours of travel. ";
        }
        tsp.setTravelTime(tt);
        tsp.addTime(tt.getTime());
        tsp.setToNextWorkingDay();
        description += "Arrive in " + tsp.getDestination().getCityName() + " and meet with "
            + tsp.getDestination().getShortName() + " " + tsp.getHourDisplay() + ".";
        tsp.setDescription(description);
        if (tsp.weekAfter(tspPrev)) {
          trip = createTrip(tripList, generation);
        }
        trip.addTotalTime(tsp.getTravelTime().getTime());
      }
      trip.add(tsp);

      cutSize--;
      if (cutSize == 0) {
        useDestination1 = !useDestination1;
        cutSize = random.nextInt(10);
        destinationList = useDestination1 ? destinationList1 : destinationList2;
        currentPosition = 0;
        while (currentPosition < destinationList.size() && !destinationList.get(currentPosition).equals(d2)) {
          currentPosition++;
        }
      }
      currentPosition++;
      if (currentPosition >= destinationList.size()) {
        currentPosition = 0;
      }
      while (destinationsVisitedList.size() < destinationList.size()
          && destinationsVisitedList.contains(destinationList.get(currentPosition))) {
        currentPosition++;
        if (currentPosition >= destinationList.size()) {
          currentPosition = 0;
        }
      }
    }
    trip.addTotalTime(7); // Time to get home
    return tripList;

  }

  public List<Trip> clone(TravelAgent travelAgent) {
    List<Destination> destinationList = new ArrayList<Destination>();
    for (Trip trip : travelAgent.getTripList()) {
      for (TripStop tripStop : trip.getTripStopList()) {
        destinationList.add(tripStop.getDestination());
      }
    }

    Random random = new Random();
    String generation = travelAgent.getGeneration();

    List<Trip> tripList = new ArrayList<Trip>();

    Trip trip = null;
    TripStop tsp = null;
    int pickOutPosition = random.nextInt(destinationList.size());
    for (int position = 0; position < destinationList.size(); position++) {
      Destination d2;
      if (position < pickOutPosition) {
        d2 = destinationList.get(position);
      } else if (position < (destinationList.size() - 1)) {
        d2 = destinationList.get(position + 1);
      } else {
        d2 = destinationList.get(pickOutPosition);
      }

      if (trip == null) {
        trip = createTrip(tripList, generation);
        tsp = new TripStop();
        tsp.setDestination(d2);
        TravelTime tt = new TravelTime();
        tt.setTime(0);
        tsp.setDay("Tuesday");
        tsp.setHour(9);
        tsp.setTravelTime(tt);
        tsp.setDescription("Leave home Monday and arrive at " + tsp.getDestination().getCityName() + " by "
            + tsp.getHourDisplay() + ".");
      } else {
        TripStop tspPrev = tsp;
        tsp = new TripStop();
        tsp.setDestination(d2);
        tsp.setDay(tspPrev.getDay());
        tsp.setHour(tspPrev.getHour());
        tsp.addTime(5);
        String description = "Leave " + tspPrev.getDestination().getCityName() + " by " + tsp.getHourDisplay() + ". ";
        TravelTime tt = tspPrev.getDestination().getTravelTimeMap().get(d2);
        if (tt == null) {
          tt = new TravelTime();
          tt.setDestination1(tspPrev.getDestination());
          tt.setDestination2(d2);
          tt.setTime(8);
          description += "Take travel day. ";
        } else {
          description += "Expect " + tt.getTime() + " hours of travel. ";
        }
        tsp.setTravelTime(tt);
        tsp.addTime(tt.getTime());
        tsp.setToNextWorkingDay();
        description += "Arrive in " + tsp.getDestination().getCityName() + " and meet with "
            + tsp.getDestination().getShortName() + " " + tsp.getHourDisplay() + ".";
        tsp.setDescription(description);
        if (tsp.weekAfter(tspPrev)) {
          trip = createTrip(tripList, generation);
        }
        trip.addTotalTime(tsp.getTravelTime().getTime());
      }
      trip.add(tsp);

    }
    //trip.addTotalTime(7); // Time to get home
    return tripList;
  }

}
