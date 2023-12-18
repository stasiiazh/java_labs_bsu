package bsu.rfe.java.group8.lab3.Zhlobich.varA7;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 
import java.io.DataOutputStream; 
import java.io.File;
import java.io.FileNotFoundException; 
import java.io.FileOutputStream;
import java.io.PrintStream;
import javax.swing.AbstractAction; 
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton; 
import javax.swing.JFileChooser; 
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu; 
import javax.swing.JMenuBar; 
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class HornersScheme extends JFrame{

	
	
	// Константы с исходным размером окна приложения
	private static final int WIDTH = 700; 
	private static final int HEIGHT = 500;
	      // Массив коэффициентов многочлена
	private Double[] coefficients;
	// Объект диалогового окна для выбора файлов
	// Компонент не создаѐтся изначально, т.к. может и не понадобиться 
	// пользователю если тот не собирается сохранять данные в файл
	private JFileChooser fileChooser = null;
    
	// Элементы меню вынесены в поля данных класса, так как ими необходимо
	// манипулировать из разных мест
    private JMenuItem aboutMenuItem;
	private JMenuItem saveToTextMenuItem;
	private JMenuItem saveToGraphicsMenuItem;
	private JMenuItem searchValueMenuItem;
	 private JLabel aboutPhotoLabel;
	    private JLabel aboutNameTF;
	    private JButton githubLinkButton;
	// Поля ввода для считывания значений переменных
	private JTextField textFieldFrom;
	private JTextField textFieldTo;
	private JTextField textFieldStep;
	private Box hBoxResult;
	      // Визуализатор ячеек таблицы
	private GornerTableCellRenderer renderer = new GornerTableCellRenderer();
	      // Модель данных с результатами вычислений
	private GornerTableModel data;
	public HornersScheme(Double[] coefficients) {
	// Обязательный вызов конструктора предка
	super("Табулирование многочлена на отрезке по схеме Горнера"); 
	// Запомнить во внутреннем поле переданные коэффициенты 
	this.coefficients = coefficients;
	// Установить размеры окна
	setSize(WIDTH, HEIGHT);
	Toolkit kit = Toolkit.getDefaultToolkit();
	// Отцентрировать окно приложения на экране 
	setLocation((kit.getScreenSize().width - WIDTH)/2,(kit.getScreenSize().height - HEIGHT)/2);
	            // Создать меню
	JMenuBar menuBar = new JMenuBar();
	// Установить меню в качестве главного меню приложения 
	setJMenuBar(menuBar);
	// Добавить в меню пункт меню "Файл"
    JMenu aboutMenu = new JMenu("Справка");
    menuBar.add(aboutMenu);

	JMenu fileMenu = new JMenu("Файл");
	// Добавить его в главное меню
	menuBar.add(fileMenu);
	// Создать пункт меню "Таблица"
	JMenu tableMenu = new JMenu("Таблица");
	// Добавить его в главное меню
	menuBar.add(tableMenu);
	// Создать новое "действие" по сохранению в текстовый файл
	 Action aboutAction = new AbstractAction("Автор") {
         public void actionPerformed(ActionEvent event) {
             JDialog dialog = new JDialog(HornersScheme.this, "Автор", true);
             dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
             dialog.setSize(360, 360);

             aboutNameTF = new JLabel("Жлобич Анастасия");

             Box box = Box.createVerticalBox();
             box.add(aboutNameTF);

             Box hbox = Box.createHorizontalBox();
             hbox.add(Box.createHorizontalStrut(20));
             hbox.add(box);

             dialog.getContentPane().add(hbox);

             dialog.setVisible(true);
         }
     };
	  aboutMenuItem = aboutMenu.add(aboutAction);
	
	Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
        public void actionPerformed(ActionEvent event) {
            if (fileChooser == null) {
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
            }
            if (fileChooser.showSaveDialog(HornersScheme.this) == JFileChooser.APPROVE_OPTION)
                saveToTextFile(fileChooser.getSelectedFile());
        }
    };
    saveToTextMenuItem = fileMenu.add(saveToTextAction);
    saveToTextMenuItem.setEnabled(false);
	Action saveToGraphicsAction = new AbstractAction ("Сохранить данные для построения графика") {

        public void actionPerformed(ActionEvent event) {
            if (fileChooser == null) {
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
            }
            if (fileChooser.showSaveDialog(HornersScheme.this) == JFileChooser.APPROVE_OPTION)
            	saveToGraphicsFile(fileChooser.getSelectedFile());
        }
    };

	// Если результат его показа успешный,
	// сохранить данные в двоичный файл
	//saveToGraphicsFile(fileChooser.getSelectedFile());
	
	// Добавить соответствующий пункт подменю в меню "Файл"
	saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction);
	// По умолчанию пункт меню является недоступным(данных ещѐ нет) 
	saveToGraphicsMenuItem.setEnabled(false);
	// Создать новое действие по поиску значений многочлена
	Action searchValueAction = new AbstractAction("Найти значение многочлена") {
        public void actionPerformed(ActionEvent event) {
//Запросить пользователя ввести искомую строку
            String value = JOptionPane.showInputDialog(HornersScheme.this, "Введите значение для поиска", "Поиск значения", JOptionPane.QUESTION_MESSAGE);
//Установить введенное значение в качестве иголки
            renderer.setNeedle(value);
//Обновить таблицу
            getContentPane().repaint();
        }
    };

	// Добавить действие в меню "Таблица"
	searchValueMenuItem = tableMenu.add(searchValueAction);
	// По умолчанию пункт меню является недоступным (данных ещѐ нет)
	searchValueMenuItem.setEnabled(true);
	// Создать область с полями ввода для границ отрезка и шага
	// Создать подпись для ввода левой границы отрезка
	JLabel labelForFrom = new JLabel("X изменяется на интервале от:"); 
	// Создать текстовое поле для ввода значения длиной в 10 символов 
	// со значением по умолчанию 0.0
	textFieldFrom = new JTextField("0.0", 10);
	// Установить максимальный размер равный предпочтительному, чтобы
	// предотвратить увеличение размера поля ввода 
	textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());
	// Создать подпись для ввода левой границы отрезка
	JLabel labelForTo = new JLabel("до:");
	// Создать текстовое поле для ввода значения длиной в 10 символов
	// со значением по умолчанию 1.0
	textFieldTo = new JTextField("1.0", 10);
	// Установить максимальный размер равный предпочтительному, чтобы
	// предотвратить увеличение размера поля ввода
	textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());
	// Создать подпись для ввода шага табулирования
	JLabel labelForStep = new JLabel("с шагом:");
	// Создать текстовое поле для ввода значения длиной в 10 символов
	// со значением по умолчанию 1.0
	textFieldStep = new JTextField("0.1", 10);
	// Установить максимальный размер равный предпочтительному, чтобы 
	// предотвратить увеличение размера поля ввода 
	textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());
	// Создать контейнер 1 типа "коробка с горизонтальной укладкой" 
	Box hboxRange = Box.createHorizontalBox();
	// Задать для контейнера тип рамки "объѐмная"
	hboxRange.setBorder(BorderFactory.createBevelBorder(1));
	// Добавить "клей" C1-H1 
	hboxRange.add(Box.createHorizontalGlue());
	// Добавить подпись "От"
	hboxRange.add(labelForFrom);
	// Добавить "распорку" C1-H2 
	hboxRange.add(Box.createHorizontalStrut(10));
	// Добавить поле ввода "От"
	hboxRange.add(textFieldFrom);
	// Добавить "распорку" C1-H3
	hboxRange.add(Box.createHorizontalStrut(20));
	// Добавить подпись "До"
	hboxRange.add(labelForTo);
	// Добавить "распорку" C1-H4
	hboxRange.add(Box.createHorizontalStrut(10));
	// Добавить поле ввода "До"
	hboxRange.add(textFieldTo);
	// Добавить "распорку" C1-H5 
	hboxRange.add(Box.createHorizontalStrut(20));
	// Добавить подпись "с шагом"
	hboxRange.add(labelForStep);
	// Добавить "распорку" C1-H6 
	hboxRange.add(Box.createHorizontalStrut(10));
	// Добавить поле для ввода шага табулирования 
	hboxRange.add(textFieldStep);
	// Добавить "клей" C1-H7 
	hboxRange.add(Box.createHorizontalGlue());
	// Установить предпочтительный размер области
	// минимальному, чтобы при компоновке область совсем не сдавили 
    hboxRange.setPreferredSize(new Dimension((int) hboxRange.getMaximumSize().getWidth(), (int) (hboxRange.getMinimumSize().getHeight()) * 2));
    hboxRange.setPreferredSize(new Dimension((int) hboxRange.getMaximumSize().getWidth(), (int) (hboxRange.getMinimumSize().getHeight()) * 2));
    getContentPane().add(hboxRange, BorderLayout.SOUTH);
    JButton buttonCalc = new JButton("Вычислить");

    //JButton buttonCalc = new JButton("Вычислить");
    buttonCalc.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            try {
                Double from = Double.parseDouble(textFieldFrom.getText());
                Double to = Double.parseDouble(textFieldTo.getText());
                Double step = Double.parseDouble(textFieldStep.getText());
                data = new GornerTableModel(from, to, step, HornersScheme.this.coefficients);
                JTable table = new JTable(data);
                table.setDefaultRenderer(Double.class, renderer);
                table.setRowHeight(30);
                hBoxResult.removeAll();
                hBoxResult.add(new JScrollPane(table));
                getContentPane().validate();
                saveToTextMenuItem.setEnabled(true);
                saveToGraphicsMenuItem.setEnabled(true);
            } 
            catch (NumberFormatException ex) 
            {
                JOptionPane.showMessageDialog(HornersScheme.this, "Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
            }
        }
    });
    JButton buttonReset = new JButton("Очистить поля");
    buttonReset.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            textFieldFrom.setText("0.0");
            textFieldTo.setText("1.0");
            textFieldStep.setText("0.1");
            hBoxResult.removeAll();
            hBoxResult.add(new JPanel());
            saveToTextMenuItem.setEnabled(false);
            saveToGraphicsMenuItem.setEnabled(false);
            getContentPane().validate();
        }
    });
    Box hboxButtons = Box.createHorizontalBox();
    hboxButtons.setBorder(BorderFactory.createBevelBorder(1));
    hboxButtons.add(Box.createHorizontalGlue());
    hboxButtons.add(buttonCalc);
    hboxButtons.add(Box.createHorizontalStrut(30));
    hboxButtons.add(buttonReset);
    hboxButtons.add(Box.createHorizontalGlue());
    hboxButtons.setPreferredSize(new Dimension((int) hboxButtons.getMaximumSize().getWidth(), (int) hboxButtons.getMinimumSize().getHeight() * 2));
    getContentPane().add(hboxButtons, BorderLayout.NORTH);
    hBoxResult = Box.createHorizontalBox();
    hBoxResult.add(new JPanel());
    getContentPane().add(hBoxResult, BorderLayout.CENTER);
}



	protected void saveToGraphicsFile(File selectedFile)
	{ 
		try {
	// Создать новый байтовый поток вывода, направленный в
//	указанный файл
	DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
	// Записать в поток вывода попарно значение X в точке, значение многочлена в точке
	for (int i = 0; i<data.getRowCount(); i++)
	{ 
		out.writeDouble((Double)data.getValueAt(i,0)); out.writeDouble((Double)data.getValueAt(i,1));
	}
	                  // Закрыть поток вывода
	out.close();
	}
		catch (Exception e) {
	// Исключительную ситуацию "ФайлНеНайден" в данном случае можно не обрабатывать,
	// так как мы файл создаѐм, а не открываем для чтения
		}
	}
	protected void saveToTextFile(File selectedFile) { try {
	// Создать новый символьный поток вывода, направленный в указанный файл

	PrintStream out = new PrintStream(selectedFile); // Записать в поток вывода заголовочные сведения

	out.println("Результаты табулирования многочлена по схеме Горнера");
	out.print("Многочлен: ");
	for (int i=0; i<coefficients.length; i++) {
	                        out.print(coefficients[i] + "*X^" + (coefficients.length-i-1));
	if (i!=coefficients.length-1) out.print(" + ");
	}
	out.println("");
	out.println("Интервал от " + data.getFrom() + " до " +
	data.getTo() + " с шагом " + data.getStep());
	out.println("===================================================="); // Записать в поток вывода значения в точках
	for (int i = 0; i<data.getRowCount(); i++) {
	out.println("Значение в точке " + data.getValueAt(i,0) + " равно " + data.getValueAt(i,1));
	}
	                  // Закрыть поток
	out.close();
	} catch (FileNotFoundException e) {
	// Исключительную ситуацию "ФайлНеНайден" можно не
	// обрабатывать, так как мы файл создаѐм, а не открываем
	} }
	
}