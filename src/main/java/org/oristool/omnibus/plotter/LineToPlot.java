package org.oristool.omnibus.plotter;

public class LineToPlot {

	private String seriesName;
	private double[] xValues;
	private double[] yValues;
	
	public LineToPlot(String seriesName, double[] xValues, double[] yValues) {
		if(xValues.length!=yValues.length)
			throw new IllegalArgumentException("Can't be plotted.");
		
		this.seriesName = seriesName;
		this.xValues = new double[xValues.length];
		this.yValues = new double[yValues.length];
		for(int i=0; i<xValues.length; i++) {
			this.xValues[i] = xValues[i];
			this.yValues[i] = yValues[i];
		}
	}
	
	public String getSeriesName() {
		return seriesName;
	}

	public double[] getxValues() {
		return xValues;
	}

	public double[] getyValues() {
		return yValues;
	}

}
