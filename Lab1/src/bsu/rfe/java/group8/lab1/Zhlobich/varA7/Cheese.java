package bsu.rfe.java.group8.lab1.Zhlobich.varA7;

public class Cheese extends Food {
	static int Count = 0;
	public Cheese() 
		{
		super("Сыр");
		}
	
	public void consume()
	{
		System.out.println(this + " съеден");
	}

}
