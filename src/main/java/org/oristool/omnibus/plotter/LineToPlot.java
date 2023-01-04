/* This program is part of the ORIS Tool.
  * Copyright (C) 2011-2023 The ORIS Authors.
  *
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU Affero General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.
  */

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
