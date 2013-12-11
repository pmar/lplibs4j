package org.lplibs4j.solver.analysis;

import java.util.Enumeration;
import java.util.Map;

public class SystemAnalysis {

    public static void main (String[] args) {

	System.out.println("Checking all current properties set ...");

	Enumeration<?> e = System.getProperties().propertyNames();
	while (e.hasMoreElements()) {
	    String p = (String) e.nextElement();
	    System.out.println("Property: " + p + "\tValue: " + System.getProperty(p));
	}

	System.out.println("\nChecking all current system environment variables ...");
	for(Map.Entry<String,String> entry : System.getenv().entrySet())
	    System.out.println("Variable: " + entry.getKey() + "\tValue: " + entry.getValue());
			       
    }
}