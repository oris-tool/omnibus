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
