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

import org.oristool.omnibus.utils.Config;
import org.oristool.omnibus.utils.SingleTestLauncher;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Table2SuiteTestExperiment {

    private static final String[] roadLengths = {"50", "150", "450"};
    private static final String[] lambdas = {"0.5", "0.9", "1.3"};
    private static final String[] allowedSpeedsKmh = {"30", "50", "70"};

    public static void main(String[] args) {
        Config.plotResults = false;
        Config.saveAsPNG = true;
        Config.generateCSV = true;

        File results_folder = new File("results");
        if (!results_folder.exists())
            results_folder.mkdir();

        ArrayList<Long> durations = new ArrayList<>();
        int analysis = roadLengths.length * lambdas.length * allowedSpeedsKmh.length;
        int count = 0;
        for (String r : roadLengths) {
            for (String l : lambdas) {
                for (String s : allowedSpeedsKmh) {
                    Date start = new Date();
                    System.out.println("-----> Analysis " + ++count + " of " + analysis + " <-----");
                    Config.roadLenght = new BigDecimal(r);
                    Config.lambda = new BigDecimal(l);
                    Config.maxVehicleSpeedKmh = new BigDecimal(s);
                    Config.updateFields();
                    SingleTestLauncher.main(null);
                    Date end = new Date();
                    durations.add(end.getTime() - start.getTime());
                    double meanDuration = durations.stream().mapToDouble(a -> a).average().getAsDouble();
                    Date expectedEnd = new Date((long) (end.getTime() + meanDuration * (analysis - count)));
                    System.out.println("\nExpected termination of suite test: \n\t" + expectedEnd);
                    System.out.println();
                }
            }
        }
    }
}
