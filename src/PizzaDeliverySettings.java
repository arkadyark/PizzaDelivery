import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class PizzaDeliverySettings {
	private static final double[] LEFT_PIZZA_COORDS = {15.1, 36.5, -90};
	private static final double[] RIGHT_PIZZA_COORDS = {15.1, -36.5, 90};
	private static final double[] LEFT_ROAD_START_COORDS = {219, 39, 30};
	private static final double[] CENTER_ROAD_START_COORDS = {216, 0, 0};
	private static final double[] RIGHT_ROAD_START_COORDS = {219, -39, -30};

	private int houseNumber;
	private double[] pizzaCoords;
	private double[] roadStartCoords;
	private String deliverySide;

	public PizzaDeliverySettings() {
		while (true) {
			LCD.clearDisplay();
			LCD.drawString("L/R pizza?", 0, 0);
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
			LCD.drawString("L/M/R road?", 0, 0);
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
			LCD.drawString("L/R house?", 0, 0);
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
		LCD.drawString("House number?", 0, 0);
		incrementHouseNumber();
		LCD.clear(1);
		LCD.drawInt(houseNumber, 0, 1);
		while (true) {		
			int buttonID = Button.waitForAnyPress();
			if (buttonID == Button.ID_UP) {
				incrementHouseNumber();
				LCD.clear(1);
				LCD.drawInt(houseNumber, 0, 1);
			}
			if (buttonID == Button.ID_DOWN) {
				decrementHouseNumber();
				LCD.clear(1);
				LCD.drawInt(houseNumber, 0, 1);
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
		houseNumber = Math.min(houseNumber + 1, 5);
	}
	
	private void decrementHouseNumber() {
		houseNumber = Math.max(houseNumber - 1, 1);
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
