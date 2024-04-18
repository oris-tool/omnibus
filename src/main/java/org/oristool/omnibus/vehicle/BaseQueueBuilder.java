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

package org.oristool.omnibus.vehicle;

import org.oristool.math.expression.Variable;
import org.oristool.math.function.EXP;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This builder is the only way to obtain instances of BaseQueue.
 */
public class BaseQueueBuilder {

	private static BigDecimal LAMBDA = new BigDecimal("1.05");
	private static BigDecimal MU = new BigDecimal("1.20");

	/**
	 * This returns a default instances of a BaseQueue.
	 * 
	 * @return an instance of BaseQueue
	 */
	public static BaseQueue getDefaultInstance() {
		BaseQueue baseQueue = new BaseQueue();
		baseQueue.setArrivalDistribution(new EXP(Variable.X, LAMBDA));
		baseQueue.setServiceDistribution(new EXP(Variable.X, MU));
		baseQueue.setSize(new BigInteger("41"));
		baseQueue.setInitialElements(new BigInteger("7"));
		return baseQueue;
	}

	/**
	 * This returns an instance of BaseQueue with given parameters.
	 * 
	 * @param lambda          the arrival exponential distribution
	 * @param mu              the service exponential distribution
	 * @param size            the maximum number of elements in the queue
	 * @param initialElements the number of initial elements in the queue
	 * @return an instance of BaseQueue
	 */
	public static BaseQueue getInstance(BigDecimal lambda, BigDecimal mu, BigInteger size, BigInteger initialElements) {
		BaseQueue baseQueue = new BaseQueue();
		baseQueue.setArrivalDistribution(new EXP(Variable.X, lambda));
		baseQueue.setServiceDistribution(new EXP(Variable.X, mu));
		baseQueue.setSize(size);
		baseQueue.setInitialElements(initialElements);
		return baseQueue;
	}

	/**
	 * This returns an instance of BaseQueue with given parameters.
	 *
	 * @param lambda                the arrival exponential distribution
	 * @param mu                    the service exponential distribution
	 * @param size                  the maximum number of elements in the queue
	 * @param initialDistribution   the distribution of initial elements in the queue
	 * @return an instance of BaseQueue
	 */
	public static BaseQueue getInstance(BigDecimal lambda, BigDecimal mu, BigInteger size,
	                                    BigDecimal[] initialDistribution) {
		BaseQueue baseQueue = new BaseQueue();
		baseQueue.setArrivalDistribution(new EXP(Variable.X, lambda));
		baseQueue.setServiceDistribution(new EXP(Variable.X, mu));
		baseQueue.setSize(size);
		baseQueue.setInitialDistribution(initialDistribution);
		return baseQueue;
	}

	/**
	 * This returns an istance of BaseQueue with imposed load factor, based on given mu.
	 * 
	 * @param mu the service exponential distribution
	 * @param loadFactor the wanted load factor
	 * @param size            the maximum number of elements in the queue
	 * @param initialElements the number of initial elements in the queue
	 * @return an instance of BaseQueue
	 */
	public static BaseQueue getInstanceWithImposedLoadFactor(BigDecimal mu, BigDecimal loadFactor, BigInteger size,
			BigInteger initialElements) {
		BaseQueue baseQueue = new BaseQueue();
		baseQueue.setArrivalDistribution(new EXP(Variable.X, mu.multiply(loadFactor)));
		baseQueue.setServiceDistribution(new EXP(Variable.X, mu));
		baseQueue.setSize(size);
		baseQueue.setInitialElements(initialElements);
		return baseQueue;
	}

}
