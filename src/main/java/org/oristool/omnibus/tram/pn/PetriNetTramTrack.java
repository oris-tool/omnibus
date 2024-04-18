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

package org.oristool.omnibus.tram.pn;

import org.oristool.omnibus.tram.TramTrack;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;

/**
 * This abstract class represents a single tram track modelled by a Petri net.
 */
public abstract class PetriNetTramTrack extends TramTrack {

	protected PetriNet petriNet;
	protected Marking marking;

	public PetriNetTramTrack(String name) {
		super(name);
	}

	/**
	 * API internal utils. This methods build the model as a new Petri net, ready to
	 * be analyzed.
	 */
	public void buildModel() {
		this.petriNet = new PetriNet();
		this.marking = new Marking();
		this.buildModel(this.getPetriNet(), this.getMarking());
	}

	/**
	 * API internal utils. This integrates the model in given Petri Net and Marking.
	 * 
	 * @param net
	 * @param marking
	 */
	public abstract void buildModel(PetriNet net, Marking marking);

	/**
	 * API internal utils. This returns the reward that represent the probability to
	 * have the intersection free from tram crossing.
	 * 
	 * @return the string reward (ex. "1-setRed")
	 */
	public abstract String getGreenReward();

	protected abstract void checkLegality();

	/**
	 * API internal utils. This returns the Petri Net object.
	 * 
	 * @return the Petri Net
	 */
	public PetriNet getPetriNet() {
		return petriNet;
	}

	/**
	 * API internal utils. This returns the Marking object for the Petri net.
	 * 
	 * @return the Marking
	 */
	public Marking getMarking() {
		return marking;
	}

}