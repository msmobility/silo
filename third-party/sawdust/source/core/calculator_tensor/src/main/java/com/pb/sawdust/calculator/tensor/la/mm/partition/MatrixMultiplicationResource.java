package com.pb.sawdust.calculator.tensor.la.mm.partition;

import com.pb.sawdust.calculator.tensor.la.mm.AbstractMatrixMultiplication;
import com.pb.sawdust.util.JavaType;

/**
* The {@code MatrixMultiplicationResource} ...
*
* @author crf <br/>
*         Started 1/11/11 10:06 AM
*/
public interface MatrixMultiplicationResource {
    AbstractMatrixMultiplication getMatrixMultiplication();
    int getMaxCellCount(JavaType type);
    boolean isAvailable();  //is available in general - on system
    PartitionRule getPartitionRule(JavaType type, int[] m1Dim, int[] m2Dim, boolean transpose1, boolean transpose2);
}
