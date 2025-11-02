package com.project.utils;

public class BitUtils {
    public static int Bitforint (int x) {
        //return the number of bits required to represent an integer x
        if (x == 0){
            return 1;
        } 
        else{
            return 32 - Integer.numberOfLeadingZeros(x);
        } 
    } // Added closing brace for Bitforint method
    public static int chooseBitWidth(int[] array) {
        // Choose the minimum bit width required to represent all integers in the array
        int max = 0;
        for (int v : array) {
            if (v > max) {
                max = v;
            }
        }
        return Bitforint(max);
    }
    public static int chooseBitWidthOverflow(int[] array){//choose the bit width such as 95% of the integers can be represented
        int n = array.length;
        int[] bitCounts = new int[33]; // from 0 to 32 bits
        for (int v : array) {
            int bits = Bitforint(v);
            bitCounts[bits]++; // count how many integers need 'bits' bits
        }
        int limite = (int)(n * 0.95);
        int idx = 0;
        for (int k = 1; k <= 32; k++) {
            idx += bitCounts[k]; // cumulative count
            if (idx >= limite) {
                return k;
            }
        }
        return 32; 
    }
}
