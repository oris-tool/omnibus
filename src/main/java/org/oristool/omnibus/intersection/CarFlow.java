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

package org.oristool.omnibus.intersection;

import org.oristool.omnibus.intersection.analysis.SteadyStateAnalyzer;
import org.oristool.omnibus.utils.OmnibusMath;
import org.oristool.omnibus.vehicle.BaseQueue;
import org.oristool.omnibus.vehicle.analysis.TransientAnalyzer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * It models a single car flow, consisting of a car queue and a set of obstacle
 * (traffic lights or tram crossing) that cars should overcome.
 * <p>
 * It provides methods to manage obstacles, launch the analysis and get results
 * of interest.
 */
public class CarFlow {

    private String name;
    private BaseQueue queue;
    private List<Obstacle> obstacles;
    private boolean queueAnalyzed;
    private double[][][] stateProbabilitiesAlongTime;
    private int maxDenials;
    private boolean steadyStateAnalyzed;
    private double[] steadyStateDistribution;

    /**
     * The constructor.
     *
     * @param name The name of the flow. Be careful, it's unmodifiable.
     */
    public CarFlow(String name) {
        this.name = name;
        this.obstacles = new ArrayList<>();
        this.queueAnalyzed = false;
        this.steadyStateAnalyzed = false;
    }

    // Utils

    /**
     * It returns a clone object of this CarFlow. Nested objects like Queue and Obstacles are cloned too.
     */
    public CarFlow getClone() {
        CarFlow clone = new CarFlow(this.name);
        clone.setQueue(this.queue.getClone());
        for (Obstacle o : obstacles) {
            clone.addObstacle(o.getClone());
        }
        return clone;
    }

    /**
     * Adds an obstacle to the scenario
     *
     * @param obstacle
     */
    public void addObstacle(Obstacle obstacle) {
        this.obstacles.add(obstacle);
    }

    /**
     * Removes an obstacle from the scenario
     *
     * @param obstacle
     */
    public void removeObstacle(Obstacle obstacle) {
        this.obstacles.remove(obstacle);
    }

    /**
     * It returns the probability along time for which cars have the traffic light
     * set to green.
     *
     * @param steps the number of the time steps of the analysis for which
     *              availability will be returned
     * @return a double array of availabilities
     */
    public double[] getIntersectionAvailability(int steps) {
        double[] intAv = new double[steps];
        for (int i = 0; i < steps; i++) {
            intAv[i] = 1;
            for (Obstacle o : obstacles) {
                intAv[i] = intAv[i] * o.getAvailability(i);
            }
        }
        return intAv;
    }

    /**
     * It starts the analysis of the queue, it computes the state probabilities along
     * time. It assumes that the intersection availability was already computed.
     *
     * @param analyzer  the queue analyzer that will do the analysis
     * @param timeBound the temporal limit until which the analysis will be done
     * @param timeStep  the temporal resolution with which the analysis will be done
     * @return the object itself, to allows iterative calls
     */
    public CarFlow analyzeQueue(TransientAnalyzer analyzer, BigInteger timeBound, BigDecimal timeStep) {
        return analyzeQueueWithDenials(analyzer, 0, timeBound, timeStep);
    }

    /**
     * It starts the analysis of the queue, it computes the state probabilities along
     * time. It assumes that the intersection availability was already computed.
     *
     * @param analyzer   the queue analyzer that will do the analysis
     * @param maxDenials the max number of denials of which compute probabilities
     * @param timeBound  the temporal limit until which the analysis will be done
     * @param timeStep   the temporal resolution with which the analysis will be done
     * @return the object itself, to allows iterative calls
     */
    public CarFlow analyzeQueueWithDenials(TransientAnalyzer analyzer, int maxDenials, BigInteger timeBound, BigDecimal timeStep) {
        isInitialized();

        this.maxDenials = maxDenials;
        this.stateProbabilitiesAlongTime = analyzer
                .analyze(queue, getIntersectionAvailability(new BigDecimal(timeBound).divide(timeStep).intValue()),
                        maxDenials, timeStep.doubleValue())
                .getStateMatrix();
        this.queueAnalyzed = true;

        return this;
    }

    /**
     * It starts the analysis of the steady state of the distribution of the number
     * of cars in the queue at the beginning of every hyper period.
     *
     * @param steadyStateAnalyzer
     * @param queueAnalyzer       the queue analyzer that will do the analysis
     * @param timeStep            the temporal resolution with which the analysis
     *                            will be done
     * @return the object itself, to allows iterative calls
     */
    public CarFlow analyzeSteadyStateDistribution(SteadyStateAnalyzer steadyStateAnalyzer, TransientAnalyzer queueAnalyzer,
                                                  BigDecimal timeStep) {
        isInitialized();

        this.steadyStateDistribution = steadyStateAnalyzer.getSteadyStateDistribution(this, queueAnalyzer, timeStep);
        this.steadyStateAnalyzed = true;

        return this;
    }

    /**
     * Given a temporal index, it returns the distribution of the number of expected
     * cars at that time.
     *
     * @param time the index (in step!) for which the state probabilities will be
     *             returned
     * @return an array of K+1 probabilities
     */
    public double[] getStateProbabilities(int time) {
        checkAnalysis();
        double[] ret = new double[stateProbabilitiesAlongTime[0].length];
        for (int i = 0; i < ret.length; i++) {
            for (int d = 0; d <= maxDenials; d++) {
                ret[i] += stateProbabilitiesAlongTime[time][i][d];
            }
        }
        return ret;
    }

    /**
     * Given a state, it returns the probability along time to have that number of
     * cars in queue.
     *
     * @param state the number of cars whose probability along time we are
     *              interested
     * @return an array of probabilities, the length depends on the analysis
     */
    public double[] getTimeProbabilities(int state) {
        checkAnalysis();
        double[] ret = new double[stateProbabilitiesAlongTime.length];
        for (int t = 0; t < ret.length; t++) {
            for (int d = 0; d <= maxDenials; d++) {
                ret[t] = stateProbabilitiesAlongTime[t][state][d];
            }
        }
        return ret;
    }

    /**
     * Equals to getTimeProbabilities(K). It returns the probability along time to
     * have a full queue.
     *
     * @return an array of probabilities, the length depends on the analysis
     */
    public double[] getBlockProbabilities() {
        checkAnalysis();
        return getTimeProbabilities(queue.getSize().intValue());
    }

    /**
     * It returns the expected state (the expected number of cars in queue) along
     * time.
     *
     * @return an array of values in [0,K], long as the analysis calculates.
     */
    public double[] getExpectedStateAlongTime() {
        checkAnalysis();
        double[] eDimCoda = new double[this.stateProbabilitiesAlongTime.length];
        double accum = 0.0;
        for (int t = 0; t < this.stateProbabilitiesAlongTime.length; t++) {
            accum = 0.0;
            for (int c = 0; c < this.stateProbabilitiesAlongTime[0].length; c++) {
                double accum2 = 0.0;
                for (int d = 0; d <= maxDenials; d++) {
                    accum2 += this.stateProbabilitiesAlongTime[t][c][d];
                }
                accum += accum2 * c;
            }
            eDimCoda[t] = accum;
        }
        return eDimCoda;
    }

    /**
     * It returns the probabilities along time to have at least "k" denials, where k is the input.
     *
     * @param denials the number of denials which we are interested in
     */
    public double[] getDenialsProbabilityAlongTime(int denials) {
        if (denials > maxDenials)
            throw new IllegalArgumentException("denials greater than maxDenials computed");

        double[] denialsProbabilityAlongTime = new double[this.stateProbabilitiesAlongTime.length];
        double accum = 0.0;
        for (int t = 0; t < this.stateProbabilitiesAlongTime.length; t++) {
            accum = 0.0;
            for (int c = 0; c < this.stateProbabilitiesAlongTime[0].length; c++) {
                for (int d = denials; d <= maxDenials; d++) {
                    accum += this.stateProbabilitiesAlongTime[t][c][d];
                }
            }
            denialsProbabilityAlongTime[t] = accum;
        }

        return denialsProbabilityAlongTime;
    }

    /**
     * It returns the steady state of the distribution of the number of cars in the
     * queue at the beginning of every hyper period.
     *
     * @return the array of lenght K+1 computed by the steady state analysis
     */
    public double[] getSteadyStateDistribution() {
        if (!steadyStateAnalyzed) {
            throw new IllegalAccessError(
                    "Steady state not yet computed, please call analyzeSteadyStateDistribution() first.");
        }
        return steadyStateDistribution;
    }

    /*
     * API internal utils
     */

    private void isInitialized() {
        if (queue == null) {
            throw new IllegalAccessError("Uninitialized queues.");
        }
    }

    private void checkAnalysis() {
        isInitialized();
        if (!queueAnalyzed) {
            throw new IllegalAccessError("BaseQueue not yet analyzed. Please, invoke analyze() first.");
        }
    }

    /**
     * An API internal util.
     *
     * @return the hyper period of the periods of the obstacles
     */
    public int getObstaclesHyperPeriod() {
        int hyperPeriod = 1;
        for (Obstacle o : obstacles) {
            hyperPeriod = OmnibusMath.mcm(hyperPeriod, o.getPeriod().intValue());
        }
        return hyperPeriod;
    }

    private void reset() {
        this.queueAnalyzed = false;
        this.stateProbabilitiesAlongTime = null;
        this.steadyStateAnalyzed = false;
        this.steadyStateDistribution = null;
    }

    // Getters & Setters

    /**
     * Ceci n'est pas un javadoc.
     *
     * @return the name of the car flows
     */
    public String getName() {
        return name;
    }

    /**
     * @return the object that represents the queue
     */
    public BaseQueue getQueue() {
        return queue;
    }

    /**
     * That's the setter for the queue. It resets all the analysis to be done.
     *
     * @param queue
     */
    public void setQueue(BaseQueue queue) {
        this.queue = queue;
        reset();
    }

    /**
     * Gli obstacles stanno qui e non direttamente in CrossRoad perché è vero che
     * tutti i flussi auto hanno la tramvia d'intralcio, ma ognuno ha il suo
     * specifico semaforo che rispetta il ciclo semaforico classico in assenza di
     * tram.
     *
     * @return an unmodifiable list of obstacles
     */
    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    @Override
    public String toString() {
        return "CarFlow [name=" + name + ", queue=" + queue + ", obstacles=" + obstacles + ", queueAnalyzed="
                + queueAnalyzed + ", stateProbabilitiesAlongTime=" + Arrays.toString(stateProbabilitiesAlongTime)
                + ", steadyStateAnalyzed=" + steadyStateAnalyzed + ", steadyStateDistribution="
                + Arrays.toString(steadyStateDistribution) + "]";
    }

}
