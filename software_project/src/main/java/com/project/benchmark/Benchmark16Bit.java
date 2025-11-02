package com.project.benchmark;

public class Benchmark16Bit {
    public static final int MAX_VALUE = (1 <<16)-1;      // 2^16 - 1
    public static final int SIZE = 100_000;         // taille du tableau
    public static final int[] data = RandomizedArray.generateRandomArray(SIZE, MAX_VALUE);
    public static final int RUNS = 10;             
    public static void run() {
        Benchmarker.benchmark(data, RUNS);
    }
}
