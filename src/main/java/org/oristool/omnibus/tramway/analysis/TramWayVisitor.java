package org.oristool.omnibus.tramway.analysis;

import org.oristool.omnibus.tramway.TramLine;
import org.oristool.omnibus.tramway.pn.PetriNetTramTrack;

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