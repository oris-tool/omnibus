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

package org.oristool.omnibus.tram;

import org.oristool.omnibus.tram.analysis.TramWayVisitor;
import org.oristool.omnibus.utils.OmnibusMath;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This represent the concept of tram line, composed by an arbitrary number of
 * binaries.
 */
public class TramLine extends TramWay {

	private List<TramTrack> tramTracks;

	/**
	 * The constructor set the name and initialize the collection of tram tracks,
	 * initially empty.
	 * 
	 * @param name the name of the tram way
	 */
	public TramLine(String name) {
		super(name);
		this.tramTracks = new ArrayList<TramTrack>();
	}

	// Utils

	/**
	 * This is the method to add a new tram binary to this line.
	 * 
	 * @param tramTracks the tram track to add
	 */
	public void addTramTrack(TramTrack... tramTracks) {
		Arrays.asList(tramTracks).forEach(this.tramTracks::add);
	}

	/**
	 * This is the method to remove a tram binary from this line.
	 * 
	 * @param tramTrack the tram track to remove
	 */
	public void removeTramTrack(TramTrack tramTrack) {
		this.tramTracks.remove(tramTrack);
	}

	@Override
	public void accept(TramWayVisitor tramWayVisitor) {
		tramWayVisitor.visit(this);
	}

	@Override
	public BigInteger getSuggestedTimeBound() {
		return this.getHyperPeriod().add(this.getMaxPhaseTime());
	}

	@Override
	public BigInteger getHyperPeriod() {
		int hp = 1;
		for (TramTrack tt : tramTracks) {
			hp = OmnibusMath.mcm(hp, tt.getHyperPeriod().intValue());
		}
		return new BigInteger("" + hp);
	}

	@Override
	public BigInteger getMaxPhaseTime() {
		BigInteger phaseTime = new BigInteger("0");
		for (TramWay child : tramTracks) {
			phaseTime = child.getMaxPhaseTime().compareTo(phaseTime) > 0 ? child.getMaxPhaseTime() : phaseTime;
		}
		return phaseTime;
	}

	// Getters

	/**
	 * A getter for the list of tracks that are in this line.
	 * 
	 * @return an unmodifiable list of TramTrack
	 */
	public List<TramTrack> getTramTracks() {
		return Collections.unmodifiableList(tramTracks);
	}

	@Override
	public String toString() {
		return "TramLine [tramTracks=" + tramTracks + "]";
	}

}
