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

public class TripBuilderDisconnected implements TripBuilderInterface {
  
  @Override
  public List<Trip> makeTrip(DataStore dataStore, List<Destination> destinationList) {
    List<Trip> tripList = new ArrayList<Trip>();
    List<Destination> destinationsList = new ArrayList<Destination>(dataStore.getDestinationList());
    setupTrips(tripList, null, destinationsList, "1");
    Collections.sort(tripList);
    return tripList;
  }
  public List<Trip> makeTrip(DataStore dataStore) {
    List<Trip> tripList = new ArrayList<Trip>();
    Random random = new Random();
    List<Destination> destinationsList = new ArrayList<Destination>(dataStore.getDestinationList());
    setupTrips(tripList, random, destinationsList, "1");
    Collections.sort(tripList);
    return tripList;
  }
  
  private static List<Trip> setupTrips(List<Trip> tripList, Random random, List<Destination> destinationsNotVisitedList,
      String generation) {
    while (destinationsNotVisitedList.size() > 0) {
      Trip trip = new Trip();
      trip.setGeneration(generation);
      tripList.add(trip);

      TripStop tsp;
      {
        int position = random == null ? 0 : random.nextInt(destinationsNotVisitedList.size());
        Destination d1 = destinationsNotVisitedList.get(position);
        destinationsNotVisitedList.remove(position);
        TripStop tripStop = new TripStop();
        tripStop.setDestination(d1);
        TravelTime travelTime = new TravelTime();
        travelTime.setDestination2(d1);
        travelTime.setTime(8);
        tripStop.setTravelTime(travelTime);
        tripStop.setDay("Tuesday");
        tripStop.setHour(9);
        trip.getTripStopList().add(tripStop);
        tsp = tripStop;
      }
      trip.setTotalTime(tsp.getTravelTime().getTime());
      int count = 0;
      while (tsp != null && !tsp.getDay().equals("Friday") && !tsp.getDay().equals("Monday") && count < 5) {
        count++;
        // List<TravelTime> travelTimePotential = new ArrayList<TravelTime>();
        // for (TravelTime ttp :
        // tsp.getDestination().getTravelTimeMap().values()) {
        // if (destinationsNotVisitedList.contains(ttp.getDestination2())) {
        // travelTimePotential.add(ttp);
        // }
        // }
        if (destinationsNotVisitedList.size() > 0) {
          // Collections.sort(destinationsNotVisitedList);
          // double g = Math.abs(random.nextGaussian());
          // g = g * 0.3 * destinationsNotVisitedList.size();
          // int position = (int) g;
          // if (position >= destinationsNotVisitedList.size()) {
          // position = destinationsNotVisitedList.size() - 1;
          // }
          int position = random == null ? 0 : random.nextInt(destinationsNotVisitedList.size());
          Destination dn = destinationsNotVisitedList.get(position);
          TravelTime travelTimeNext = tsp.getDestination().getTravelTimeMap().get(dn);

          if (travelTimeNext == null) {
            travelTimeNext = new TravelTime();
            travelTimeNext.setDestination1(tsp.getDestination());
            travelTimeNext.setDestination2(dn);
            travelTimeNext.setTime(12);
          }
          destinationsNotVisitedList.remove(position);
          TripStop tripStopNext = new TripStop();
          tripStopNext.setDestination(dn);
          tripStopNext.setTravelTime(travelTimeNext);
          tripStopNext.setDay(tsp.getDay());
          tripStopNext.setHour(tsp.getHour());
          tripStopNext.addTime(5);// 5 hour visit
          tripStopNext.addTime(travelTimeNext.getTime()); // Travel Time
          tripStopNext.setToNextWorkingDay(); // Sleep and roll forward to
                                              // next working day
          trip.getTripStopList().add(tripStopNext);
          tsp = tripStopNext;
          trip.addTotalTime(tsp.getTravelTime().getTime());
        }
      }
      trip.addTotalTime(7); // Time to get home
    }
    return tripList;
  }

  public List<Trip> makeNewTripList(List<Trip> tripList1, List<Trip> tripList2, DataStore dataStore,
      String generation) {
    List<Trip> tripList = new ArrayList<Trip>();
    Random random = new Random();
    tripList1 = new ArrayList<Trip>(tripList1);
    tripList2 = new ArrayList<Trip>(tripList2);
    List<Trip> tripListParent = new ArrayList();
    while (!tripList1.isEmpty() || !tripList2.isEmpty()) {
      if (random.nextBoolean() && !tripList1.isEmpty()) {
        tripListParent.add(tripList1.get(0));
        tripList1.remove(0);
      } else if (!tripList2.isEmpty()) {
        tripListParent.add(tripList2.get(0));
        tripList2.remove(0);
      }
    }
    List<Destination> destinationsNotVisitedList = new ArrayList<Destination>(dataStore.getDestinationMap().values());
    for (Trip tripParent : tripListParent) {
      boolean destinationsAvailable = true;
      for (TripStop tripStop : tripParent.getTripStopList()) {
        if (!destinationsNotVisitedList.contains(tripStop.getDestination())) {
          destinationsAvailable = false;
        }
      }
      if (destinationsAvailable) {
        tripList.add(tripParent);
        for (TripStop tripStop : tripParent.getTripStopList()) {
          destinationsNotVisitedList.remove(tripStop.getDestination());
        }
      }
    }
    setupTrips(tripList, random, destinationsNotVisitedList, generation);
    Collections.sort(tripList);
    return tripList;
  }

  public List<Trip> clone(TravelAgent travelAgent) {
    List<Trip> tripList = new ArrayList<Trip>();
    Random random = new Random();
    int position = random.nextInt(travelAgent.getTripList().size());
    int p = 0;
    for (Trip trip : travelAgent.getTripList()) {
      if (position == p) {
        List<Destination> destinationsNotVisitedList = new ArrayList<Destination>();
        for (TripStop tripStop : trip.getTripStopList()) {
          destinationsNotVisitedList.add(tripStop.getDestination());
        }
        tripList.addAll(TripBuilderDisconnected.setupTrips(tripList, random, destinationsNotVisitedList,
            travelAgent.getGeneration()));
      } else {
        tripList.add(trip);
      }
      p++;
    }
    Collections.sort(tripList);
    return tripList;
  }

}
