package com.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.project.benchmark.Benchmark15Bit;
import com.project.benchmark.Benchmark16Bit;
import com.project.benchmark.Overflow16Bit;
import com.project.bitpacking.BitPacking;
import com.project.bitpacking.BitPackingFactory;
import com.project.bitpacking.BitPackingType;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) { 
            System.out.println("Usage :");
            System.out.println("  compress   <Implementation> <input.txt>");
            System.out.println("  decompress <Implementation> <input.txt>");
            System.out.println("  get        <Implementation> <input.txt> ");
            System.out.println("  benchmark  <type>           (type = 16bit | 15bit | overflow)");
            System.out.println("Implementation = NO_CROSS | CROSS | OVERFLOW");
            return;
        }

        String mode = args[0].toLowerCase(); // compress / decompress / get / benchmark

        // if benchmark mode no need for input or implementation
        if (mode.equals("benchmark")) {
            String benchType = args[1].toLowerCase();
            switch (benchType) {
                case "16bit":
                    Benchmark16Bit.run();
                    break;
                case "15bit":
                    Benchmark15Bit.run();
                    break;
                case "overflow":
                    Overflow16Bit.run();
                    break;
                default:
                    System.err.println("Type de benchmark inconnu : " + benchType);
            }
            return;
        }

        // we need 3 arg for the rest
        if (args.length < 3) {
            System.err.println("Il manque le fichier d'entrée.");
            return;
        }

        String impl = args[1].toUpperCase();
        String inputFile = args[2];

        BitPackingType type;
        try {
            type = BitPackingType.valueOf(impl);
        } catch (IllegalArgumentException e) {
            System.err.println("Implémentation inconnue : " + impl);
            return;
        }

        BitPacking bp = BitPackingFactory.create(type);
        if (bp == null) {
            System.err.println("Impossible de créer l'implémentation : " + impl);
            return;
        }

        try {
            if (mode.equals("compress")) {
                int[] data = readIntArrayFromTxt(inputFile); // read input data
                long start = System.nanoTime(); //implementation time measurement
                int[] compressed = bp.compress(data);
                long end = System.nanoTime();
                System.out.println("Compression time (nanos): " + (end - start));
                System.out.println("== tableau compressé ==");
                printIntArray(compressed);

            } else if (mode.equals("decompress")) {
                int[] compressed = readIntArrayFromTxt(inputFile);
                long start = System.nanoTime();
                int[] decompressed = bp.decompress(compressed);
                long end = System.nanoTime();
                System.out.println("Decompression time (nanos): " + (end - start));
                System.out.println("== tableau décompressé ==");
                printIntArray(decompressed);

            } else if (mode.equals("get")) {
                int[] compressed = readIntArrayFromTxt(inputFile);

                // we need to decompress first to know n
                bp.decompress(compressed);

                System.out.println("== premiers éléments du tableau décompressé (via get) ==");
                long start = System.nanoTime();
                int n = compressed[0]; 
                int limit = Math.min(10, n); //fisrt 10 elements at most
                for (int i = 0; i < limit; i++) {
                    int v = bp.get(i);
                    System.out.println("Element " + i + ": " + v);
                }
                long end = System.nanoTime();
                System.out.println("Get time (nanos): " + (end - start));
            } else {
                System.err.println("Mode inconnu : " + mode);
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture fichier : " + e.getMessage());
        }
    }


    private static int[] readIntArrayFromTxt(String file) throws IOException { //very generic function to convert string to array
        String content = Files.readString(Path.of(file));
        content = content.replace(',', ' ');
        String[] parts = content.trim().split("\\s+");
        List<Integer> values = new ArrayList<>();
        for (String p : parts) {
            if (!p.isEmpty()) {
                values.add(Integer.parseInt(p));
            }
        }
        int[] result = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    private static void printIntArray(int[] a) { //verry generic function to print array
        if (a == null) {
            System.out.println("null");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.length; i++) {
            sb.append(a[i]);
            if (i < a.length - 1) sb.append(' ');
        }
        System.out.println(sb.toString());
    }
}
