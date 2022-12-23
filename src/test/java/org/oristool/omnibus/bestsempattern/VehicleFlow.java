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
