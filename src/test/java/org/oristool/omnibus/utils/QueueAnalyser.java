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

import org.oristool.omnibus.intersection.CarFlow;
import org.oristool.omnibus.vehicle.analysis.TransientAnalyzer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class QueueAnalyser {

	public static double[] makeAnalysis(CarFlow cf, TransientAnalyzer qa, String analysisType) {
		double[] expectedStateAlongTime = null;
		boolean exception = false;
		BigDecimal timeStep = Config.timeStep;
		System.out.println(analysisType + " analysis, timestep = " + timeStep);
		do {
			try {
				if (exception) {
					timeStep = timeStep.divide(new BigDecimal("10"), RoundingMode.HALF_UP);
					System.out.println("Trying with timeStep " + timeStep);
				}
				expectedStateAlongTime = cf.analyzeQueue(qa, Config.timeBound, timeStep).getExpectedStateAlongTime();
				if (exception) {
					expectedStateAlongTime = MathUtils.changeTimeStep(expectedStateAlongTime, timeStep.doubleValue(),
					                                                  Config.timeStep.doubleValue());
				}
				exception = false;
			} catch (IllegalArgumentException e2) {
				System.err.println(e2.getLocalizedMessage() + "\nAborted on " + analysisType + " analysis.");
				exception = true;
			}
		} while (exception);
		return expectedStateAlongTime;
	}
}
