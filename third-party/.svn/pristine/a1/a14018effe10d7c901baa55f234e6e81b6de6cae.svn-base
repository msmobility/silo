package com.pb.sawdust.calculator.tensor.la.mm.partition;

import com.pb.sawdust.calculator.tensor.la.mm.AbstractMatrixMultiplication;
import com.pb.sawdust.util.JavaType;

/**
 * The {@code MemoryLimitMatrixMultiplicationResource} ...
 *
 * @author crf <br/>
 *         Started 1/11/11 10:20 AM
 */
public class MemoryLimitMatrixMultiplicationResource implements MatrixMultiplicationResource {
    private final int memoryLimit;
    private final AbstractMatrixMultiplication mm;

    public MemoryLimitMatrixMultiplicationResource(int memoryLimit, AbstractMatrixMultiplication mm) {
        this.memoryLimit = memoryLimit;
        this.mm = mm;
    }

    @Override
    public AbstractMatrixMultiplication getMatrixMultiplication() {
        return mm;
    }

    @Override
    public int getMaxCellCount(JavaType type) {
        return MemoryPartitionRule.getCellLimit(memoryLimit,type);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public PartitionRule getPartitionRule(JavaType type, int[] m1Dim, int[] m2Dim, boolean transpose1, boolean transpose2) {
        return new MemoryPartitionRule(memoryLimit,type,transpose1 ? m1Dim[1] : m1Dim[0],transpose1 ? m1Dim[0] : m1Dim[1],transpose2 ? m2Dim[0] : m2Dim[1]);
    }
}
