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

import java.math.BigInteger;

/**
 * This builder is the only way to get instances of (Basic)PetriNetTramTrack
 */
public class PetriNetTramTrackBuilder {

	/**
	 * This returns a BasicPetriNetTramTrack instance with some default values.
	 * 
	 * @param name the name of the net
	 * @return an instance of BasicPetriNetTramTrack
	 */
	public static PetriNetTramTrack getDefaultInstance(String name) {
		checkName(name);
		return new BasicPetriNetTramTrack(name);
	}

	/**
	 * This returns a BasicPetriNetTramTrack instance with some default values and a
	 * specified phase.
	 * 
	 * @param name  the name of the net
	 * @param phase the wanted phase
	 * @return an instance of BasicPetriNetTramTrack
	 */
	public static PetriNetTramTrack getDefaultInstanceWithPhase(String name, BigInteger phase) {
		checkPositivity(phase);
		BasicPetriNetTramTrack t1 = new BasicPetriNetTramTrack(name);
		t1.setPhaseTime(phase);
		return t1;
	}

	/**
	 * This returns a BasicPetriNetTramTrack instance with all parameters specified.
	 * 
	 * @param name
	 * @param periodTime
	 * @param phaseTime
	 * @param delayEFTime
	 * @param delayLFTime
	 * @param crosslightAntTime
	 * @param leavingEFTime
	 * @param leavingLFTime
	 * @return an instance of BasicPetriNetTramTrack
	 */
	public static PetriNetTramTrack getInstance(String name, BigInteger periodTime, BigInteger phaseTime,
			BigInteger delayEFTime, BigInteger delayLFTime, BigInteger crosslightAntTime, BigInteger leavingEFTime,
			BigInteger leavingLFTime) {
		checkName(name);
		checkPositivity(periodTime, phaseTime, delayEFTime, delayLFTime, crosslightAntTime, leavingEFTime,
				leavingLFTime);
		if (delayEFTime.compareTo(delayLFTime) > 0)
			throw new IllegalArgumentException("delayLFTime must be greater than delayEFTime");
		if (leavingEFTime.compareTo(leavingLFTime) > 0)
			throw new IllegalArgumentException("leavingLFTime must be greater than leavingEFTime");
		return new BasicPetriNetTramTrack(name, periodTime, phaseTime, delayEFTime, delayLFTime,
		                                  crosslightAntTime, leavingEFTime, leavingLFTime);
	}

	private static void checkPositivity(BigInteger... xa) {
		for (BigInteger x : xa) {
			if (x.compareTo(new BigInteger("0")) < 0)
				throw new IllegalArgumentException("All arguments must be positive.");
		}
	}

	private static void checkName(String name) {
		if (name.contains(" "))
			throw new IllegalArgumentException("Names must be without spaces.");
	}

}
