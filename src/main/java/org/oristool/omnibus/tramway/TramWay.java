package org.oristool.omnibus.tramway;

import org.oristool.omnibus.tramway.analysis.TramWayVisitor;

import java.math.BigInteger;

/**
 * It provides an interface for the tramway, that could be a single track or a
 * line, composed by multiple track.
 */
public abstract class TramWay {

	private String name;

	/**
	 * The constructor.
	 * 
	 * @param name the name for the TramWay (ex. "Linea 2")
	 */
	public TramWay(String name) {
		this.name = name;
	}

	/**
	 * Name getter.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	protected String wrap(String string) {
		return new String(name + "_" + string);
	}

	/**
	 * This is the accept method of the pattern visitor.
	 * 
	 * @param tramWayVisitor
	 */
	public abstract void accept(TramWayVisitor tramWayVisitor);

	/**
	 * This suggest a time until which do transient analysis.
	 * 
	 * @return the time bound suggested
	 */
	public abstract BigInteger getSuggestedTimeBound();

	/**
	 * This returns the hyper period of tramways, after which behavior is globally
	 * periodic.
	 * 
	 * @return the hyperperiod
	 */
	public abstract BigInteger getHyperPeriod();

	/**
	 * This returns the maximum phase of a tram.
	 * 
	 * @return the max phase time
	 */
	public abstract BigInteger getMaxPhaseTime();

}
