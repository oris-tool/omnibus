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

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CSV_IOUtils {

	public static CSVReader getReaderInstance(String fileName) {
		CSVReader dataReader = null;

		try {
			dataReader = new CSVReader(new FileReader(fileName + ".csv"), '\t');
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return dataReader;
	}

	public static CSVWriter getWriterInstance(String fileName) {
		CSVWriter dataWriter = null;

		try {
			dataWriter = new CSVWriter(new FileWriter(fileName + ".csv"), '\t');
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dataWriter;
	}

	public static void writeLine(CSVWriter writer, ArrayList<String> entries) {
		String[] entriesArray = entries.toArray(new String[1]);
		for (int j = 0; j < entriesArray.length; j++) {
			entriesArray[j] = entriesArray[j].replace(".", ",");
		}
		writer.writeNext(entriesArray);
	}

	public static void closeWriter(CSVWriter writer) {
		try {
			Objects.requireNonNull(writer).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
