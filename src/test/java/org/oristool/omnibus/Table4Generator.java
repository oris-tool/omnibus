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
