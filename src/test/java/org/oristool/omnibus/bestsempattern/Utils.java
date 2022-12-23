package org.oristool.omnibus.bestsempattern;

import org.oristool.omnibus.crossroad.CarSemaphore;

import java.math.BigInteger;
import java.util.Arrays;

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
