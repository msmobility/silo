package com.pb.sawdust.tools.tensor;

import com.pb.sawdust.tensor.alias.matrix.Matrix;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

/**
 * The {@code MatrixTable} ...
 *
 * @author crf
 *         Started 6/12/12 2:51 PM
 */
public class MatrixTable extends JPanel {
    public MatrixTable(Matrix<?> m) {
        super(new GridLayout(1,0));
        createViewer(m);
    }

    private void createViewer(Matrix<?> m) {
        AbstractTableModel dataModel = new MatrixDataModel(m);

        JTable dataTable = new JTable(dataModel);
        dataTable.setPreferredScrollableViewportSize(new Dimension(800, 600));
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        initColumnSizes(dataTable);

        JScrollPane scrollPane = createJScrollPane(dataTable);
        this.add(scrollPane);
    }

    private JScrollPane createJScrollPane(JTable dataTable) {
        int nRows = dataTable.getRowCount();
        MatrixDataModel model = (MatrixDataModel) dataTable.getModel();

        DefaultTableModel headerData = new DefaultTableModel(0,1);

        for (int i = 0; i < nRows; i++)
            headerData.addRow(new Object[] {" " + model.getExternalRowNumber(i) + " "} );
        JTable rowHeader = new JTable(headerData);
        LookAndFeel.installColorsAndFont
            (rowHeader, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");

        JScrollPane scrollPane = new JScrollPane(dataTable,
                                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rowHeader.setIntercellSpacing(new Dimension(0,0));
        Dimension d = rowHeader.getPreferredScrollableViewportSize();
        d.width = rowHeader.getPreferredSize().width;
        rowHeader.setPreferredScrollableViewportSize(d);
        rowHeader.setRowHeight(dataTable.getRowHeight());

        scrollPane.setRowHeaderView(rowHeader);

        return scrollPane;
    }

    private void initColumnSizes(JTable table) {
        TableColumn column;
        Component comp;
        int headerWidth;
        int maxHeaderWidth = 0;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        // find the column width of the largest header value
        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(),
                                                                false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            if ( headerWidth > maxHeaderWidth )
            	maxHeaderWidth = headerWidth;
        }

        if (maxHeaderWidth < 30)
            maxHeaderWidth = 30;


        // set all column widths uniformly to the max width increased by some fixed percent (i.e. 100%)
        for (int i = 0; i < table.getColumnCount(); i++) {
        	column = table.getColumnModel().getColumn(i);
        	column.setPreferredWidth( (int)(maxHeaderWidth*2.0) );
        }
    }

    private class MatrixDataModel extends AbstractTableModel {
        private final Matrix<?> matrix;
        private final List<?> rowIds;
        private final List<?> columnIds;

        public MatrixDataModel(Matrix matrix) {
            this.matrix = matrix;
            rowIds = (List<?>) matrix.getIndex().getIndexIds().get(0);
            columnIds = (List<?>) matrix.getIndex().getIndexIds().get(1);
        }

        public int getRowCount() {
            return matrix.size(0);
        }

        public int getColumnCount() {
            return matrix.size(1);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return matrix.getValue(rowIndex,columnIndex);
        }

        public String getColumnName(int columnIndex) {
            return columnIds.get(columnIndex).toString();
        }

        public String getExternalRowNumber(int i) {
            return rowIds.get(i).toString();
        }
    }
}
