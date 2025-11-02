package com.project.bitpacking;

public class BitPackingFactory {
    public static BitPacking create(BitPackingType type) {
        return switch (type) {
            case NO_CROSS -> new NoCrossBitPacking();
            case CROSS    -> new CrossBitPacking();
            case OVERFLOW -> new OverflowBitPacking();
        };
    }

}
