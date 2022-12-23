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

public class Table3PatternComparator {

    private static List<Integer> semSlotDurations = Arrays.asList(15, 25, 35);
    private static int redTime = 5;

    public static void main(String[] args) throws IOException {

        ScenarioDefiner.roadLenghts = Arrays.asList(
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(150)
        );

        ScenarioDefiner.arrivalRates = Arrays.asList(
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(0.2),
                BigDecimal.valueOf(0.3)
        );

        ScenarioDefiner.maxVehicleSpeedsKmh = Arrays.asList(
                new BigDecimal("50"),
                new BigDecimal("50"),
                new BigDecimal("50")
        );

        Date start = new Date();

        File resultsFolder = new File("results");
        if (!resultsFolder.exists())
            resultsFolder.mkdir();

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String executionFolderName = dateFormat.format(new Date()) + "_omnibus";
        File executionFolder = new File("results/" + executionFolderName);
        executionFolder.mkdir();

        CSVWriter resultListWriter = new CSVWriter(
                new FileWriter("results/" + executionFolderName + "/" + executionFolderName + ".csv"), '\t'
        );
        ReentrantLock resultListWriterLock = new ReentrantLock();

        // END

        CarFlow[] carFlows = ScenarioDefiner.createScenario();

        int threadPoolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        double bestOccupationPercentage = Double.MAX_VALUE;
        String bestPattern = null;

        List<VehicleFlow> vehicleFlows = new ArrayList<>();
        for (int i = 0; i < carFlows.length; i++) {
            vehicleFlows.add(new VehicleFlow(Integer.toString(i), semSlotDurations));
        }

        BlockingQueue<String> patternsToAnalyze = new LinkedBlockingQueue<>();
        SemPatternGenerator
                .generateAllPatternWithGreenSlotSets(vehicleFlows, ScenarioDefiner.semPeriod.intValue(), redTime)
                .forEach(sp -> patternsToAnalyze.add(sp.getSchedule()));
        for (int i = 0; i < threadPoolSize; i++) {
            patternsToAnalyze.add("%%%%");
        }

        System.out.println((patternsToAnalyze.size() - threadPoolSize) + " patterns to analyze");
        for (int i = 0; i < (patternsToAnalyze.size() - threadPoolSize); i++) {
            System.out.print("x");
        }
        List<Callable<Double>> semAnalyzers = new ArrayList<>();
        List<Future<Double>> futures = new ArrayList<>();
        for (int i = 0; i < threadPoolSize; i++) {
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
            for (int i = 0; i < futures.size(); i++) {
                Double maxWeightedIntegral = futures.get(i).get();
                if (maxWeightedIntegral < bestOccupationPercentage) {
                    bestOccupationPercentage = maxWeightedIntegral;
                    bestPattern = ((MinMaxOccupationSemAnalyzer) semAnalyzers.get(i)).getBestPattern();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Best reward: " + bestOccupationPercentage);
        System.out.println(bestPattern);

        resultListWriter.close();

        Date end = new Date();

        System.out.println("Duration = " + (end.getTime() - start.getTime()) + " ms");

        System.exit(0);
    }
}