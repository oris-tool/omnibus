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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Classe di configurazione per una simulazione.
 */
public class Config {

    public static final BigDecimal crossroadLenght = new BigDecimal("12.20"); // unmodifiable
    public static boolean plotResults = true;
    public static boolean saveAsPNG = false;
    public static boolean generateCSV = false;

    // Analysis & Results
    public static BigDecimal timeStep = new BigDecimal("0.1");
    public static int analysisHyperperiods = 5;
    public static BigInteger timeBound = new BigInteger("1100");
    public static int cutHyperperiods = 2;
    public static BigInteger cutTimeBound = new BigInteger("0");

    // Variable params
    public static BigDecimal roadLenght = new BigDecimal("150");
    public static BigDecimal lambda = new BigDecimal("0.9");

    public static BigDecimal maxVehicleSpeedKmh = new BigDecimal("50");
    public static BigDecimal maxVehicleSpeed = maxVehicleSpeedKmh.divide(new BigDecimal("3.6"), 3, RoundingMode.FLOOR);
    public static BigDecimal maxSpeedOnQueue = maxVehicleSpeed.add(new BigDecimal("10"));
    public static BigDecimal mu = maxVehicleSpeed.divide(crossroadLenght, 3, RoundingMode.FLOOR);
    public static BigDecimal mu_mmkk = maxVehicleSpeed.divide(roadLenght, 3, RoundingMode.FLOOR);
    public static BigDecimal minVehicleGap = new BigDecimal("0.3");

    // Queue Params
    public static BigDecimal vehicleLength = new BigDecimal("4.5");
    public static BigInteger maxQueueSize =
            roadLenght.divide(vehicleLength.add(minVehicleGap), 0, RoundingMode.FLOOR).toBigIntegerExact();

    public static BigInteger initialElements = new BigInteger("0");

    // First Tram Params

    public static BigInteger periodTime = new BigInteger("220");
    public static BigInteger phaseTime = new BigInteger("0");
    public static BigInteger delayEFTime = new BigInteger("0");
    public static BigInteger delayLFTime = new BigInteger("120");
    public static BigInteger crosslightAntTime = new BigInteger("5");
    public static BigInteger leavingEFTime = new BigInteger("6");
    public static BigInteger leavingLFTime = new BigInteger("14");

    // Second Tram Params

    public static boolean twoTram = true;

    public static BigInteger periodTime2 = new BigInteger("220");
    public static BigInteger phaseTime2 = new BigInteger("40");
    public static BigInteger delayEFTime2 = new BigInteger("0");
    public static BigInteger delayLFTime2 = new BigInteger("40");
    public static BigInteger crosslightAntTime2 = new BigInteger("5");
    public static BigInteger leavingEFTime2 = new BigInteger("6");
    public static BigInteger leavingLFTime2 = new BigInteger("14");

    public static void updateFields() {
        maxVehicleSpeed = maxVehicleSpeedKmh.divide(new BigDecimal("3.6"), 3, RoundingMode.FLOOR);
        maxSpeedOnQueue = maxVehicleSpeed.add(new BigDecimal("10"));
        maxQueueSize =
                roadLenght.divide(vehicleLength.add(minVehicleGap), 0, RoundingMode.FLOOR).toBigIntegerExact();

        mu = maxVehicleSpeed.divide(crossroadLenght, 3, RoundingMode.FLOOR);
        mu_mmkk = maxVehicleSpeed.divide(roadLenght, 3, RoundingMode.FLOOR);
    }

}
