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

package org.oristool.omnibus.intersection.analysis;

import org.oristool.omnibus.intersection.CarFlow;
import org.oristool.omnibus.vehicle.BaseQueue;
import org.oristool.omnibus.vehicle.analysis.TransientAnalyzer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This class provides a common interface for steady state analyzers.
 */
public abstract class SteadyStateAnalyzer {

	/**
	 * It computes and returns the steady state distribution of the length of the
	 * queue at the beginning of each period.
	 * 
	 * @param carFlow  the carFlow of which compute the steady state
	 * @param analyzer the queue analyzer that should be used
	 * @param timeStep the temporal resolution of the transient analysis of the
	 *                 first period (needed for the steady state analysis)
	 * @return the steady state distribution of the length of the queue at the
	 *         beginning of each period
	 */
	public abstract double[] getSteadyStateDistribution(CarFlow carFlow, TransientAnalyzer analyzer, BigDecimal timeStep);

	protected double[][] getPkjMatrix(CarFlow carFlow, TransientAnalyzer analyzer, BigDecimal timeStep) {
		int hyperPeriod = carFlow.getObstaclesHyperPeriod();
		int hyperPeriodStep = new BigDecimal(hyperPeriod).divide(timeStep).intValue() + 1;

		double[] firstPeriodAvailability = carFlow.getIntersectionAvailability(hyperPeriodStep);

		double[][] pkjMatrix = new double[carFlow.getQueue().getSize().add(BigInteger.ONE).intValue()][carFlow
				.getQueue().getSize().add(BigInteger.ONE).intValue()];
		for (int k = 0; k <= carFlow.getQueue().getSize().intValue(); k++) {
			BaseQueue tmpQueue = carFlow.getQueue().getClone();
			tmpQueue.setInitialElements(new BigInteger(Integer.toString(k)));
			double[][] stateProbabilitiesAlongFirstPeriod = analyzer
					.analyze(tmpQueue, firstPeriodAvailability, timeStep.doubleValue())
					.getStateProbabilitiesAlongTime();
			for (int j = 0; j <= carFlow.getQueue().getSize().intValue(); j++) {
				pkjMatrix[k][j] = stateProbabilitiesAlongFirstPeriod[firstPeriodAvailability.length - 1][j];
			}
		}

		return pkjMatrix;
	}

}
