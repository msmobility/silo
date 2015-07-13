package com.pb.sawdust.calculator.tensor.la.mm.partition;

import com.pb.sawdust.util.JavaType;

/**
* The {@code MemoryPartitionRule} ...
*
* @author crf <br/>
*         Started 1/11/11 10:11 AM
*/
public class MemoryPartitionRule extends EquiPartitionRule {

    public static int getCellLimit(int memoryLimit, JavaType type) {
        return memoryLimit / type.getPrimitiveSize();
    }

    private static int getSegmentSize(int memoryLimit, JavaType type, int d10, int d11, int d21) {
        int cellLimit = getCellLimit(memoryLimit,type);
        boolean partitionFirstIndex = d10 > d21;

        int shiftAmount = partitionFirstIndex ? d21 : d10;
        int scale = d11 + shiftAmount;
        int baseSize = cellLimit - d11*shiftAmount;
        if (baseSize < scale)
            throw new IllegalArgumentException("Not enough memory to handle calculation");

        //strange, but best way seems to be find a good (base) starting point and then iterate till valid
        int partitionDim = (partitionFirstIndex ? d10 : d21);
        int maxSize = baseSize / scale;
        int segmentCount = partitionDim / maxSize;
        if (segmentCount == 0)
            segmentCount++;
        int partitionWidth = partitionDim < segmentCount ? 1 : partitionDim / segmentCount;
        while ((partitionDim - (segmentCount-1)*partitionWidth) > maxSize)
            partitionWidth = partitionDim / ++segmentCount;
        System.out.println(segmentCount);
        return segmentCount;
    }

    public MemoryPartitionRule(int memoryLimit, JavaType type, int d10, int d11, int d21) {
        super(getSegmentSize(memoryLimit,type,d10,d11,d21),d10,d21);
    }
}
