
#include <NewPing.h> 
#include <LiquidCrystal.h>
#include <math.h> 

//Ultrasonic 
#define trigPin 3 
#define echoPin 2
#define MAX_DISTANCE 400 

//LCD
#define rs 7 
#define en 8
#define d4 9 
#define d5 10
#define d6 11
#define d7 12

//#define led 10

NewPing sonar = NewPing(trigPin, echoPin, MAX_DISTANCE); 

LiquidCrystal lcd(rs, en, d4, d5, d6, d7);



void setup()
{
  Serial.begin(9600);
  lcd.begin(16, 2);
  lcd.print("sup"); 
  Serial.println("should be displaying sup");
  delay(1000);
  //pinMode(led, OUTPUT);
  
}

void loop()
{
  
 
  Serial.flush();
  arduinoToAndroid();
  Serial.flush();
  androidToArduino();
  
 
}
