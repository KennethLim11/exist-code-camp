package com.exist;

import java.util.List;
import java.util.ArrayList;

public class Table {
    private List<Row> rows;

    public Table() {
        this.rows = new ArrayList<>();
    }

    public void addRow(Row row) {
        rows.add(row);
    }

    public void addRowAt(int index, Row row) {
        rows.add(index, row);
    }

    public Row getRow(int index) {
        return rows.get(index);
    }

    public List<Row> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Row row : rows) {
            sb.append(row).append("\n");
        }
        return sb.toString();
    }
}
