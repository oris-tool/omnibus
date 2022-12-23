package org.oristool.omnibus.utils;

public class OmnibusMath {

	public static int mcm(int a, int b) {
		return a*b/MCDEuclide(a, b);
	}
	
	private static int MCDEuclide(int a, int b) {
	    int r;
	    while(b != 0) {
	         r = a % b;
	         a = b; 
	         b = r;
	    }
	    return a;
	}
	
}
