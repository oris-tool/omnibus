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
import org.oristool.omnibus.bestsempattern.MinMaxOccupationSemAnalyzer;
import org.oristool.omnibus.bestsempattern.ScenarioDefiner;
import org.oristool.omnibus.bestsempattern.SemPatternGenerator;
import org.oristool.omnibus.bestsempattern.VehicleFlow;
import org.oristool.omnibus.crossroad.CarFlow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class VaryingParamsPatternComparator {

    private static List<Integer> semSlotDurations = Arrays.asList(15, 25, 35);
    private static int redTime = 5;

    public static void main(String[] args) throws IOException {

        File resultsFolder = new File("results");
        if (!resultsFolder.exists())
            resultsFolder.mkdir();

        ScenarioDefiner.roadLenghts = Arrays.asList(
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(150)
        );

        ScenarioDefiner.maxVehicleSpeedsKmh = Arrays.asList(
                new BigDecimal("50"),
                new BigDecimal("50"),
                new BigDecimal("50")
        );

        List<List<BigDecimal>> varyingRates = List.of(
                Arrays.asList(
                        BigDecimal.valueOf(0.025),
                        BigDecimal.valueOf(0.025),
                        BigDecimal.valueOf(0.025)),
                Arrays.asList(
                        BigDecimal.valueOf(0.1),
                        BigDecimal.valueOf(0.2),
                        BigDecimal.valueOf(0.3)),
                Arrays.asList(
                        BigDecimal.valueOf(0.05),
                        BigDecimal.valueOf(0.1),
                        BigDecimal.valueOf(0.15)),
                Arrays.asList(
                        BigDecimal.valueOf(0.75),
                        BigDecimal.valueOf(0.15),
                        BigDecimal.valueOf(0.2)));

        Date start = new Date();

        // START - CREAZIONE CARTELLA RISULTATI E FILE LISTA PATTERN

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String executionFolderName = dateFormat.format(new Date()) + "_varyingParameters";
        File executionFolder = new File("results/" + executionFolderName);
        executionFolder.mkdir();

        for (int i = 0; i < varyingRates.size(); i++) {
            ScenarioDefiner.arrivalRates = varyingRates.get(i);

            CSVWriter resultListWriter = new CSVWriter(
                    new FileWriter("results/" + executionFolderName + "/rates_" + i + ".csv"), '\t'
            );
            ReentrantLock resultListWriterLock = new ReentrantLock();

            // END

            CarFlow[] carFlows = ScenarioDefiner.createScenario();

            int threadPoolSize = Runtime.getRuntime().availableProcessors();
            ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

            double bestOccupationPercentage = Double.MAX_VALUE;
            String bestPattern = null;

            List<VehicleFlow> vehicleFlows = new ArrayList<>();
            for (int j = 0; j < carFlows.length; j++) {
                vehicleFlows.add(new VehicleFlow(Integer.toString(j), semSlotDurations));
            }

            BlockingQueue<String> patternsToAnalyze = new LinkedBlockingQueue<>();
            SemPatternGenerator
                    .generateAllPatternWithGreenSlotSets(vehicleFlows, ScenarioDefiner.semPeriod.intValue(), redTime)
                    .forEach(sp -> patternsToAnalyze.add(sp.getSchedule()));
            for (int j = 0; j < threadPoolSize; j++) {
                patternsToAnalyze.add("%%%%");
            }

            System.out.println((patternsToAnalyze.size() - threadPoolSize) + " patterns to analyze");
            for (int j = 0; j < (patternsToAnalyze.size() - threadPoolSize); j++) {
                System.out.print("x");
            }
            List<Callable<Double>> semAnalyzers = new ArrayList<>();
            List<Future<Double>> futures = new ArrayList<>();
            for (int j = 0; j < threadPoolSize; j++) {
                MinMaxOccupationSemAnalyzer semAnalyzer = new MinMaxOccupationSemAnalyzer();
                semAnalyzer.setResultListWriter(resultListWriter, resultListWriterLock);
                semAnalyzer.setCarFlows(carFlows);
                semAnalyzer.setTimeStep(ScenarioDefiner.timeStep);
                semAnalyzer.setPatternsToAnalyze(patternsToAnalyze);
                semAnalyzers.add(semAnalyzer);
                futures.add(executorService.submit(semAnalyzer));
            }

            // ATTESA ESECUZIONE ANALISI PATTERN E RACCOLTA RISULTATI
            try {
                for (int j = 0; j < futures.size(); j++) {
                    Double maxWeightedIntegral = futures.get(j).get();
                    if (maxWeightedIntegral < bestOccupationPercentage) {
                        bestOccupationPercentage = maxWeightedIntegral;
                        bestPattern = ((MinMaxOccupationSemAnalyzer) semAnalyzers.get(j)).getBestPattern();
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Best reward: " + bestOccupationPercentage);
            System.out.println(bestPattern);

            resultListWriter.close();

        }

        Date end = new Date();

        System.out.println("Total duration = " + (end.getTime() - start.getTime()) + " ms");

        System.exit(0);
    }
}