package org.openimmunizationsoftware.traveltime.domain;

import java.util.ArrayList;
import java.util.List;

public class Trip implements Comparable<Trip> {
  private List<TripStop> tripStopList = new ArrayList<TripStop>();
  private int totalTime = 0;
  private Double travelScore = null;
  private String generation = "1";

  public String getGeneration() {
    return generation;
  }

  public void setGeneration(String generation) {
    this.generation = generation;
  }

  public int getTotalTime() {
    return totalTime;
  }

  public double getTravelScore() {
    if (travelScore == null) {
      if (totalTime == 0) {
        return Double.NaN;
      }
      travelScore = tripStopList.size() / ((double) totalTime);
    }
    return travelScore;
  }

  public void addTotalTime(int totalTime) {
    this.totalTime = this.totalTime + totalTime;
  }

  public void setTotalTime(int totalTime) {
    this.totalTime = totalTime;
  }

  public List<TripStop> getTripStopList() {
    return tripStopList;
  }

  @Override
  public int compareTo(Trip t) {
    if (this.getTravelScore() < t.getTravelScore()) {
      return 1;
    } else if (this.getTravelScore() > t.getTravelScore()) {
      return -1;
    }
    return 0;

  }
}
