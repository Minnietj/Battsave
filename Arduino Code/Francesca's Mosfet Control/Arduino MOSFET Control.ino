//very basic code to test out the arduino's ability to work through the MOSFET. Will greatly update with testing and when working with actual data

vint analogPin = A0; 
  int v = 0; //voltage of battery
void setup() {
// initialize digital pin 13 as an output.
pinMode(3, OUTPUT);
  Serial.begin(9600);       
  //  setup serial

}
// the loop function runs over and over again forever
void loop() {

 
int thresh = 100 ; //low threshold
/*digitalWrite(3, HIGH); // turn the LED on (HIGH is the voltage level)
delay(1000); // wait for a second
digitalWrite(3, LOW); // turn the LED off by making the voltage LOW
delay(1000);  // wait for a second */


  v = analogRead(analogPin);  // read the input pin
  Serial.println(v);          // debug value

if(v >= thresh){

  digitalWrite(3, LOW);
  
}
else{

  digitalWrite(3, HIGH); 
}
}


