/*
  Button

  Turns on and off a light emitting diode(LED) connected to digital pin 13,
  when pressing a pushbutton attached to pin 2.

  The circuit:
  - LED attached from pin 13 to ground
  - pushbutton attached to pin 2 from +5V
  - 10K resistor attached to pin 2 from ground

  - Note: on most Arduinos there is already an LED on the board
    attached to pin 13.

  created 2005
  by DojoDave <http://www.0j0.org>
  modified 30 Aug 2011
  by Tom Igoe

  This example code is in the public domain.

  http://www.arduino.cc/en/Tutorial/Button
*/

// constants won't change. They're used here to set pin numbers:
const int buttonPinUp = 2;     // the number of the pushbutton pin. This button increases number
const int ledPin =  13;      // the number of the LED pin
const int buttonPinDown = 3;    // This button decreases number
// variables will change:
int buttonStateUp = 0;         // variable for reading the pushbutton status
int buttonStateDown = 0;
int number = 80;            //number that increases/decreases
int lastButtonStateUp = 0;
int lastButtonStateDown = 0;
int buttonPushCounterUp = 0;
int buttonPushCounterDown = 9;

void setup() {
  // initialize the LED pin as an output:
  pinMode(ledPin, OUTPUT);
  Serial.begin(9600);
  // initialize the pushbutton pin as an input:
  pinMode(buttonPinUp, INPUT);
  pinMode(buttonPinDown, INPUT);
}

void loop() {
  // read the state of the pushbutton value:
  buttonStateUp = digitalRead(buttonPinUp);
  buttonStateDown = digitalRead(buttonPinDown);


  if(buttonStateUp != lastButtonStateUp){
    
  // check if the pushbutton is pressed. If it is, the buttonState is HIGH:
    if (buttonStateUp == HIGH) {
          number++;
          Serial.print(number);
          Serial.print("\n");
           
    }    
 }
  if(buttonStateDown != lastButtonStateDown){
    
  // check if the pushbutton is pressed. If it is, the buttonState is HIGH:
    if (buttonStateDown == HIGH) {
          number--;
          //buttonPushCounterUp++;
          Serial.print(number);
          Serial.print("\n");
          
    }    
 }
  lastButtonStateUp = buttonStateUp;
  lastButtonStateDown = buttonStateDown;
}
