/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.webpackage.extender.http.impl.osgi;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertNull;


public class ActivatorTest {
  private Activator activator;

  @Before
  public void setUp() {
    this.activator = new Activator();
  }

  @Test
  public void serviceTrackerIsOpenedOnActivatorStart() {
    BundleContext mockBundleContext = mock( BundleContext.class );
    ServiceTracker serviceTrackerMock = spy( activator.createPentahoWebPackageServiceTracker( mockBundleContext ) );
    activator = spy( new Activator() );
    doReturn( serviceTrackerMock ).when( activator ).createPentahoWebPackageServiceTracker( mockBundleContext );

    activator.start( mockBundleContext );

    verify( serviceTrackerMock, times( 1 ) ).open( true );
  }

  @Test
  public void serviceTrackerIsClosedOnActivatorStop() {
    ServiceTracker mockServiceTracker = mock( ServiceTracker.class );
    activator.pentahoWebPackageServiceTracker = mockServiceTracker;

    activator.stop( null /* value doesnt matter */ );

    verify( mockServiceTracker, times( 1 ) ).close();
  }

  @Test
  public void serviceTrackerIsSetToNullOnActivatorStop() {
    activator.pentahoWebPackageServiceTracker = mock( ServiceTracker.class );

    activator.stop( null /* value doesnt matter */ );

    assertNull( activator.pentahoWebPackageServiceTracker );
  }

}
