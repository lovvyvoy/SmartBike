// Importamos las librerias a usar
#include <SoftwareSerial.h>
#include <LiquidCrystal.h>

//definimos los pines que usaremos en la placa
#define ledPin 13 // led pin
#define rxPin 9 // pin de recepcion de datos (RX). Se conecta al pin TX de Arduino. Con SoftwareSerial podemos emular estos pines y usar pines digitales de la placa en vez de los de RX y TX.
#define txPin 8  // pin de transmision de datos (TX). Se conecta al pin RX de Arduino.
#define menuPin 10 // pin para el boton izquierdo del menu
#define nextPin 6 // pin para el boton siguiente del menu
#define previousPin 7 // pin para el boton anterior del menu

#define totalMenu 4 // total de secciones en el menu

// inicializamos el modulo de la pantalla LCD con los valores respectivos en la placa Arduino.
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

// inicializamos el modulo bluetooth con los valores respectivos en la placa Arduino.
SoftwareSerial bluetooth(rxPin, txPin); // RX, TX

// utilizamos un flag para evitar mensajes duplicados provenientes de la app Android.
boolean flag = false;

// un byte que representa a una cara feliz. Para mayor informacion ver http://www.fibidi.com/arduino-lcd-16x2-characters/
//para generarlos, ir a http://omerk.github.io/lcdchargen/
byte steering[8] = {
  0b00000,
  0b00100,
  0b01100,
  0b11111,
  0b01101,
  0b00101,
  0b00001,
  0b00000
};

byte music[8] = {
  0b00000,
  0b01111,
  0b01001,
  0b01001,
  0b01001,
  0b11011,
  0b11011,
  0b00000
};

byte volume[8] = {
  0b00000,
  0b00001,
  0b00101,
  0b10101,
  0b10101,
  0b00101,
  0b00001,
  0b00000
};

byte lights[8] = {
  0b10101,
  0b10101,
  0b10101,
  0b00000,
  0b11111,
  0b11111,
  0b01110,
  0b00000
};

byte telephone[8] = {
  0b00000,
  0b01110,
  0b01110,
  0b00010,
  0b00010,
  0b01110,
  0b01110,
  0b00000
};

// menus
String menus[] = {
  "Viraje",
  "Musica",
  "Volumen",
  "Luces"
};

int currentMenu = 0;

String currentStatus = "";

// valores para detectar si los botones han sido presionados
int menuVal = 0;
int nextVal = 0;
int previousVal = 0;

// valor para detectar si el usuario dejo de tocar botones
int delayed = 0;

void setup() {
  //comenzamos la lectura de la consola
  
  Serial.begin(9600);

  // ------ modulo bluetooth ------
  // lo primero que hacemos es borrar la memoria para evitar basuras en cache. Luego iniciamos el modulo.
  bluetooth.flush();
  delay(500);
  bluetooth.begin(115200);
  bluetooth.print("$$$");
  delay(100);
  bluetooth.println("U,9600,N");
  bluetooth.begin(9600); 

  // ------ modulo LCD ------
  // para crear emoticons, necesitamos crear el caracter
  lcd.createChar(1, steering);
  lcd.createChar(2, music);
  lcd.createChar(3, volume);
  lcd.createChar(4, lights);
  lcd.createChar(5, telephone);
  // creamos un display de 16 columnas (caracteres) y 2 filas
  lcd.begin(16, 2);
  // borramos la pantalla completa
  lcd.clear();
  // inicializamos el cursor a la posicion 0,0
  lcd.setCursor(0,0); 
  // le decimos que realice scroll automaticamente
  lcd.autoscroll();  
  
  String welcome = "Esperando conexion\n";

  lcd.print(welcome);

  //realizamos el scroll de 0 a 11 pues el mensaje anterior posee 18 caracteres
  for (int i = 0; i < welcome.length() - 1; i++) {
    // realizamos el scroll por una posicion hacia la derecha
    lcd.scrollDisplayRight();
    // esperamos un poco...
    delay(250);
  }

  delay(1000);
  lcd.noAutoscroll();
  lcd.clear();
  
  lcd.print(welcome);
  
  // ------ otras variables ------
  // iniciamos y luego seteamos el pin para el LED en apagado
  pinMode(ledPin, OUTPUT);  
  digitalWrite(ledPin, LOW);

  // botones push
  pinMode(menuPin, INPUT);    // configuramos el pulsador como ENTRADA
  pinMode(nextPin, INPUT);    // configuramos el pulsador como ENTRADA
  pinMode(previousPin, INPUT);    // configuramos el pulsador como ENTRADA   

}

void loop() {
  
  // leemos los estados de los botones
  menuVal = digitalRead(menuPin);  
  nextVal = digitalRead(nextPin);
  previousVal = digitalRead(previousPin);

  // verificamos si pulsamos alguno de los botones. Segun nuestra configuracion en la placa, el boton esta pulsado cuando se lee LOW
  if (menuVal == LOW) {
    currentMenu++;
    currentMenu = clampMenuStatus();
    lcd.clear();
    //cargamos 5 imagenes antes segun el menu
    lcd.write(currentMenu + 1);    
    lcd.print(" " + menus[currentMenu]);
  }

  if (nextVal == LOW) {
    if(currentStatus.startsWith("C")){
      // contestar llamada entrante
      lcd.clear();
      lcd.print("Llamada aceptada");
      bluetooth.println("C0");
      currentStatus = "";
      nextVal = HIGH;
      delay(1000);
    } 
    else {
      switch(currentMenu) {
      case 0:
        // Viraje
        blink();
        break;
      case 1:
        // Musica
        bluetooth.println("M0");
        break;
      case 2:
        // Volumen
        bluetooth.println("V1");
        break;
      case 3:
        // Luces
        digitalWrite(ledPin, HIGH);
        break;    
      }
    }
  }

  if (previousVal == LOW) {
    if(currentStatus.startsWith("C")){
      // rechazar llamada entrante
      lcd.clear();
      lcd.print("Llamada rechazada");
      bluetooth.println("C1");
      currentStatus = "";
      previousVal = HIGH;
      delay(1000);
    } 
    else {
      switch(currentMenu) {
      case 0:
        // Viraje
        digitalWrite(ledPin, LOW);
        break;
      case 1:
        // Musica
        bluetooth.println("M1");
        break;
      case 2:
        // Volumen
        bluetooth.println("V2");
        break;
      case 3:
        // Luces
        digitalWrite(ledPin, LOW);
        break;
      }
    }
  }

  String menuInput = checkBluetoothInput();

  // si fallamos leyendo desde bluetooth, probamos con el terminal
  if(menuInput == "") {
    menuInput = checkSerialInput();
  }

  if(flag && menuInput != "") {

    currentStatus = menuInput;
    Serial.println(menuInput+"\n");

    // la conexion con Android se ha establecido
    if (menuInput == "S0") {  
      lcd.clear();    
      lcd.setCursor(0,0);      
      lcd.print("Ready ");
    }

    // estado del volumen
    if (menuInput.startsWith("V")) {
      // imprimimos la seccion    
      Serial.println(menus[currentMenu]+"\n");

      lcd.clear();
      lcd.setCursor(0,0);
      lcd.write(3);
      lcd.print(" " + menus[currentMenu]);
      lcd.setCursor(0,1);
      lcd.print(menuInput.substring(1));   
    }

    // estado de la cancion
    if(menuInput.startsWith("M")) {
      // imprimimos la seccion      
      Serial.println(menus[currentMenu]+"\n");
      identifyMusic(menuInput.substring(1));  
    }

    // estado de llamada entrante
    if(menuInput.startsWith("C")) {
      // imprimimos la seccion
      Serial.println("Llamada entrante\n");
      //despliega el nombre del contacto
      identifyCaller(menuInput.substring(1));      
    }

    flag = false;
  }
  
  delay(250);
}

void blink() {
  digitalWrite(ledPin, HIGH);
  delay(500);
  digitalWrite(ledPin, LOW);
  delay(500);
}

void identifyCaller(String text) {
  text = text + " llamando";
  int length = text.length();

  lcd.clear();

  if(length > 16) {
    //solo si pasamos el limite, mostramos el texto con autoscroll
    lcd.setCursor(0,0);
    lcd.write(5);
    lcd.print(" " + text);
    lcd.setCursor(0,1);
    lcd.print("1. OK - 2. NO");

    delay(2000);

    lcd.autoscroll();

    //realizamos el scroll
    for (int i = 0; i < length - 1; i++) {
      // realizamos el scroll por una posicion hacia la izquierda
      lcd.scrollDisplayLeft();
      // esperamos un poco...
      delay(250);
    }
    lcd.clear();
    delay(1500);

    lcd.noAutoscroll();

    text = text.substring(0, 13) + "...";
    lcd.setCursor(0,0);
    lcd.write(5);
    lcd.print(" " + text);
    lcd.setCursor(0,1);
    lcd.print("1. OK - 2. NO");
    Serial.print("1. OK - 2. NO\n");
  } 
  else {
    lcd.setCursor(0,0);
    lcd.write(5);
    lcd.print(" " + text);
    Serial.print(" " + text+"\n");
    lcd.setCursor(0,1);
    lcd.print("1. OK - 2. NO");
    Serial.print("1. OK - 2. NO\n");
  }
}

String split(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {
    0, -1                };
  int maxIndex = data.length() - 1;

  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i) == separator || i == maxIndex){
      found++;
      strIndex[0] = strIndex[1]+1;
      strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}

void identifyMusic(String text) {
  lcd.clear();

  String artist = split(text, '-', 0);
  String song = split(text, '-', 1);

  int artistLength = artist.length();
  int songLength = song.length();

  boolean artistLengthBigger = artistLength > 16;
  boolean songLengthBigger = songLength > 16;

  if(artistLengthBigger || songLengthBigger) {
    //solo si pasamos el limite, mostramos el texto con autoscroll
    lcd.setCursor(0,0);
    lcd.print(artist);
    Serial.print(artist+"\n");
    lcd.setCursor(0,1);
    lcd.print(song);
    Serial.print(song+"\n");

    delay(2000);

    lcd.autoscroll();

    int length = 0;
    if(artistLength > songLength) {
      length = artistLength;
    } 
    else {
      length = songLength;
    }

    //realizamos el scroll
    for (int i = 0; i < length - 1; i++) {
      // realizamos el scroll por una posicion hacia la izquierda
      lcd.scrollDisplayLeft();
      // esperamos un poco...
      delay(250);
    }

    delay(1000);

    lcd.noAutoscroll();

    text = text.substring(0, 13) + "...";

    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print(artist);
    lcd.setCursor(0,1);
    lcd.print(song);
  } 
  else {

    lcd.setCursor(0,0);
    lcd.print(artist);
    lcd.setCursor(0,1);
    lcd.print(song);
  }
}

/**
 * Funcion para restringir los valores del menu principal
 */
int clampMenuStatus() {
  if(currentMenu < 0 || currentMenu > totalMenu - 1) {
    currentMenu = 0;
  }

  return currentMenu;
}

String checkBluetoothInput() {
  // leemos el string proveniente del modulo bluetooth (o sea, de la app Android)
  // si el valor es 2, encendemos el LED, si el valor entregado es 1, apagamos el LED
  String state = "";

  while (bluetooth.available() > 0) {
    // guardamos lo leido en la variable character
    char character = bluetooth.read();

    // concatenamos todo el string proveniente de Android
    state.concat(character);

    //si llega a ser un fin de linea, parseamos la variable leida y reseteamos las variables respectivas
    if (character == '\n') {
      Serial.println("Received Bluetooth:");
      flag = true;
      state.replace("\0", "");
      state.replace("\n", "");

      return state;
    }
  }

  return "";
}

String checkSerialInput() {
  // leemos el string proveniente del modulo bluetooth (o sea, de la app Android)
  // si el valor es 2, encendemos el LED, si el valor entregado es 1, apagamos el LED
  String state = "";

  while (Serial.available() > 0) {
    // guardamos lo leido en la variable character
    char character = Serial.read();

    // concatenamos todo el string proveniente de Android
    state.concat(character);

    //si llega a ser un fin de linea, parseamos la variable leida y reseteamos las variables respectivas
    if (character == '\n') {
      Serial.println("Received Serial:");
      flag = true;
      state.replace("\0", "");
      state.replace("\n", "");

      return state;
    }
  }

  return "";
}
