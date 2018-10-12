#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

const char* ssid = "ChrisEze";
const char* password = "33339999";

int calibrationTime = 30;       
long unsigned int lowIn;        
long unsigned int pause = 5000; 
boolean lockLow = true;
boolean takeLowTime; 

char* sensor_status;

int PIRPin = D5;
int LEDPin = D6;

void setup() {
  Serial.begin(115200);
  pinMode(PIRPin, INPUT);
  pinMode(LEDPin, OUTPUT);
  WiFi.begin(ssid, password);

  //give the sensor some time to calibrate
  Serial.print("calibrating sensor ");
  for(int i = 0; i < calibrationTime; i++){
      Serial.print(".");
      delay(1000);
   }
     
  Serial.println(" done");
  Serial.println("SENSOR ACTIVE");
  delay(50);
    
}

void loop() {
  if(digitalRead(PIRPin) == HIGH){ //Room isn't empty

      if(lockLow){
        lockLow = false;
        Serial.println("No motion....");
        digitalWrite(LEDPin, HIGH);
        sensor_status = "1";
        
      }
      takeLowTime = true;
      
    }
    
    if(digitalRead(PIRPin) == LOW){ //Room is empty / someone passed

      if(takeLowTime){
        lowIn = millis();
        takeLowTime = false;
      }
      if(!lockLow && (millis() - lowIn > pause)){
        lockLow = true;
        Serial.println("Motion detected!"); 
        digitalWrite(LEDPin, LOW);
        sensor_status = "0";
        
      }
      
    }

    delay(5000);

    if(WiFi.status() == WL_CONNECTED) {

      HTTPClient http;
      StaticJsonBuffer<200> jsonBuffer2;
      JsonObject& outletEncoder = jsonBuffer2.createObject();
      int httpCode;

      outletEncoder["state"] = sensor_status;

      char outletJSONMessage[300];
      outletEncoder.prettyPrintTo(outletJSONMessage, sizeof(outletJSONMessage));
      Serial.println(outletJSONMessage);
      
      http.begin("http://192.168.43.242:5050/sensor");
      http.addHeader("Content-Type", "application/json");
      httpCode = http.PUT(outletJSONMessage);
      
      if(httpCode == HTTP_CODE_OK){
        Serial.println(http.getString());
      } else{
        Serial.println("Error in HTTP Request");
      }
      http.end();

    } else{
      Serial.println("Connection failed!");
    }
    

}
