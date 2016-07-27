package org.openimmunizationsoftware.traveltime.domain;

import java.util.ArrayList;
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
  private int currentGeneration = 1;
  private List<TravelAgent> travelAgentList = new ArrayList<TravelAgent>();
  private Set<String> signatureSet = new HashSet<String>();
  private TripBuilderInterface tripBuilder = null;

  public TripBuilderInterface getTripBuilder() {
    return tripBuilder;
  }

  public void setTripBuilder(TripBuilderInterface tripBuilder) {
    this.tripBuilder = tripBuilder;
  }

  public DataStore() {
    this(TripBuilderFactory.getTripBuilder(TRIP_BUILDER_TYPE));
  }

  public DataStore(TripBuilderInterface tripBuilder) {
    this.tripBuilder = tripBuilder;
  }

  public Set<String> getSignatureSet() {
    return signatureSet;
  }

  public static final int POPULATION_SIZE = 10000;
  public static final int PARENT_SIZE = 5000;
  public static final TripBuilderType TRIP_BUILDER_TYPE = TripBuilderType.CONTINOUS;

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
