#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <Adafruit_ADS1015.h>
#include "RTClib.h"

void initializeLCD();
void initializeRTC();
void initializeWiFi();
void displayInfo(String energy1, String energy2, String cost1, String cost2);
void resetTimer1(int httpCode);
void resetTimer2(int httpCode);
void saveThreshold(int httpCode);
void saveOutlet(int httpCode);
void saveOutlet1(int httpCode);
void saveOutlet2(int httpCode);
void saveEnergy(int httpCode);
void getThreshold(int httpCode);
void getTimed(int httpCode);
void getOutlet(int httpCode);
void triggerPowerStatus(int httpCode);
double ac_read();
float getADSVal();

LiquidCrystal_I2C  lcd1(0x3F,2,1,0,4,5,6,7);
LiquidCrystal_I2C  lcd2(0x3E,2,1,0,4,5,6,7);

RTC_DS1307 rtc;
Adafruit_ADS1115 ads(0x48);

char daysOfTheWeek[7][12] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};


const int relay1 = D5;  //Wemos pin D5
const int relay2 = D6;  //Wemos pin D6


double Amps1 = 0;
double power1 = 0;

double Amps2 = 0;
double power2 = 0;

double energy1 = 0;
double energy2 = 0;

double cost1 = 0;
double cost2 = 0;



unsigned long timer;

int btnVal1, btnVal2;

String val1, val2;

const char* ssid = "ChrisEze";
const char* password = "33339999";

String payload = "";

int status1, status2;


void setup() {
  Serial.begin(115200);
  pinMode(A0, INPUT);
  pinMode(relay1, OUTPUT);
  pinMode(relay2, OUTPUT);

  digitalWrite(relay1, HIGH);
  digitalWrite(relay2, HIGH);

  initializeLCD();
  initializeRTC();
  initializeWiFi();

  
  ads.begin();

}

void loop() {
if (WiFi.status() == WL_CONNECTED) {
      HTTPClient http;
      
      int httpCode;

      Serial.println("Connected!!");

      
//***********************************************************************************//
//=================================GET OPS==========================================//
//*********************************************************************************//

       getOutlet(httpCode);
       
       getTimed(httpCode);
       
       getThreshold(httpCode);

       

//***********************************************************************************//
//=================================PUT/POST OPS=====================================//
//*********************************************************************************//


        saveEnergy(httpCode);

//        saveOutlet(httpCode);

        triggerPowerStatus(httpCode);

       
   
}
else{
  lcd1.home();
  lcd1.clear();
  lcd1.print("Connection lost");
  

  lcd2.home();
  lcd2.clear();
  lcd2.print("Connection lost");
  
  Serial.println("connection failed");
}

 delay(2000);    //Send a request every 2 seconds
 timer += 1;

 

}

void initializeLCD(){
  lcd1.begin (16,2);
  lcd2.begin (16,2);
  
  lcd1.setBacklightPin(3, POSITIVE);
  lcd1.setBacklight(HIGH);

  lcd2.setBacklightPin(3, POSITIVE);
  lcd2.setBacklight(HIGH);

  lcd1.print("You're Welcome");
  lcd1.home();
  
  lcd2.print("You're Welcome");
  lcd2.home();// go home
}

void initializeRTC(){
  rtc.adjust(DateTime(F(__DATE__), F(__TIME__)));
  if (! rtc.begin()) {
    Serial.println("Couldn't find RTC");
    while (1);
  }

  if (! rtc.isrunning()) {
    Serial.println("RTC is NOT running!");
    // following line sets the RTC to the date & time this sketch was compiled
    rtc.adjust(DateTime(F(__DATE__), F(__TIME__)));
    // This line sets the RTC with an explicit date & time, for example to set
    // January 21, 2014 at 3am you would call:
    // rtc.adjust(DateTime(2014, 1, 21, 3, 0, 0));
  }
}


void initializeWiFi(){
  WiFi.begin(ssid, password);

  timer = millis();

  while(WiFi.status() != WL_CONNECTED){
      delay(1000);
      Serial.println("Waiting for connection...");
  }

  Serial.print("\n\nConnecting to ");
  Serial.println(ssid);
}

void displayInfo(String energy1, String energy2, String cost1, String cost2){
   lcd1.home();
   lcd1.clear();
   lcd1.print("Energy: ");
   lcd1.print(energy1);
   lcd1.print("KWh");
   lcd1.setCursor ( 0, 1 );
   lcd1.print("Cost:   ");
   lcd1.print("N");
   lcd1.print(cost1);

   lcd2.home();
   lcd2.clear();
   lcd2.print("Energy: ");
   lcd2.print(energy2);
   lcd2.print("KWh");
   lcd2.setCursor ( 0, 1 );
   lcd2.print("Cost:   ");
   lcd2.print("N");
   lcd2.print(cost2);
}


//float fmap(float x, float in_min, float in_max, float out_min, float out_max) {
//  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
//}

float getADSVal(){
  int16_t adc0;
  adc0 = ads.readADC_SingleEnded(1);
  return (adc0 * 0.1875)/1000;
}

double ac_read() {
  int rVal = 0;
  int maxVal = 0;
  int minVal = 1023;
  int sampleDuration = 100; // 100ms

  uint32_t startTime = millis();
  // take samples for 100ms
  while((millis()-startTime) < sampleDuration)
  {
    rVal = analogRead(A0);
    if (rVal > maxVal) 
      maxVal = rVal;

    if (rVal < minVal) 
      minVal = rVal;
  }

  // Subtract min from max to determine the peak to peak range
  // 1023 = the max value we'll get on the input (1024, zero indexed)
  // 5.0 = 5v total on adc input
  double volt = ((maxVal - minVal) * (5.0/1023.0));

  // div by 2 is to calculate RMS from peak to peak
  // 0.35355 is factor to calculate RMS from peak to peak
  // see http://www.learningaboutelectronics.com/Articles/Voltage-rms-calculator.php
  double voltRMS = volt * 0.35355;

  // x 1000 to convert volts to millivolts
  // divide by the number of millivolts per amp to determine amps measured
  // the 20A module 100 mv/A (so in this case ampsRMS = voltRMS
  double ampsRMS = (voltRMS * 1000)/100;
  return ampsRMS;
}


int switchCase(String value){
    int outlet1Timee;
    if(value == "0") outlet1Timee = 0;
    else if(value == "1") outlet1Timee = 1; 
    else if(value == "2") outlet1Timee = 2; 
    else if(value == "3") outlet1Timee = 3;
    else if(value == "4") outlet1Timee = 4;
    else if(value == "5") outlet1Timee = 5;
    else if(value == "6") outlet1Timee = 6;
    else if(value == "7") outlet1Timee = 7;
    else if(value == "8") outlet1Timee = 8;
    else if(value == "9") outlet1Timee = 9;
    else if(value == "10") outlet1Timee = 10;
    else if(value == "11") outlet1Timee = 11;
    else if(value == "12") outlet1Timee = 12;
    else if(value == "13") outlet1Timee = 13;
    else if(value == "14") outlet1Timee = 14;
    else if(value == "15") outlet1Timee = 15;
    else if(value == "16") outlet1Timee = 16;
    else if(value == "17") outlet1Timee = 17;
    else if(value == "18") outlet1Timee = 18;
    else if(value == "19") outlet1Timee = 19;
    else if(value == "20") outlet1Timee = 20;
    else if(value == "21") outlet1Timee = 21;
    else if(value == "22") outlet1Timee = 22;
    else if(value == "23") outlet1Timee = 23;
    else if(value == "24") outlet1Timee = 24;
    else if(value == "25") outlet1Timee = 25;
    else if(value == "26") outlet1Timee = 26;
    else if(value == "27") outlet1Timee = 27;
    else if(value == "28") outlet1Timee = 28;
    else if(value == "29") outlet1Timee = 29;
    else if(value == "30") outlet1Timee = 30;
    else if(value == "31") outlet1Timee = 31;
    else if(value == "32") outlet1Timee = 32;
    else if(value == "33") outlet1Timee = 33;
    else if(value == "34") outlet1Timee = 34;
    else if(value == "35") outlet1Timee = 35;
    else if(value == "36") outlet1Timee = 36;
    else if(value == "37") outlet1Timee = 37;
    else if(value == "38") outlet1Timee = 38;
    else if(value == "39") outlet1Timee = 39;
    else if(value == "40") outlet1Timee = 40;
    else if(value == "41") outlet1Timee = 41;
    else if(value == "42") outlet1Timee = 42;
    else if(value == "43") outlet1Timee = 43;
    else if(value == "44") outlet1Timee = 44;
    else if(value == "45") outlet1Timee = 45;
    else if(value == "46") outlet1Timee = 46;
    else if(value == "47") outlet1Timee = 47;
    else if(value == "48") outlet1Timee = 48;
    else if(value == "49") outlet1Timee = 49;
    else if(value == "50") outlet1Timee = 50;
    else if(value == "51") outlet1Timee = 51;
    else if(value == "52") outlet1Timee = 52;
    else if(value == "53") outlet1Timee = 53;
    else if(value == "54") outlet1Timee = 54;
    else if(value == "55") outlet1Timee = 55;
    else if(value == "56") outlet1Timee = 56;
    else if(value == "57") outlet1Timee = 57;
    else if(value == "58") outlet1Timee = 58;
    else if(value == "59") outlet1Timee = 59;
    else if(value == "60") outlet1Timee = 60; 
    
    return outlet1Timee;
  }


void resetTimer1(int httpCode){
  //SAVE TIMER
       Serial.println("************SAVE TIMER**************");

       StaticJsonBuffer<200> jsonBufferTimer;
       JsonObject& timedEncoder = jsonBufferTimer.createObject();
       HTTPClient http;

       timedEncoder["tag1"] = "-1";       

       char timedJSONMessage[300];
       timedEncoder.prettyPrintTo(timedJSONMessage, sizeof(timedJSONMessage));
       Serial.println(timedJSONMessage);

       http.begin("http://192.168.43.242:5000/timed");
       http.addHeader("Content-Type", "application/json");
       httpCode = http.PUT(timedJSONMessage);
       
       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       }
       else{
         Serial.println("Error in HTTP Request");
       }

      http.end();
}

void resetTimer2(int httpCode){
  //SAVE TIMER
       Serial.println("************SAVE TIMER**************");

       StaticJsonBuffer<200> jsonBufferTimer;
       JsonObject& timedEncoder = jsonBufferTimer.createObject();
       HTTPClient http;

       timedEncoder["tag2"] = "-1";

       char timedJSONMessage[300];
       timedEncoder.prettyPrintTo(timedJSONMessage, sizeof(timedJSONMessage));
       Serial.println(timedJSONMessage);

       http.begin("http://192.168.43.242:5000/timed");
       http.addHeader("Content-Type", "application/json");
       httpCode = http.PUT(timedJSONMessage);
       
       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       }
       else{
         Serial.println("Error in HTTP Request");
       }

      http.end();
}

void resetThreshold1(int httpCode){
         //SAVE THRESHOLD
       Serial.println("************SAVE THRESHOLD**************");
       
       StaticJsonBuffer<200> jsonBufferThreshold;
       JsonObject& thresholdEncoder = jsonBufferThreshold.createObject();
       HTTPClient http;

       thresholdEncoder["outlet1"] = "-1";

       char thresholdJSONMessage[300];
       thresholdEncoder.prettyPrintTo(thresholdJSONMessage, sizeof(thresholdJSONMessage));
       Serial.println(thresholdJSONMessage);

       http.begin("http://192.168.43.242:5000/threshold");
       http.addHeader("Content-Type", "application/json");
       httpCode = http.PUT(thresholdJSONMessage);

       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       }
       else{
         Serial.println("Error in HTTP Request");
       }

       http.end();
}

void resetThreshold2(int httpCode){
         //SAVE THRESHOLD
       Serial.println("************SAVE THRESHOLD**************");
       
       StaticJsonBuffer<200> jsonBufferThreshold;
       JsonObject& thresholdEncoder = jsonBufferThreshold.createObject();
       HTTPClient http;

       thresholdEncoder["outlet2"] = "-1";

       char thresholdJSONMessage[300];
       thresholdEncoder.prettyPrintTo(thresholdJSONMessage, sizeof(thresholdJSONMessage));
       Serial.println(thresholdJSONMessage);

       http.begin("http://192.168.43.242:5000/threshold");
       http.addHeader("Content-Type", "application/json");
       httpCode = http.PUT(thresholdJSONMessage);

       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       }
       else{
         Serial.println("Error in HTTP Request");
       }

       http.end();
}

void saveOutlet(int httpCode){
  //SAVE OUTLET
       Serial.println("************SAVE OUTLET**************");

       StaticJsonBuffer<200> jsonBufferOutlet;
       JsonObject& outletEncoder = jsonBufferOutlet.createObject();
       HTTPClient http;

         if(digitalRead(relay1) == HIGH) { outletEncoder["state1"] = "1"; }
         else { outletEncoder["state1"] = "0"; }
  
         if(digitalRead(relay2) == HIGH) { outletEncoder["state2"] = "1"; }
         else { outletEncoder["state2"] = "0"; }
  
         char outletJSONMessage[300];
         outletEncoder.prettyPrintTo(outletJSONMessage, sizeof(outletJSONMessage));
         Serial.println(outletJSONMessage);
         
         http.begin("http://192.168.43.242:5000/outlet");
         http.addHeader("Content-Type", "application/json");
         httpCode = http.PUT(outletJSONMessage);
     
         if(httpCode == HTTP_CODE_OK){
           Serial.println(http.getString());
         }
         else{
           Serial.println("Error in HTTP Request");
         }
  
         http.end();
       
       
}

void saveOutlet1(int httpCode){
  //SAVE OUTLET
       Serial.println("************SAVE OUTLET**************");

       StaticJsonBuffer<200> jsonBufferOutlet;
       JsonObject& outletEncoder = jsonBufferOutlet.createObject();
       HTTPClient http;

       
       outletEncoder["state1"] = "0";
  
       char outletJSONMessage[300];
       outletEncoder.prettyPrintTo(outletJSONMessage, sizeof(outletJSONMessage));
       Serial.println(outletJSONMessage);
       
       http.begin("http://192.168.43.242:5000/outlet");
       http.addHeader("Content-Type", "application/json");
       httpCode = http.PUT(outletJSONMessage);
   
       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       }
       else{
         Serial.println("Error in HTTP Request");
       }

       http.end();
   
}

void saveOutlet2(int httpCode){
  //SAVE OUTLET
       Serial.println("************SAVE OUTLET**************");

       StaticJsonBuffer<200> jsonBufferOutlet;
       JsonObject& outletEncoder = jsonBufferOutlet.createObject();
       HTTPClient http;

       
       outletEncoder["state2"] = "0";
  
       char outletJSONMessage[300];
       outletEncoder.prettyPrintTo(outletJSONMessage, sizeof(outletJSONMessage));
       Serial.println(outletJSONMessage);
       
       http.begin("http://192.168.43.242:5000/outlet");
       http.addHeader("Content-Type", "application/json");
       httpCode = http.PUT(outletJSONMessage);
   
       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       }
       else{
         Serial.println("Error in HTTP Request");
       }

       http.end();
     
}

void saveEnergy(int httpCode){
         //SAVE ENERGY
      HTTPClient http;

//      Amps1 = (fabs(fmap(analogRead(A0), 0.0, 1023.0, 0.01, 5.0)));
      Amps1 = ac_read() * 1.2;
      power1 = Amps1 * 230;

      Serial.print("analogRead(A0) ");
      Serial.println(analogRead(A0));

      Serial.print("Outlet1 Amps: ");
      Serial.println(Amps1);

      Amps2 = getADSVal() * 0.3373;
      power2 = Amps2 * 230;

      Serial.print("ads.readADC_SingleEnded(1) ");
      Serial.println(ads.readADC_SingleEnded(1));

      Serial.print("Outlet2 Amps: ");
      Serial.println(Amps2);

       energy1 = energy1 + (power1 * 2)/(1000 * 60 * 60);
       energy2 = energy2 + (power2 * 2)/(1000 * 60 * 60);

       cost1 = energy1 * 24.3;
       cost2 = energy2 * 24.3;

       displayInfo(String(energy1, 2), String(energy2, 2), String(cost1, 2), String(cost2, 2));

       Serial.println("************SAVE ENERGY**************");
       StaticJsonBuffer<200> jsonBufferEnergy1;
       JsonObject& energyEncoder1 = jsonBufferEnergy1.createObject();

       char energyJSONMessage1[300];

       energyEncoder1["outlet_id"] = "1";
       energyEncoder1["energy_value"] = String(energy1, 2);
       energyEncoder1["timestamp"] = ""; 

       energyEncoder1.prettyPrintTo(energyJSONMessage1, sizeof(energyJSONMessage1));
       
       http.begin("http://192.168.43.242:5000");
       http.addHeader("Content-Type", "application/json");
       httpCode = http.POST(energyJSONMessage1);
   
       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       }
       else{
         Serial.println("Error in HTTP Request");
       }

       http.end();

       StaticJsonBuffer<200> jsonBufferEnergy2;
       JsonObject& energyEncoder2 = jsonBufferEnergy2.createObject();

       char energyJSONMessage2[300];

       energyEncoder2["outlet_id"] = "2";
       energyEncoder2["energy_value"] = String(energy2, 2);
       energyEncoder2["timestamp"] = ""; 

       energyEncoder2.prettyPrintTo(energyJSONMessage2, sizeof(energyJSONMessage2));
       
       http.begin("http://192.168.43.242:5000");
       http.addHeader("Content-Type", "application/json");
       httpCode = http.POST(energyJSONMessage2);

   
       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       }
       else{
         Serial.println("Error in HTTP Request");
       }

       http.end();
}

void getThreshold(int httpCode){
  //THRESHOLD
       HTTPClient http;
       StaticJsonBuffer<200> jsonBuffer;
       
       Serial.println("************THRESHOLD**************");
       http.begin("http://192.168.43.242:5000/threshold");
       httpCode = http.GET();

       if(httpCode > 1){
         payload = http.getString();
         JsonObject& threshold = jsonBuffer.parseObject(payload);

         String outlet1 = threshold["Outlet1"];
         String outlet2 = threshold["Outlet2"];

//         Serial.print("Outlet1 - ");
//         Serial.println(outlet1);
//
//         Serial.print("Outlet2 - ");
//         Serial.println(outlet2);

         String energyOut1 = String(energy1, 2);
         String energyOut2 = String(energy2, 2);
 
         if(energyOut1 >= outlet1){
          digitalWrite(relay1, LOW);
          resetThreshold1(httpCode);
         }

         if(energyOut2 >= outlet2){
          digitalWrite(relay2, LOW);
          resetThreshold2(httpCode);
         }

       }
       else{
         Serial.println("Connection failed");
       }

       http.end();
}

void getTimed(int httpCode){
  //TIMED
       HTTPClient http;
       StaticJsonBuffer<200> jsonBuffer;
       
       Serial.println("************TIMED**************");
       http.begin("http://192.168.43.242:5000/timed");
       httpCode = http.GET();

       if(httpCode > 1){
           payload = http.getString();
           JsonObject& timed = jsonBuffer.parseObject(payload);

           String outlet1_hour = timed["Outlet1 Hour"];
           String outlet1_min  = timed["Outlet1 Min"];
           String outlet2_hour = timed["Outlet2 Hour"];
           String outlet2_min  = timed["Outlet2 Min"];
           String tag1  = timed["Tag1"];
           String tag2  = timed["Tag2"];

           Serial.print("Outlet1 Hour - ");
           Serial.println(outlet1_hour);
           Serial.print("Outlet1 Min - ");
           Serial.println(outlet1_min);
           Serial.print("Outlet2 Hour - ");
           Serial.println(outlet2_hour);
           Serial.print("Outlet2 Min - ");
           Serial.println(outlet2_min);
           Serial.print("Tag1 - ");
           Serial.println(tag1);
           Serial.print("Tag2 - ");
           Serial.println(tag2);

           DateTime now = rtc.now();
           Serial.print("The time is: ");
         
//           Serial.print(now.hour(), DEC);
//           Serial.print(':');
//           Serial.println(now.minute(), DEC);

           Serial.print(now.hour());
           Serial.print(':');
           Serial.println(now.minute());

           int hour1 = switchCase(outlet1_hour);
           int min1 = switchCase(outlet1_min);

           int hour2 = switchCase(outlet2_hour);
           int min2 = switchCase(outlet2_min);

           if(tag1 == "1"){
            if(hour1 <= now.hour() && min1 <= (now.minute() + 2)){
                 digitalWrite(relay1, HIGH);
                 lcd1.setBacklight(LOW);
                 resetTimer1(httpCode);
                 saveOutlet1(httpCode);
                 
             }  
           }

           if(tag2 == "1"){
             if(hour2 <= now.hour() && min2 <= (now.minute() + 2)){
                 digitalWrite(relay2, HIGH);
                 lcd2.setBacklight(LOW);
                 resetTimer2(httpCode);
                 saveOutlet2(httpCode);
                 
             }
           }
           
       }
       else{
         Serial.println("Connection failed");
       }

       http.end();
}

void getOutlet(int httpCode){
  //OUTLET
       HTTPClient http;
       StaticJsonBuffer<200> jsonBuffer;
       
       Serial.println("************OUTLET**************");
       http.begin("http://192.168.43.242:5000/outlet");
       httpCode = http.GET();
       
       if(httpCode > 1){
           payload = http.getString();
           JsonObject& outletJSON = jsonBuffer.parseObject(payload);
           
           String outlet1 = outletJSON["Outlet1"];
           String outlet2 = outletJSON["Outlet2"];

           Serial.print("Outlet1 - ");


           if (outlet1 == "1"){
             digitalWrite(relay1, LOW);
             Serial.println("ON");
             lcd1.setBacklight(HIGH);
             status1 = 1;
           } 
           
           if (outlet1 == "0"){
             digitalWrite(relay1, HIGH);
             Serial.println("OFF");
             lcd1.setBacklight(LOW);
             status1 = 0;
           }
           
           Serial.print("Outlet2 - ");
           
           if (outlet2 == "1"){
             digitalWrite(relay2, LOW);
             Serial.println("ON");
             lcd2.setBacklight(HIGH);
             status2 = 1;
           } 
           if (outlet2 == "0"){
             digitalWrite(relay2, HIGH);
             Serial.println("OFF");
             lcd2.setBacklight(LOW);
             status2 = 0;
           }

       }
       else{
           Serial.println("Connection failed");
       }

       http.end();
}

void triggerPowerStatus(int httpCode){
  //SAVE POWER STATUS
       Serial.println("************SAVE POWER STATUS**************");
       
       StaticJsonBuffer<200> jsonBuffer;
       JsonObject& jsonEncoder = jsonBuffer.createObject();
       HTTPClient http;

       jsonEncoder["status"] = "";

       char JSONMessage[300];
       jsonEncoder.prettyPrintTo(JSONMessage, sizeof(JSONMessage));

       http.begin("http://192.168.43.242:5000/status");
       http.addHeader("Content-Type", "application/json");
       httpCode = http.PUT(JSONMessage);

       if(httpCode == HTTP_CODE_OK){
         Serial.println(http.getString());
       }
       else{
         Serial.println("Error in HTTP Request");
       }

       http.end();
}

