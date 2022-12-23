package org.oristool.omnibus.tramway.pn;

import org.oristool.omnibus.tramway.TramTrack;
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