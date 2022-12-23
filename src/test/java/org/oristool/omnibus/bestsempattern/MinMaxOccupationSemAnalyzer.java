package org.oristool.omnibus.bestsempattern;

import au.com.bytecode.opencsv.CSVWriter;
import org.oristool.omnibus.crossroad.CarFlow;
import org.oristool.omnibus.crossroad.analysis.DTMCSteadyStateAnalyzer;
import org.oristool.omnibus.queue.analysis.MMSS_QueueAnalyzer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MinMaxOccupationSemAnalyzer extends SemAnalyzer {

    private double bestPatternOccupationPercentage = Double.MAX_VALUE;
    private ReentrantLock resultListWriterLock;
    private CSVWriter resultListWriter;

    @Override
    public Double call() {
        String pattern;
        while (!(pattern = patternsToAnalyze.poll()).equals("%%%%")) {
            Utils.assignGreen(pattern, carSemaphores);

            double maxOccupationPercentage = Double.MIN_VALUE;
            for (int i = 0; i < carFlows.length; i++) {
                double w = getMaxOccupationPercentage(carFlows[i]);
                maxOccupationPercentage = Math.max(maxOccupationPercentage, w);
            }

            resultListWriterLock.lock();
            resultListWriter.writeNext(List.of(
                    pattern, Double.toString(maxOccupationPercentage)
            ).toArray(new String[1]));
            resultListWriterLock.unlock();

            if (maxOccupationPercentage < this.bestPatternOccupationPercentage) {
                this.bestPattern = pattern;
                this.bestPatternOccupationPercentage = maxOccupationPercentage;
            }
            System.out.print("\b");
        }
        return bestPatternOccupationPercentage;
    }

    public double getMaxOccupationPercentage(CarFlow carFlow) {
        double[] steadyStateDistribution = carFlow.analyzeSteadyStateDistribution(new DTMCSteadyStateAnalyzer(), new MMSS_QueueAnalyzer(),
                timeStep).getSteadyStateDistribution();

        BigDecimal[] bdDist = Arrays.stream(steadyStateDistribution).mapToObj(BigDecimal::valueOf)
                .collect(Collectors.toList()).toArray(new BigDecimal[0]);

        carFlow.getQueue().setInitialDistribution(bdDist);

        return Arrays.stream(carFlow.analyzeQueue(
                                new MMSS_QueueAnalyzer(),
                                BigInteger.valueOf(carFlow.getObstaclesHyperPeriod()),
                                timeStep)
                        .getExpectedStateAlongTime())
                .max().getAsDouble()
                / carFlow.getQueue().getSize().doubleValue();
    }

    public void setResultListWriter(CSVWriter resultListWriter, ReentrantLock resultListWriterLock) {
        this.resultListWriter = resultListWriter;
        this.resultListWriterLock = resultListWriterLock;
    }
}