import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class PizzaDeliverySettings {

	private static final double[] LEFT_PIZZA_COORDS = {-60.7, 6.2};
	private static final double[] RIGHT_PIZZA_COORDS = {60.7, 6.2};
	private static final double[] LEFT_ROAD_START_COORDS = {-39, 216.6};
	private static final double[] CENTER_ROAD_START_COORDS = {0, 216.6};
	private static final double[] RIGHT_ROAD_START_COORDS = {39, 216.6};

	private int houseNumber;
	private double[] pizzaCoords;
	private double[] roadStartCoords;
	private String deliverySide;

	public PizzaDeliverySettings() {
		houseNumber = 0;
		
		while (true) {
			LCD.clear();
			System.out.println("Left pizza or right pizza?");
			int buttonID = Button.waitForAnyPress();
			if (buttonID == Button.ID_LEFT) {
				setPizzaDirection("LEFT");
				break;
			}
			if (buttonID == Button.ID_RIGHT) {
				setPizzaDirection("RIGHT");
			}
		}
		
		while (true) {			
			LCD.clear();
			System.out.println("Left, middle, or right road?");
			int buttonID = Button.waitForAnyPress();
			if (buttonID == Button.ID_ENTER) {
				setRoadDirection("MIDDLE");
				break;
			}
			if (buttonID == Button.ID_LEFT) {
				setRoadDirection("LEFT");
				break;
			}
			if (buttonID == Button.ID_RIGHT) {
				setRoadDirection("RIGHT");
			}
		}
		
		while (true) {			
			LCD.clear();
			System.out.println("Which side is the house on?");
			int buttonID = Button.waitForAnyPress();
			if (buttonID == Button.ID_LEFT) {
				setPizzaDirection("LEFT");
				break;
			}
			if (buttonID == Button.ID_RIGHT) {
				setPizzaDirection("RIGHT");
			}
		}
		
		while (true) {			
			LCD.clear();
			System.out.println("Which house number on that side?");
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
	
	private void setRoadDirection(String string) {
		
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

	public void setPizzaDirection(String direction) {
		if (direction == "LEFT") {
			pizzaCoords = LEFT_PIZZA_COORDS;
		} else if (direction == "RIGHT") {
			pizzaCoords = RIGHT_PIZZA_COORDS;
		}
	}
}
