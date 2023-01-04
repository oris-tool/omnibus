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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Table4Generator {

    public static void main(String[] args) throws IOException {

        CSVReader table3omnibusReader = CSV_IOUtils.getReaderInstance(
                "results/table4_omnibus/table4_omnibus"
        );
        Map<String, Double> omnibusResults = new HashMap<>();
        for (String[] strings : table3omnibusReader.readAll()) {
            omnibusResults.put(strings[0], Double.valueOf(strings[1]));
        }
        table3omnibusReader.close();

        CSVReader table3sumoReader = CSV_IOUtils.getReaderInstance(
                "results/table4_sumo/table4_sumo"
        );
        Map<String, Double> sumoResults = new HashMap<>();
        for (String[] strings : table3sumoReader.readAll()) {
            sumoResults.put(strings[1], Double.valueOf(strings[2]));
        }
        table3sumoReader.close();

        List<Entry<String, Double>> omnibusList = new ArrayList<>(omnibusResults.entrySet());
        omnibusList.sort(Entry.comparingByValue());

        List<Entry<String, Double>> sumoList = new ArrayList<>(sumoResults.entrySet());
        sumoList.sort(Entry.comparingByValue());


        CSVWriter table3Writer = CSV_IOUtils.getWriterInstance("results/table3");

        table3Writer.writeNext(List.of("Pattern", "Omnibus Reward", "Omnibus Rank", "Sumo Reward", "Sumo Rank").toArray(new String[0]));

        for (int i = 0; i < omnibusList.size(); i++) {
            for (int j = 0; j < sumoList.size(); j++) {
                if (omnibusList.get(i).getKey().equals(sumoList.get(j).getKey())) {
                    table3Writer.writeNext(List.of(
                            omnibusList.get(i).getKey(),
                            omnibusList.get(i).getValue().toString(),
                            Integer.toString(i+1),
                            sumoList.get(j).getValue().toString(),
                            Integer.toString(j+1))
                            .toArray(new String[0]));
                    break;
                }
            }
        }

        table3Writer.close();
    }
}
