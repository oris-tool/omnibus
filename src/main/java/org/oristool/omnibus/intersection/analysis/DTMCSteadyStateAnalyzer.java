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

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.oristool.models.gspn.chains.DTMCStationary;
import org.oristool.models.gspn.chains.DTMCStationary.Builder;
import org.oristool.omnibus.intersection.CarFlow;
import org.oristool.omnibus.vehicle.analysis.TransientAnalyzer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * This analyzer compute the steady state distribution of the length of the
 * queue at the beginning of each period in an analytic way.
 */
public class DTMCSteadyStateAnalyzer extends SteadyStateAnalyzer {

	@Override
	public double[] getSteadyStateDistribution(CarFlow carFlow, TransientAnalyzer analyzer, BigDecimal timeStep) {
		double[][] pkjMatrix = this.getPkjMatrix(carFlow, analyzer, timeStep);

		Map<Integer, QueueState> states = new HashMap<>();

		MutableValueGraph<QueueState, Double> mvg = ValueGraphBuilder.directed().allowsSelfLoops(true).build();
		for (int i = 0; i < carFlow.getQueue().getSize().add(BigInteger.ONE).intValue(); i++) {
			QueueState queueState = new QueueState();
			states.put(i, queueState);
			mvg.addNode(queueState);
		}
		for (int k = 0; k < carFlow.getQueue().getSize().add(BigInteger.ONE).intValue(); k++) {
			for (int j = 0; j < carFlow.getQueue().getSize().add(BigInteger.ONE).intValue(); j++) {
				mvg.putEdgeValue(states.get(k), states.get(j), pkjMatrix[k][j]);
			}
		}
		Builder<QueueState> dtmcStBuilder = DTMCStationary.builder();
		DTMCStationary<QueueState> dtmcSt = dtmcStBuilder.build();
		Map<QueueState, Double> steadyStateMap = dtmcSt.apply(mvg);

		double[] steadyState = new double[carFlow.getQueue().getSize().add(BigInteger.ONE).intValue()];
		double sum = 0;

		for (int i = 0; i < carFlow.getQueue().getSize().add(BigInteger.ONE).intValue(); i++) {
			QueueState queueState = states.get(i);
			steadyState[i] = steadyStateMap.get(queueState) != null ? steadyStateMap.get(queueState) : 0.;
			sum += steadyState[i];
		}

		for (int i = 0; i < steadyState.length; i++) {
			steadyState[i] = steadyState[i] / sum;
		}

		return steadyState;
	}

	private class QueueState {
	}

}
