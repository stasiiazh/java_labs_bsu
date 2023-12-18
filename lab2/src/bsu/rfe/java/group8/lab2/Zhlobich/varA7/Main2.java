package bsu.rfe.java.group8.lab2.Zhlobich.varA7;

import java.awt.BorderLayout; 
import java.awt.Color;
import java.awt.Dimension; 
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;
import java.awt.TextField;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory; 
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane; 
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JRadioButton; 
import javax.swing.JTextField;
import javax.swing.JTextArea;



public class Main2  extends JFrame{
private static final int WIDTH = 1040; private static final int HEIGHT = 520;

private JTextField textFieldY;
private JTextField textFieldX;
private JTextField textFieldZ;
private JTextField textFieldResult;
private ButtonGroup radioButtons = new ButtonGroup(); 
private Box hboxFormulaType = Box.createHorizontalBox();
private int formulaId = 1;
private double newValue = 0;  


public double calculate1(double x, double y, double z)
{return Math.pow(Math.cos(Math.exp(x))+ Math.log(Math.pow(1.0 + y, 2)) + Math.pow(Math.exp(Math.cos(x)) + Math.pow(Math.sin(Math.PI * z), 2), 1/2) + Math.pow(1.0/x, 1/2)+ Math.cos(Math.pow(y, 2)), Math.sin(z));}
public double calculate2(double x, double y, double z)
{return (Math.pow(1 + x*x, 1/y)/Math.exp(Math.sin(z) + x));}
private void addRadioButton(String buttonName, final int formulaId) { 
	JRadioButton button = new JRadioButton(buttonName);
	button.addActionListener(new ActionListener() { 
	public void actionPerformed(ActionEvent ev) {
		Main2.this.formulaId = formulaId; 
		} 
	});
radioButtons.add(button);
hboxFormulaType.add(button);
}

public Main2() {

	  super("Вычисление формулы");
setSize(WIDTH, HEIGHT);
Toolkit kit = Toolkit.getDefaultToolkit();
setLocation((kit.getScreenSize().height - HEIGHT)/2, (kit.getScreenSize().height - HEIGHT)/2);
hboxFormulaType.add(Box.createHorizontalGlue()); 
addRadioButton("Формула 1", 1);
addRadioButton("Формула 2", 2);
radioButtons.setSelected(((AbstractButton) radioButtons.getElements().nextElement()).getModel(), true);
//radioButtons.setSelected(radioButtons.getElements().nextElement().getModel(), true);
hboxFormulaType.add(Box.createHorizontalGlue()); 
hboxFormulaType.setBorder(BorderFactory.createLineBorder(Color.YELLOW));

JLabel labelForX = new JLabel("X:"); 
textFieldX = new JTextField("0", 10);
textFieldX.setMaximumSize(textFieldX.getPreferredSize());

JLabel labelForY = new JLabel("Y:"); 
textFieldY = new JTextField("0", 10);
textFieldY.setMaximumSize(textFieldY.getPreferredSize());

JLabel labelForZ = new JLabel("Z:");
textFieldZ = new JTextField("0", 10);
textFieldZ.setMaximumSize(textFieldZ.getPreferredSize());
Box hboxVariables = Box.createHorizontalBox();

hboxVariables.setBorder(BorderFactory.createLineBorder(Color.RED));
hboxVariables.add(labelForX, BorderLayout.WEST);
hboxVariables.add(textFieldX,  BorderLayout.WEST);

 hboxVariables.add(Box.createHorizontalGlue());
hboxVariables.add(labelForY, BorderLayout.CENTER);
hboxVariables.add(textFieldY,  BorderLayout.CENTER);

hboxVariables.add(Box.createHorizontalGlue());
hboxVariables.add(labelForZ, BorderLayout.EAST);
hboxVariables.add(textFieldZ,  BorderLayout.EAST);


JButton buttonCleanEntry = new JButton("CE");
buttonCleanEntry.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
		  textFieldY.setText("0");
		  textFieldX.setText("0");
		  textFieldZ.setText("0");
		  textFieldResult.setText("");
	  }
	});

JButton buttonCalc = new JButton("Вычислить");
buttonCalc.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent ev) { try {
	
double x = Double.parseDouble(textFieldX.getText());
double y = Double.parseDouble(textFieldY.getText()); 
double z = Double.parseDouble(textFieldZ.getText()); 
double result;
if (formulaId == 1)
result = calculate1(x, y, z);
else
result = calculate2(x, y, z); 
textFieldResult.setText(Double.toString(result));
} catch (NumberFormatException ex) 
  { 
	JOptionPane.showMessageDialog(Main2.this,"Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
  }
}
});

JLabel labelForResult = new JLabel("Результат:");
textFieldResult = new JTextField("0", 20); 
textFieldResult.setMaximumSize(textFieldResult.getPreferredSize());


Box hboxResult = Box.createHorizontalBox();
hboxResult.add(Box.createHorizontalGlue()); 
hboxResult.add(Box.createHorizontalStrut(30)); 
hboxResult.add(buttonCleanEntry);
hboxResult.add(Box.createHorizontalStrut(30)); 
hboxResult.add(labelForResult); 
hboxResult.add(Box.createHorizontalStrut(10));
hboxResult.add(textFieldResult);
hboxResult.add(Box.createHorizontalStrut(30)); 
hboxResult.add(buttonCalc);
hboxResult.add(Box.createHorizontalGlue());
hboxResult.setBorder(BorderFactory.createLineBorder(Color.BLUE));






JButton buttonReset = new JButton("MC"); 
buttonReset.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent ev) { 
	newValue = 0;
}
} );

JButton buttonMemoryPlus = new JButton("M+");
buttonMemoryPlus.addActionListener(new ActionListener() {
  public void actionPerformed(ActionEvent e) {
      newValue += Double.parseDouble(textFieldResult.getText());
  }
});


JButton buttonMemoryRead = new JButton("MR");
buttonMemoryRead.addActionListener(new ActionListener() {
  public void actionPerformed(ActionEvent e) {
      textFieldResult.setText(Double.toString(newValue));
  }
});

Box hboxButtons = Box.createHorizontalBox();
hboxButtons.add(Box.createHorizontalGlue()); 
hboxButtons.add(Box.createHorizontalStrut(30)); 
hboxButtons.add(buttonReset);
hboxButtons.add(Box.createHorizontalStrut(30)); 
hboxButtons.add(buttonMemoryPlus);
hboxButtons.add(Box.createHorizontalStrut(30)); 
hboxButtons.add(buttonMemoryRead);
hboxButtons.add(Box.createHorizontalGlue()); 
hboxButtons.setBorder(
BorderFactory.createLineBorder(Color.GREEN));

Box contentBox = Box.createVerticalBox();
contentBox.add(Box.createVerticalGlue());
contentBox.add(hboxFormulaType);
contentBox.add(hboxVariables); 
contentBox.add(hboxButtons);
contentBox.add(hboxResult); 
//contentBox.add(hboxFormulaType);
contentBox.add(Box.createVerticalGlue()); 
getContentPane().add(contentBox, BorderLayout.CENTER);
}

public static void main(String[] args) {
Main2 frame = new Main2(); 
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.setVisible(true);
} }
