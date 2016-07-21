package org.openimmunizationsoftware.traveltime.domain;

public class TripStop {
  private Destination destination = null;
  private TravelTime travelTime = null;
  private String day = "";
  private int hour = 0;

  public void addTime(int h) {
    this.hour = this.hour + h;
    while (hour >= 24) {
      hour = hour - 24;
      if (day.equals("Tuesday")) {
        day = "Wednesday";
      } else if (day.equals("Wednesday")) {
        day = "Thursday";
      } else if (day.equals("Thursday")) {
        day = "Friday";
      } else if (day.equals("Friday")) {
        day = "Saturday";
      } else if (day.equals("Saturday")) {
        day = "Sunday";
      } else if (day.equals("Sunday")) {
        day = "Monday";
      } else if (day.equals("Monday")) {
        day = "Tuesday";
      }
    }
  }

  public boolean isWorkDay() {
    return day.equals("Monday") || day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday")
        || day.equals("Friday");
  }

  public void setToNextWorkingDay() {
    String currentDay = day;
    addTime(12);
    if (day.equals(currentDay)) {
      setToNextWorkingDay();
    }
    if (hour < 9) {
      hour = 9;
    } else if (hour > 12) {
      setToNextWorkingDay();
    }
    if (!isWorkDay()) {
      setToNextWorkingDay();
    }
  }

  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }

  public int getHour() {
    return hour;
  }

  public String getHourDisplay() {
    if (hour < 12) {
      return day + " " + hour;
    } else if (hour == 12) {
      return day + " " + hour;
    }
    return day + " " + (hour - 12);
  }

  public String getDescription() {
    if (hour < 12) {
      return day + " " + hour + " AM - " + destination.getCityName();
    } else if (hour == 12) {
      return day + " " + hour + " PM - " + destination.getCityName();
    }
    return day + " " + (hour - 12) + " PM - " + destination.getCityName();
  }

  public void setHour(int hour) {
    this.hour = hour;
  }

  public Destination getDestination() {
    return destination;
  }

  public void setDestination(Destination destination) {
    this.destination = destination;
  }

  public TravelTime getTravelTime() {
    return travelTime;
  }

  public void setTravelTime(TravelTime travelTime) {
    this.travelTime = travelTime;
  }
}
