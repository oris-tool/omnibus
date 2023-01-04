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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Plotter extends ApplicationFrame {

	private static final long serialVersionUID = 1L;

	private Plotter(String title) {
		super(title);
	}

	public static void plot(String title, String seriesName, String xUnit, String yUnit, double[] xValues,
			double[] yValues, boolean saveAsPNG, String titleSplitter, boolean setVisible) {

		if (xValues.length != yValues.length)
			throw new IllegalArgumentException("Ascisse ed ordinate non sono in egual numero.");

		String shortTitle = getShortTitle(title, titleSplitter);
		Plotter frame = new Plotter(shortTitle);

		XYSeries series = new XYSeries(seriesName);
		for (int i = 0; i < xValues.length; i++)
			series.add(xValues[i], yValues[i]);
		XYSeriesCollection seriesCollection = new XYSeriesCollection(series);
		JFreeChart chart = ChartFactory.createXYLineChart(cleanTitleFromSplitter(title, titleSplitter), xUnit, yUnit, seriesCollection);
		ChartPanel panel = new ChartPanel(chart);

		if (saveAsPNG)
			saveAsPNG(title, titleSplitter, chart);

		frame.setContentPane(panel);
		frame.setSize(new Dimension(1080, 640));
		frame.setMinimumSize(new Dimension(540, 320));
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setVisible(setVisible);

	}

	public static void plotBar(String title, String seriesName, String xUnit, String yUnit, double[] xValues,
			double[] yValues, boolean saveAsPNG, String titleSplitter, boolean setVisible) {

		if (xValues.length != yValues.length)
			throw new IllegalArgumentException("Ascisse ed ordinate non sono in egual numero.");

		String shortTitle = getShortTitle(title, titleSplitter);
		Plotter frame = new Plotter(shortTitle);

		XYSeries series = new XYSeries(seriesName);
		for (int i = 0; i < xValues.length; i++)
			series.add(xValues[i], yValues[i]);
		XYSeriesCollection seriesCollection = new XYSeriesCollection(series);

		JFreeChart chart = ChartFactory.createXYBarChart(cleanTitleFromSplitter(title, titleSplitter), xUnit, false, yUnit, seriesCollection);
		ChartPanel panel = new ChartPanel(chart);

		if (saveAsPNG)
			saveAsPNG(title, titleSplitter, chart);

		frame.setContentPane(panel);
		frame.setSize(new Dimension(1080, 640));
		frame.setMinimumSize(new Dimension(540, 320));
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setVisible(setVisible);
	}

	public static void plot(String title, String xUnit, String yUnit, ArrayList<LineToPlot> linesToPlot,
			boolean saveAsPNG, String titleSplitter, boolean setVisible) {
		String shortTitle = getShortTitle(title, titleSplitter);
		Plotter frame = new Plotter(shortTitle);

		XYSeriesCollection seriesCollection = new XYSeriesCollection();
		for (LineToPlot line : linesToPlot) {
			if (line.getxValues().length != line.getyValues().length)
				throw new IllegalArgumentException("Ascisse ed ordinate non sono in egual numero.");
			XYSeries series = new XYSeries(line.getSeriesName());
			for (int j = 0; j < line.getxValues().length; j++) {
				series.add(line.getxValues()[j], line.getyValues()[j]);
			}
			seriesCollection.addSeries(series);
		}

		JFreeChart chart = ChartFactory.createXYLineChart(cleanTitleFromSplitter(title, titleSplitter), xUnit, yUnit, seriesCollection);
		ChartPanel panel = new ChartPanel(chart);

		if (saveAsPNG)
			saveAsPNG(title, titleSplitter, chart);

		frame.setContentPane(panel);
		frame.setSize(new Dimension(1080, 640));
		frame.setMinimumSize(new Dimension(540, 320));
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setVisible(setVisible);
	}

	private static void saveAsPNG(String title, String titleSplitter, JFreeChart chart) {
		try {
			File folder = new File("results");
			if (!folder.exists())
				folder.mkdir();
			String shortTitle = getShortTitle(title, titleSplitter);
			OutputStream out = new FileOutputStream("results\\" + shortTitle + ".png");
			ChartUtilities.writeChartAsPNG(out, chart, 1980, 1020);
		} catch (IOException ex) {
			System.out.println("Can't save png for some reason.");
//			System.out.println(ex.getMessage());
		}
	}

	private static String getShortTitle(String title, String titleSplitter) {
		String shortTitle = title;
		shortTitle = shortTitle.replace("\n", " ");
		shortTitle = shortTitle.replace("\t", " ");
		shortTitle = shortTitle.replace(": ", "=");
		if( !isNullOrBlank(titleSplitter)) {
			String[] split = shortTitle.split(titleSplitter,2);
			shortTitle = split[0];
		}
		return shortTitle;
	}

	private static String cleanTitleFromSplitter(String title, String titleSplitter) {
		if( !isNullOrBlank(titleSplitter) ) {
			String[] split = title.split(titleSplitter,2);
			return split[0] + split[1];
		}
		return title;
	}

	private static boolean isNullOrBlank(String param) {
		return param == null || param.trim().length() == 0;
	}
}
