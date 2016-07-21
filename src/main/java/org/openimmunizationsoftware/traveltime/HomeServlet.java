package org.openimmunizationsoftware.traveltime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.openimmunizationsoftware.traveltime.domain.DataStore;
import org.openimmunizationsoftware.traveltime.domain.Destination;
import org.openimmunizationsoftware.traveltime.domain.TravelAgent;
import org.openimmunizationsoftware.traveltime.domain.TravelTime;

public class HomeServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    HttpSession session = req.getSession();
    PrintWriter out = resp.getWriter();
    resp.setContentType("text/html");
    try {
      out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\">");
      out.println("<html>");
      out.println("  <head>");
      out.println("    <title>Travel Time</title>");
      out.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"index.css\">");
      out.println("  </head>");
      out.println("  <body>");
      DataStore dataStore = (DataStore) session.getAttribute("dataStore");
      if (dataStore == null) {
        dataStore = new DataStore();
        setupDataStore(dataStore);
        session.setAttribute("dataStore", dataStore);
        for (int i = 0; i < DataStore.POPULATION_SIZE; i++) {
          dataStore.getTravelAgentList().add(new TravelAgent(dataStore, makeName(i)));
          Collections.sort(dataStore.getTravelAgentList());
        }
      } else {
        Set<String> signatureSet = dataStore.getSignatureSet();
        signatureSet.clear();
        List<TravelAgent> travelAgentList = dataStore.getTravelAgentList();
        for (TravelAgent ta : travelAgentList) {
          signatureSet.add(ta.getSignature());
        }
        Random random = new Random();
        dataStore.incrementCurrentGeneration();
        for (int i = DataStore.PARENT_SIZE; i < DataStore.POPULATION_SIZE; i++) {
          int momP = pickParent(random);
          int dadP = pickParent(random);
          TravelAgent mom = travelAgentList.get(momP);
          TravelAgent dad = travelAgentList.get(dadP);
          int tooManyTimes = 0;
          while (mom.areSignaturesEqual(dad) || mom.getTotalTravelTime() == dad.getTotalTravelTime()) {
            dadP = pickParent(random);
            dad = travelAgentList.get(dadP);
            tooManyTimes++;
            if (tooManyTimes > 1000) {
              // give up!
              break;
            }
          }
          TravelAgent child = new TravelAgent(mom, dad, makeName(i));
          if (!signatureSet.contains(child.getSignature())) {
            travelAgentList.set(i, child);
            signatureSet.add(child.getSignature());
          }
        }
        Collections.sort(dataStore.getTravelAgentList());
      }

      out.println("<h3>Top 100</h3>");
      out.println("<table>");
      out.println("  <tr>");
      out.println("    <th>Pos</th>");
      out.println("    <th>Signature</th>");
      out.println("    <th>Total Time</th>");
      out.println("    <th>Generation</th>");
      // out.println(" <th>Name</th>");
      out.println("  </tr>");
      for (int i = 0; i < DataStore.POPULATION_SIZE; i++) {
        List<TravelAgent> travelAgentList = dataStore.getTravelAgentList();
        TravelAgent ta = travelAgentList.get(i);
        out.println("  <tr>");
        out.println("    <td>" + i + "</td>");
        out.println("    <td>" + ta.getSignature() + "</td>");
        out.println("    <td>" + ta.getTotalTravelTime() + "</td>");
        out.println("    <td>" + ta.getGeneration() + "</td>");
        // out.println(" <td>" + ta.getName() + "</td>");
        out.println("  </tr>");
      }
      out.println("</table>");

      out.println("<h3>Best</h3>");
      dataStore.getTravelAgentList().get(0).printSchedule(out);
      printDestinationTable(out, dataStore);
      out.println("</body>");
      out.println("</html>");

    } finally {
      out.close();
    }
  }

  private static final boolean GAUSIAN = false;

  private int pickParent(Random random) {
    if (GAUSIAN) {
      int pickParent = (int) (Math.abs(random.nextGaussian()) * ((double) DataStore.PARENT_SIZE * 0.7));
      if (pickParent >= DataStore.PARENT_SIZE) {
        pickParent = DataStore.PARENT_SIZE;
      }
      return pickParent;
    } else {
      return random.nextInt(DataStore.PARENT_SIZE);
    }
  }

  private void printDestinationTable(PrintWriter out, DataStore dataStore) {
    List<Destination> destinationList = new ArrayList<Destination>(dataStore.getDestinationMap().values());
    Collections.sort(destinationList);
    out.println("<table>");
    out.println("  <tr>");
    out.println("    <th>Short Name</th>");
    out.println("    <th>City, State</th>");
    for (Destination destination : destinationList) {
      out.println("    <th width=\"40\">" + destination + "</th>");
    }
    out.println("  </tr>");
    for (Destination destination1 : destinationList) {
      out.println("  <tr>");
      out.println("    <th>" + destination1 + "</th>");
      out.println("    <td>" + destination1.getCityName() + "</td>");
      for (Destination destination2 : destinationList) {
        TravelTime travelTime = destination1.getTravelTimeMap().get(destination2);
        if (travelTime == null) {
          out.println("    <td style=\"background-color: black;\">X</td>");
        } else {
          out.println("    <td style=\"background-color: lightblue;\">" + travelTime.getTime() + "</td>");
        }
      }

      out.println("  </tr>");
    }

    out.println("</table>");
  }

  private void setupDataStore(DataStore dataStore) throws IOException {
    {
      Reader in = new InputStreamReader(HomeServlet.class.getResourceAsStream("/travel-times.csv"));
      CSVParser parser = new CSVParser(in, CSVFormat.EXCEL);
      List<CSVRecord> list = parser.getRecords();

      if (list.size() > 0) {
        Map<Integer, Destination> destinationByPosition = new HashMap<Integer, Destination>();
        {
          CSVRecord csvRecord = list.get(0);
          for (int position = 2; position < csvRecord.size(); position++) {
            String shortName = csvRecord.get(position);
            if (!shortName.equals("")) {
              Destination destination = dataStore.getDestination(shortName);
              destinationByPosition.put(position, destination);
            }
          }
        }

        for (int lineNumber = 1; lineNumber < list.size(); lineNumber++) {
          CSVRecord csvRecord = list.get(lineNumber);
          String shortName = csvRecord.get(0);
          String cityName = csvRecord.get(1);
          if (shortName != null && cityName != null && !shortName.equals("") && !cityName.equals("")) {
            Destination destination1 = dataStore.getDestination(shortName);
            destination1.setCityName(cityName);

            for (int position = 2; position < csvRecord.size(); position++) {
              String value = csvRecord.get(position);
              Destination destination2 = destinationByPosition.get(position);
              if (destination2 != null && !value.equals("")) {
                int time = Integer.parseInt(value);
                {
                  TravelTime travelTime = new TravelTime();
                  travelTime.setDestination1(destination1);
                  travelTime.setDestination2(destination2);
                  travelTime.setTime(time);
                  destination1.getTravelTimeMap().put(destination2, travelTime);
                }
                {
                  TravelTime travelTime = new TravelTime();
                  travelTime.setDestination1(destination2);
                  travelTime.setDestination2(destination1);
                  travelTime.setTime(time);
                  destination2.getTravelTimeMap().put(destination1, travelTime);
                }
              }
            }
          }
        }
      }
      parser.close();
    }
  }

  // 100
  // 100 % 26 =
  //

  public static String makeName(int i) {
    String name = "";
    int div = i / 26;
    int re = i % 26;
    while (re > 0 || div > 0) {
      name = ((char) (((int) 'A') + re)) + name;
      i = div;
      div = i / 26;
      re = i % 26;
    }
    return name;
  }
}
