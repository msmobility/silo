package com.pb.sawdust.calculator.tensor.la.mm.partition;

/**
* The {@code EquiPartitionRule} ...
*
* @author crf <br/>
*         Started 1/11/11 10:11 AM
*/
public class EquiPartitionRule implements PartitionRule {
    private final boolean partitionFirstIndex;
    private final int partitionDimensionSize;
    private final int partitionWidth;
    private final int edge;

    public EquiPartitionRule(int segmentCount, int d10, int d21) {
        partitionFirstIndex = d10 > d21;
        partitionDimensionSize = partitionFirstIndex ? d10 : d21;
        partitionWidth = partitionDimensionSize < segmentCount ? 1 : partitionDimensionSize / segmentCount;//(segmentCount-1);
        edge = partitionWidth*(segmentCount-1);
    }

    @Override
    public int nextPartitionLength(int start) {
        if (start >= partitionDimensionSize)
            return -1;
        return start >= edge ? partitionDimensionSize - start : partitionWidth;
    }

    @Override
    public boolean partitionFirstIndex() {
        return partitionFirstIndex;
    }
}
