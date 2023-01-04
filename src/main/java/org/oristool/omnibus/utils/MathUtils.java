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

package org.oristool.omnibus.utils;

import java.util.ArrayList;

public class MathUtils {

	/**
	 * It returns the mean relative error of the vector b on vector a.
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static double averageRelativeError(double[] a, double[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("Arrays has different lengths.");
		}
		double e = 0.;
		int nanInfCount = 0;
		for (int i = 0; i < a.length; i++) {
			double ei = b[i] / a[i];
			if (Double.isFinite(ei))
				e += Math.abs(1 - ei);
			else
				nanInfCount++;
		}
		return e / (double) (a.length - nanInfCount);
	}

	/**
	 * It returns the mean error of the vector b on vector a, relative to the given max.
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static double averageSizeRelativeError(double[] a, double[] b, double max) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("Arrays has different lengths.");
		}
		if (max <= 0) {
			throw new IllegalArgumentException("max must be a positive number");
		}
		double e = 0.;
		int nanInfCount = 0;
		for (int i = 0; i < a.length; i++) {
			if (b[i] > max || a[i] > max) {
				throw new IllegalArgumentException("Some values are greater than max.");
			}
			double ei = Math.abs(b[i] - a[i]) / max;
			if (Double.isFinite(ei))
				e += ei;
			else
				nanInfCount++;
		}
		return e / (double) (a.length - nanInfCount);
	}

	public static double averageAbsoluteError(double[] a, double[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("Arrays has different lengths.");
		}
		double e = 0.;
		for (int i = 0; i < a.length; i++) {
			e += Math.abs(a[i] - b[i]);
		}
		return e / ((double) a.length);
	}

	public static double rootMeanSquareDeviation(double[] a, double[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("Arrays has different lengths.");
		}
		double rmsd = 0.;
		for (int i = 0; i < a.length; i++) {
			rmsd += Math.pow((a[i] - b[i]), 2);
		}
		rmsd = rmsd / ((double) a.length);
		rmsd = Math.sqrt(rmsd);
		return rmsd;
	}

	public static double[] changeTimeStep(double[] array, double oldTimeStep, double newTimeStep) {
		double timeBound = oldTimeStep * array.length;
		int newArrayLength = (int) (timeBound / newTimeStep);
		double[] newArray = new double[newArrayLength];
		for (int i = 0; i < newArrayLength; i++) {
			newArray[i] = array[(int) ((double) i * newTimeStep / oldTimeStep)];
		}
		return newArray;
	}

	/**
	 * Restituisce un array dove ogni elemento è la media del suo rispettivo
	 * originale con i suoi step "vicini" per parte.
	 */
	public static double[] smoothArray(double[] array, int step) {
		if (array.length <= step)
			return array;
		if (step <= 0)
			return array;

		double[] ret = new double[array.length];
		for (int i = 0; i < ret.length; i++) {
			ArrayList<Double> neighbors = new ArrayList<Double>();
			neighbors.add(array[i]);
			int j = i;
			while (--j >= 0 && j >= i - step) {
				neighbors.add(array[j]);
			}
			j = i;
			while (++j < array.length && j <= i + step) {
				neighbors.add(array[j]);
			}
			double[] neighborsArray = new double[neighbors.size()];
			for (int k = 0; k < neighborsArray.length; k++) {
				neighborsArray[k] = neighbors.get(k).doubleValue();
			}
			ret[i] = mean(neighborsArray);
		}
		return ret;
	}

	private static double mean(double[] ds) {
		double mean = 0.;
		for (double d : ds) {
			mean += d;
		}
		mean = mean / ds.length;
		return mean;
	}

}
