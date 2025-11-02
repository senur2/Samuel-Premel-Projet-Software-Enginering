package com.project.bitpacking;

import com.project.utils.BitUtils;

public class CrossBitPacking extends AbstractBitPacking { // Implementation of BitPacking with crossing integer boundaries
    private int mask;
    private int bitPos;

    @Override
    protected int computeCompressedSize(int[] a, int k) {
        int totalBits = a.length * k;
        return (totalBits + 31) / 32 + 2; //we account for the 2 first ints used for metadata
    }

    @Override
    protected void initStateForCompress(int[] a, int k) { //we compute everything which don't change during the compression for optimization
        this.mask = (1 << k) - 1;
        this.bitPos = 0;
        
    }

    @Override
    protected void initStateForDecompress(int[] a, int k) { //we compute everything which don't change during the decompression for optimization
        this.mask = (1 << k) - 1;
        this.bitPos = 0;
        
    }

    @Override
    protected void writeOne(int[] out, int i, int v, int k) {
        //we want to optimize the wordinex and bitOffset computation so we avoir using division and modulo
        int wordIndex = bitPos >> 5;        // which int 
        int bitOffset = bitPos & 31;      // which bit in the int
        int vv = v & mask;  // bitmasking to k bits

        out[wordIndex + 2] |= (vv << bitOffset);

        // handle crossing integer
        if (bitOffset + k > 32) { 
            int bitsInNextWord = (bitOffset + k) - 32;
            out[wordIndex + 3] |= (vv >>> (k - bitsInNextWord)); //we use >>> to avoid sign extension (it will put 1 on the most significant bits if vv is negative)

        }
        bitPos += k;
    }

    @Override
    protected final int readOne(int[] compressed, int i, int k) {
        //we want to optimize the wordindex and bitOffset computation so we avoir using division and modulo
        int wordIndex = bitPos >> 5;        // which int
        int bitOffset = bitPos & 31;      // which bit in the int

        int value = (compressed[wordIndex + 2] >>> bitOffset) & mask;

        // handle crossing integer
        if (bitOffset + k > 32) {
            int bitsInNextWord = (bitOffset + k) - 32;
            int nextPart = compressed[wordIndex + 3] & ((1 << bitsInNextWord) - 1);
            value |= (nextPart << (k - bitsInNextWord));
        }
        bitPos += k;
        return value;
    }

    @Override
    protected final int readAt(int[] compressed, int i, int k) {
        int bitPosition = i * k;
        int wordIndex = bitPosition >> 5;        // which int
        int bitOffset = bitPosition & 31;      // which bit in the int

        int value = (compressed[wordIndex + 2] >>> bitOffset) & mask;

        // handle crossing integer
        if (bitOffset + k > 32) {
            int bitsInNextWord = (bitOffset + k) - 32;
            int nextPart = compressed[wordIndex + 3] & ((1 << bitsInNextWord) - 1);
            value |= (nextPart << (k - bitsInNextWord));
        }
        return value;
    }

    @Override
    protected int chooseBitWidth(int[] array) {
        return BitUtils.chooseBitWidth(array);
    }

}
