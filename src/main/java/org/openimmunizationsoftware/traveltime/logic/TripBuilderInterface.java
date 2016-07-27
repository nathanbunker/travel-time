package org.openimmunizationsoftware.traveltime.logic;

import java.util.List;

import org.openimmunizationsoftware.traveltime.domain.DataStore;
import org.openimmunizationsoftware.traveltime.domain.TravelAgent;
import org.openimmunizationsoftware.traveltime.domain.Trip;

public interface TripBuilderInterface {
  
  public List<Trip> makeTrip(DataStore dataStore);
  public List<Trip> makeNewTripList(List<Trip> tripList1, List<Trip> tripList2, DataStore dataStore, String generation) ;
  public List<Trip> clone(TravelAgent travelAgent) ;

}
