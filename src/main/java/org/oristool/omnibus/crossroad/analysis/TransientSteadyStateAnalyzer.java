package org.oristool.omnibus.crossroad.analysis;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import org.oristool.omnibus.crossroad.CarFlow;
import org.oristool.omnibus.queue.analysis.QueueAnalyzer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This analyzer compute the steady state distribution of the length of the
 * queue at the beginning of each period in a transient way. It's a little bit
 * faster than DTMCSteadyStateAnalyzer but can be vague.
 */
public class TransientSteadyStateAnalyzer extends SteadyStateAnalyzer {

	private double epsilon = 0.00001;

	/**
	 * The constructor.
	 * 
	 * @param epsilon a bound under which difference between vectors is supposed to
	 *                be zero
	 */
	public TransientSteadyStateAnalyzer(double epsilon) {
		this.epsilon = epsilon;
	}

	@Override
	public double[] getSteadyStateDistribution(CarFlow carFlow, QueueAnalyzer analyzer, BigDecimal timeStep) {
		double[][] pkjMatrix = this.getPkjMatrix(carFlow, analyzer, timeStep);

		DoubleMatrix2D matrix = new DenseDoubleMatrix2D(carFlow.getQueue().getSize().add(BigInteger.ONE).intValue(),
				carFlow.getQueue().getSize().add(BigInteger.ONE).intValue());
		DoubleMatrix2D initialState = new DenseDoubleMatrix2D(1,
				carFlow.getQueue().getSize().add(BigInteger.ONE).intValue());
		for (int row = 0; row < carFlow.getQueue().getSize().add(BigInteger.ONE).intValue(); row++) {
			for (int column = 0; column < carFlow.getQueue().getSize().add(BigInteger.ONE).intValue(); column++) {
				matrix.set(row, column, pkjMatrix[row][column]);
			}
			if (row != carFlow.getQueue().getInitialElements().intValue()) {
				initialState.set(0, row, 0.);
			} else {
				initialState.set(0, row, 1.);
			}
		}

		Algebra algebra = new Algebra();
		DoubleMatrix2D currentState;
		DoubleMatrix2D nextState = initialState;
		DoubleMatrix2D sub = new DenseDoubleMatrix2D(1, carFlow.getQueue().getSize().add(BigInteger.ONE).intValue());
		do {
			currentState = nextState;
			nextState = algebra.mult(currentState, matrix);
			for (int i = 0; i < carFlow.getQueue().getSize().add(BigInteger.ONE).intValue(); i++) {
				sub.set(0, i, nextState.get(0, i) - currentState.get(0, i));
			}
		} while (sub.aggregate(Functions.plus, Functions.abs) > epsilon);

		double[] steadyState = new double[carFlow.getQueue().getSize().add(BigInteger.ONE).intValue()];
		for (int i = 0; i < carFlow.getQueue().getSize().add(BigInteger.ONE).intValue(); i++) {
			steadyState[i] = nextState.get(0, i);
		}

		return steadyState;
	}

}
