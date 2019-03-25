
  #include <SPI.h>
  #include <Wire.h>
  #include <Adafruit_GFX.h>
  #include <Adafruit_SSD1306.h>
  #define SCREEN_WIDTH 128 // OLED display width, in pixels
  #define SCREEN_HEIGHT 32 // OLED display height, in pixels
  // Declaration for an SSD1306 display connected to I2C (SDA, SCL pins)
  #define OLED_RESET     4 // Reset pin # (or -1 if sharing Arduino reset pin)
  Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);




    //Global Variables for Incoming Serial - Functions are designed in such a way that variables are stored globally. 
    //I dont forsee this causing any issues, but if you suspect it is, lemme knmow and i might be able to change it to passing variables.
    
    double Temperature = 0;
    double Voltage = 0;
    double Current = 0;
    double Percentage = 0;
    
    String incoming;
    String message;
    boolean newData = false;

    int analogPin = A0; 
    int Threshold = 6;

    const int buttonPinUp = 6;
    const int buttonPinDown = 5;
    int buttonStateUp = 0;         // variable for reading the pushbutton status
    int buttonStateDown = 0;
    int lastButtonStateUp = 0;
    int lastButtonStateDown = 0;
    int buttonPushCounterUp = 0;
    int buttonPushCounterDown = 9;

    int loopCounter = 0;

void setup() {
    pinMode(3, OUTPUT);
    pinMode(6, INPUT); //Moved to 6 instead of 4 because 4 is reserved as the OLED reset Pin
    pinMode(5, INPUT);
    Serial.begin(9600);

    //Display Setup
     if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) { // Address 0x3C for 128x32
     Serial.println(F("SSD1306 allocation failed"));
     for(;;); // Don't proceed, loop forever
     }
     // Show initial display buffer contents on the screen --
     // the library initializes this with an Adafruit splash screen.
     display.display();
     delay(2000); // Pause for 2 seconds
     // Clear the buffer
     display.clearDisplay();
}

void UpdateScreen(){
  display.clearDisplay();
  display.setTextSize(1);      // Normal 1:1 pixel scale
  display.setTextColor(WHITE); // Draw white text
  display.setCursor(0, 0);     // Start at top-left corner
  display.cp437(true);         // Use full 256 char 'Code Page 437' font

  // Not all the characters will fit on the display. This is normal.
  // Library will draw what it can and the rest will be clipped.
  //for(int16_t i=0; i<256; i++) {
  //  if(i == '\n') display.write(' ');
  //  else          display.write(i);
  //}
  
   
  display.print("Temp: ");
  display.println(Temperature);
  display.print("Volt: ");
  display.println(Voltage);
  display.print("Amp: ");
  display.println(Current);
  display.print("Prcnt: ");
  display.println(Percentage);
  

  display.setCursor(86, 10);
  display.setTextSize(3);
  display.print(Threshold);
  display.display();
}




//Takes raw incoming serial data from buffer, separates it into individual messages upon detection of null character
//Also triggers newData flag to signal that the serial data has arrived and we should now update our variables.
void getMessage(){
  
  if(Serial.available()){
        
        char c = Serial.read();
        String test = "test";

        if (c!= '\0'){ 
          incoming += c;
          //Serial.print("recorded");
        }
        if (c == '\0'){
          //Serial.print("full message received: ");
          //Serial.print(incoming);
          message = incoming;
          incoming = "";
          newData = true;
        }
    }
}


//Takes messages from getMessage() and parses them into their respective variables
//Also clears the newData flag
void parseMessage(){
  //Structure of message this function is designed to parse:
  //Temp + \n + Voltage + \n + Current + \n + Percentage + \n
  
  String temporary = "";
  int i = 0;
  int varCounter = 0; //increments to switch between vars
 
    
    for (i=0; i < message.length(); i++){
      if (message[i] != '\n'){
        temporary += message [i];
        //Serial.println(message[i]);
      }
      
      if (message[i] == '\n'){
        if(varCounter == 0){
          Temperature = temporary.toDouble();
          varCounter++;
        }
        else if(varCounter == 1){
          Voltage = temporary.toDouble();
          varCounter++;
        }
        else if(varCounter == 2){
          Current = temporary.toDouble();
          varCounter++;
        }
        else if(varCounter == 3){
          Percentage = temporary.toDouble();
          varCounter++;
        }
        temporary = "";
        
      }
    }
  

  
  newData = false;
  
}


void loop() {
  
  getMessage(); //if there is any incoming serial to record, record it.
  if (newData == true){
    parseMessage(); //if a full message has been recieved, parse and extract the data from it.

    // To test on PC serial monitor: exchange '\0' in getMessage() for '\r' , uncomment below
    /* 
    Serial.println("Temperature: ");
    Serial.println(Temperature);
    Serial.println("Voltage: ");
    Serial.println(Voltage);
    Serial.println("Current: ");
    Serial.println(Current);
    Serial.println("Percentage: ");
    Serial.println(Percentage);
    */
  }


  //Rest of our code


  //Button Read
  buttonStateUp = digitalRead(buttonPinUp);
  buttonStateDown = digitalRead(buttonPinDown);
  if(buttonStateUp != lastButtonStateUp){
    
  // check if the pushbutton is pressed. If it is, the buttonState is HIGH:
    if (buttonStateUp == HIGH) {
       Threshold++;
       if (Threshold > 100){
         Threshold = 100;
       }
    }    
   }
   if(buttonStateDown != lastButtonStateDown){
    // check if the pushbutton is pressed. If it is, the buttonState is HIGH:
    if (buttonStateDown == HIGH) {
        Threshold--;
        if (Threshold < 0){
          Threshold = 0;
        }
    }    
   }
   lastButtonStateUp = buttonStateUp;
   lastButtonStateDown = buttonStateDown;






  //Relay Control
  if(Percentage >= Threshold){
    digitalWrite(3, HIGH);
  }
  else{
    if (Percentage <= (Threshold-5)){ //Implemented Hysteresis
      digitalWrite(3, LOW); 
    }
  }

  //Serial.println(Threshold);
  UpdateScreen();
  
   loopCounter++;
   if (loopCounter == 133){
   loopCounter = 0;
   }
   

//BE WARY OF USING DELAYS
//Arduinos can only run one loop. This means that they can only do one thing at a time. 
//If the code tells them to delay for 5 seconds, any incoming serial messages from the phone might be ignored and missed.
//(I believe theres actually a hardware buffer to help mitigate this problem, but it won't eliminate it)
//Idk why we would need to use delays anywhere in this code, but if we do, we will need to implement an interrupt handler :(
  
}




/*
    //Echoes back to Phone. Used for testing purposes, will likely not include in final product.
    if(Serial.available()){
        char c = Serial.read();
        Serial.print(c);
    }
*/





    
