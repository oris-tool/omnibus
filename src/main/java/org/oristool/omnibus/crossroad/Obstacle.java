package org.oristool.omnibus.crossroad;

import java.math.BigInteger;

/**
 * It models the concept of an impediment for a car flow.
 */
public abstract class Obstacle {

	protected double[] availability;

	// Utils

	/**
	 * It returns a clone object of this Obstacle.
	 */
	public abstract Obstacle getClone();

	/**
	 * It returns the availability of the obstacle to be passed at a certain time.
	 * 
	 * @param timeStep the temporal index we are interested in
	 * @return the availability at the given index
	 */
	public abstract double getAvailability(int timeStep);

	/**
	 * All obstacle should be periodic. 
	 * 
	 * @return the period of the obstacle
	 */
	public abstract BigInteger getPeriod();

	protected double getAvailabilityStandard(int timeStep) {
		if (timeStep < 0)
			throw new IllegalArgumentException("Negative timeStep requested.");
		return this.availability[timeStep % availability.length];
	}

	protected void setAvailability(double[] availability) {
		this.availability = availability.clone();
	}

}
