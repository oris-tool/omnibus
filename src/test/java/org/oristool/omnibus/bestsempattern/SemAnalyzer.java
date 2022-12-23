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