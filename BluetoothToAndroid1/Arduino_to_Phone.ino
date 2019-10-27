


void arduinoToAndroid() {


  sendData(sonar.ping_cm());
  //Serial.println(sonar.ping_cm());
 
  delay(5000); //delay 1 seconds
}


int distances[10];
int cur_dist = 0;
int avg_dist;
float std_dev;
int reading;

int sendData(int new_dist) { 
  
  if (abs(new_dist - cur_dist) > 1) {
    //Serial.println("\t\tWe're verifying.");
    
    for (int i = 0; i < 10; i++) {    //verify that it's not an extraneous reaading. 
      reading = sonar.ping_cm();
      
      /*
      if (reading < 2 || reading > 25) {
        continue;
      }
      */
      distances[i] = reading; 
      
      delay(100);
    }
    
    avg_dist = average(distances, 10);
    std_dev = calculateSD( distances, 10);
     
    
    if (abs(avg_dist - cur_dist) > 1 && std_dev < 1.5) {
      cur_dist = avg_dist;
      
      //Serial.print("\tUpdated current dist: ");  //send the reading if it's not extraneous
      //Serial.println(cur_dist);
    }
    else {
      //Serial.print("\tNo update. Current dist is still: ");
      //Serial.println(cur_dist);
    }
  }
  Serial.println(new_dist);

/*
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Progress: ");
  lcd.setCursor(0, 1);
  lcd.print(new_dist);
  */
}
