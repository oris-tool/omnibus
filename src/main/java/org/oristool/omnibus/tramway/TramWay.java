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
