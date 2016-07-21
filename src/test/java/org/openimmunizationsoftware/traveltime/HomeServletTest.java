package org.openimmunizationsoftware.traveltime;

import static org.junit.Assert.*;

import org.junit.Test;

public class HomeServletTest {

  @Test
  public void test() {
    assertEquals("B", HomeServlet.makeName(1));
    assertEquals("W", HomeServlet.makeName(22));
    assertEquals("BMN", HomeServlet.makeName(1001));
  }

}
