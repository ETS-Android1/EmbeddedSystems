#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

const char* ssid = "ChrisEze";
const char* password = "33339999";

int relay1 = D5;  //Wemos pin D5
int relay2 = D6;  //Wemos pin D6
int led = D6;

String payload;
int btnVal1, btnVal2;

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
       http.begin("http://192.168.43.242:5050");
       httpCode = http.GET();
       
       if(httpCode > 1){
           payload = http.getString();
           JsonObject& outletJSON = jsonBuffer.parseObject(payload);
           
           String outlet1 = outletJSON["Outlet1"];
           String outlet2 = outletJSON["Outlet2"];

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

//  No saving to database from here, only from arduino
       //SAVE OUTLET
       Serial.println("************SAVE OUTLET**************");

       StaticJsonBuffer<200> jsonBuffer2;
       JsonObject& outletEncoder = jsonBuffer2.createObject();

       btnVal1 = digitalRead(relay1);
       btnVal2 = digitalRead(relay2);

       if(btnVal1 == 1) { outletEncoder["Outlet1"] = "0"; }
       else { outletEncoder["Outlet1"] = "1"; }

       if(btnVal2 == 1) { outletEncoder["Outlet2"] = "0"; }
       else { outletEncoder["Outlet2"] = "1"; }

       char outletJSONMessage[300];
       outletEncoder.prettyPrintTo(outletJSONMessage, sizeof(outletJSONMessage));
       
       Serial.println(outletJSONMessage);
       
       http.begin("http://192.168.43.242:5050");
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
  }
