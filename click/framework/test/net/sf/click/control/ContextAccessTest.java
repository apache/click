package net.sf.click.control;

import junit.framework.TestCase;

public class ContextAccessTest extends TestCase {
	
	private static final int INTERATIONS = 100000;
	
	private Object object;
	
	private static ThreadLocal threadLocal = new ThreadLocal();
	
	public void testA() {
		long start = System.currentTimeMillis();
		
		int count = 0;
		for (int i = 0; i < INTERATIONS; i++) {
			Object objectA = getObjectA();
			if (objectA != null) {
				count += 1;
			}
		}
		
		System.out.println("testA time:" + (System.currentTimeMillis() - start));
	}
	
	public void testB() {
		long start = System.currentTimeMillis();
		
		int count = 0;
		for (int i = 0; i < INTERATIONS; i++) {
			Object objectA = getObjectB();
			if (objectA != null) {
				count += 1;
			}
		}
		
		System.out.println("testB time:" + (System.currentTimeMillis() - start) + " ms");
	}

	public Object getObjectA() {
		if (object == null) {
			object = new Object();
		}
		return object;
	}

	public Object getObjectB() {
		Object bObject = threadLocal.get();
		if (bObject == null) {
			bObject = new Object();
			threadLocal.set(bObject);
		}
		return bObject;
	}

}
