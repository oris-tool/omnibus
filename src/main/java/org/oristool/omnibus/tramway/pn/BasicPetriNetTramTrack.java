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

package org.oristool.omnibus.tramway.pn;

import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.omnibus.tramway.analysis.TramWayVisitor;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This is a single tram track modelled by a Petri Net, with some specific
 * places and transitions.
 */
public class BasicPetriNetTramTrack extends PetriNetTramTrack {

	private BigInteger periodTime = new BigInteger("220");
	private BigInteger phaseTime = new BigInteger("0");
	private BigInteger delayEFTime = new BigInteger("0");
	private BigInteger delayLFTime = new BigInteger("120");
	private BigInteger crosslightAntTime = new BigInteger("5");
	private BigInteger leavingEFTime = new BigInteger("6");
	private BigInteger leavingLFTime = new BigInteger("14");

	private String greenReward = "1-" + wrap("setRed");

	protected BasicPetriNetTramTrack(String name) {
		super(name);
	}

	protected BasicPetriNetTramTrack(String name, BigInteger periodTime, BigInteger phaseTime, BigInteger delayEFTime,
			BigInteger delayLFTime, BigInteger crossLightAntTime, BigInteger leavingEFTime, BigInteger leavingLFTime) {
		super(name);
		this.setPeriodTime(periodTime);
		this.setPhaseTime(phaseTime);
		this.setDelayTimes(delayEFTime, delayLFTime);
		this.setCrosslightAntTime(crossLightAntTime);
		this.setLeavingTimes(leavingEFTime, leavingLFTime);
	}

	@Override
	public void buildModel(PetriNet net, Marking marking) {
		checkLegality();
		// Generating Nodes
		Place crossing = net.addPlace(wrap("crossing"));
		Place p0 = net.addPlace(wrap("p0"));
		Place p1 = net.addPlace(wrap("p1"));
		Place sensor = net.addPlace(wrap("sensor"));
		Place setRed = net.addPlace(wrap("setRed"));
		Transition crossLigthAnt = net.addTransition(wrap("crossLigthAnt"));
		Transition delay = net.addTransition(wrap("delay"));
		Transition phase = net.addTransition(wrap("phase"));
		Transition leaving = net.addTransition(wrap("leaving"));
		Transition period = net.addTransition(wrap("period"));

		// Generating Connectors
		net.addPrecondition(p1, delay);
		net.addPrecondition(crossing, leaving);
		net.addPrecondition(p0, phase);
		net.addPostcondition(delay, sensor);
		net.addPostcondition(crossLigthAnt, crossing);
		net.addPostcondition(phase, p1);
		net.addPrecondition(sensor, crossLigthAnt);
		net.addPostcondition(delay, setRed);
		net.addPrecondition(setRed, leaving);
		net.addPostcondition(period, p0);

		// Generating Properties
		marking.setTokens(crossing, 0);
		marking.setTokens(p0, 1);
		marking.setTokens(p1, 0);
		marking.setTokens(sensor, 0);
		marking.setTokens(setRed, 0);
		crossLigthAnt.addFeature(StochasticTransitionFeature
				.newDeterministicInstance(new BigDecimal(crosslightAntTime.doubleValue()), MarkingExpr.from("1", net)));
		crossLigthAnt.addFeature(new Priority(0));
		delay.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal(delayEFTime.doubleValue()),
				new BigDecimal(delayLFTime.doubleValue())));
		phase.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal(phaseTime.doubleValue()),
				MarkingExpr.from("1", net)));
		phase.addFeature(new Priority(0));
		leaving.addFeature(StochasticTransitionFeature.newUniformInstance(new BigDecimal(leavingEFTime.doubleValue()),
				new BigDecimal(leavingLFTime.doubleValue())));
		period.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal(periodTime.doubleValue()),
				MarkingExpr.from("1", net)));
		period.addFeature(new Priority(0));

	}

	@Override
	public void accept(TramWayVisitor tramWayVisitor) {
		tramWayVisitor.visit(this);
	}

	protected void setPeriodTime(BigInteger periodTime) {
		if (isPositive(periodTime))
			this.periodTime = periodTime;
		else
			throw new IllegalArgumentException("PeriodTime must be equal or greater than zero.");
	}

	protected void setDelayTimes(BigInteger delayEFTTime, BigInteger delayLFTTime) {
		if (isPositive(delayEFTTime) && isPositive(delayLFTTime)) {
			this.delayEFTime = delayEFTTime;
			this.delayLFTime = delayLFTTime;
		} else
			throw new IllegalArgumentException(
					"DelayEFTTime and delayLFTTTime must be both equal or greater then zero.");
	}

	protected void setCrosslightAntTime(BigInteger crosslightAntTime) {
		if (isPositive(crosslightAntTime))
			this.crosslightAntTime = crosslightAntTime;
		else
			throw new IllegalArgumentException("CrosslightAntTime must be equal or greater than zero.");
	}

	protected void setLeavingTimes(BigInteger leavingEFTTime, BigInteger leavingLFTTime) {
		if (isPositive(leavingEFTTime) && isPositive(leavingLFTTime)) {
			this.leavingEFTime = leavingEFTTime;
			this.leavingLFTime = leavingLFTTime;
		} else
			throw new IllegalArgumentException(
					"LeavingEFTTime and leavingLFTTime must be both equal or greater then zero.");
	}

	protected void setPhaseTime(BigInteger phaseTime) {
		if (isPositive(phaseTime))
			this.phaseTime = phaseTime;
		else
			throw new IllegalArgumentException("PhaseTime must be equal or greater than zero.");
	}

	private boolean isPositive(BigInteger b) {
		if (b.compareTo(new BigInteger("0")) < 0) {
			return false;
		}
		return true;
	}

	@Override
	public BigInteger getSuggestedTimeBound() {
		return periodTime.add(phaseTime);
	}

	@Override
	public String getGreenReward() {
		return greenReward;
	}

	@Override
	protected void checkLegality() {
		if (delayLFTime.add(crosslightAntTime).add(leavingLFTime).compareTo(periodTime) > 0)
			throw new IllegalStateException("Tramway " + this.getName()
					+ ". For analysis reasons, PeriodTime must be greather than summation of DelayLFT, CrosslightAntTime and LeavingLFT.");
	}

	@Override
	public BigInteger getMaxPhaseTime() {
		return phaseTime;
	}

	@Override
	public BigInteger getHyperPeriod() {
		return periodTime;
	}

	@Override
	public String toString() {
		return "BasicPetriNetTramTrack [periodTime=" + periodTime + ", phaseTime=" + phaseTime + ", delayEFTime="
				+ delayEFTime + ", delayLFTime=" + delayLFTime + ", crosslightAntTime=" + crosslightAntTime
				+ ", leavingEFTime=" + leavingEFTime + ", leavingLFTime=" + leavingLFTime + ", greenReward="
				+ greenReward + "]";
	}

}
