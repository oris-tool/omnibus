package org.oristool.omnibus.queue;

import org.oristool.math.function.EXP;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * It models a MM1K queue system, according to the Kendall notation.
 */
public class BaseQueue {

	private EXP arrivalDistribution;
	private EXP serviceDistribution;
	private BigInteger size;

	private BigDecimal[] initialDistribution;

	protected BaseQueue() {
	}

	protected BaseQueue(
			EXP arrivalDistribution, EXP serviceDistribution, BigInteger size,
			BigDecimal[] initialDistribution) {
		this.setArrivalDistribution(arrivalDistribution);
		this.setServiceDistribution(serviceDistribution);
		this.setSize(size);
		checkInitialDistributionLegality(initialDistribution);
		this.setInitialDistribution(initialDistribution);
	}

	protected BaseQueue(
			EXP arrivalDistribution, EXP serviceDistribution, BigInteger size,
			BigInteger initialElements) {
		this.setArrivalDistribution(arrivalDistribution);
		this.setServiceDistribution(serviceDistribution);
		this.setSize(size);
		this.setInitialElements(initialElements);
	}

	// Utils

	/**
	 * An API internal util.
	 *
	 * @return a clone of the queue
	 */
	public BaseQueue getClone() {
		return new BaseQueue(arrivalDistribution, serviceDistribution, size, initialDistribution);
	}

	/**
	 * An API internal util.
	 * <p>
	 * It check the legality of the queue.
	 */
	public void checkLegality() {
		if (arrivalDistribution == null)
			throw new IllegalStateException("Distribuzione di arrivo assente.");
		if (serviceDistribution == null)
			throw new IllegalStateException("Distribuzione di servizio assente.");
		if (size == null || size.compareTo(new BigInteger("1")) < 0)
			throw new IllegalStateException("Capienza del sistema assente o inferiore ad uno.");
		if (this.getInitialElements().compareTo(size) > 0)
			throw new IllegalStateException("InitialElements greater than size.");
	}

	/**
	 * An API internal util.
	 * <p>
	 * It check the legality of the initial distribution.
	 */
	private void checkInitialDistributionLegality(BigDecimal[] initialDistribution) {
		if ((size.intValue() + 1) != initialDistribution.length) {
			throw new IllegalArgumentException("initialDistribution must have 'size' plus one elements, it has " + initialDistribution.length + " instead of " + (size.intValue() + 1));
		}
		BigDecimal sum = Arrays.stream(initialDistribution).reduce(BigDecimal.valueOf(0), BigDecimal::add);
		if (Math.abs(sum.doubleValue() - 1.0) > 0.0000000001) {
			throw new IllegalArgumentException("sum of initialDistribution elements must be one, it's " + sum);
		}
	}

	// Getters & Setters

	/**
	 * @return the EXP arrival distribution of the queue
	 */
	public EXP getArrivalDistribution() {
		return arrivalDistribution;
	}

	protected void setArrivalDistribution(EXP arrivalDistribution) {
		this.arrivalDistribution = arrivalDistribution;
	}

	/**
	 * @return the EXP service distribution of the queue
	 */
	public EXP getServiceDistribution() {
		return serviceDistribution;
	}

	protected void setServiceDistribution(EXP serviceDistribution) {
		this.serviceDistribution = serviceDistribution;
	}

	/**
	 * @return the max size of the queue
	 */
	public BigInteger getSize() {
		return size;
	}

	protected void setSize(BigInteger size) {
		this.size = size;
	}

	/**
	 * It returns the nearest integer to the expected initial elements for the initial distribution
	 *
	 * @return the estimated initial elements in the queue, based on distribution
	 */
	public BigInteger getInitialElements() {
		if (this.initialDistribution == null || this.initialDistribution.length == 0)
			throw new NoSuchElementException("No elements on the initial distribution");
		BigDecimal accum = BigDecimal.ZERO;
		for (int c = 0; c < this.initialDistribution.length; c++) {
			accum = accum.add(this.initialDistribution[c].multiply(new BigDecimal(Integer.toString(c))));
		}
		return new BigInteger(accum.setScale(0, RoundingMode.HALF_DOWN).toString());
	}

	/**
	 * A setter for the initial elements of the queue. This set the initial distribution to be all zeros except the
	 * right element, which will be one.
	 *
	 * @param initialElements the number of elements on queue at the start
	 */
	public void setInitialElements(BigInteger initialElements) {
		if (initialElements.compareTo(size) > 0) {
			throw new IllegalArgumentException("initialElements must be lower or equal to the queue size");
		}
		this.initialDistribution = new BigDecimal[this.size.intValue() + 1];
		for (int i = 0; i < this.initialDistribution.length; i++) {
			this.initialDistribution[i] =
					initialElements.equals(new BigInteger(Integer.toString(i))) ? BigDecimal.ONE : BigDecimal.ZERO;
		}
	}

	/**
	 * @return the initial distribution of elements
	 */
	public BigDecimal[] getInitialDistribution() {
		return initialDistribution;
	}

	/**
	 * A setter for the initial distribution of the queue.
	 *
	 * @param initialDistribution the distribution of the elements on queue at the start
	 */
	public void setInitialDistribution(BigDecimal[] initialDistribution) {
		this.checkInitialDistributionLegality(initialDistribution);
		this.initialDistribution = initialDistribution;
	}

	@Override
	public String toString() {
		return "BaseQueue [arrivalDistribution=" + arrivalDistribution + ", serviceDistribution=" + serviceDistribution
				+ ", size=" + size + ", estimatedInitialElements=" + getInitialElements() + "]";
	}

}
