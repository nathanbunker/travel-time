package org.openimmunizationsoftware.traveltime.domain;

public class TravelTime implements Comparable<TravelTime> {
  private Destination destination1 = null;
  private Destination destination2 = null;
  private int time = 0;

  @Override
  public int compareTo(TravelTime tt) {
    if (this.getTime() < tt.getTime()) {
      return -1;
    } else if (this.getTime() > tt.getTime()) {
      return 1;
    } else {
      return 0;
    }

  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public Destination getDestination1() {
    return destination1;
  }

  public void setDestination1(Destination destination1) {
    this.destination1 = destination1;
  }

  public Destination getDestination2() {
    return destination2;
  }

  public void setDestination2(Destination desination2) {
    this.destination2 = desination2;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TravelTime) {
      TravelTime tt = (TravelTime) obj;
      return (tt.getDestination1().equals(getDestination1()) && tt.getDestination2().equals(getDestination2()))
          || (tt.getDestination2().equals(getDestination1()) && tt.getDestination2().equals(getDestination1()));
    }
    return super.equals(obj);
  }
}
