package com.perfectorium.ui;

import com.google.common.eventbus.EventBus;
import com.perfectorium.controller.SupplierPresenter;
import com.perfectorium.model.SupplierModel;
import org.jdesktop.swingx.JXFrame;

public class TableView {
    public static void main(String[] args) {
        final JXFrame jxFrame = new JXFrame("Test Table");
        final SupplierTable supplierTable = new SupplierTable();
        new SupplierPresenter(new SupplierModel(), supplierTable);
        jxFrame.add(supplierTable.getView());
        jxFrame.pack();
        jxFrame.setVisible(true);
    }
}
