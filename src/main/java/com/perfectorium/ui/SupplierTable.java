package com.perfectorium.ui;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTextField;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ItemListener;
import java.awt.print.Printable;
import java.util.List;

public class SupplierTable {
    private JXTable resultT;
    private JXTextField sqlQueryTF;
    private JXButton executeQueryB;
    private JComboBox<String> exportTypesCB;
    private JXButton exportResultB;
    private JPanel view;

    public SupplierTable() {
        createComponents();
        createView();
    }

    private void createComponents() {
        resultT = new JXTable();
        sqlQueryTF = new JXTextField();
        executeQueryB = new JXButton();
        exportTypesCB = new JComboBox<>();
        exportResultB = new JXButton();
    }

    public void createView() {
        final PanelBuilder builder = new PanelBuilder(new FormLayout("right:50dlu:grow, 10dlu, 70dlu:none",
                "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref"));
        final CellConstraints cc = new CellConstraints(1, 1);
        builder.addLabel("Input the SQL query", cc.xyw(1, 1, 2));
        builder.add(sqlQueryTF, cc.xy(1, 3, "f,f"));
        builder.add(executeQueryB, cc.xy(3, 3));
        builder.addLabel("Result Table", cc.xyw(1, 5, 2));
        resultT.setVisible(false);
        builder.add(new JScrollPane(resultT), cc.xyw(1, 7, 3, "f,f"));
        builder.addLabel("Select the type", cc.xyw(1, 9, 2));
        builder.add(exportTypesCB, cc.xy(1, 11, "f,f"));
        builder.add(exportResultB, cc.xy(3, 11));

        final JPanel panel = builder.getPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        view = panel;
    }

    public void setModelForResultTable(final AbstractTableModel model) {
        resultT.setModel(model);
    }

    public void showTable(final boolean visible) {
        resultT.setVisible(visible);
    }

    public void setExecuteQueryAction(final AbstractAction action) {
        executeQueryB.setAction(action);
    }

    public void setExportResultAction(final AbstractAction action) {
        exportResultB.setAction(action);
    }

    public JPanel getView() {
        return view;
    }

    public void setExportFormats(final List<String> formats) {
        formats.forEach(exportTypesCB::addItem);
    }

    public void whenExportFormatChanges(final ItemListener listener) {
        exportTypesCB.addItemListener(listener);
    }

    public String getQuery() {
        return sqlQueryTF.getText();
    }

    public void showErrorMessage(final String message) {
        JOptionPane.showMessageDialog(view, message, "Something went wrong", JOptionPane.ERROR_MESSAGE);
    }
}
