package bsu.rfe.java.group8.lab1.Zhlobich.varA7;

public class MainApp {

	
	public static void main(String[] args) throws Exception {

		Food[] breakfast = new Food[200];
		
		Milkshake milkshake = new Milkshake (null);
		Cheese cheese = new Cheese();
		Apple apple = new Apple(null);
		
		int itemsSoFar = 0;
		for (String arg: args) 
		{
		String[] parts = arg.split("/");
		if (parts[0].equals("Milkshake"))
		{
			breakfast[itemsSoFar] = new Milkshake(parts[1]);
		}
		
		else
			if (parts[0].equals("Cheese"))	
			{	
			breakfast[itemsSoFar] = new Cheese();
			}
		
		else 
			if (parts[0].equals("Apple"))
			{
			breakfast[itemsSoFar] = new Apple(parts[1]);
			}
		
		itemsSoFar++;
		}
		
		for (Food item : breakfast)  
		//на каждой итерации цикла переменная будет ссылаться на текущий элемент массива 
		if (item!=null) 
		{
			if (item.equals(milkshake)) Milkshake.Count++;
			if (item.equals(cheese)) Cheese.Count++;
			if (item.equals(apple)) Apple.Count++;
			item.consume();//вызывает метод для объекта
		}  
		else  
			break;
			
	     System.out.println("Съедено сыра: " + Cheese.Count);
	     System.out.println("Съедено яблок: " + Apple.Count);
	     System.out.println("Выпито коктелей: " + Milkshake.Count);	
		 System.out.println("Всего хорошего!");
		}
}
