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

import java.math.BigInteger;
import java.util.Arrays;

import org.oristool.omnibus.intersection.CarSemaphore;

public class Utils {

    public static void assignGreen(String pattern, CarSemaphore... carSemaphores) {
        Arrays.stream(carSemaphores).sequential().forEach(carSemaphore ->
                carSemaphore.setRed(new BigInteger("0"), carSemaphore.getPeriod()));

        char[] chars = pattern.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int greenFlow = Integer.parseInt(String.valueOf(chars[i]));
            if (greenFlow != 9)
                carSemaphores[greenFlow].setGreen(BigInteger.valueOf(i), BigInteger.valueOf(i + 1));
        }
    }

    public static int lcm(int... numbers) {
        if (numbers.length > 2)
            return lcm(numbers[0], lcm(Arrays.copyOfRange(numbers, 1, numbers.length)));
        else
            return lcm(numbers[0], numbers[1]);
    }

    private static int lcm(int number1, int number2) {
        if (number1 == 0 || number2 == 0) {
            return 0;
        }
        int absNumber1 = Math.abs(number1);
        int absNumber2 = Math.abs(number2);
        int absHigherNumber = Math.max(absNumber1, absNumber2);
        int absLowerNumber = Math.min(absNumber1, absNumber2);
        int lcm = absHigherNumber;
        while (lcm % absLowerNumber != 0) {
            lcm += absHigherNumber;
        }
        return lcm;
    }
}
