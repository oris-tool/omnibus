package org.oristool.omnibus.plotter;

public class PlotUtils {

	public static double[] getLinSpace(double start, double end, double step, boolean includingEnd) {
		int length = (int) ((end - start) / step);
		if (includingEnd)
			length++;
		double[] ret = new double[length];
		ret[0] = start;
		for (int i = 1; i < length; i++) {
			ret[i] = ret[i - 1] + step;
		}
		return ret;
	}

}
