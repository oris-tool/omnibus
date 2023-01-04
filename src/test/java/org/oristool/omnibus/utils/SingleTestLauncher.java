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

package org.oristool.omnibus.utils;

import au.com.bytecode.opencsv.CSVWriter;
import org.oristool.omnibus.crossroad.CarFlow;
import org.oristool.omnibus.plotter.LineToPlot;
import org.oristool.omnibus.plotter.PlotUtils;
import org.oristool.omnibus.plotter.Plotter;
import org.oristool.omnibus.queue.BaseQueueBuilder;
import org.oristool.omnibus.queue.analysis.MMSS_QueueAnalyzer;
import org.oristool.omnibus.queue.analysis.QueueAnalyzer;
import org.oristool.omnibus.tramway.TramCrossing;
import org.oristool.omnibus.tramway.analysis.ParallelGreenProbabilityVisitor;
import org.oristool.omnibus.tramway.pn.PetriNetTramTrackBuilder;
import org.oristool.omnibus.utils.Config;
import org.oristool.omnibus.utils.QueueAnalyser;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SingleTestLauncher {

    public static void main(String[] args) {
        CSVWriter writer = null;
        if (Config.generateCSV) {
            String fileName =
                    "results/omnibus_" + Config.roadLenght + "_" + Config.lambda + "_" + Config.maxVehicleSpeedKmh;
            try {
                writer = new CSVWriter(new FileWriter(fileName + ".csv"), '\t');
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        Config.updateFields();
        executeTest(writer);
        if (Config.generateCSV)
            try {
                Objects.requireNonNull(writer).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void executeTest(CSVWriter writer) {

        System.out.println("Test Launch \n"
                + "roadLength = " + Config.roadLenght + "m \t"
                + "maxQueueSize = " + Config.maxQueueSize + "\t"
                + "maxVehicleSpeed = " + Config.maxVehicleSpeedKmh + "km/h \n"
                + "lambda = " + Config.lambda + "\t"
                + "mu = " + Config.mu.setScale(3, RoundingMode.HALF_DOWN) + " \t"
                + "mu_mmkk = " + Config.mu_mmkk.setScale(3, RoundingMode.HALF_DOWN));

        // DEFINIZIONE OBSTACLES

        TramCrossing bin1 = new TramCrossing(
                PetriNetTramTrackBuilder.getInstance("bin1", Config.periodTime, Config.phaseTime, Config.delayEFTime,
                        Config.delayLFTime, Config.crosslightAntTime,
                        Config.leavingEFTime, Config.leavingLFTime));
        bin1.analyze(new ParallelGreenProbabilityVisitor(), Config.timeStep);

        TramCrossing bin2 = new TramCrossing(
                PetriNetTramTrackBuilder.getInstance("bin2", Config.periodTime2, Config.phaseTime2,
                        Config.delayEFTime2,
                        Config.delayLFTime2, Config.crosslightAntTime2,
                        Config.leavingEFTime2, Config.leavingLFTime2));
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

        // CALCOLO DEL TIMEBOUND

        final int mmkkHyperPeriod = mmkkCarFlow.getObstaclesHyperPeriod();
        final int mm1kHyperPeriod = mm1kCarFlow.getObstaclesHyperPeriod();

        if (mmkkHyperPeriod != mm1kHyperPeriod)
            throw new IllegalArgumentException("Queues has different hyper-periods.");

        Config.timeBound = BigInteger.valueOf((long) mmkkHyperPeriod * Config.analysisHyperperiods);
        Config.cutTimeBound = BigInteger.valueOf((long) mmkkHyperPeriod * Config.cutHyperperiods);
        int cutStep = new BigDecimal(Config.cutTimeBound).divide(Config.timeStep, 0, RoundingMode.FLOOR).intValue();

        System.out.println("Timebound = "
                + mmkkHyperPeriod + " * " + Config.analysisHyperperiods + " = "
                + Config.timeBound + "\n");

        // ANALISI CODE

        double[] mmkkExpectedCarsAlongTime = QueueAnalyser.makeAnalysis(mmkkCarFlow, new MMSS_QueueAnalyzer(), "MMKK");
        mmkkExpectedCarsAlongTime = Arrays.copyOfRange(mmkkExpectedCarsAlongTime, cutStep,
                mmkkExpectedCarsAlongTime.length);

        double[] mm1kExpectedCarsAlongTime = QueueAnalyser.makeAnalysis(mm1kCarFlow, new QueueAnalyzer(), "MM1K");
        mm1kExpectedCarsAlongTime = Arrays.copyOfRange(mm1kExpectedCarsAlongTime, cutStep,
                mm1kExpectedCarsAlongTime.length);

        double[] intersectionAvailability = mmkkCarFlow
                .getIntersectionAvailability((int) (Config.timeBound.doubleValue() / Config.timeStep.doubleValue()));
        intersectionAvailability = Arrays.copyOfRange(intersectionAvailability, cutStep,
                intersectionAvailability.length);

        // SAVE DATA

        String[] strings = new String[mm1kExpectedCarsAlongTime.length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = Double.toString(intersectionAvailability[i]);
        }
        writer.writeNext(strings);
        for (int i = 0; i < strings.length; i++) {
            strings[i] = Double.toString(mm1kExpectedCarsAlongTime[i]);
        }
        writer.writeNext(strings);
        for (int i = 0; i < strings.length; i++) {
            strings[i] = Double.toString(mmkkExpectedCarsAlongTime[i]);
        }
        writer.writeNext(strings);

        // SAVE PLOTS

        ArrayList<LineToPlot> linesToPlot = new ArrayList<>();

        double[] linSpace = PlotUtils.getLinSpace(0.,
                Config.timeBound.subtract(Config.cutTimeBound).doubleValue(),
                Config.timeStep.doubleValue(), false);

        linesToPlot.add(new LineToPlot("Intersection Availability", linSpace, intersectionAvailability));
        linesToPlot.add(new LineToPlot("MM1K Expectation", linSpace, mm1kExpectedCarsAlongTime));
        linesToPlot.add(new LineToPlot("MMKK Expectation", linSpace, mmkkExpectedCarsAlongTime));

        Plotter.plot(
                "omnibus_" + Config.roadLenght + "_" + Config.lambda + "_" + Config.maxVehicleSpeedKmh,
                "s", "cars", linesToPlot, Config.saveAsPNG, null, Config.plotResults);
    }
}
