package bsu.rfe.java.group8.lab1.Zhlobich.varA7;

public class Milkshake  extends Food {
	
		private String flover;
		static int Count = 0;

		public Milkshake(String flover) {
			super ("Коктейль");
			this.flover = flover;
		}
		public void consume() 
			{
			System.out.println(this + "выпит");
			}
		public String getColor() 
			{
			return flover; 
			}
		
		public void setColor(String color) 
		{
			this.flover = flover;
		}

		public String toString()
		{
			return super.toString() + " вкуса '" + flover.toUpperCase() + "'";
		}
	

}
  