package bsu.rfe.java.group8.lab3.Zhlobich.varA7;

import javax.swing.*;
import java.util.Arrays;

public class mainA3 {
    public static void main(String[] args) {
        Double[] a = {123., 5.,-6.7};

        HornersScheme frame = new HornersScheme(a);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }


}