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

package org.oristool.omnibus.tramway;

import org.oristool.omnibus.crossroad.Obstacle;
import org.oristool.omnibus.tramway.analysis.GreenProbabilityVisitor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * The crossing of the tram over the crossroad is intended to be an obstacle for
 * car flows. This class model this concept.
 */
public class TramCrossing extends Obstacle {

	private TramWay tramWay;
	private double[] periodicAvailability;
	private boolean analyzed;

	/**
	 * A TramCrossing, of course, has a tramWay. This is the only parameter of the
	 * constructor.
	 * 
	 * @param tramWay
	 */
	public TramCrossing(TramWay tramWay) {
		this.tramWay = tramWay;
		this.analyzed = false;
	}

	// Utils

	/**
	 * This launch the analysis of the tramway to get the availability of the
	 * obstacle.
	 * 
	 * @param tramWayVisitor the green probability visitor we want to use for the
	 *                       analysis
	 * @param timeStep       the temporal resolution that will be used for the
	 *                       analysis
	 */
	public void analyze(GreenProbabilityVisitor tramWayVisitor, BigDecimal timeStep) {
		this.availability = tramWayVisitor.computeGreenProbability(tramWay, timeStep).getResult();
		this.periodicAvailability = tramWayVisitor.getPeriodicResult();
		this.analyzed = true;
	}

	private void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}

	private void setPeriodicAvailability(double[] periodicAvailability) {
		this.periodicAvailability = periodicAvailability.clone();
	}

	@Override
	public Obstacle getClone() {
		TramCrossing clone = new TramCrossing(this.tramWay);
		clone.setAvailability(this.availability);
		clone.setAnalyzed(this.analyzed);
		clone.setPeriodicAvailability(this.periodicAvailability);
		return clone;
	}

	@Override
	public double getAvailability(int timeStep) {
		if (!analyzed) {
			throw new IllegalAccessError("TramCrossing not yet analyzed. Please, invoke analyze() first.");
		}
		if (timeStep < this.availability.length)
			return super.getAvailabilityStandard(timeStep);
		else
			return this.periodicAvailability[(timeStep - this.availability.length) % periodicAvailability.length];
	}

	@Override
	public BigInteger getPeriod() {
		return tramWay.getHyperPeriod();
	}

	@Override
	public String toString() {
		return "TramCrossing [tramWay=" + tramWay + ", periodicAvailability=" + Arrays.toString(periodicAvailability)
				+ ", analyzed=" + analyzed + ", availability=" + Arrays.toString(availability) + "]";
	}

}
