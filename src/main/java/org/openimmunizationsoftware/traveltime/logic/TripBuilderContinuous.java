package org.openimmunizationsoftware.traveltime.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openimmunizationsoftware.traveltime.domain.DataStore;
import org.openimmunizationsoftware.traveltime.domain.Destination;
import org.openimmunizationsoftware.traveltime.domain.TravelAgent;
import org.openimmunizationsoftware.traveltime.domain.TravelTime;
import org.openimmunizationsoftware.traveltime.domain.Trip;
import org.openimmunizationsoftware.traveltime.domain.TripStop;

public class TripBuilderContinuous implements TripBuilderInterface {
  
  @Override
  public List<Trip> makeTrip(DataStore dataStore, List<Destination> destinationList) {
    List<Trip> tripList = new ArrayList<Trip>();
    Bag bag = new Bag();

    for (Destination d2 : destinationList)
    {
      addDestination(tripList, "1", bag, d2);
    }
    loopBackIn(tripList.get(0), bag.tsp);
    return tripList;
  }
  
  public List<Trip> makeTrip(DataStore dataStore) {
    List<Trip> tripList = new ArrayList<Trip>();
    Random random = new Random();
    List<Destination> destinationsNotVisitedList = new ArrayList<Destination>(dataStore.getDestinationMap().values());
    setupTrips(tripList, random, destinationsNotVisitedList, "1");
    return tripList;
  }
  
  private static class Bag
  {
    Trip trip = null;
    TripStop tsp = null;
  }

  private static List<Trip> setupTrips(List<Trip> tripList, Random random, List<Destination> destinationsNotVisitedList,
      String generation) {
    Bag bag = new Bag();
    while (destinationsNotVisitedList.size() > 0) {
      Destination d2 = getNextDestinationNotVisited(random, destinationsNotVisitedList);
      addDestination(tripList, generation, bag, d2);
    }
    loopBackIn(tripList.get(0), bag.tsp);
    return tripList;
  }

  private static void addDestination(List<Trip> tripList, String generation, Bag bag, Destination d2) {
    if (bag.trip == null) {
      bag.trip = createTrip(tripList, generation);
      bag.tsp = new TripStop();
      bag.tsp.setDestination(d2);
      TravelTime tt = new TravelTime();
      tt.setTime(0);
      bag.tsp.setDay("Tuesday");
      bag.tsp.setHour(9);
      bag.tsp.setTravelTime(tt);
      bag.tsp.setDescription("Leave home Monday and arrive at " + bag.tsp.getDestination().getCityName() + " by "
          + bag.tsp.getHourDisplay() + ".");
    } else {
      TripStop tspPrev = bag.tsp;
      bag.tsp = new TripStop();
      bag.tsp.setDestination(d2);
      bag.tsp.setDay(tspPrev.getDay());
      bag.tsp.setHour(tspPrev.getHour());
      bag.tsp.addTime(5);
      String description = "Leave " + tspPrev.getDestination().getCityName() + " by " + bag.tsp.getHourDisplay() + ". ";
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
      bag.tsp.setTravelTime(tt);
      bag.tsp.addTime(tt.getTime());
      bag.tsp.setToNextWorkingDay();
      description += "Arrive in " + bag.tsp.getDestination().getCityName() + " and meet with "
          + bag.tsp.getDestination().getShortName() + " " + bag.tsp.getHourDisplay() + ".";
      bag.tsp.setDescription(description);
      if (bag.tsp.weekAfter(tspPrev)) {
        bag.trip = createTrip(tripList, generation);
      }
      bag.trip.addTotalTime(bag.tsp.getTravelTime().getTime());
    }
    bag.trip.add(bag.tsp);
  }

  private static void loopBackIn(Trip trip, TripStop tsp) {
    TripStop tspFirst = trip.getTripStopList().get(0);
    TravelTime tt = tsp.getDestination().getTravelTimeMap().get(tspFirst.getDestination());
    tspFirst.setTravelTime(tt);
    tspFirst.setDescription("Leave from " + tsp.getDestination().getCityName() + " and arrive at "
        + tspFirst.getDestination().getCityName() + " by " + tspFirst.getHourDisplay() + ".");
    trip.addTotalTime(tt.getTime());
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
    loopBackIn(tripList.get(0), tsp);
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
    loopBackIn(tripList.get(0), tsp);
    return tripList;
  }

}
