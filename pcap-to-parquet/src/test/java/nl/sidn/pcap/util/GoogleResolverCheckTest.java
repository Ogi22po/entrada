/*
 * ENTRADA, a big data platform for network data analytics
 *
 * Copyright (C) 2016 SIDN [https://www.sidn.nl]
 * 
 * This file is part of ENTRADA.
 * 
 * ENTRADA is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ENTRADA is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ENTRADA. If not, see
 * [<http://www.gnu.org/licenses/].
 *
 */
package nl.sidn.pcap.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import nl.sidn.pcap.config.Settings;
import nl.sidn.pcap.ip.GoogleResolverCheck;

public class GoogleResolverCheckTest {

  @Before
  public void setup() {
    ClassLoader classLoader = getClass().getClassLoader();
    Settings.setPath(classLoader.getResource("test-settings.properties").getFile());
    Settings.getInstance().setSetting(Settings.STATE_LOCATION, "/tmp/");
  }

  @Test
  public void createGoogleCheckTest() {
    GoogleResolverCheck check = new GoogleResolverCheck();
    check.update();
    Assert.assertTrue(check.getSize() > 0);
  }

}
