package com.perfectorium.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.perfectorium.entity.Supplier;
import com.perfectorium.model.SupplierModel;
import com.perfectorium.ui.SupplierTable;
import com.perfectorium.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SupplierPresenter {
    private final SupplierModel model;
    private final SupplierTable view;
    private ExportResultAction exportAction;
    private DefaultTableModel tableModel;

    public SupplierPresenter(final SupplierModel model, final SupplierTable view) {
        this.view = view;
        this.model = model;
        this.view.setExecuteQueryAction(new ExecuteQueryAction());
        this.exportAction = new ExportResultAction();
        this.exportAction.setEnabled(false);
        this.view.setExportResultAction(this.exportAction);
        this.view.setExportFormats(this.model.getExportFormats());
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(this.model.getFieldsNames().toArray());
        this.view.setModelForResultTable(tableModel);
        this.view.whenExportFormatChanges(evt -> this.model.setExportFormat((String) evt.getItem()));
        this.model.whenCanExportChanges(evt -> exportAction.setEnabled((Boolean) evt.getNewValue()));
    }

    private class SupplierResultTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return model.getSuppliers().size();
        }

        @Override
        public int getColumnCount() {
            return model.getFieldsNames().size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final Supplier supplier = model.getSuppliers().get(rowIndex);
            return model.getSupplierFields().get(columnIndex).apply(supplier);
        }
    }

    private class ExecuteQueryAction extends AbstractAction {

        public ExecuteQueryAction() {
            super("Execute");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setCanExport(false);
            final String queryS = view.getQuery();
            if (queryS.isEmpty()) {
                view.showErrorMessage("You can not run empty query");
                return;
            }
            final SessionFactory factory = HibernateUtil.getSessionFactory();
            try (final Session session = factory.getCurrentSession()) {
                session.getTransaction().begin();

                @SuppressWarnings("deprecated") final NativeQuery<?> query = session.createSQLQuery(queryS)
                        .addScalar("sid", new LongType())
                        .addScalar("id", new StringType())
                        .addScalar("name", new StringType())
                        .addScalar("address", new StringType())
                        .addScalar("phone", new StringType())
                        .addScalar("phone2", new StringType())
                        .addScalar("email", new StringType());
                @SuppressWarnings("unchecked") final List<Object[]> resultList = (List<Object[]>) query.list();
                final List<Supplier> suppliers = new ArrayList<>();
                resultList.forEach(data ->
                        suppliers.add(new Supplier((long) data[0],
                                (String) data[1],
                                (String) data[2],
                                (String) data[3],
                                (String) data[4],
                                (String) data[5],
                                (String) data[6]))
                );
                model.setSuppliers(suppliers);
                model.setCanExport(true);
            } catch (Exception ex) {
                view.showErrorMessage(ex.getMessage());
                model.setCanExport(false);
            }
        }
    }

    private class ExportResultAction extends AbstractAction {

        public ExportResultAction() {
            super("Export");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final List<Supplier> suppliers = model.getSuppliers();
            final int rowCount = suppliers.size();
            final List<String> fieldsNames = model.getFieldsNames();
            final int columnCount = fieldsNames.size();
            final Map<Integer, Function<Supplier, String>> supplierFields = model.getSupplierFields();
            final String exportFormat = model.getExportFormat();
            switch (exportFormat) {
                case "Table":
                    tableModel.setRowCount(rowCount);
                    tableModel.setColumnCount(columnCount);
                    for (int i = 0; i < rowCount; i++) {
                        for (int j = 0; j < columnCount; j++) {
                            final Supplier supplier = suppliers.get(i);
                            final Function<Supplier, String> function = supplierFields.get(j);
                            final String value = function.apply(supplier);
                            tableModel.setValueAt(value, i, j);
                        }
                    }
                    view.showTable(true);
                    break;
                case "CSV":
                    final StringBuilder csvSB = new StringBuilder();
                    for (int i = 0; i < columnCount; i++) {
                        csvSB.append(fieldsNames.get(i));
                        if (i != columnCount - 1) {
                            csvSB.append(",");
                        }
                    }
                    csvSB.append(System.lineSeparator());
                    for (final Supplier supplier : suppliers) {
                        for (int j = 0; j < columnCount; j++) {
                            final Function<Supplier, String> function = supplierFields.get(j);
                            final String value = function.apply(supplier);
                            csvSB.append(value);
                            if (j != columnCount - 1) {
                                csvSB.append(",");
                            }
                        }
                        csvSB.append(System.lineSeparator());
                    }
                    try {
                        final JFileChooser chooser = new JFileChooser();
                        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                        chooser.setAcceptAllFileFilterUsed(false);
                        chooser.setSelectedFile(new File(chooser.getCurrentDirectory(), "Result table.csv"));
                        chooser.setFileFilter(new FileNameExtensionFilter("EXCEL Table", "csv"));
                        chooser.setDialogTitle("Select directory to export result data");
                        if (chooser.showDialog(view.getView(), "Save") == JFileChooser.APPROVE_OPTION) {
                            final Path path = chooser.getSelectedFile().toPath();
                            Files.write(path, csvSB.toString().getBytes());
                            Desktop.getDesktop().open(path.toFile());
                        }
                    } catch (IOException ex) {
                        view.showErrorMessage(ex.getMessage());
                    }
                    break;
                case "PDF":
                    final JFileChooser chooser = new JFileChooser();
                    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                    chooser.setAcceptAllFileFilterUsed(false);
                    chooser.setSelectedFile(new File(chooser.getCurrentDirectory(), "Result table.pdf"));
                    chooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
                    chooser.setDialogTitle("Select directory to export result data");
                    if (chooser.showDialog(view.getView(), "Save") == JFileChooser.APPROVE_OPTION) {
                        try {
                            final Document document = new Document();
                            final Path path = chooser.getSelectedFile().toPath();
                            PdfWriter.getInstance(document, new FileOutputStream(path.toFile()));

                            document.open();

                            final PdfPTable table = new PdfPTable(columnCount);
                            addTableHeader(table, fieldsNames);
                            for (final Supplier supplier : suppliers) {
                                for (int j = 0; j < columnCount; j++) {
                                    final Function<Supplier, String> function = supplierFields.get(j);
                                    final String value = function.apply(supplier);
                                    table.addCell(value);
                                }
                            }
                            document.add(table);
                            document.close();
                            Desktop.getDesktop().open(path.toFile());
                        } catch (Exception ex) {
                            view.showErrorMessage(ex.getMessage());
                        }
                    }
                    break;
            }
        }

        private void addTableHeader(final PdfPTable table, final List<String> tableIdentifiers) {
            tableIdentifiers
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });
        }
    }
}
