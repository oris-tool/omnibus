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

import org.oristool.omnibus.crossroad.CarFlow;
import org.oristool.omnibus.crossroad.CarSemaphore;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public abstract class SemAnalyzer implements Callable<Double> {

    protected BlockingQueue<String> patternsToAnalyze;
    protected BigDecimal timeStep;
    protected CarSemaphore[] carSemaphores;
    protected CarFlow[] carFlows;

    protected String bestPattern;

    public void setPatternsToAnalyze(BlockingQueue<String> patternsToAnalyze) {
        this.patternsToAnalyze = patternsToAnalyze;
    }

    public void setTimeStep(BigDecimal timeStep) {
        this.timeStep = timeStep;
    }

    public SemAnalyzer setCarFlows(CarFlow... carFlows) {
        this.carFlows = new CarFlow[carFlows.length];
        this.carSemaphores = new CarSemaphore[carFlows.length];
        for (int i = 0; i < carFlows.length; i++) {
            this.carFlows[i] = carFlows[i].getClone();
            this.carSemaphores[i] = (CarSemaphore) this.carFlows[i].getObstacles().stream()
                    .filter(o -> o instanceof CarSemaphore).collect(Collectors.toList()).get(0);
        }
        return this;
    }

    public String getBestPattern() {
        return bestPattern;
    }
}