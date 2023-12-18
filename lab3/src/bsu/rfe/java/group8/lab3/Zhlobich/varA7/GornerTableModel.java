package bsu.rfe.java.group8.lab3.Zhlobich.varA7;

import javax.swing.table.AbstractTableModel;

public class GornerTableModel extends AbstractTableModel{
	private Double[] coefficients; 
	private Double from;
	private Double to;
	private Double step;


	public GornerTableModel(Double from, Double to, Double step, Double[] coefficients) {
	this.from = from;
	this.to = to;
	this.step = step; 
	this.coefficients = coefficients;
	}
	public Double getFrom() {
	return from; 
	}
	public Double getTo() {
	return to;
	}
	public Double getStep() {
	return step;
	}
	public int getColumnCount() {
	return 3;
	}
	
	 public int getRowCount() {
	        return (int)Math.ceil((to-from)/step) + 1;
	    }
	 
	public Object getValueAt(int row, int col) {
		double x = from + step * row; 

        Double result = 0.0;
        for (int i = coefficients.length - 1; i >= 0; i--) {
            result = result * x + coefficients[i];
        }
	switch (col){
	case 0:
		return x;
	case 1: 
		return  result;
	case 2:
		if (result.intValue() % 2 == 0) 
			 return true;
		else return false;
			default:
           return 0 ;
	}
	
	}
	
	public String getColumnName (int col) {
		switch (col) {
	case 0:
		return "Значение X";
	case 1:
		return "Значение многочлена";
	default:
		return "Целая часть чётная?";
	} 
		}
	 private double calculateHorner(double x){
	        Double b = coefficients[coefficients.length-1];
	        for (int i = coefficients.length - 2; i >= 0; i--){
	            b = b * x + coefficients[i];
	        }
	        return b;
	    }
	
	 public Class<?> getColumnClass(int col) {
	        if (col == 2)
	            return Boolean.class;
	        else
	            return Double.class;
	    }
	}

