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

import java.util.*;

public class SemPatternGenerator {

	public static Set<String> generatePatterns(int flows, int totalLength, int minimumSlot, int advancement,
			int lastFlow) {
		if (flows > 10 || flows < 1)
			throw new IllegalArgumentException("Questo generatore non funziona con più di dieci flussi (o meno di 1).");

		Set<String> patterns = new LinkedHashSet<>();

		if (minimumSlot > totalLength) {
			char[] base = new char[totalLength];
			Arrays.fill(base, Integer.toString(lastFlow).toCharArray()[0]);
			patterns.add(new String(base));
		} else {
			for (int i = 0; i < flows; i++) {
				if (i == lastFlow)
					continue;
				for (int length = minimumSlot; length <= totalLength; length += advancement) {

					char[] base = new char[length];
					Arrays.fill(base, Integer.toString(i).toCharArray()[0]);

					Set<String> subPatterns = generatePatterns(flows, totalLength - length, minimumSlot, advancement,
							i);
					for (String subPattern : subPatterns) {
						StringBuilder sb = new StringBuilder();
						sb.append(base);
						sb.append(subPattern);
						patterns.add(sb.toString());
					}
				}
			}
		}
		return patterns;
	}

	/**
	 * Questo metodo genera un pattern semaforico per cui il periodo di lunghezza
	 * {@param totalLength} è suddiviso in slot di lunghezza {@param minimumSlot}.
	 * L'assegnazione di ogni slot è sorteggiata casualmente tra uno dei flussi
	 * {@param flows}.
	 * <p>
	 * Qualora il periodo non sia divisibile per la lunghezza degli slot, l'ultimo
	 * slot è più piccolo.
	 * <p>
	 * Restituisce una stringa in cui a ogni carattere corrisponde un secondo
	 * assegnato al flusso corrispondente, ad esempio 000011112222 indica un periodo
	 * di 12 secondi con quattro secondi assegnati al flusso zero, quattro al flusso
	 * uno e quattro al flusso due.
	 */
	public static String generateRandomPattern(int flows, int totalLength, int minimumSlot) {
		if (flows > 10 || flows < 1)
			throw new IllegalArgumentException("Questo generatore non funziona con più di dieci flussi (o meno di 1).");

		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		while (sb.length() < totalLength) {
			int i = random.nextInt(flows); // sorteggia un flusso
			for (int j = 0; j < minimumSlot; j++) {
				sb.append(Integer.toString(i).toCharArray()[0]); // "appende" le assegnazioni
			}
		}
		while (sb.length() > totalLength) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static List<String> generateNearPatterns(String pattern, int flows, int slotDuration) {
		ArrayList<String> patterns = new ArrayList<>();
		char[] pa = pattern.toCharArray();
		for (int i = 0; i < pa.length; i += slotDuration) {
			for (int f = 0; f < flows; f++) {
				if (Integer.parseInt(String.valueOf(pa[i])) != f) {
					char[] clone = pa.clone();
					if (i + 2 * slotDuration <= clone.length) {
						for (int j = i; j < i + slotDuration; j++) {
							clone[j] = String.valueOf(f).charAt(0);
						}
					} else {
						for (int j = i; j < clone.length; j++) {
							clone[j] = String.valueOf(f).charAt(0);
						}
					}
					patterns.add(new String(clone));
				}
			}
		}
		return patterns;
	}

	/**
	 * Metodo riunione 04/10/2022 (rivelatosi cattivo, perché privilegia gli slot
	 * piccoli)
	 */
	public static List<String> generateRandomPatternsWithSlots(int flows, int period, List<Integer> slotDurations,
			int patterns) {
		ArrayList<String> strings = new ArrayList<>();
		for (int i = 0; i < patterns; i++) {
			strings.add(generateRandomPatternWithSlots(flows, period, slotDurations));
		}
		return strings;
	}

	private static String generateRandomPatternWithSlots(int flows, int period, List<Integer> slotDurations) {
		// ordina per lunghezza le durate degli slot
		Collections.sort(slotDurations);

		List<Integer> chunks = new ArrayList<>();
		Random random = new Random();
		int sum = 0;

		while (sum < period) {
			int maxSlotIndex = slotDurations.size() - 1;
			while (maxSlotIndex >= 0 && slotDurations.get(maxSlotIndex) + sum > period) {
				maxSlotIndex--;
			}
			switch (maxSlotIndex) {
			case -1: // nessuno slot è abbastanza piccolo da poter essere collocato, il più corto
						// viene allungato
			{
				Collections.sort(chunks);
				Collections.reverse(chunks);
				sum -= chunks.get(chunks.size() - 1);
				chunks.remove(chunks.size() - 1);
				chunks.add(period - sum);
				sum = period;
				break;
			}
			case 0: // solo lo slot più piccolo può essere collocato
			{
				chunks.add(slotDurations.get(0));
				sum += chunks.get(chunks.size() - 1);
				break;
			}
			default: // più di uno slot può essere collocato, quindi occorre sorteggiare
			{
				chunks.add(slotDurations.get(random.nextInt(maxSlotIndex)));
				sum += chunks.get(chunks.size() - 1);
				break;
			}
			}
		}

		Collections.shuffle(chunks);

		StringBuilder sb = new StringBuilder();
		int lastFlow = -1;
		for (Integer c : chunks) {
			int f;
			while ((f = random.nextInt(flows)) == lastFlow) {
			}
			for (int i = 0; i < c; i++) {
				sb.append(f);
			}
		}
		return sb.toString();
	}

	/**
	 * Generatore di pattern senza ripetizioni all'interno del periodo (un solo slot
	 * per coda)
	 */
	public static List<String> generateRandomPatternsWithSingleSlot(int flows, int period, int minimumSlot,
			int patterns) {
		ArrayList<String> strings = new ArrayList<>();
		for (int i = 0; i < patterns; i++) {
			strings.add(generateRandomPatternWithSingleSlot(flows, period, minimumSlot));
		}
		return strings;
	}

	private static String generateRandomPatternWithSingleSlot(int flows, int period, int minimumSlot) {
		List<Integer> flowList = new ArrayList<>();
		for (int i = 0; i < flows; i++) {
			flowList.add(i);
		}

		int managedTime = 0;
		Random rand = new Random();
		Map<Integer, Integer> flowGreenTime = new HashMap<>();
		while (flowList.size() > 0) {
			// sorteggia un flusso e lo rimuove dalla lista
			int f_index = rand.nextInt(flowList.size());
			int f = flowList.get(f_index);
			flowList.remove(f_index);

			// sorteggia una durata per il verde del flusso, lasciando abbastanza tempo agli
			// altri
			int greenTime = flowList.size() != 0 ? rand.nextInt(period - managedTime - flowList.size() * minimumSlot)
					: period - managedTime;

			// inserisce il flusso e la durata in una mappa
			flowGreenTime.put(f, greenTime);
			managedTime += greenTime;
		}

		StringBuilder sb = new StringBuilder();
		while (!flowGreenTime.isEmpty()) {
			// sorteggia un flusso e lo recupera (e rimuove) dalla mappa
			int f = (int) flowGreenTime.keySet().toArray()[rand.nextInt(flowGreenTime.size())];
			int greenTime = flowGreenTime.get(f);
			flowGreenTime.remove(f);

			// inserisce nel pattern il flusso per la sua durata
			for (int i = 0; i < greenTime; i++) {
				sb.append(f);
			}
		}

		return sb.toString();
	}

	/**
	 * Generatore di pattern con ripetizioni all'interno del periodo e periodo di
	 * rosso per tutti
	 */
	public static List<String> generateRandomPatternWithRedTime(int flows, int period, int minimumSlot, int redTime,
			int patterns) {
		ArrayList<String> strings = new ArrayList<>();
		for (int i = 0; i < patterns; i++) {
			strings.add(generateRandomPatternWithRedTime(flows, period, minimumSlot, redTime));
		}
		return strings;
	}

	public static String generateRandomPatternWithRedTime(int flows, int period, int minimumSlot, int redTime) {
		if (flows > 9 || flows < 1)
			throw new IllegalArgumentException("Questo generatore non funziona con più di nove flussi (o meno di 1).");

		int red4All = 9;

		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		int firstFlow = -1, previousFlow = -1;
		while (sb.length() < period - 2 * redTime - minimumSlot) {
			int i = random.nextInt(flows); // sorteggia un flusso
			if (firstFlow == -1)
				firstFlow = i;
			// se il flusso sorteggiato è diverso dal precedente, assegna un periodo di
			// rosso per tutti (cifra
			// riservata : red4All)
			if (i != previousFlow && previousFlow != -1) {
				for (int j = 0; j < redTime; j++) {
					sb.append(red4All);
				}
			}
			for (int j = 0; j < minimumSlot; j++) {
				sb.append(i); // "appende" le assegnazioni
			}
			previousFlow = i;
		}
		// if (sb.length() > period - 2 * redTime - minimumSlot) {
		if (previousFlow == firstFlow) {
			// se l'ultimo slot è assegnato allo stesso flusso che al primo non serve il
			// periodo di rosso per tutti
			while (sb.length() < period) {
				sb.append(previousFlow);
			}
		} else { // l'ultimo flusso è diverso dal primo, quindi occorre il periodo di rosso
			// se non c'è spazio per il periodo di rosso, l'ultimo slot viene accorciato
			// (forse non capita mai qui)
			while (sb.length() > period - redTime) {
				sb.deleteCharAt(sb.length() - 1);
			}
			// se invece avanza tempo al termine del periodo allunga l'ultimo slot
			while (sb.length() < period - redTime) {
				sb.append(previousFlow);
			}
			// inserisce il periodo di rosso per tutti
			while (sb.length() < period) {
				sb.append(red4All);
			}
		}
		return sb.toString();
	}

	public static Set<SemaphorePattern> generateAllPatternWithGreenSlotSets(List<VehicleFlow> flows, int period,
																			int redTime) {
		Set<SemaphorePattern> finalPatterns = new HashSet<SemaphorePattern>();
		List<SemaphorePattern> subPatterns = new ArrayList<SemaphorePattern>();
		SemaphorePattern rootPattern = new SemaphorePattern(flows, period, redTime);
		subPatterns.add(rootPattern);
		while (!subPatterns.isEmpty()) {
			List<SemaphorePattern> newSubPatterns = new ArrayList<SemaphorePattern>();
			for (SemaphorePattern subPattern : subPatterns) {
				List<SemaphorePattern> derivedSubPatterns = generetateAllFeasibleNextSubpatterns(subPattern);
				for (SemaphorePattern newSubPattern : derivedSubPatterns) {
					if (newSubPattern.noMorePossibleSlotsExist()) {
						if (newSubPattern.representsAllFlowsAtLeastOnce() && !newSubPattern.firstAndLastFlowsCoincide()) {
//							newSubPattern.fillRemainingWithRed(); // all remaining red strategy
							newSubPattern.fillRemainingGreenTimeWithLastFlow();
							finalPatterns.add(newSubPattern);
						}
					} else {
						newSubPatterns.add(newSubPattern);
					}
				}
			}
			subPatterns = newSubPatterns;
		}
		return finalPatterns;
	}

	private static List<SemaphorePattern> generetateAllFeasibleNextSubpatterns(SemaphorePattern subPattern) {
		List<SemaphorePattern> newSubPatterns = new ArrayList<>();
		for (VehicleFlow flow : subPattern.getInvolvedFlows()) {
			if (!subPattern.hasServedThisFlowPreviously(flow)) {
				for (int greenSlot : flow.getGreenSlots()) {
					if (greenSlot + subPattern.getRedTime() <= subPattern.getRemainingTime()) {
						SemaphorePattern newSubPattern = new SemaphorePattern(subPattern);
						newSubPattern.addGreenSlot(flow.getClone(), greenSlot);
						newSubPatterns.add(newSubPattern);
					}
				}
			}
		}
		return newSubPatterns;
	}

}
