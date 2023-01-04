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

package org.oristool.omnibus.tramway.analysis;

import org.oristool.omnibus.tramway.TramWay;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This abstract class provides an interface for visitors that compute the
 * probabilities with which TramWays let the intersection free.
 */
public abstract class GreenProbabilityVisitor implements TramWayVisitor {

	protected BigInteger timeBound;
	protected BigInteger hyperPeriod;

	protected GreenProbabilityVisitor() {
		timeBound = new BigInteger("0");
		hyperPeriod = new BigInteger("0");
	}

	/**
	 * This method compute the probabilities with which TramWays let the
	 * intersection free until the suggested time bound given by tramway itself.
	 * 
	 * @param tramWay
	 * @param timeStep
	 * @return the visitor itself
	 */
	public abstract GreenProbabilityVisitor computeGreenProbability(TramWay tramWay, BigDecimal timeStep);

	/**
	 * This method compute the probabilities with which TramWays let the
	 * intersection free, at least until the given time. It can compute availability
	 * for more time if suggested time bound is greater than minimum time bound.
	 * 
	 * @param tramWay
	 * @param minimumTimeBound
	 * @param timeStep
	 * @return the visitor itself
	 */
	public abstract GreenProbabilityVisitor computeGreenProbability(TramWay tramWay, BigInteger minimumTimeBound,
			BigDecimal timeStep);

	/**
	 * This returns the computed array of availability.
	 * 
	 * @return the array of availability
	 */
	public abstract double[] getResult();

	/**
	 * This returns only the periodic part of the array of availability.
	 * 
	 * @return the periodic part of the array of availability
	 */
	public abstract double[] getPeriodicResult();

	/**
	 * This returns the time bound until which the analysis was done.
	 * 
	 * @return the used time bound
	 */
	public BigInteger getComputedTimeBound() {
		return timeBound;
	}

	/**
	 * This returns the same hyper period of the tramway.
	 * 
	 * @return the tramway hyper period
	 */
	public BigInteger getHyperPeriod() {
		return hyperPeriod;
	}

	protected void reset() {
		timeBound = new BigInteger("0");
		hyperPeriod = new BigInteger("0");
	}

}