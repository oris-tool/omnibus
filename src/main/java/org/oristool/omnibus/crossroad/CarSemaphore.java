package org.oristool.omnibus.crossroad;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * This class is a specification of an Obstacle. It models the normal cycle of a
 * traffic light for a cars queue. The semaphore is always red by default.
 */
public class CarSemaphore extends Obstacle {

	private final BigInteger period;
	private final BigDecimal timeStep;

	/**
	 * The constructor. The semaphore is always red by default.
	 *
	 * @param period   the period, in seconds, of the entire semaphore cycle
	 * @param timeStep the temporal resolution to which semaphore can change colour,
	 *                 it should be the same of the analysis of the queues
	 */
	public CarSemaphore(BigInteger period, BigDecimal timeStep) {
		this.period = period;
		this.timeStep = timeStep;
		this.availability = new double[new BigDecimal(period).divide(timeStep, 0, RoundingMode.FLOOR).intValue()];
		Arrays.fill(this.availability, 0.);
	}

	// Utils

	/**
	 * This set the traffic light to be green at certain time.
	 *
	 * @param start the second at which the traffic light starts to be green
	 * @param end   the second at which the traffic light will turn red
	 */
	public void setGreen(BigInteger start, BigInteger end) {
		checkStartEnd(start,end);

		int startStep = new BigDecimal(start).divide(timeStep, 0, RoundingMode.FLOOR).intValue();
		int endStep = Math.min(this.availability.length, new BigDecimal(end).divide(timeStep, 0, RoundingMode.FLOOR).intValue());
		for (int i = startStep; i < endStep; i++) {
			this.availability[i] = 1.;
		}
	}

	/**
	 * This set the traffic light to be red at certain time.
	 *
	 * @param start the second at which the traffic light starts to be red
	 * @param end   the second at which the traffic light will turn green
	 */
	public void setRed(BigInteger start, BigInteger end) {
		if (start.compareTo(BigInteger.ZERO) < 0)
			throw new IllegalArgumentException("start must be at least zero.");
		if (end.compareTo(start) <= 0)
			throw new IllegalArgumentException("end must be greater then start.");
		if (end.compareTo(period) > 0)
			throw new IllegalArgumentException("end must be lower then period, at most equal.");

		int startStep = new BigDecimal(start).divide(timeStep, 0, RoundingMode.FLOOR).intValue();
		int endStep = Math.min(this.availability.length, new BigDecimal(end).divide(timeStep, 0, RoundingMode.FLOOR).intValue());
		for (int i = startStep; i < endStep; i++) {
			this.availability[i] = 0.;
		}
	}

	/**
	 * This set the traffic light to be green at certain temporal steps.
	 *
	 * @param startStep the temporal step at which the traffic light starts to be
	 *                  green
	 * @param endStep   the temporal step at which the traffic light will turn red
	 */
	public void setGreen(int startStep, int endStep) {
		if (startStep < 0)
			throw new IllegalArgumentException("startStep must be at least zero.");
		if (endStep <= startStep)
			throw new IllegalArgumentException("endStep must be greater then startStep.");
		if (endStep > this.availability.length)
			throw new IllegalArgumentException("endStep must be lower then periodSteps, at most equal.");
		for (int i = startStep; i < endStep; i++) {
			this.availability[i] = 1.;
		}
	}

	/**
	 * This set the traffic light to be red at certain temporal steps.
	 *
	 * @param startStep the temporal step at which the traffic light starts to be
	 *                  red
	 * @param endStep   the temporal step at which the traffic light will turn green
	 */
	public void setRed(int startStep, int endStep) {
		if (startStep < 0)
			throw new IllegalArgumentException("startStep must be at least zero.");
		if (endStep <= startStep)
			throw new IllegalArgumentException("endStep must be greater then startStep.");
		if (endStep > this.availability.length)
			throw new IllegalArgumentException("endStep must be lower then periodSteps, at most equal.");
		for (int i = startStep; i < endStep; i++) {
			this.availability[i] = 0.;
		}
	}

	private void checkStartEnd(BigInteger start, BigInteger end) {
		if (start.compareTo(BigInteger.ZERO) < 0)
			throw new IllegalArgumentException("start must be at least zero.");
		if (end.compareTo(start) <= 0)
			throw new IllegalArgumentException("end must be greater then start.");
		if (end.compareTo(period) > 0)
			throw new IllegalArgumentException("end must be lower then period, at most equal.");
	}

	@Override
	public Obstacle getClone() {
		CarSemaphore clone = new CarSemaphore(this.period, this.timeStep);
		clone.setAvailability(this.availability);
		return clone;
	}

	@Override
	public double getAvailability(int timeStep) {
		return super.getAvailabilityStandard(timeStep);
	}

	@Override
	public BigInteger getPeriod() {
		return period;
	}

	@Override
	public String toString() {
		return "CarSemaphore [period=" + period + ", timeStep=" + timeStep + ", availability="
				+ Arrays.toString(availability) + "]";
	}

}
