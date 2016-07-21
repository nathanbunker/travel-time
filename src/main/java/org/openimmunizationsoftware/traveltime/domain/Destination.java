package org.openimmunizationsoftware.traveltime.domain;

import java.util.HashMap;
import java.util.Map;

public class Destination implements Comparable<Destination> {
  private String shortName = "";
  private String cityName = "";
  private Map<Destination, TravelTime> travelTimeMap = new HashMap<Destination, TravelTime>();

  public Map<Destination, TravelTime> getTravelTimeMap() {
    return travelTimeMap;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  @Override
  public int hashCode() {
    return shortName.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Destination) {
      Destination d = (Destination) obj;
      return d.getShortName().equals(getShortName());
    }
    return super.equals(obj);
  }

  @Override
  public int compareTo(Destination d) {
    return this.getShortName().compareTo(d.getShortName());
  }
  
  @Override
  public String toString() {
    return getShortName();
  }

}
