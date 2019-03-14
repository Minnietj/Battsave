

    //Global Variables for Incoming Serial - Functions are designed in such a way that variables are stored globally. 
    //I dont forsee this causing any issues, but if you suspect it is, lemme knmow and i might be able to change it to passing variables.
    
    double Temperature = 0;
    double Voltage = 0;
    double Current = 0;
    int Percentage = 0;
    
    String incoming;
    String message;
    boolean newData = false;

    

void setup() {
    Serial.begin(9600);
  pinMode(6,OUTPUT);
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
          Percentage = temporary.toInt();
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





    
