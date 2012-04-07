package org.apache.click.service;

import org.apache.click.servlet.MockServletContext;


public class OGNLPropertyServiceTest extends PropertyServiceTestCase {
	
	@Override
	protected void setUp() {
		propertyService = new OGNLPropertyService();

		try {
			propertyService.onInit(new MockServletContext());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void tearDown() {
		try {
			propertyService.onDestroy();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
