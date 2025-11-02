package com.project.benchmark;
import java.util.Random;

public class RandomizedArray {
    public static int[] generateRandomArray(int size, int maxValue) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(maxValue + 1); // +1 to include maxValue
        }
        return array;
    }
}
