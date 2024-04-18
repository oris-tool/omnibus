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

import org.oristool.omnibus.intersection.CarFlow;
import org.oristool.omnibus.intersection.CarSemaphore;
import org.oristool.omnibus.tram.TramCrossing;
import org.oristool.omnibus.tram.TramLine;
import org.oristool.omnibus.tram.analysis.ParallelGreenProbabilityVisitor;
import org.oristool.omnibus.tram.pn.PetriNetTramTrack;
import org.oristool.omnibus.tram.pn.PetriNetTramTrackBuilder;
import org.oristool.omnibus.vehicle.BaseQueueBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScenarioDefiner {

    private static final BigDecimal crossroadLenght = new BigDecimal("12.20"); // unmodifiable
    private static BigDecimal carSpace = new BigDecimal("4.8");

    /*
        Info tram 1
     */
    private static String t1_name = "bin1";
    public static BigInteger t1_periodTime = BigInteger.valueOf(220);
    public static BigInteger t1_phaseTime = BigInteger.ZERO;
    public static BigInteger t1_delayEFTime = BigInteger.ZERO;
    public static BigInteger t1_delayLFTime = BigInteger.valueOf(120);
    public static BigInteger t1_crosslightAntTime = BigInteger.valueOf(5);
    public static BigInteger t1_leavingEFTime = BigInteger.valueOf(6);
    public static BigInteger t1_leavingLFTime = BigInteger.valueOf(14);

    /*
        Info tram 2
     */
    public static String t2_name = "bin2";
    public static BigInteger t2_periodTime = BigInteger.valueOf(220);
    public static BigInteger t2_phaseTime = BigInteger.valueOf(40);
    public static BigInteger t2_delayEFTime = BigInteger.ZERO;
    public static BigInteger t2_delayLFTime = BigInteger.valueOf(40);
    public static BigInteger t2_crosslightAntTime = BigInteger.valueOf(5);
    public static BigInteger t2_leavingEFTime = BigInteger.valueOf(6);
    public static BigInteger t2_leavingLFTime = BigInteger.valueOf(14);

    public static int carFlows = 3;

    public static List<BigDecimal> arrivalRates = Arrays.asList(
            BigDecimal.valueOf(0.05),
            BigDecimal.valueOf(0.1),
            BigDecimal.valueOf(0.15)
    );

    public static List<BigDecimal> maxVehicleSpeedsKmh = Arrays.asList(
            new BigDecimal("50"),
            new BigDecimal("50"),
            new BigDecimal("50")
    );

    public static List<BigDecimal> roadLenghts = Arrays.asList(
            BigDecimal.valueOf(150),
            BigDecimal.valueOf(150),
            BigDecimal.valueOf(150)
    );

    public static List<BigInteger> maxQueueSizes =
            roadLenghts.stream().map(rl ->
                    rl.divide(carSpace, 0, RoundingMode.FLOOR).toBigInteger()
            ).collect(Collectors.toList());

    public static List<BigDecimal> maxVehicleSpeeds =
            maxVehicleSpeedsKmh.stream().map(skmh ->
                    skmh.divide(new BigDecimal("3.6"), 3, RoundingMode.FLOOR)
            ).collect(Collectors.toList());

    public static List<BigDecimal> mus =
            maxVehicleSpeeds.stream().map(mvs ->
                    mvs.divide(crossroadLenght, 3, RoundingMode.FLOOR)
            ).collect(Collectors.toList());

    public static List<BigDecimal> mus_mmkk = Arrays.asList(
            maxVehicleSpeeds.get(0).divide(carSpace.multiply(new BigDecimal(maxQueueSizes.get(0))), 3, RoundingMode.FLOOR),
            maxVehicleSpeeds.get(1).divide(carSpace.multiply(new BigDecimal(maxQueueSizes.get(1))), 3, RoundingMode.FLOOR),
            maxVehicleSpeeds.get(2).divide(carSpace.multiply(new BigDecimal(maxQueueSizes.get(2))), 3, RoundingMode.FLOOR)
    );

    public static BigDecimal timeStep = BigDecimal.valueOf(0.1);
    public static BigInteger semPeriod = new BigInteger("110");

    public static void updateFields() {
        maxQueueSizes =
                roadLenghts.stream().map(rl ->
                        rl.divide(carSpace, 0, RoundingMode.FLOOR).toBigInteger()
                ).collect(Collectors.toList());

        maxVehicleSpeeds =
                maxVehicleSpeedsKmh.stream().map(skmh ->
                        skmh.divide(new BigDecimal("3.6"), 3, RoundingMode.FLOOR)
                ).collect(Collectors.toList());

        mus = maxVehicleSpeeds.stream().map(mvs ->
                mvs.divide(crossroadLenght, 3, RoundingMode.FLOOR)
        ).collect(Collectors.toList());

        mus_mmkk = Arrays.asList(
                maxVehicleSpeeds.get(0).divide(carSpace.multiply(new BigDecimal(maxQueueSizes.get(0))), 3, RoundingMode.FLOOR),
                maxVehicleSpeeds.get(1).divide(carSpace.multiply(new BigDecimal(maxQueueSizes.get(1))), 3, RoundingMode.FLOOR),
                maxVehicleSpeeds.get(2).divide(carSpace.multiply(new BigDecimal(maxQueueSizes.get(2))), 3, RoundingMode.FLOOR)
        );

    }

    public static CarFlow[] createScenario() {

        // DEFINIZIONE E ANALISI ATTRAVERSAMENTO TRAMVIARIO

        TramCrossing tramCross;
        {
            PetriNetTramTrack bin1 = PetriNetTramTrackBuilder.getInstance(t1_name, t1_periodTime, t1_phaseTime,
                    t1_delayEFTime, t1_delayLFTime, t1_crosslightAntTime, t1_leavingEFTime, t1_leavingLFTime);
            PetriNetTramTrack bin2 = PetriNetTramTrackBuilder.getInstance(t2_name, t2_periodTime, t2_phaseTime,
                    t2_delayEFTime, t2_delayLFTime, t2_crosslightAntTime, t2_leavingEFTime, t2_leavingLFTime);

            TramLine tramLine = new TramLine("line1");
            tramLine.addTramTrack(bin1, bin2);
            tramCross = new TramCrossing(tramLine);
            tramCross.analyze(new ParallelGreenProbabilityVisitor(), timeStep);
        }

        // DEFINIZIONE CODE DI AUTO E AGGIUNTA ATTRAVERSAMENTO TRAMVIARIO

        CarFlow carFlow0 = new CarFlow("carFlow0");
        {
            carFlow0.setQueue(BaseQueueBuilder.getInstance(
                    arrivalRates.get(0),
                    mus_mmkk.get(0),
                    maxQueueSizes.get(0),
                    new BigInteger("0")));
            carFlow0.addObstacle(tramCross);
        }

        CarFlow carFlow1 = new CarFlow("carFlow1");
        {
            carFlow1.setQueue(BaseQueueBuilder.getInstance(
                    arrivalRates.get(1),
                    mus_mmkk.get(1),
                    maxQueueSizes.get(1),
                    new BigInteger("0")));
            carFlow1.addObstacle(tramCross);
        }

        CarFlow carFlow2 = new CarFlow("carFlow2");
        {
            carFlow2.setQueue(BaseQueueBuilder.getInstance(
                    arrivalRates.get(2),
                    mus_mmkk.get(2),
                    maxQueueSizes.get(2),
                    new BigInteger("0")));
            carFlow2.addObstacle(tramCross);
        }

        CarSemaphore carSemaphore1 = new CarSemaphore(semPeriod, timeStep);
        CarSemaphore carSemaphore2 = new CarSemaphore(semPeriod, timeStep);
        CarSemaphore carSemaphore3 = new CarSemaphore(semPeriod, timeStep);
        carFlow0.addObstacle(carSemaphore1);
        carFlow1.addObstacle(carSemaphore2);
        carFlow2.addObstacle(carSemaphore3);

        return new CarFlow[]{carFlow0, carFlow1, carFlow2};
    }

}
