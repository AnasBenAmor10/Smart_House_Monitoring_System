#include <DHT11.h>

// Pin Definitions
#define DHT_PIN 2               
#define AIRQUALITY_PIN 3  
#define PIR_PIN 4
#define LIGHT_BEDROOM_PIN 5
#define LIGHT_LIVINGROOM_PIN 6
#define BUZZER_PIN 48
#define AC_PIN 10       
#define ELECTROVALVE_PIN 11    
#define DIR_PIN2 12
#define DIR_PIN1 13  

#define WATERCONSUMPTION_PIN_A0  A0
#define WATERCONSUMPTION_PIN_A1  A1 
#define WATERCONSUMPTION_PIN_A2  A2 

#define MotorDelay 900 
#define DHT_TYPE DHT11
DHT11 dht11(DHT_PIN);

float temperature = 0.0;
float humidity = 0.0;
int airQuality = 0;
int pirStatus = 0;
int waterConsumptionHouse = 0;
int waterConsumptionKitchen = 0;
int waterConsumptionGarden = 0;
unsigned long lightOnTime = 0;
bool lightOn = false;
bool Alarm= false ; 
bool PERSON_DETECTED = false ; 

void setup() {
  Serial.begin(9600);
  dht11.setDelay(2000);
  
  pinMode(LIGHT_BEDROOM_PIN, OUTPUT);
  digitalWrite(LIGHT_BEDROOM_PIN,LOW);

  pinMode(LIGHT_LIVINGROOM_PIN, OUTPUT);
  digitalWrite(LIGHT_LIVINGROOM_PIN,HIGH);

  pinMode(DIR_PIN1, OUTPUT);
  pinMode(DIR_PIN2, OUTPUT);
  analogWrite(DIR_PIN1,0);
  analogWrite(DIR_PIN2,0);

  pinMode(AC_PIN, OUTPUT);
  digitalWrite(AC_PIN,HIGH);

  pinMode(ELECTROVALVE_PIN, OUTPUT);
  digitalWrite(ELECTROVALVE_PIN,HIGH);
 
  pinMode(AIRQUALITY_PIN, INPUT);
  pinMode(PIR_PIN, INPUT);
  pinMode(WATERCONSUMPTION_PIN_A0, INPUT);
  pinMode(WATERCONSUMPTION_PIN_A1, INPUT);
  pinMode(WATERCONSUMPTION_PIN_A2, INPUT);

  pinMode(BUZZER_PIN, OUTPUT);
}

void openGarageDoor() {
  analogWrite(DIR_PIN1, 100);
  analogWrite(DIR_PIN2, 0);
  delay(MotorDelay);
  analogWrite(DIR_PIN1, 0);

}

void closeGarageDoor() {
  analogWrite(DIR_PIN1, 0);
  analogWrite(DIR_PIN2, 100);
  delay(MotorDelay);
  analogWrite(DIR_PIN2, 0);
}


void loop() {

  unsigned long currentMillis = millis();

  temperature = dht11.readTemperature();     
  humidity = dht11.readHumidity();           
  airQuality = analogRead(AIRQUALITY_PIN); 
  pirStatus = digitalRead(PIR_PIN);       

  waterConsumptionHouse = analogRead(WATERCONSUMPTION_PIN_A0);
  waterConsumptionKitchen = analogRead(WATERCONSUMPTION_PIN_A1);
  waterConsumptionGarden = analogRead(WATERCONSUMPTION_PIN_A2);

  String output = "Temperature:" + String(temperature) + 
                  ",Humidity:" + String(humidity) +
                  ",AirQuality:" + String(airQuality) + 
                  ",WaterConsumptionHouse:" + String(waterConsumptionHouse) + 
                  ",WaterConsumptionKitchen:" + String(waterConsumptionKitchen) + 
                  ",WaterConsumptionGarden:" + String(waterConsumptionGarden);

  Serial.println(output);


if (Serial.available() > 0) {
  String command = Serial.readString(); 
  command.trim(); 

  if (command == "LIGHT") {
    digitalWrite(LIGHT_LIVINGROOM_PIN, !digitalRead(LIGHT_LIVINGROOM_PIN));
  }

  else if (command == "AC") {
    digitalWrite(AC_PIN, !digitalRead(AC_PIN));
  }

  else if (command == "EV") {
    digitalWrite(ELECTROVALVE_PIN, !digitalRead(ELECTROVALVE_PIN));
  }

  else if (command == "GO") {
    openGarageDoor();
  } 

  else if (command == "GC") {
    closeGarageDoor();
  }

  else if (command == "ALARM_ON") {
    Alarm=true ; 
  }

  else if (command == "ALARM_OFF") {
    Alarm=false ; 
  }

  else if (command == "triggerAlarm") {
    PERSON_DETECTED=true ; 
  }



}

    if (Alarm && PERSON_DETECTED) {
      digitalWrite(BUZZER_PIN, HIGH);
    } 
    // else if (!Alarm) {
    //   digitalWrite(BUZZER_PIN, LOW);
    //   PERSON_DETECTED=false ; 
    // }

  delay(500);
  
}

