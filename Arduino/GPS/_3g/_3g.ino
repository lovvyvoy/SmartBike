/*
 *  Description: This example shows how to use TCP clients in single client mode
 *  This example shows the AT commands (and the answers of the module) used
 *  to work with the TCP/IP. For more information about the AT commands, 
 *  refer to the AT command manual.
 *
 *  Copyright (C) 2013 Libelium Comunicaciones Distribuidas S.L.
 *  http://www.libelium.com
 *
 *  This program is free software: you can redistribute it and/or modify 
 *  it under the terms of the GNU General Public License as published by 
 *  the Free Software Foundation, either version 3 of the License, or 
 *  (at your option) any later version. 
 *  
 *  This program is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU General Public License for more details. 
 *  
 *  You should have received a copy of the GNU General Public License 
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *
 *  Version 0.2
 *  Author: Alejandro Gallego 
 */


int8_t answer;
int onModulePin= 2;

char aux_str[50];

char server[ ]="127.0.0.1";
char port[ ]="5005";
char TCP_message[ ]="enviado desde arduino";

void setup(){
    
    pinMode(onModulePin, OUTPUT);
    Serial.begin(115200);   
    
    Serial.println("Starting...");
    power_on();
    
    delay(3000);
    
    // sets the PIN code
    sendATcommand("AT+CPIN=1111", "OK", 2000);
    
    delay(3000);
    
    while( (sendATcommand("AT+CREG?", "+CREG: 0,1", 500) || 
            sendATcommand("AT+CREG?", "+CREG: 0,5", 500)) == 0 );
    
    // sets APN, user name and password
    sendATcommand("AT+CGSOCKCONT=1,\"IP\",\"apn\"", "OK", 2000);
    sendATcommand("AT+CSOCKAUTH=1,1,\"username\",\"password\"", "OK", 2000);
   

}
void loop(){
    
    sprintf(aux_str, "AT+NETOPEN=\"TCP\",%s", port);
    answer = sendATcommand(aux_str, "Network opened", 20000);
    
    if (answer == 1)
    {
        Serial.println("Network opened");
        sprintf(aux_str, "AT+TCPCONNECT=\"%s\",%s", server, port);
        answer = sendATcommand(aux_str, "Connect ok", 20000);
        if (answer == 1)
        {
            Serial.println("Socket opened");
            sprintf(aux_str, "AT+TCPWRITE=%d", strlen(TCP_message));
            answer = sendATcommand(aux_str, ">", 20000);
            if (answer == 1)
            {
                sendATcommand(TCP_message, "Send OK", 20000);                
            }
            sendATcommand("AT+NETCLOSE", "OK", 20000);
        }
        else
        {
            Serial.println("Error opening the socket");    
        }
    }
    else
    {
        Serial.println("Error opening the nertwork");    
    }

}

void power_on(){

    uint8_t answer=0;
    
    // checks if the module is started
    answer = sendATcommand("AT", "OK", 2000);
    if (answer == 0)
    {
        // power on pulse
        digitalWrite(onModulePin,HIGH);
        delay(3000);
        digitalWrite(onModulePin,LOW);
    
        // waits for an answer from the module
        while(answer == 0){    
            // Send AT every two seconds and wait for the answer
            answer = sendATcommand("AT", "OK", 2000);    
        }
    }
    
}


int8_t sendATcommand(char* ATcommand, char* expected_answer1,
        unsigned int timeout)
{

    uint8_t x=0,  answer=0;
    char response[100];
    unsigned long previous;

    memset(response, '\0', 100);    // Initialize the string
    
    delay(100);
    
    while( Serial.available() > 0) Serial.read();    // Clean the input buffer
    
    Serial.println(ATcommand);    // Send the AT command 


    x = 0;
    previous = millis();

    // this loop waits for the answer
    do{

        if(Serial.available() != 0){    
            response[x] = Serial.read();
            x++;
            // check if the desired answer is in the response of the module
            if (strstr(response, expected_answer1) != NULL)    
            {
                answer = 1;
            }
        }
        // Waits for the asnwer with time out
    }while((answer == 0) && ((millis() - previous) < timeout));    

    return answer;
}
