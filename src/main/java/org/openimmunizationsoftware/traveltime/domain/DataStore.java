package org.openimmunizationsoftware.traveltime.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataStore {
  private Map<String, Destination> destinationMap = new HashMap<String, Destination>();
  private int currentGeneration = 1;
  private List<TravelAgent> travelAgentList = new ArrayList<TravelAgent>();
  private Set<String> signatureSet  = new HashSet<String>(); 
  public Set<String> getSignatureSet() {
    return signatureSet;
  }

  public static final int POPULATION_SIZE = 10000;
  public static final int PARENT_SIZE = 5000;

  public List<TravelAgent> getTravelAgentList() {
    return travelAgentList;
  }

  public int getCurrentGeneration() {
    return currentGeneration;
  }

  public void incrementCurrentGeneration()
  {
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
