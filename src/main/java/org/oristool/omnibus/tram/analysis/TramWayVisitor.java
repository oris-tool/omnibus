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

import org.oristool.omnibus.tram.TramLine;
import org.oristool.omnibus.tram.pn.PetriNetTramTrack;

/**
 * This is an interface for visitors that do analysis on tramways.
 */
public interface TramWayVisitor {

	/**
	 * The visit method for TramLine objects.
	 * 
	 * @param tramLine
	 */
	public void visit(TramLine tramLine);

	/**
	 * The visit method for PetriNetTramTrack objects.
	 * 
	 * @param petriNetTramWay
	 */
	public void visit(PetriNetTramTrack petriNetTramWay);

}