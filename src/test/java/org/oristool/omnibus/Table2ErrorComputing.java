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

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.oristool.omnibus.utils.CSV_IOUtils;
import org.oristool.omnibus.utils.MathUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Table2ErrorComputing {

    private static final String[] roadLengths = {"50", "150", "450"};
    private static final String[] lambdas = {"0.5", "0.9", "1.3"};
    private static final String[] allowedSpeedsKmh = {"30", "50", "70"};

    public static void main(String[] args) {

        String basePath = "results/";
        String fileName = "results/computedErrors";

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(fileName + ".csv"), '\t');
            addIndecesLine(writer);
            for (String r : roadLengths) {
                for (String l : lambdas) {
                    for (String s : allowedSpeedsKmh) {

                        String capacity = Integer.toString((int) (Double.parseDouble(r) / 4.8));
                        BigDecimal maxVehicleSpeedKmh = new BigDecimal(s);
                        BigDecimal maxVehicleSpeed = maxVehicleSpeedKmh.divide(new BigDecimal("3.6"), 3,
                                RoundingMode.FLOOR);
                        BigDecimal mu_m1kk = maxVehicleSpeed.divide(BigDecimal.valueOf(12.20), 3, RoundingMode.FLOOR);
                        BigDecimal mu_mmkk = maxVehicleSpeed.divide(new BigDecimal(r), 3, RoundingMode.FLOOR);

                        CSVReader omnibusReader =
                                CSV_IOUtils.getReaderInstance(basePath + "omnibus_" + r + "_" + l + "_" + s);
                        CSVReader sumoReader =
                                CSV_IOUtils.getReaderInstance(basePath + "sumo_" + r + "_" + l + "_" + s);

                        double[] omnibusAvailability = getDoubleArray(omnibusReader.readNext());
                        double[] sumoAvailability = getDoubleArray(sumoReader.readNext());
                        double[] omnibusMM1KCarsMean = getDoubleArray(omnibusReader.readNext());
                        double[] omnibusMMKKCarsMean = getDoubleArray(omnibusReader.readNext());
                        double[] sumoCarsMean = getDoubleArray(sumoReader.readNext());
                        omnibusReader.close();
                        sumoReader.close();

                        BigDecimal availabilityMRE =
                                BigDecimal.valueOf(MathUtils.averageRelativeError(sumoAvailability, omnibusAvailability))
                                        .setScale(3, RoundingMode.HALF_DOWN);

                        BigDecimal mm1kMRE =
                                BigDecimal.valueOf(MathUtils.averageRelativeError(sumoCarsMean, omnibusMM1KCarsMean))
                                        .setScale(3, RoundingMode.HALF_DOWN);

                        BigDecimal mmkkMRE =
                                BigDecimal.valueOf(MathUtils.averageRelativeError(sumoCarsMean, omnibusMMKKCarsMean))
                                        .setScale(3, RoundingMode.HALF_DOWN);

                        BigDecimal mm1kRMSD =
                                BigDecimal.valueOf(MathUtils.rootMeanSquareDeviation(sumoCarsMean, omnibusMM1KCarsMean))
                                        .setScale(3, RoundingMode.HALF_DOWN);

                        BigDecimal mmkkRMSD =
                                BigDecimal.valueOf(MathUtils.rootMeanSquareDeviation(sumoCarsMean, omnibusMMKKCarsMean))
                                        .setScale(3, RoundingMode.HALF_DOWN);

                        BigDecimal mm1kNRMSD = mm1kRMSD.divide(new BigDecimal(capacity), 3, RoundingMode.HALF_DOWN);

                        BigDecimal mmkkNRMSD = mmkkRMSD.divide(new BigDecimal(capacity), 3, RoundingMode.HALF_DOWN);

                        addResultsLine(writer, r, capacity, l, s,
                                mu_m1kk.toPlainString(), mu_mmkk.toPlainString(), availabilityMRE.toPlainString(),
                                mm1kMRE.toPlainString(), mmkkMRE.toPlainString(), mm1kRMSD.toPlainString(),
                                mmkkRMSD.toPlainString(), mm1kNRMSD.toPlainString(), mmkkNRMSD.toPlainString());
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static double[] getDoubleArray(String[] strings) {
        double[] da = new double[strings.length];
        for (int i = 0; i < strings.length; i++) {
            da[i] = Double.parseDouble(strings[i]);
        }
        return da;
    }

    private static void addIndecesLine(CSVWriter writer) {
        ArrayList<String> entries = new ArrayList<>();

        entries.add("S(m)");
        entries.add("K");
        entries.add("lambda");
        entries.add("maxSpeed");

        entries.add("->");

        entries.add("mu_mm1k");
        entries.add("mu_mmkk");

        entries.add("->");

        entries.add("availabilityRE");

        entries.add("RE_MM1K");
        entries.add("RE_MMKK");
        entries.add("RMSD_MM1K");
        entries.add("RMSD_MMKK");
        entries.add("NormRMSD_MM1K");
        entries.add("NormRMSD_MMKK");

        writer.writeNext(entries.toArray(new String[1]));
    }

    private static void addResultsLine(
            CSVWriter writer,
            String roadLenght, String capacity, String lambda, String maxSpeed, String mu_mm1k, String mu_mmkk,
            String availabilityMRE, String mm1kMRE, String mmkkMRE, String mm1kRMSD, String mmkkRMSD, String mm1kNRMSD,
            String mmkkNRMSD) {

        ArrayList<String> entries = new ArrayList<>();

        entries.add(roadLenght);
        entries.add(capacity);
        entries.add(lambda);
        entries.add(maxSpeed);

        entries.add("->");

        entries.add(mu_mm1k);
        entries.add(mu_mmkk);

        entries.add("->");

        entries.add(availabilityMRE);

        entries.add(mm1kMRE);
        entries.add(mmkkMRE);
        entries.add(mm1kRMSD);
        entries.add(mmkkRMSD);
        entries.add(mm1kNRMSD);
        entries.add(mmkkNRMSD);

        CSV_IOUtils.writeLine(writer, entries);
    }

}
