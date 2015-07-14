package com.pb.sawdust.tools.tensor;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.group.NameTensorGroup;
import com.pb.sawdust.tensor.group.TensorGroup;
import com.pb.sawdust.tensor.read.ZipTensorReader;

import javax.swing.*;

/**
 * The {@code MatrixViewer} ...
 *
 * @author crf
 *         Started 6/12/12 3:36 PM
 */
public class MatrixViewer extends JFrame {

    public MatrixViewer(Matrix<?> matrix) {
        super("Matrix Viewer a0.1");

        //Set Look and Feel before creating any UI components
        String nativeLF = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(nativeLF);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(100, 100);
        this.setSize(300, 300);
        this.setVisible(true);

        getContentPane().add(new MatrixTable(matrix));
        pack();
    }

    public static void main(String[] args) {

        TensorGroup<?,?> tg = new NameTensorGroup(new ZipTensorReader("D:/projects/reno/model/scenarios/test4/outputs/destination_choice/dc_trips_hbe.zpt"),ArrayTensor.getFactory());
        System.out.println(tg.tensorKeySet());
//        Matrix<?> m = (Matrix<?>) tg.getTensor("short");
        Matrix<?> m = (Matrix<?>) tg.getTensor("long");
//        Matrix<?> m = (Matrix<?>) tg.getTensor("medium_income_short");

//        Matrix<?> m = ArrayTensor.getFactory().doubleMatrix(7,9);
        new MatrixViewer(m);
    }
}
