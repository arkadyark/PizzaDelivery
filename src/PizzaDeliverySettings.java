import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class PizzaDeliverySettings {
	private static final double[] LEFT_PIZZA_COORDS = {6.2, 41.7, 90};
	private static final double[] RIGHT_PIZZA_COORDS = {6.2, -41.7, -90};
	private static final double[] LEFT_ROAD_START_COORDS = {216.6, 39, 30};
	private static final double[] CENTER_ROAD_START_COORDS = {216.6, 0, 0};
	private static final double[] RIGHT_ROAD_START_COORDS = {216.6, -39, -30};

	private int houseNumber;
	private double[] pizzaCoords;
	private double[] roadStartCoords;
	private String deliverySide;

	public PizzaDeliverySettings() {
		while (true) {
			LCD.clearDisplay();
<<<<<<< HEAD
			LCD.drawString("Left or right pizza?", 2, 2);
=======
			System.out.println("Left pizza or right pizza?");
>>>>>>> 4c920af249bf26ac547b3c8e1c2ef75d4d5a488a
			int buttonID = Button.waitForAnyPress();
			if (buttonID == Button.ID_LEFT) {
				setPizzaDirection("LEFT");
				break;
			}
			if (buttonID == Button.ID_RIGHT) {
				setPizzaDirection("RIGHT");
				break;
			}
		}
		
		while (true) {			
			LCD.clearDisplay();
<<<<<<< HEAD
			LCD.drawString("Left, middle or right road?", 2, 2);
=======
			System.out.println("Left, middle, or right road?");
>>>>>>> 4c920af249bf26ac547b3c8e1c2ef75d4d5a488a
			int buttonID = Button.waitForAnyPress();
			if (buttonID == Button.ID_ENTER) {
				roadStartCoords = CENTER_ROAD_START_COORDS;
				break;
			}
			if (buttonID == Button.ID_LEFT) {
				roadStartCoords = LEFT_ROAD_START_COORDS;
				break;
			}
			if (buttonID == Button.ID_RIGHT) {
				roadStartCoords = RIGHT_ROAD_START_COORDS;
				break;
			}
		}
		
		while (true) {			
			LCD.clearDisplay();
<<<<<<< HEAD
			LCD.drawString("Which side is the house on?", 2, 2);
=======
			System.out.println("Which side is the house on?");
>>>>>>> 4c920af249bf26ac547b3c8e1c2ef75d4d5a488a
			int buttonID = Button.waitForAnyPress();
			if (buttonID == Button.ID_LEFT) {
				deliverySide = "LEFT";
				break;
			}
			if (buttonID == Button.ID_RIGHT) {
				deliverySide = "RIGHT";
				break;
				
			}
		}
		
		houseNumber = 0;
		LCD.clearDisplay();
<<<<<<< HEAD
		LCD.drawString("Which house number on that side?", 2, 2);
=======
		System.out.println("Which house number on that side?");
>>>>>>> 4c920af249bf26ac547b3c8e1c2ef75d4d5a488a
		while (true) {			
			int buttonID = Button.waitForAnyPress();
			if (buttonID == Button.ID_UP) {
				incrementHouseNumber();
				System.out.println(houseNumber);
			}
			if (buttonID == Button.ID_DOWN) {
				decrementHouseNumber();
				System.out.println(houseNumber);
			}
			if (buttonID == Button.ID_ENTER) {
				break;
			}
		}
	}
	
	public PizzaDeliverySettings(boolean useDefaults) {
		setPizzaDirection("LEFT");
		roadStartCoords = LEFT_ROAD_START_COORDS;
		deliverySide = "LEFT";
		incrementHouseNumber();
		incrementHouseNumber();
	}
	
	public void setPizzaDirection(String direction) {
		if (direction == "LEFT") {
			pizzaCoords = LEFT_PIZZA_COORDS;
		} else if (direction == "RIGHT") {
			pizzaCoords = RIGHT_PIZZA_COORDS;
		}
	}
	
	public void incrementHouseNumber() {
		houseNumber++;
	}
	
	private void decrementHouseNumber() {
		houseNumber--;
	}

	public int getHouseNumber() {
		return houseNumber;
	}
	
	public double[] getPizzaCoords() {
		return pizzaCoords;
	}

	public double[] getRoadCoords() {
		return roadStartCoords;
	}
	
	public String getDeliverySide() {
		return deliverySide;
	}
}
