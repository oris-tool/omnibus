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

package org.oristool.omnibus.bestsempattern;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class VehicleFlow {

	@Override
	public int hashCode() {
		return Objects.hash(greenSlots, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VehicleFlow other = (VehicleFlow) obj;
		return Objects.equals(greenSlots, other.greenSlots) && Objects.equals(id, other.id);
	}

	private String id;
	private List<Integer> greenSlots;

	public VehicleFlow(String id, List<Integer> greenSlots) {
		this.id = id;
		this.greenSlots = greenSlots;
	}
	
	public VehicleFlow getClone() {
		return new VehicleFlow(id, greenSlots);
	}

	public List<Integer> getGreenSlots() {
		return greenSlots;
	}

	public String getId() {
		return id;
	}

	public int getMinimumGreenSlot() {
		return greenSlots.stream().min(Comparator.comparing(Integer::valueOf)).get();
	}

}
