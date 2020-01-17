package com.perfectorium.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.perfectorium.entity.Supplier;
import lombok.Getter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class SupplierModel {
    private String exportFormat;
    private final List<Supplier> suppliers;
    private boolean canExport;
    public final Map<Integer, Function<Supplier, String>> supplierFields = ImmutableMap.<Integer, Function<Supplier, String>>builder()
            .put(0, s -> String.valueOf(s.getSysId()))
            .put(1, Supplier::getId)
            .put(2, Supplier::getName)
            .put(3, Supplier::getAddress)
            .put(4, Supplier::getPhone)
            .put(5, Supplier::getPhone2)
            .put(6, Supplier::getEmail)
            .build();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public SupplierModel() {
        this.exportFormat = getExportFormats().get(0);
        this.suppliers = new ArrayList<>();
        this.canExport = false;
    }

    public void setCanExport(final boolean canExport) {
        final boolean temp = this.canExport;
        this.canExport = canExport;
        support.firePropertyChange("canExport", temp, this.canExport);
    }

    public boolean canExport() {
        return canExport;
    }

    public List<String> getExportFormats() {
        return ImmutableList.of("Table", "CSV", "PDF");
    }

    public void setExportFormat(final String format) {
        this.exportFormat = format;
    }

    public void setSuppliers(final List<Supplier> suppliers) {
        this.suppliers.clear();
        this.suppliers.addAll(suppliers);
    }

    public List<String> getFieldsNames() {
        return Stream.of(Supplier.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
    }

    public void whenCanExportChanges(final PropertyChangeListener listener) {
        support.addPropertyChangeListener("canExport", listener);
    }
}
