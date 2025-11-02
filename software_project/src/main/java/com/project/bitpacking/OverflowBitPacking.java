package com.project.bitpacking;

import java.util.ArrayList;

import com.project.utils.BitUtils;


public class OverflowBitPacking extends AbstractBitPacking {

    private ArrayList<Integer> overflowValue = new ArrayList<>();
    private int indexOverflow; // index for the next overflow value to write
    private int slotSize;  // size of each slot in bits
    private int currentN; // number of integers to compress/decompress
    private int overflowBit; // number of bits to store the overflow index
    private int endMain; // number of overflowed integers
    private int mask; // mask for k bits

    public int SizeOverflow(int[] a, int k) { //Compute the number of overflowed integers
        int countOverflow = 0;
        for (int value : a) {
            if (BitUtils.Bitforint(value) > k) {
                countOverflow++;
            }
        }
        return countOverflow;
    }

    public int SlotComputation (int k,int m){ //Compute the size of a slot
        int overflowbit; 
        if (m ==0){
            overflowbit = 1;
        }
        else {
            overflowbit = BitUtils.Bitforint(m-1);
        }
        return 1+Math.max(k,overflowbit);
    }

    @Override
    public int computeCompressedSize(int[] a, int k) {
        int m = SizeOverflow(a, k); // number of overflowed integers
        int slotSize = SlotComputation(k, m); // size of each slot
        return ((a.length * slotSize)+31)/32 + 3 + m; // total size with the header
    }

@Override
protected void initStateForCompress(int[] a, int k) { // initialization before compression
    this.currentN = a.length;
    this.mask = (1 << k) - 1;
    int m = SizeOverflow(a, k);            
    this.overflowBit = (m == 0) ? 1 : BitUtils.Bitforint(m - 1);
    this.slotSize = SlotComputation(k, m); 
    this.overflowValue.clear();
    this.indexOverflow = 0;
}


    @Override
    protected void writeOne(int[] out, int i, int v, int k) {
        int totalBitPos = i * slotSize;
        int wordIndex = totalBitPos >> 5;
        int bitOffset = totalBitPos & 31;

        if (BitUtils.Bitforint(v) <= k) {
            int vv = v & mask;          // k bits
            int slotValue = vv << 1;    // we account for the trigger bit

            out[wordIndex + 3] |= (slotValue << bitOffset);

            if (bitOffset + 1 + k > 32) { 
                int bitsInNextWord = (bitOffset + (1 + k)) - 32;
                out[wordIndex + 4] |= (slotValue >>> (1 + k - bitsInNextWord)); //we use >>> to avoid sign extension (it will put 1 on the most significant bits if vv is negative)
        }
        } else {
            overflowValue.add(v); 
            int slotValue = (indexOverflow << 1) | 1;  // [1][idx]
            int slotLen = 1 + overflowBit;
            out[wordIndex + 3] |= (slotValue << bitOffset);
            if (bitOffset + slotLen > 32) {
                int bitsInNextWord = bitOffset + slotLen - 32;
                out[wordIndex + 4] |= (slotValue >>> (slotLen - bitsInNextWord));
            }
            indexOverflow++;
        }
    }

    @Override
    protected  void finalizeCompress(int[] out) { // write the header and overflow values
    out[2] = overflowValue.size();  // m

    int totalSlotBits = currentN * slotSize;
    int slotWords = (totalSlotBits + 31) /32;   

    int overflowBase = 3 + slotWords;  


    for (int j = 0; j < overflowValue.size(); j++) { // write overflow values in out
        out[overflowBase + j] = overflowValue.get(j);
    }
}

    @Override
    protected void initStateForDecompress(int[] compressed, int k) { // initialization before decompression
        this.currentN = compressed[0];
        this.mask = (1 << k) - 1;
        this.endMain = compressed[2];
        this.overflowBit = (endMain ==0) ? 1 : BitUtils.Bitforint(endMain - 1);
        this.slotSize = SlotComputation(k, compressed[2]);
    }

    @Override
    protected int readOne(int[] compressed, int i, int k) { // read one integer at position i, i didn't succed to do it sequentially 
        int totalBitPos = i * slotSize;
        int wordIndex = totalBitPos >> 5;
        int bitOffset = totalBitPos & 31;
        int value = compressed[wordIndex + 3] >>> bitOffset  & ((1 << slotSize) - 1); // read slotSize bits
        if (bitOffset + slotSize > 32) {
            int bitsInNextWord = (bitOffset + slotSize) - 32;
            int nextPart = compressed[wordIndex + 4] & ((1 << bitsInNextWord) - 1);
            value |= (nextPart << (slotSize - bitsInNextWord));
        }        
        int trigger = value & 1; // read trigger bit
        if (trigger == 0) {
            return value >>> 1;
        } else {
            int overflowIndex = value >>> 1;
            int overflowBase = 3 + ((currentN * slotSize) + 31) / 32; // base index of overflow values
            return compressed[overflowBase + overflowIndex];
        }
    }

    @Override
    protected int readAt(int[] compressed, int i, int k) {
        return readOne(compressed, i, k);
    }

    @Override
    public int chooseBitWidth(int[] array) {
        return BitUtils.chooseBitWidthOverflow(array);
    }
}
