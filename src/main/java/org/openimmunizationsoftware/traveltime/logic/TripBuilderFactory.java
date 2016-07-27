package org.openimmunizationsoftware.traveltime.logic;

public class TripBuilderFactory {
  
  public static TripBuilderInterface getTripBuilder(TripBuilderType tripBuilderType)
  {
    switch (tripBuilderType) {
    case DISCONNECTED:
      return new TripBuilderDisconnected();
    case CONTINOUS:
      return new TripBuilderContinuous();
    default:
      return new TripBuilderDisconnected();
    }
  }
}
