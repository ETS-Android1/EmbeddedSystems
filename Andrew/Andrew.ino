#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include "RTClib.h"

const char* ssid = "AndrewGreen";
const char* password = "andrew2386";

int relay1 = D5;  //Wemos pin D5
int relay2 = D6;  //Wemos pin D6

LiquidCrystal_I2C  lcd1(0x3F,2,1,0,4,5,6,7);
LiquidCrystal_I2C  lcd2(0x3E,2,1,0,4,5,6,7);

RTC_DS1307 rtc;
char daysOfTheWeek[7][12] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

int btnVal1, btnVal2;

String payload;
unsigned long timer = 0;

void setup(){
  Serial.begin(115200);
  WiFi.begin(ssid, password);
  
  pinMode(relay1, OUTPUT);
  pinMode(relay2, OUTPUT);

  digitalWrite(relay1, HIGH);
  digitalWrite(relay2, HIGH);
  
}
 
void loop(){

   if (WiFi.status() == WL_CONNECTED) {
       
       HTTPClient http;
       StaticJsonBuffer<200> jsonBuffer;
       int httpCode;

       //GET OUTLET
       Serial.println("************GET OUTLET**************");
       http.begin("http://192.168.43.64:5005");
       httpCode = http.GET();
       
       if(httpCode > 1){
           payload = http.getString();
           JsonObject& outletJSON = jsonBuffer.parseObject(payload);
           
           String outlet1 = outletJSON["outlet1"];
           String outlet2 = outletJSON["outlet2"];

           Serial.print("Outlet1 - ");
           
           if(outlet1 == "1"){
            digitalWrite(relay1, LOW);
            Serial.println("ON");
           } else if(outlet1 == "0") {
            digitalWrite(relay1, HIGH);
            Serial.println("OFF");
           }
           
           Serial.print("Outlet2 - ");
           
           if(outlet2 == "1"){
            digitalWrite(relay2, LOW);
            Serial.println("ON");
           } else if(outlet2 == "0") {
            digitalWrite(relay2, HIGH);
            Serial.println("OFF");
           }

       }
       else{
           Serial.println("Connection failed");
       }

       http.end();

       //GET TIMER
       Serial.println("************GET TIMER**************");
       http.begin("http://192.168.43.64:5005/timer");
       httpCode = http.GET();
       
       if(httpCode > 1){
           payload = http.getString();
           JsonObject& outletJSON = jsonBuffer.parseObject(payload);
           
           String outlet1Time = outletJSON["outlet1"];
           String outlet2Time = outletJSON["outlet2"];

           Serial.print("Outlet1 - ");
           Serial.println(outlet1Time);
           
           Serial.print("Outlet2 - ");
           Serial.println(outlet2Time);

           Serial.print("Timer - ");
           Serial.println(timer);
           
           if(timer >= String(outlet1Time, 3)){
            digitalWrite(relay1, LOW);
           }

           if(timer >= String(outlet2Time, 3)){
            digitalWrite(relay2, LOW);
           }

       }
       else{
           Serial.println("Connection failed");
       }

       http.end();

       //MASTER
       Serial.println("************MASTER**************");
       http.begin("http://192.168.43.64:5005/master");
       httpCode = http.GET();
       
       if(httpCode > 1){
         payload = http.getString();
         JsonObject& master = jsonBuffer.parseObject(payload);
         
         String code = master["master"];

         Serial.print("code - ");
         Serial.println(code);

         if (code == "1"){
            digitalWrite(relay1, HIGH);
            digitalWrite(relay2, HIGH);
         } else {
            digitalWrite(relay1, LOW);
            digitalWrite(relay2, LOW);
         }

       }
       else{
         Serial.println("Connection failed");
       }

       http.end();


//  No saving to database from here, only from arduino
       //SAVE OUTLET
       Serial.println("************SAVE OUTLET**************");

       StaticJsonBuffer<200> jsonBuffer2;
       JsonObject& outletEncoder = jsonBuffer2.createObject();

       btnVal1 = digitalRead(relay1);
       btnVal2 = digitalRead(relay2);

       if(btnVal1 == 1) { outletEncoder["outlet1"] = "0"; }
       else { outletEncoder["outlet1"] = "1"; }

       if(btnVal2 == 1) { outletEncoder["outlet2"] = "0"; }
       else { outletEncoder["outlet2"] = "1"; }

       char outletJSONMessage[300];
       outletEncoder.prettyPrintTo(outletJSONMessage, sizeof(outletJSONMessage));
       
       Serial.println(outletJSONMessage);
       
       http.begin("http://192.168.43.64:5005");
       http.addHeader("Content-Type", "application/json");
       
       httpCode = http.PUT(outletJSONMessage);
   
       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       } else{
         Serial.println("Error in HTTP Request");
       }
       http.end();
          
    } else {
        Serial.println("connection failed");
    }
      delay(3000);
      timer += 1000;
  }

  void saveTimer1(HTTPClient http){
    //SAVE TIMER
       Serial.println("************SAVE TIMER**************");

       StaticJsonBuffer<200> jsonTimer1;
       JsonObject& timer1 = jsonTimer1.createObject();

       timer1["outlet1"] = "20000";

       char timer1JSONMessage[300];
       timer1.prettyPrintTo(timer1JSONMessage, sizeof(timer1JSONMessage));
       
       Serial.println(timer1JSONMessage);
       
       http.begin("http://192.168.43.64:5005/timer");
       http.addHeader("Content-Type", "application/json");
       
       httpCode = http.PUT(timer1JSONMessage);
   
       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       } else{
         Serial.println("Error in HTTP Request");
       }
       http.end();
  }

void saveTimer2(HTTPClient http){
    //SAVE TIMER
       Serial.println("************SAVE TIMER**************");

       StaticJsonBuffer<200> jsonTimer2;
       JsonObject& timer2 = jsonTimer2.createObject();

       timer2["outlet2"] = "20000";

       char timer2JSONMessage[300];
       timer2.prettyPrintTo(timer2JSONMessage, sizeof(timer2JSONMessage));
       
       Serial.println(timer2JSONMessage);
       
       http.begin("http://192.168.43.64:5005/timer");
       http.addHeader("Content-Type", "application/json");
       
       httpCode = http.PUT(timer2JSONMessage);
   
       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       } else{
         Serial.println("Error in HTTP Request");
       }
       http.end();
  }

