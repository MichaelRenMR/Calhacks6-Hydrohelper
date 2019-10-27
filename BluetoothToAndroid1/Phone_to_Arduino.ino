
void androidToArduino() {
  
  char k = Serial.read(); //ASCII 
  int bars = (int) k;
  //bars = (bars > 57 ? (10 + ((bars - 8) % 10)) : (bars-48)); 
  bars -= 97;

  //Serial.print("bars: ");
  //Serial.println(bars);

  if (bars >= 0) {
    String toDisplay = "";
    for (int i = 0; i < bars; i++) {
      toDisplay.concat((char) 255);
    }
    Serial.print("toDisplay: ");
    Serial.println(toDisplay);

    lcd.clear();
    lcd.setCursor(0, 0);    //printing in row 0
    lcd.print("Progress:");
  
    lcd.setCursor(0, 1);   //printing in row 1
    lcd.print(toDisplay);   
  }

   
} 
