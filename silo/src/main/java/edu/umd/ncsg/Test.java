package edu.umd.ncsg;

import com.pb.common.datafile.TableDataSet;

public class Test {


    public static void main (String[] args) {
        // Program to test the TableDataSet

        TableDataSet aTable = new TableDataSet();
        TableDataSet bTable = new TableDataSet();

        int[] ids = new int[]{1,2,3};
        float[] dummyA = new float[ids.length];
        float[] dummyB = new float[ids.length];
        for (int i = 0; i < ids.length; i++) {
            dummyA[i] = 0f;
            dummyB[i] = 0f;
        }

        aTable.appendColumn(ids, "ID");
        aTable.appendColumn(dummyA, "value");
        aTable.buildIndex(aTable.getColumnPosition("ID"));
        bTable.appendColumn(ids, "ID");
        bTable.appendColumn(dummyB, "value");
        bTable.buildIndex(bTable.getColumnPosition("ID"));

        aTable.setIndexedValueAt(2, "value", 5);
        bTable.setIndexedValueAt(3, "value", 8);

        System.out.println("Row aTable bTable");
        for (int id: ids)
            System.out.println(id+"     "+aTable.getIndexedValueAt(id, "value") + "    " +
                    bTable.getIndexedValueAt(id, "value"));

    }

}