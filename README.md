# Calhacks6-Hydrohelper
![](logo.png)

Hydrohelper is a waterbottle which helps its users keep track of water consumption. The smart bottle keeps track of how much water one has drank in any given day and sends that information to a companion app over Bluetooth. 

## Overview
Back-end code for the Hydrohelper water bottle. Hardware consists of an Arduino Nano, HC-06 Bluetooth module, HC-SR04 Ultrasonic sensor, and 16x2 Arduino LCD. The code manages these devices and communicates information to the companion Android app. 


## How It Works
### BluetoothToAndroid1.ino
Driver code for the entire backend. Manages communication of main board with peripheral devices. 

### Arduino_to_Phone.ino
Manages communication of data from the Arduino to the app through the Bluetooth module. Ultrasonic readings of water level are taken in one minute intervals. If a change is detected between readings, more readings are taken to determine whether the reading was extraneous. If the reading is not extraneous, the new reading is sent to the phone. 

### Phone_to_Arduino.ino 
Manages communication of data from the app to the Arduino through the Bluetooth module. The app uses the data sent to it by the Arduino and computes how much water the user still needs to drink, and what percent of their recommended intake they have already drank. The app sends this information back to the bottle, where it is displayed on the Arduino LCD display. 

### HelperFunctions.ino 
Helper functions average() and calculateSD() to allow Arduino_to_Phone to perform some statistical analysis and determine whether or not to send its readings to the app. 

## Usage 
Download the app .apk file onto an Android phone (targeting Android 9). Connect to "HC-06" via Bluetooth using password '1234'. Turn on the bottle and then click Connect within the app. 
Tracking will begin immediately. The current implementation takes measurements every 5 seconds and skips some statistical analysis methods for testing purposes. In real world use, these methods would be enabled and measurements would be taken in 60 second intervals. 
