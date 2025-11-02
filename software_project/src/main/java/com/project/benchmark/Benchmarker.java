package com.project.benchmark;

import com.project.bitpacking.BitPacking;
import com.project.bitpacking.BitPackingFactory;
import com.project.bitpacking.BitPackingType;

public class Benchmarker {

    public static void benchmark(int[] data, int runs) { //Core function to benchmark all BitPacking types
        BitPackingType[] types = {
            BitPackingType.NO_CROSS,
            BitPackingType.CROSS,
            BitPackingType.OVERFLOW
        };
        for (BitPackingType type : types) { // iterate over all BitPacking types
            System.out.println("Benchmarking " + type + " :");
            BitPacking bp = BitPackingFactory.create(type);

            long totalCompress = 0;
            int[] lastCompressed = null;
            for (int i = 0; i < runs; i++) {
                long start = System.nanoTime();
                int[] c = bp.compress(data);
                long end = System.nanoTime();
                totalCompress += (end - start);
                lastCompressed = c; // keep last compressed for decompress benchmark
            }
            long avgCompress = totalCompress / runs;
            System.out.println("compress avg (" + runs + " runs) = " + avgCompress + " ns");
            System.out.println("input size (ints) = " + data.length);
            System.out.println("input size (bytes) = " + (data.length * 4)); 
            System.out.println("compressed size (ints) = " + lastCompressed.length);
            System.out.println("compressed size (bytes) = " + (lastCompressed.length * 4));
            System.out.println("compression ratio = " +
            String.format("%.2f", 100.0 * lastCompressed.length / data.length) + "%"); // print compression ratio


            long totalDecompress = 0;
            int[] lastDecompressed = null;
            for (int i = 0; i < runs; i++) {
                long start = System.nanoTime();
                int[] d = bp.decompress(lastCompressed);
                long end = System.nanoTime();
                totalDecompress += (end - start);
                lastDecompressed = d;
            }
            long avgDecompress = totalDecompress / runs;
            System.out.println("decompress avg (" + runs + " runs) = " + avgDecompress + " ns");

            long totalGet = 0;
            for (int i = 0; i < runs; i++) { // benchmark get method over full pass
                long start = System.nanoTime();
                for (int j = 0; j < data.length; j++) {
                    int v = bp.get(j); 
                }
                long end = System.nanoTime();
                totalGet += (end - start);
            }
            long avgGet = totalGet / runs;
            System.out.println("get (full pass) avg (" + runs + " runs) = " + avgGet + " ns");

            boolean ok = java.util.Arrays.equals(data, lastDecompressed); // verify correctness
            System.out.println("decompress == original ? " + ok);
            System.out.println();
        }
    }
}