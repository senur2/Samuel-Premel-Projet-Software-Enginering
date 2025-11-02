package com.project.bitpacking;

public interface BitPacking {
    // Methods for bit-packing implementation  using strategy pattern
    int[] compress(int[] array);
    int[] decompress(int[] compressed);
    int get(int index);
}

