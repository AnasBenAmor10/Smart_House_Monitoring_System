#include <Wire.h>
#include <Keypad.h>
#include <LiquidCrystal_I2C.h>
#include <Servo.h>

Servo myservo;  // create servo object to control a servo
// Pin configuration for keypad
const byte ROW_NUM    = 4; // four rows
const byte COL_NUM    = 4; // four columns
char keys[ROW_NUM][COL_NUM] = {
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};
byte pin_rows[ROW_NUM] ={11, 3, 4, 5} ;  // connect to the row pinouts of the keypad
byte pin_column[COL_NUM] ={6, 7, 8, 9} ; // connect to the column pinouts of the keypad

// LCD with I2C configuration (Adjust the address if necessary, common address is 0x27)
LiquidCrystal_I2C lcd(0x27, 16, 2); // I2C address, columns, rows

// Door control
String correctCode = "2025*"; // Correct access code
String enteredCode = ""; // Variable to store entered code

// Set up the keypad
Keypad keypad = Keypad(makeKeymap(keys), pin_rows, pin_column, ROW_NUM, COL_NUM);

void setup() {

  myservo.attach(10);  // attaches the servo on pin 9 to the servo object
  myservo.write(180);              // tell servo to go to position in variable 'pos'

  lcd.begin();   // Initialize LCD (I2C)
  lcd.backlight();  // Turn on the LCD backlight
  
  lcd.setCursor(0, 0);  
  lcd.print("Enter Code:");
  
}

void loop() {
  char key = keypad.getKey();
  
  if (key) {
    // Display the pressed key on the LCD
    lcd.setCursor(0, 1); // Move to the second row
    lcd.print(key);
    
    // Append the pressed key to the entered code
    enteredCode += key;

    // Check if the entered code is correct
    if (enteredCode.length() == correctCode.length()) {
      if (enteredCode == correctCode) {
        openDoor();
        enteredCode = ""; // Reset the entered code after correct input
      } else {
        lcd.clear();
        lcd.print("Incorrect code!");
        delay(2000);
        lcd.clear();
        lcd.print("Enter Code:");
        enteredCode = ""; // Reset the entered code on failure
      }
    }
  }
}

void openDoor() {
  lcd.clear();
  lcd.print("Welcome!");
  myservo.write(0);            
  delay(10000); 
  myservo.write(180);              
  lcd.clear();
  lcd.print("Enter Code:");
}
