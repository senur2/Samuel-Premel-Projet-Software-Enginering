package com.project.bitpacking;

public abstract class AbstractBitPacking implements BitPacking {
    //implementation of the template method pattern for bit-packing
     protected int[] lastCompressed; // to store the last compressed data for get() method


    @Override
    public final int[] compress(int[] a) {
        int k = chooseBitWidth(a);      
        initStateForCompress(a, k);     // HOOK (optionnel)

        int[] out = new int[computeCompressedSize(a, k)]; // commun
        out[0] = a.length;  // number of integers compressed
        out[1] = k;         // sizee of bits used
        for (int i = 0; i < a.length; i++) {
            writeOne(out, i, a[i], k);  
        }

        finalizeCompress(out);         
        this.lastCompressed = out; // store the last compressed data
        return out;
    }
    @Override
    public final int[] decompress(int[] compressed) {
        int n = compressed[0];
        int k = compressed[1];

        // on prépare l’état (perWord, mask, wordIndex = 0, slot = 0, etc.)
        initStateForDecompress(compressed, k);

        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            // LECTURE SÉQUENTIELLE
            out[i] = readOne(compressed,i, k); 
        }
        return out;
    }


    @Override
    public final int get(int index){
        if (lastCompressed == null) {
            throw new IllegalStateException("No compressed data available. Please compress data first.");
        }
        if (index < 0 || index >= lastCompressed[0]) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + lastCompressed[0]);
        }
        int k = lastCompressed[1];
        return readAt(lastCompressed, index, k);

    }

    // Hooks abstract are to be implemented by subclasses other are optional
    protected void initStateForCompress(int[] a, int k) { }       
    protected void initStateForDecompress(int[] compressed, int k) { } 
    protected void finalizeCompress(int[] out) { }                  
    protected abstract int computeCompressedSize(int[] a, int k);   
    protected abstract int chooseBitWidth(int[] array);       
    protected abstract void writeOne(int[] out, int i, int v, int k);
    protected abstract int readOne(int[] compressed, int i, int k);   
    protected abstract int readAt(int[] compressed, int i, int k); 
    
}
