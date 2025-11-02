package com.project.bitpacking;

import com.project.utils.BitUtils;

public class NoCrossBitPacking extends AbstractBitPacking { // Implementation of BitPacking without crossing integer boundaries
    private int perWord;  // how many value per int
    private int mask; // bitmask for k bits
    private int wordIndex; // current int index during compression
    private int slot; // current slot in the int during compression 
    private int bitOffSet; // current bit offset in the int during compression
    @Override
    protected int computeCompressedSize(int[] a, int k) {
        int localPerWord = 32 / k;
        return (a.length + localPerWord - 1) / perWord + 2; //number of ints needed(with rounding up) we account for the 2 first ints used for metadata
    }
    
    private void commonBehavior(int k) { //preprocessing common to compression and decompression
        this.perWord = 32 / k;          // how many value per int
        this.mask = (1 << k) - 1; // bitmask for k bits
        this.wordIndex = 0; // current int index during compression
        this.slot = 0; // current slot in the int during compression
        this.bitOffSet = 0;  // wich bit to start writing
    }

    private void updateState(int k) {// update wordIndex and slot for next read/write
        if (slot == perWord - 1) {
            slot = 0;
            wordIndex ++;
            bitOffSet = 0;
        } 
        else {
            slot ++;
            bitOffSet += k;
        }
    }
    @Override
    protected void initStateForCompress(int[] a, int k) { //we compute everything which don't change during the compression for optimization
        commonBehavior(k);
    }

    @Override
    protected void initStateForDecompress(int[] a, int k) { //we compute everything which don't change during the decompression for optimization
        commonBehavior(k);
    }

    @Override
    protected void writeOne(int[] out, int i, int v, int k) { // write the next value sequentially
        int vv = v & mask;  // bitmasking to k bits
        
        // write the value into the correct position in the output array
        out[wordIndex+2] |= (vv << bitOffSet);

        updateState(k);
    }

    @Override
    protected final int readOne(int[] compressed, int i, int k) { // read the next value sequentially
        int value = (compressed[wordIndex + 2] >>> bitOffSet) & mask; // read the value from the correct position in the compressed array

        updateState(k); 

        return value;
    }

    @Override
    protected int readAt(int[] compressed, int i, int k) { // read the value randomly at index i
        int wordIndexAt = (i / perWord); // which int
        int slotAt = i % perWord;       // which slot in the int
        int bitOffset = slotAt * k;     // wich bit to start reading

        int value = (compressed[wordIndexAt + 2] >>> bitOffset) & mask; // read the value from the correct position in the compressed array

        return value;
    }

     @Override
    protected int chooseBitWidth(int[] array) {
        return BitUtils.chooseBitWidth(array);
    }
    
}
