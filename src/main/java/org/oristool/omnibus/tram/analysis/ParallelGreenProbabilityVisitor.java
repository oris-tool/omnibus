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

package org.oristool.omnibus.tram.analysis;

import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trans.TreeTransient;
import org.oristool.omnibus.tram.TramLine;
import org.oristool.omnibus.tram.TramWay;
import org.oristool.omnibus.tram.pn.PetriNetTramTrack;
import org.oristool.petrinet.Marking;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This implements the parallel count of the availability of the intersection.
 * This analyze single tracks separately and combine rewards by multiplication.
 */
public class ParallelGreenProbabilityVisitor extends GreenProbabilityVisitor {

	private BigDecimal timeStep;

	private double[] result;
	private double[] periodicResult;

	public ParallelGreenProbabilityVisitor() {
		super();
	}

	@Override
	public void visit(TramLine tramLine) {
		timeBound = tramLine.getSuggestedTimeBound().compareTo(timeBound) > 0 ? tramLine.getSuggestedTimeBound()
				: timeBound;
		hyperPeriod = tramLine.getHyperPeriod();

		int step = (int) (timeBound.doubleValue() / timeStep.doubleValue());
		result = new double[step];

		double[][] childResults = new double[tramLine.getTramTracks().size()][step];
		for (int i = 0; i < tramLine.getTramTracks().size(); i++) {
			ParallelGreenProbabilityVisitor childVisitor = new ParallelGreenProbabilityVisitor();
			double[] childResult = childVisitor
					.computeGreenProbability(tramLine.getTramTracks().get(i), timeBound, timeStep).getResult();
			for (int j = 0; j < childResult.length; j++)
				childResults[i][j] = childResult[j];
		}

		for (int i = 0; i < step; i++) {
			result[i] = 1.;
			for (int j = 0; j < tramLine.getTramTracks().size(); j++) {
				result[i] = result[i] * childResults[j][i];
			}
		}

		int periodicStep = (int) ((tramLine.getHyperPeriod().doubleValue() / timeStep.doubleValue()));

		this.periodicResult = new double[periodicStep];
		for (int i = 0; i < periodicStep; i++) {
			periodicResult[i] = result[step - periodicStep + i];
		}

	}

	@Override
	public void visit(PetriNetTramTrack petriNetTramWay) {
		timeBound = petriNetTramWay.getSuggestedTimeBound().compareTo(timeBound) > 0
				? petriNetTramWay.getSuggestedTimeBound()
				: timeBound;
		hyperPeriod = petriNetTramWay.getHyperPeriod();

		petriNetTramWay.buildModel();

		TreeTransient analysis;
		TransientSolution<Marking, Marking> solution;
		TransientSolution<Marking, RewardRate> reward;

		BigInteger analysisTimeBound = petriNetTramWay.getSuggestedTimeBound();
		BigDecimal bd_analysisTimeBound = new BigDecimal(analysisTimeBound);
		analysis = TreeTransient.builder().greedyPolicy(bd_analysisTimeBound, BigDecimal.ZERO)
				.timeBound(bd_analysisTimeBound).timeStep(timeStep).build();

		solution = analysis.compute(petriNetTramWay.getPetriNet(), petriNetTramWay.getMarking());
		reward = TransientSolution.computeRewards(false, solution, petriNetTramWay.getGreenReward());

		int step = (int) (timeBound.doubleValue() / timeStep.doubleValue());

		int solutionStep = (int) ((analysisTimeBound.doubleValue()) / timeStep.doubleValue());

		this.result = new double[step];
		for (int i = 0; i < solutionStep; i++) {
			result[i] = reward.getSolution()[i][0][0];
		}

		int periodicStep = (int) ((petriNetTramWay.getHyperPeriod().doubleValue() / timeStep.doubleValue()));

		this.periodicResult = new double[periodicStep];
		for (int i = 0; i < periodicStep; i++) {
			periodicResult[i] = result[solutionStep - periodicStep + i];
		}

		for (int i = solutionStep; i < step; i++) {
			result[i] = periodicResult[(i - solutionStep) % periodicResult.length];
		}

	}

	@Override
	public GreenProbabilityVisitor computeGreenProbability(TramWay tramWay, BigDecimal timeStep) {
		reset();
		this.timeStep = timeStep;
		tramWay.accept(this);
		return this;
	}

	@Override
	public GreenProbabilityVisitor computeGreenProbability(TramWay tramWay, BigInteger minimumTimeBound,
			BigDecimal timeStep) {
		reset();
		this.timeBound = minimumTimeBound;
		this.timeStep = timeStep;
		tramWay.accept(this);
		return this;
	}

	@Override
	public double[] getResult() {
		return result;
	}

	@Override
	public double[] getPeriodicResult() {
		return periodicResult;
	}

}
