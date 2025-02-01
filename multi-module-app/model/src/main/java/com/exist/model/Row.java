package com.exist.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class Row {
    private List<Cell> cells;

    public Row() {
        this.cells = new ArrayList<>();
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void addCell(Cell cell) {
        cells.add(cell);
    }

    public void sortCells(Comparator<Cell> comparator) {
        cells.sort(comparator);
    }

    @Override
    public String toString() {
        return cells.toString();
    }
}