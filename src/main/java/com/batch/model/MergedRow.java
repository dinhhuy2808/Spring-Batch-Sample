package com.batch.model;

public class MergedRow {
	private int firstRow;
	private int lastRow;
	private int firstCol;
	private int lastCol;

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getLastRow() {
		return lastRow;
	}

	public void setLastRow(int lastRow) {
		this.lastRow = lastRow;
	}

	public int getFirstCol() {
		return firstCol;
	}

	public void setFirstCol(int firstCol) {
		this.firstCol = firstCol;
	}

	public int getLastCol() {
		return lastCol;
	}

	public void setLastCol(int lastCol) {
		this.lastCol = lastCol;
	}

	@Override
	public String toString() {
		return "firstRow: " + firstRow + " - " + "lastRow: " + lastRow + " - " + "firstCol: " + firstCol + " - "
				+ "lastCol: " + lastCol;
	}

	@Override
	public boolean equals(Object obj) {
		MergedRow newMergedRow = (MergedRow) obj;
		return this.firstCol == newMergedRow.getFirstCol() && this.lastCol == newMergedRow.getLastCol()
				&& this.firstRow == newMergedRow.getFirstRow() && this.lastRow == newMergedRow.getLastRow();

	}
}
