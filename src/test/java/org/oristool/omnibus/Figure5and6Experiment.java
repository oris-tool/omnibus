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

package org.oristool.omnibus;

import au.com.bytecode.opencsv.CSVWriter;

import org.oristool.omnibus.intersection.CarFlow;
import org.oristool.omnibus.intersection.analysis.DTMCSteadyStateAnalyzer;
import org.oristool.omnibus.plotter.LineToPlot;
import org.oristool.omnibus.plotter.PlotUtils;
import org.oristool.omnibus.plotter.Plotter;
import org.oristool.omnibus.tram.TramCrossing;
import org.oristool.omnibus.tram.analysis.ParallelGreenProbabilityVisitor;
import org.oristool.omnibus.tram.pn.PetriNetTramTrackBuilder;
import org.oristool.omnibus.utils.CSV_IOUtils;
import org.oristool.omnibus.utils.Config;
import org.oristool.omnibus.utils.QueueAnalyser;
import org.oristool.omnibus.vehicle.BaseQueueBuilder;
import org.oristool.omnibus.vehicle.analysis.MMSS_QueueAnalyzer;
import org.oristool.omnibus.vehicle.analysis.TransientAnalyzer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Figure5and6Experiment {

    public static void main(String[] args) {
        Config.maxVehicleSpeedKmh = BigDecimal.valueOf(50);
        Config.lambda = BigDecimal.valueOf(0.9);
        Config.roadLenght = BigDecimal.valueOf(150);
        Config.analysisHyperperiods = 3;
        Config.cutHyperperiods = 0;
        Config.updateFields();

        // DEFINIZIONE OBSTACLES

        TramCrossing bin1 = new TramCrossing(
                PetriNetTramTrackBuilder.getInstance("bin1",
                        BigInteger.valueOf(220),
                        BigInteger.valueOf(0),
                        BigInteger.ZERO,
                        BigInteger.valueOf(120),
                        BigInteger.valueOf(5),
                        BigInteger.valueOf(6),
                        BigInteger.valueOf(14)));
        bin1.analyze(new ParallelGreenProbabilityVisitor(), Config.timeStep);

        TramCrossing bin2 = new TramCrossing(
                PetriNetTramTrackBuilder.getInstance("bin2",
                        BigInteger.valueOf(220),
                        BigInteger.valueOf(110),
                        BigInteger.ZERO,
                        BigInteger.valueOf(40),
                        BigInteger.valueOf(5),
                        BigInteger.valueOf(6),
                        BigInteger.valueOf(14)));
        bin2.analyze(new ParallelGreenProbabilityVisitor(), Config.timeStep);

        // DEFINIZIONE CODA MMKK

        CarFlow mmkkCarFlow = new CarFlow("diacceto-alamanni");

        mmkkCarFlow.setQueue(BaseQueueBuilder.getInstance(Config.lambda, Config.mu_mmkk, Config.maxQueueSize,
                Config.initialElements));

        mmkkCarFlow.addObstacle(bin1);
        mmkkCarFlow.addObstacle(bin2);

        // DEFINIZIONE CODA MM1K

        CarFlow mm1kCarFlow = new CarFlow("diacceto-alamanni");

        mm1kCarFlow.setQueue(
                BaseQueueBuilder.getInstance(Config.lambda, Config.mu, Config.maxQueueSize, Config.initialElements));

        mm1kCarFlow.addObstacle(bin1);
        mm1kCarFlow.addObstacle(bin2);

        // PRIMA ANALISI CODE

        mmkkCarFlow.getQueue().setInitialElements(BigInteger.ZERO);
        double[] mmkkExpectedCarsAlongTimeTransient = QueueAnalyser.makeAnalysis(mmkkCarFlow, new MMSS_QueueAnalyzer(), "MMKK");
        mmkkExpectedCarsAlongTimeTransient = Arrays.copyOfRange(mmkkExpectedCarsAlongTimeTransient, 0, mmkkExpectedCarsAlongTimeTransient.length);

        mm1kCarFlow.getQueue().setInitialElements(BigInteger.ZERO);
        double[] mm1kExpectedCarsAlongTimeTransient = QueueAnalyser.makeAnalysis(mm1kCarFlow, new TransientAnalyzer(), "MM1K");
        mm1kExpectedCarsAlongTimeTransient = Arrays.copyOfRange(mm1kExpectedCarsAlongTimeTransient, 0, mm1kExpectedCarsAlongTimeTransient.length);

        ArrayList<LineToPlot> linesToPlot1 = new ArrayList<>();

        linesToPlot1.add(new LineToPlot("MMKK Expectation",
                PlotUtils.getLinSpace(0.,
                        Config.timeBound.subtract(Config.cutTimeBound).doubleValue(),
                        Config.timeStep.doubleValue(), false),
                mmkkExpectedCarsAlongTimeTransient));

        linesToPlot1.add(new LineToPlot("MM1K Expectation",
                PlotUtils.getLinSpace(0.,
                        Config.timeBound.subtract(Config.cutTimeBound).doubleValue(),
                        Config.timeStep.doubleValue(), false),
                mm1kExpectedCarsAlongTimeTransient));

        Plotter.plot("Expected size from empty queue", "s", "cars", linesToPlot1, true, null, Config.plotResults);

        // CALCOLO DEGLI STEADY STATE

        mmkkCarFlow.analyzeSteadyStateDistribution(new DTMCSteadyStateAnalyzer(), new MMSS_QueueAnalyzer(),
                Config.timeStep);

        mm1kCarFlow.analyzeSteadyStateDistribution(new DTMCSteadyStateAnalyzer(), new TransientAnalyzer(),
                Config.timeStep);

        double[] mmkkSteadyStateDistributionD = mmkkCarFlow.getSteadyStateDistribution();
        double[] mm1kSteadyStateDistributionD = mm1kCarFlow.getSteadyStateDistribution();

        BigDecimal[] mmkkSteadyStateDistribution = new BigDecimal[mmkkSteadyStateDistributionD.length];
        BigDecimal[] mm1kSteadyStateDistribution = new BigDecimal[mm1kSteadyStateDistributionD.length];

        for (int i = 0; i < mmkkSteadyStateDistributionD.length; i++) {
            mmkkSteadyStateDistribution[i] = BigDecimal.valueOf(mmkkSteadyStateDistributionD[i]);
            mm1kSteadyStateDistribution[i] = BigDecimal.valueOf(mm1kSteadyStateDistributionD[i]);
        }

        Plotter.plotBar("MmkkSteadyStateDistribution", "MmkkSteadyStateDistribution", "cars", "probability",
                PlotUtils.getLinSpace(0, mmkkSteadyStateDistribution.length, 1, false),
                mmkkSteadyStateDistributionD,
                true, null, true);

        Plotter.plotBar("Mm1kSteadyStateDistribution", "Mm1kSteadyStateDistribution", "cars", "probability",
                PlotUtils.getLinSpace(0, mm1kSteadyStateDistribution.length, 1, false),
                mm1kSteadyStateDistributionD,
                true, null, true);

        // CALCOLO DEL TIMEBOUND

        final int mmkkHyperPeriod = mmkkCarFlow.getObstaclesHyperPeriod();
        final int mm1kHyperPeriod = mm1kCarFlow.getObstaclesHyperPeriod();

        if (mmkkHyperPeriod != mm1kHyperPeriod)
            throw new IllegalArgumentException("Queues has different hyper-periods.");

        Config.timeBound = BigInteger.valueOf(mmkkHyperPeriod * Config.analysisHyperperiods);
        Config.cutTimeBound = BigInteger.valueOf(mmkkHyperPeriod * Config.cutHyperperiods);
        int cutStep = new BigDecimal(Config.cutTimeBound).divide(Config.timeStep, 0, RoundingMode.FLOOR).intValue();

        // SECONDA ANALISI CODE

        mmkkCarFlow.getQueue().setInitialDistribution(mmkkSteadyStateDistribution);
        double[] mmkkExpectedCarsAlongTimeSteadyState = QueueAnalyser.makeAnalysis(mmkkCarFlow, new MMSS_QueueAnalyzer(), "MMKK");
        mmkkExpectedCarsAlongTimeSteadyState = Arrays.copyOfRange(mmkkExpectedCarsAlongTimeSteadyState, cutStep,
                mmkkExpectedCarsAlongTimeSteadyState.length);

        mm1kCarFlow.getQueue().setInitialDistribution(mm1kSteadyStateDistribution);
        double[] mm1kExpectedCarsAlongTimeSteadyState = QueueAnalyser.makeAnalysis(mm1kCarFlow, new TransientAnalyzer(), "MM1K");
        mm1kExpectedCarsAlongTimeSteadyState = Arrays.copyOfRange(mm1kExpectedCarsAlongTimeSteadyState, cutStep,
                mm1kExpectedCarsAlongTimeSteadyState.length);


        // PLOT

        ArrayList<LineToPlot> linesToPlot2 = new ArrayList<>();

        linesToPlot2.add(new LineToPlot("MMKK Expectation",
                PlotUtils.getLinSpace(0.,
                        Config.timeBound.subtract(Config.cutTimeBound).doubleValue(),
                        Config.timeStep.doubleValue(), false),
                mmkkExpectedCarsAlongTimeSteadyState));

        linesToPlot2.add(new LineToPlot("MM1K Expectation",
                PlotUtils.getLinSpace(0.,
                        Config.timeBound.subtract(Config.cutTimeBound).doubleValue(),
                        Config.timeStep.doubleValue(), false),
                mm1kExpectedCarsAlongTimeSteadyState));

        Plotter.plot(
                "Expected size from steady state",
                "s", "cars", linesToPlot2, true, null, Config.plotResults);


        // WRITE DATA

        String fileName = "results/f5_road_lenght= " + Config.roadLenght + "m lambda= " + Config.lambda +
                " maxVehicleSpeedKmh= " + Config.maxVehicleSpeedKmh + "kmph _ data";

        CSVWriter carsDataWriter = CSV_IOUtils.getWriterInstance(fileName);

        ArrayList<String> cdw_indices = new ArrayList<>();
        cdw_indices.add("Timestep (" + Config.timeStep.toString() + "s)");
        cdw_indices.add("MM1K Expected Cars");
        cdw_indices.add("MMKK Expected Cars");
        CSV_IOUtils.writeLine(carsDataWriter, cdw_indices);

        for (int ts = 0; ts < mm1kExpectedCarsAlongTimeSteadyState.length; ts++) {
            ArrayList<String> entries = new ArrayList<>();
            entries.add(String.valueOf(ts));
            entries.add(String.valueOf(mm1kExpectedCarsAlongTimeSteadyState[ts]));
            entries.add(String.valueOf(mmkkExpectedCarsAlongTimeSteadyState[ts]));
            CSV_IOUtils.writeLine(carsDataWriter, entries);
        }
        
        
        String fileNameEmpty = "results/from-empty-f5_road_lenght= " + Config.roadLenght + "m lambda= " + Config.lambda +
                " maxVehicleSpeedKmh= " + Config.maxVehicleSpeedKmh + "kmph _ data";

        CSVWriter emptyCarsDataWriter = CSV_IOUtils.getWriterInstance(fileNameEmpty);

        CSV_IOUtils.writeLine(emptyCarsDataWriter, cdw_indices);
        
        for (int ts = 0; ts < mm1kExpectedCarsAlongTimeTransient.length; ts++) {
            ArrayList<String> entries = new ArrayList<>();
            entries.add(String.valueOf(ts));
            entries.add(String.valueOf(mm1kExpectedCarsAlongTimeTransient[ts]));
            entries.add(String.valueOf(mmkkExpectedCarsAlongTimeTransient[ts]));
            CSV_IOUtils.writeLine(emptyCarsDataWriter, entries);
        }

        try {
            Objects.requireNonNull(carsDataWriter).close();
            Objects.requireNonNull(emptyCarsDataWriter).close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVWriter dataWriter = CSV_IOUtils.getWriterInstance("results/SteadyStateDistributionsData");

        ArrayList<String> indices = new ArrayList<>();
        indices.add("Cars");
        indices.add("MM1K SteadyStateProbability");
        indices.add("MMKK SteadyStateProbability");
        CSV_IOUtils.writeLine(dataWriter, indices);

        for (int i = 0; i < mmkkSteadyStateDistribution.length; i++) {
            ArrayList<String> entries = new ArrayList<>();
            entries.add(String.valueOf(i));
            entries.add(String.valueOf(mm1kSteadyStateDistribution[i]));
            entries.add(String.valueOf(mmkkSteadyStateDistribution[i]));
            CSV_IOUtils.writeLine(dataWriter, entries);
        }

        CSV_IOUtils.closeWriter(dataWriter);
    }

}
