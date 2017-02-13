package org.openimmunizationsoftware.traveltime;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openimmunizationsoftware.traveltime.domain.DataStore;
import org.openimmunizationsoftware.traveltime.domain.TravelAgent;
import org.openimmunizationsoftware.traveltime.logic.TripBuilderType;

public class DistributionServlet extends HomeServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
			DataStore dataStore = new DataStore();
			dataStore.setTripBuilderType(TripBuilderType.CONTINOUS);
			dataStore.setPopulationSize(90000);
			dataStore.setParentSize(100);
			setupDataStore(dataStore, "/" + "travel-times.csv");
			for (int i = 0; i < dataStore.getPopulationSize(); i++) {
				dataStore.getTravelAgentList().add(new TravelAgent(dataStore));
			}
			int bucketSize = 1;
			Map<Integer, Integer> bucketMap = new HashMap<Integer, Integer>();
			for (TravelAgent travelAgent : dataStore.getTravelAgentList()) {
				float totalTravelTime = travelAgent.getTotalTravelTime();
				int bucketNumber = (int) totalTravelTime / bucketSize;
				Integer bucketCount = bucketMap.get(bucketNumber);
				if (bucketCount == null) {
					bucketCount = 1;
				} else {
					bucketCount = bucketCount + 1;
				}
				bucketMap.put(bucketNumber, bucketCount);
			}
			int bucketMax = 0;
			int bucketMin = Integer.MAX_VALUE;
			for (Integer bucketNumber : bucketMap.keySet()) {
				if (bucketNumber < bucketMin) {
					bucketMin = bucketNumber;
				}
				if (bucketNumber > bucketMax) {
					bucketMax = bucketNumber;
				}
			}
			out.println("<table>");
			out.println("  <tr>");
			out.println("    <th>Time</th>");
			out.println("    <th>Solution Count</th>");
			//out.println("    <th>Graph</th>");
			out.println("  </tr>");
			for (int i = bucketMin; i <= bucketMax; i++) {
				float travelTime = i * bucketSize;
				out.println("  <tr>");
				out.println("    <td>" + travelTime + "</td>");
				Integer count = bucketMap.get(i);
				if (count == null) {
					out.println("    <td>0</td>");
					//out.println("    <td>&nbsp;</td>");
				} else {
					out.println("    <td>" + count + "</td>");
//					out.println("    <td>");
//					for (int j = 0; j < count; j++) {
//						out.println("&diams;");
//					}
//					out.println("    </td>");
				}
				out.println("  </tr>");

			}
			out.println("</table>");
			out.println("</body>");
			out.println("</html>");

		} finally {
			out.close();
		}

	}
}
