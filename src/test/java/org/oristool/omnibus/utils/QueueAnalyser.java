package org.oristool.omnibus.utils;

import org.oristool.omnibus.crossroad.CarFlow;
import org.oristool.omnibus.queue.analysis.QueueAnalyzer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class QueueAnalyser {

	public static double[] makeAnalysis(CarFlow cf, QueueAnalyzer qa, String analysisType) {
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
