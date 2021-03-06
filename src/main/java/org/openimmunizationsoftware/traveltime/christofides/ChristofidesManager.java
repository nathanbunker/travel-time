package org.openimmunizationsoftware.traveltime.christofides;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openimmunizationsoftware.traveltime.domain.DataStore;
import org.openimmunizationsoftware.traveltime.domain.Destination;
import org.openimmunizationsoftware.traveltime.domain.TravelTime;

public class ChristofidesManager {

  public static ChristofidesManager instance = new ChristofidesManager();

  private ChristofidesManager() {
  }

  public static ChristofidesManager getManager() {
    return instance;
  }

  public double[][] readDistanceMatrix(final String INPUT_FILE) throws IOException {
    double[][] matrix;
    BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE));

    StringBuilder build = new StringBuilder();
    // Find out how many cities there are in the file
    int numCities = 0;
    while (!build.append(br.readLine()).toString().equalsIgnoreCase("null")) {
      numCities++;
      build.setLength(0); // Clears the buffer
    }
    matrix = new double[numCities][numCities];
    // Reset reader to the start of the file
    br = new BufferedReader(new FileReader(INPUT_FILE));
    // Populate the distance matrix
    int currentCity = 0;
    build = new StringBuilder();
    while (!build.append(br.readLine()).toString().equalsIgnoreCase("null")) {
      String[] tokens = build.toString().split(" ");
      for (int i = 0; i < numCities; i++) {
        matrix[currentCity][i] = Double.parseDouble(tokens[i]);
      }
      currentCity++;
      build.setLength(0); // Clears the buffer
    }
    return matrix;
  }

  public double[][] readDistanceMatrix(DataStore dataStore) throws IOException {
    double[][] matrix;

    List<Destination> destinationList = dataStore.getDestinationList();
    int count = destinationList.size();
    matrix = new double[count][count];
    int i = 0;
    for (Destination destination1 : destinationList) {
      int j = 0;
      for (Destination destination2 : destinationList) {
        TravelTime travelTime = destination1.getTravelTimeMap().get(destination2);
        if (travelTime == null) {
          matrix[i][j] = 0.0;
        } else {
          matrix[i][j] = travelTime.getTime();
        }
        j++;
      }
      i++;
    }
    return matrix;
  }

  public void printMatrix(double[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        System.out.print(matrix[i][j] + " ");
      }
      System.out.println();
    }
  }
}