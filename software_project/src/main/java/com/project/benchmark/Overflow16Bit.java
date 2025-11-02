package com.project.benchmark;

public class Overflow16Bit {
    public static final int MAX_VALUE_1 = (1 << 16) - 1; 
    public static final int SIZE_1 = 94000;

    public static final int MAX_VALUE_2 = (1 << 21) - 1;
    public static final int SIZE_2 = 6000;

    public static final int[] DATA_1 = RandomizedArray.generateRandomArray(SIZE_1, MAX_VALUE_1);
    public static final int[] DATA_2 = RandomizedArray.generateRandomArray(SIZE_2, MAX_VALUE_2);

    public static final int[] DATA = new int[SIZE_1 + SIZE_2];
    static {
        System.arraycopy(DATA_1, 0, DATA, 0, SIZE_1);
        System.arraycopy(DATA_2, 0, DATA, SIZE_1, SIZE_2);
    }
    public static final int RUNS = 10;

    public static void run() {
        Benchmarker.benchmark(DATA, RUNS);
    }
}
