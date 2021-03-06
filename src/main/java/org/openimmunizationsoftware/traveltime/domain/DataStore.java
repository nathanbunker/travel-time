package org.openimmunizationsoftware.traveltime.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openimmunizationsoftware.traveltime.logic.TripBuilderFactory;
import org.openimmunizationsoftware.traveltime.logic.TripBuilderInterface;
import org.openimmunizationsoftware.traveltime.logic.TripBuilderType;

public class DataStore {
  private Map<String, Destination> destinationMap = new HashMap<String, Destination>();
  private int currentGeneration = 0;
  private List<TravelAgent> travelAgentList = new ArrayList<TravelAgent>();
  private Set<String> signatureSet = new HashSet<String>();
  private TripBuilderInterface tripBuilder = null;
  private boolean started = false;
  private TripBuilderType tripBuilderType = TripBuilderType.CONTINOUS;
  private int populationSize = 0;
  private int parentSize = 0;
  private List<Destination> destinationList = null;
  private TravelAgent travelAgentChristofides = null;
  private List<Float> totalTimeHistory = new ArrayList<Float>();
  public static String[] SOURCES = new String[] {"travel-times-original.csv", "travel-times.csv", "US48.csv"};
  

  public List<Float> getTotalTimeHistory() {
    return totalTimeHistory;
  }

  public TravelAgent getTravelAgentChristofides() {
    return travelAgentChristofides;
  }

  public void setTravelAgentChristofides(TravelAgent travelAgentChristofides) {
    this.travelAgentChristofides = travelAgentChristofides;
  }

  public List<Destination> getDestinationList() {
    if (destinationList == null) {
      synchronized (destinationMap) {
        destinationList = new ArrayList<Destination>(destinationMap.values());
        Collections.sort(destinationList);
      }
    }
    return destinationList;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public int getParentSize() {
    return parentSize;
  }

  public void setParentSize(int parentSize) {
    this.parentSize = parentSize;
  }

  public boolean isStarted() {
    return started;
  }

  public void setStarted(boolean started) {
    this.started = started;
  }

  public TripBuilderInterface getTripBuilder() {
    if (tripBuilder == null) {
      tripBuilder = TripBuilderFactory.getTripBuilder(tripBuilderType);
    }
    return tripBuilder;
  }

  public DataStore() {
  }

  public Set<String> getSignatureSet() {
    return signatureSet;
  }

  public static final int POPULATION_SIZE = 5000;
  public static final int PARENT_SIZE = 2500;

  public TripBuilderType getTripBuilderType() {
    return tripBuilderType;
  }

  public void setTripBuilderType(TripBuilderType tripBuilderType) {
    this.tripBuilderType = tripBuilderType;
  }

  public List<TravelAgent> getTravelAgentList() {
    return travelAgentList;
  }

  public int getCurrentGeneration() {
    return currentGeneration;
  }

  public void incrementCurrentGeneration() {
    currentGeneration++;
  }

  public Map<String, Destination> getDestinationMap() {
    return destinationMap;
  }

  public Destination getDestination(String shortName) {
    Destination destination = destinationMap.get(shortName);
    if (destination == null) {
      destination = new Destination();
      destination.setShortName(shortName);
      destinationMap.put(shortName, destination);
    }
    return destination;
  }
}
