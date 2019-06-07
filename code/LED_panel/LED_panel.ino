#include "Bielefeld-CeBiTec_LED_panel.h"

// display
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

// bluetooth
#include <avr/pgmspace.h>
#include <SoftwareSerial.h>

// C Strings
#include <stdio.h>
#include <ctype.h>

// temperature sensors
#include <Wire.h>
#include "Adafruit_MCP9808.h"

// acceleration sensor
#include <Adafruit_Sensor.h>
#include <Adafruit_ADXL345_U.h>

// rotary encoder
#define RE_CLK 4 //2
#define RE_DT 5//4
#define RE_SW 6

// HC-05/HC-06 bluetooth module
#define TXD 9
#define RXD 10

// tilt switch
#define TILT_SW 2
#define UPSIDE_UP 0
#define UPSIDE_DOWN 2

// Program Sequence
#define MAX_ENTRIES 30
#define ENTRY_LENGTH 38
#define CONSTANT 	0
#define LINEAR 		1
#define EXPONENTIAL 2
#define JUMP 		4
//#define LOGARITHMIC 3
#define BT_NOTHING 	0
#define BT_NEW_PROGRAM 1
#define BT_STOP 2
#define BT_ERROR 3
#define BT_CANCEL 4
#define BT_PING 5
#define UPDATE_INTERVAL 2

//display
// I2C ports!
#define OLED_RESET 2 // nicht genutzt bei diesem Display

//others
#define BLANK	 	3

// iGEM Bielefeld-CeBiTec 2017 logo
const uint8_t logo[8129] PROGMEM = {
0x0,0x0,0x0,0xf,0xc0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1f,0xe0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1f,0xe0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1f,0xe0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1f,0xe0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0xf,0xc0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x7,0x80,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x0,0x0,0x3,0xc0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0xf,0xe0,0x3f,0xfc,0x1f,0xff,0xf7,0xf0,0x7,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x19,0xb0,0x7f,0xff,0x1f,0xff,0xf7,0xfc,0x1f,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1b,0x1,0xff,0xff,0x9f,0xff,0xf7,0xfc,0x1f,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x16,0xf3,0xff,0xff,0x9f,0xff,0xf7,0xfc,0x1f,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x18,0x3,0xf8,0x1f,0x9f,0x80,0x7,0xfe,0x3f,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1d,0xd7,0xf0,0x0,0x1f,0x80,0x7,0xfe,0x3f,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x16,0x37,0xf0,0x0,0x1f,0x80,0x7,0xfe,0x3f,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x13,0xc7,0xe0,0xff,0x9f,0xff,0xf7,0xde,0x3f,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x11,0xa7,0xe0,0xff,0xdf,0xff,0xf7,0xdf,0x7f,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x12,0x17,0xe0,0xff,0xdf,0xff,0xf7,0xcf,0x79,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x15,0xc7,0xe0,0xff,0xdf,0xff,0xf7,0xcf,0xf9,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1c,0x17,0xf0,0x3,0xdf,0x80,0x7,0xcf,0xf9,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1b,0xb7,0xf0,0x7,0xdf,0x80,0x7,0xcf,0xf9,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x14,0x63,0xf8,0xf,0xdf,0x80,0x7,0xc7,0xf1,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x13,0x43,0xff,0xff,0xdf,0xff,0xf7,0xc7,0xf1,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x19,0xd1,0xff,0xff,0x9f,0xff,0xf7,0xc3,0xe1,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1f,0xf0,0x7f,0xff,0x1f,0xff,0xf7,0xc3,0xe1,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x3a,0xb8,0x3f,0xfc,0x1f,0xff,0xf7,0xc3,0xe1,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x37,0xe8,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1f,0xf3,0xed,0xd8,0xf7,0xbd,0x8e,0x1c,0xe2,0xf0,0x0,0x0,0x0,
0x0,0x0,0x0,0x1f,0xf3,0x2d,0x18,0xc6,0x31,0x89,0x16,0xa6,0x30,0x0,0x0,0x0,
0x0,0x0,0x0,0x1f,0xf3,0xed,0x98,0xe7,0xb9,0x89,0x4,0xaa,0x20,0x0,0x0,0x0,
0x0,0x0,0x0,0x1f,0xf3,0x2d,0x18,0xc6,0x31,0x89,0x8,0xa2,0x60,0x0,0x0,0x0,
0x0,0x0,0x0,0x1f,0xf3,0xed,0xde,0xf6,0x3d,0xee,0x1e,0xe2,0xc0,0x0,0x0,0x0
};

// Progam Sequence
struct Entry {
   unsigned short int		slope;
   unsigned short int		gradient;
   unsigned long int		millis;
   unsigned short int		left_0;
   unsigned short int		right_0;
   unsigned short int		left_1;
   unsigned short int		right_1;

   unsigned short int 		acceleration;
   double					accel;
   unsigned long int 		accel_millis;

   unsigned int				ledBuffer[12];
   unsigned long int   		runtime;
   unsigned long int    	start_time;
   unsigned short int 		c;
   double					x;
   unsigned short int 		c_r;
   double					x_r;

   unsigned short int 		jump_index;
   unsigned short int 		remaining_jumps;
};
int entryCounter = 0;
struct Entry programSequence[MAX_ENTRIES];
int bt_code = 0;
int currentEntry = -1;
//int intensity;
char btBuffer[ENTRY_LENGTH*(MAX_ENTRIES+1)+1];
int curPos = 0;

// create LED panel object
LED_panel led_panel = LED_panel();
unsigned short blank_state;

// rotary encoder states
int rot_clk;
int lst_rot_clk;
unsigned int reValue;
unsigned int intensity;
unsigned long int sw_pressed;
unsigned int time_inten;
unsigned long int time_end;

// timing
unsigned long int last_checked;
unsigned long int tmp_sensor_last_update;
unsigned long int acc_sensor_last_update;
unsigned long int last_accel_exceed;
unsigned int tmp_sensor_update_interval;
unsigned int acc_sensor_update_interval;
unsigned long int time_reference;

// display
Adafruit_SSD1306 display(OLED_RESET);

// temperature sensors
Adafruit_MCP9808 tempsensor_btm = Adafruit_MCP9808();
Adafruit_MCP9808 tempsensor_top = Adafruit_MCP9808();
#define TMP_BTM 0b0011001
#define TMP_TOP 0b0011010

// acceleration sensor
Adafruit_ADXL345_Unified accel = Adafruit_ADXL345_Unified(12345);
float last_x;
float last_y;
float last_z;
float absdiff_x;
float absdiff_y;
float absdiff_z;
float min_rel_acc_diff;
float last_accel_exceed_value;
float max_last_accel_exceed_value;
unsigned short int orientation;
uint32_t accel_exceed_buffer;
#define ACCEL_EXEED_BUFFER_DEFAULT_VALUE 0
#define MIN_REL_ACC_DIFF 1.0
#define TMP_SENSOR_UPDATE_INTERVAL 500
#define ACC_SENSOR_UPDATE_INTERVAL 30

#define MIN_EXEEDINGS 12
#define N_MEASUREMENTS 32

// bluetooth serial communication
SoftwareSerial btSerial(TXD, RXD);

void setup() {
	time_reference = millis();

	bt_code = 0;
	currentEntry = -1;

	// USB Serial
	Serial.begin(9600);
	//Serial.println("Thanks for using the LED panel build by the iGEM Team Bielefeld-CeBiTec 2017!");
	//Serial.println("Please write to mhaak@cebitec.uni-bielefeld.de in case of any questions regarding this product.");

	// bluetooth setup
	btSerial.begin(9600);
	btSerial.println("bluetooth available");

	// led-panel setup
	led_panel.begin();
	led_panel.setPWM(0);
	blank_state = 1;

	// rotary encoder setup
	pinMode(RE_SW, INPUT);
	pinMode(RE_CLK, INPUT);
	pinMode(RE_DT, INPUT);
	attachInterrupt(digitalPinToInterrupt(RE_CLK), irupt_re_clk, CHANGE);
	attachInterrupt(digitalPinToInterrupt(RE_SW), irupt_re_sw, CHANGE);
	lst_rot_clk = digitalRead(RE_CLK);
	intensity = 100;
	time_inten = 1;

	// temperature sensor setup
	if (!tempsensor_btm.begin(TMP_BTM) or !tempsensor_top.begin(TMP_TOP)) {
		//Serial.println("Couldn't find MCP9808!");
		while (1);
	}
	tmp_sensor_last_update = millis();

	// acceleration sensor
	if(!accel.begin())
	{
		/* There was a problem detecting the ADXL345 ... check your connections */
		//Serial.println("Ooops, no ADXL345 detected ... Check your wiring!");
		while(1);
	}
	accel.setRange(ADXL345_RANGE_16_G);
	accel.setDataRate(ADXL345_DATARATE_3200_HZ);
	sensors_event_t event; 
	accel.getEvent(&event);
	last_x = event.acceleration.x;
	last_y = event.acceleration.y;
	last_z = event.acceleration.z;
	absdiff_x = 0.0;
	absdiff_y = 0.0;
	absdiff_z = 0.0;
	max_last_accel_exceed_value = 0.0;
	tmp_sensor_update_interval = TMP_SENSOR_UPDATE_INTERVAL;
	acc_sensor_update_interval = ACC_SENSOR_UPDATE_INTERVAL;
	accel_exceeds_buffer = ACCEL_EXEED_BUFFER_DEFAULT_VALUE;

	min_rel_acc_diff = MIN_REL_ACC_DIFF;

	// initialize display with the I2C addr 0x3C
	display.begin(SSD1306_SWITCHCAPVCC, 0x3C);
	display.clearDisplay();
	display.setTextColor(WHITE);
	display.drawBitmap(0, 0, logo, 128, 32, 1);
	display.display();

	// orientation
	sensor_update();
	display.setRotation(orientation);

	// FUNCTION TEST
	// digitalWrite(BLANK, LOW);
	// blank_state = 0;
	// led_panel.setPWM(200);
	// delay(500);
	// led_panel.setPWM(0);
	// unsigned short int test_gadient[12] = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200};
	// led_panel.testGradient(test_gadient);
	// digitalWrite(BLANK, HIGH);
	// blank_state = 1;	
}

void loop(){
	noInterrupts();

	if(currentEntry == -1){
		switch (checkBluetoothForProgram()){
			case BT_NEW_PROGRAM:
				//Serial.println("new program received!");
				//Serial.println(btBuffer);
				
				if(makeProgram()){
					//Serial.println("made program");
					printProgram();
					startProgram();
				}else{
					//Serial.println("ERR: bluetooth transmission not successful");
					bt_send("  program corrupted");
				}
				break;
			case BT_ERROR:
				//Serial.println("Error occured while reading btSerial!");
				bt_send("  program corrupted");
				break;
			case BT_PING:
				//Serial.println("Received ping order.");
				bt_send("  P");
				break;
			default:
				break;
		}
	}
	else{
		switch(checkBluetoothForOrder()){
			case BT_CANCEL:
				//Serial.println("Received order to cancel program.");
				stopProgram();
				break;
			case BT_PING:
				//Serial.println("Received ping order.");
				btSerial.println("  P"); // no bt_send, as interrupts shall not occur during program execution
				break;
			default:
				break;
		}
	}
	
	interrupts();

	if(currentEntry >= 0){
		if ((millis() - last_checked) > UPDATE_INTERVAL){
			last_checked = millis();
			if((updateLEDs(currentEntry)) == 0){
				currentEntry++;
				// reset accel_exceeds_buffer
				accel_exceeds_buffer = ACCEL_EXEED_BUFFER_DEFAULT_VALUE;

				//Serial.println(currentEntry);
				// handle jumps
				while (programSequence[currentEntry].slope == JUMP && currentEntry >= 0){
					if (programSequence[currentEntry].remaining_jumps == 0){
						currentEntry++;
						//Serial.println(currentEntry);
						break;
					}else{
						programSequence[currentEntry].remaining_jumps--;
						//Serial.println(currentEntry);
						Serial.print("jumps remaining: ");
						//Serial.println(programSequence[currentEntry].remaining_jumps);
						currentEntry = programSequence[currentEntry].jump_index;
					}
				}

				if((currentEntry < entryCounter) && (currentEntry >= 0)){
					programSequence[currentEntry].start_time = millis();
					updateLEDs(currentEntry);
				}
				else{
					stopProgram();
				}
			}
		}
	}
	else if(currentEntry == -2){
		if(millis() >= time_end)stopProgram();
	}

	update_display();
	sensor_update();
}

int sum_accel_exceedings(uint32_t buffer, uint16_t n_measurements)
{
	uint32_t num_bits = 0;
	if(buffer){
		for(unsigned int i=0; i<n_measurements; i++){
			if (buffer & 1) num_bits ++;
			buffer>>=1;
		}
	}
	return num_bits;
}

void sensor_update(void)
{

	// tmp update interval equals bt_send interval!
	if ((millis() - tmp_sensor_last_update) > tmp_sensor_update_interval)
	{
		float c_btm = tempsensor_btm.readTempC();
		float c_top = tempsensor_top.readTempC();

		// only send message if program is running
		if(currentEntry >= 0){
			String s_c1 = String(c_top, 2);
			String s_c2 = String(c_btm, 2);
			// append latest acceleration sensor data
			String s_x = String(absdiff_x, 2);
			if (s_x.startsWith("-")) { s_x = s_x.substring(1); };
			String s_y = String(absdiff_y, 2);
			if (s_y.startsWith("-")) { s_y = s_y.substring(1); };
			String s_z = String(absdiff_z, 2);
			if (s_z.startsWith("-")) { s_z = s_z.substring(1); };
			String s_max = String(max_last_accel_exceed_value, 2);
			if (s_max.startsWith("-")) { s_max = s_max.substring(1); };
			String message = " status ;" + s_c1 + ";" + s_c2 + ";" + s_x + ";" + s_y + ";" + s_z + ";" + s_max ;
			btSerial.println(message);

			max_last_accel_exceed_value = 0.0;
		}
		
		tmp_sensor_last_update = millis();
	}

	if ((millis() - acc_sensor_last_update) > acc_sensor_update_interval)
	{
		sensors_event_t event; 
		accel.getEvent(&event);
	
		absdiff_x = abs(event.acceleration.x - last_x);
		absdiff_y = abs(event.acceleration.y - last_y);
		absdiff_z = abs(event.acceleration.z - last_z);

		if(currentEntry >= 0){
			// check if current program sequence entry has acceleration control
			if((absdiff_x > programSequence[currentEntry].accel) || (absdiff_y > programSequence[currentEntry].accel) || (absdiff_z > programSequence[currentEntry].accel))
			{
				//Serial.print("########### ");
				last_accel_exceed_value =  max(absdiff_x, max(absdiff_y, absdiff_z));
				//last_accel_exceed = millis();
				max_last_accel_exceed_value = max(max_last_accel_exceed_value, last_accel_exceed_value);

				accel_exceed_buffer << 1;
				accel_exceed_buffer |= 1;
			}
			else{
				accel_exceed_buffer << 1;
			}
		}
		// update orientation
		event.acceleration.z >= 0 ? orientation = UPSIDE_UP : orientation = UPSIDE_DOWN;

		last_x = event.acceleration.x;
		last_y = event.acceleration.y;
		last_z = event.acceleration.z;
		acc_sensor_last_update = millis();


		//String _s_x = String(event.acceleration.x, 2);
		//String _s_y = String(event.acceleration.y, 2);
		//String _s_z = String(event.acceleration.z, 2);
		//String output = String(acc_sensor_last_update - time_reference) + "," + _s_x + "," + _s_y + "," + _s_z;
		//Serial.println(output);
	}
}

void bt_send(char* message)
{
	btSerial.flush();
	delay(100); // to ensure that message is received seperately
	btSerial.println(message);
	btSerial.flush();
	delay(100); // to ensure that message is received seperately
}

int checkBluetoothForProgram(){
	if(btSerial.available()){
		for ( ; curPos<(MAX_ENTRIES*ENTRY_LENGTH) ; curPos++){
			if (btSerial.available()){
				btBuffer[curPos] = btSerial.read();
				if (btBuffer[curPos] == '<'){
					btBuffer[curPos]= NULL;
					curPos = 0;
					
					return BT_NEW_PROGRAM;
				}else if (btBuffer[curPos] == '>'){
					curPos = 0; //so that the first char is '/'
				}else if (btBuffer[curPos] == 'P'){
					btBuffer[curPos]= NULL;
					curPos = 0;
					return BT_PING;
				}
			}
			else{
				//Serial.print(".");
				break;
			}
		}
	}
	return BT_NOTHING;
}

int checkBluetoothForOrder(){
	if(btSerial.available()){
		char order = btSerial.read();
		//Serial.println(order);
		if (order == 'X'){
			return BT_CANCEL;
		}
		else if (order == 'P'){
			return BT_PING;
		}
	}
	else{
		return BT_NOTHING;
	}
	return BT_NOTHING;
}

short updateLEDs(int i){
	unsigned long int currentMillis = millis();
	programSequence[i].runtime = (currentMillis - programSequence[i].start_time);
	if(programSequence[i].runtime >= programSequence[i].millis){
		return 0;
	}

	if(!programSequence[i].acceleration){
		if(blank_state){
			digitalWrite(BLANK, LOW);
			blank_state = 0;
		}
	}
	else{
		//if((currentMillis - last_accel_exceed) < programSequence[i].accel_millis && last_accel_exceed_value >= programSequence[i].accel){
		if(sum_accel_exceedings(accel_exceed_buffer, N_MEASUREMENTS) < MIN_EXEEDINGS ){
			if(!blank_state){
				digitalWrite(BLANK, HIGH);
				blank_state = 1;
			}
		}
		else{
			if(blank_state){
				digitalWrite(BLANK, LOW);
				blank_state = 0;
			}
		}
	}

	// change ledBuffer
	switch (programSequence[i].slope){
		case CONSTANT:
			{
				if (programSequence[i].gradient){
					unsigned short int left = programSequence[i].left_0;
					unsigned short int right = programSequence[i].right_0;

					if(orientation == UPSIDE_DOWN){
						unsigned short temp = left;
						left = right;
						right = temp;
					}

					unsigned short int gradient[12];
					if(left <= right){
						for (int o=0; o<12; o++){
							gradient[o] = left + ((o * (right - left)) / 11);
						}
					}
					else{
						for (int o=0; o<12; o++){
							gradient[o] = left - ((o * (left - right)) / 11);
						}
					}
					if (!blank_state){
						digitalWrite(BLANK, HIGH);
						digitalWrite(BLANK, LOW);
					}
					led_panel.setGradient(gradient);
				}
				else{
					if (!blank_state){
						digitalWrite(BLANK, HIGH);
						digitalWrite(BLANK, LOW);
					}
					led_panel.setPWM(programSequence[i].left_0);
				}
			}
			break;
		case LINEAR:
			{
				unsigned short int left_abs_diff = abs(programSequence[i].left_0 - programSequence[i].left_1);
				unsigned short int right_abs_diff = abs(programSequence[i].right_0 - programSequence[i].right_1);

				if (programSequence[i].gradient){
					unsigned short int left;
					unsigned short int right;
					if (programSequence[i].left_0 <= programSequence[i].left_1){
						left = programSequence[i].left_0 + ((left_abs_diff * programSequence[i].runtime) / programSequence[i].millis);
					}
					else{
						left = programSequence[i].left_0 - ((left_abs_diff * programSequence[i].runtime) / programSequence[i].millis);
					}
					if (programSequence[i].right_0 <= programSequence[i].right_1){
						right = programSequence[i].right_0 + ((right_abs_diff * programSequence[i].runtime) / programSequence[i].millis);
					}
					else{
						right = programSequence[i].right_0 - ((right_abs_diff * programSequence[i].runtime) / programSequence[i].millis);
					}

					if(orientation == UPSIDE_DOWN){
						unsigned short int temp = left;
						left = right;
						right = temp;
					}

					unsigned short int gradient[12];
					if(left <= right){
						for (int o=0; o<12; o++){
							gradient[o] = left + ((o * (right - left)) / 11);
						}
					}
					else{
						for (int o=0; o<12; o++){
							gradient[o] = left - ((o * (left - right)) / 11);
						}
					}

					if (!blank_state){
						digitalWrite(BLANK, HIGH);
						digitalWrite(BLANK, LOW);
					}
					led_panel.setGradient(gradient);
				}
				else{
					if (programSequence[i].left_0 <= programSequence[i].left_1){
						if (!blank_state){
							digitalWrite(BLANK, HIGH);
							digitalWrite(BLANK, LOW);
						}
						led_panel.setPWM(programSequence[i].left_0 + ((left_abs_diff * programSequence[i].runtime) / programSequence[i].millis));
					}else{
						if (!blank_state){
							digitalWrite(BLANK, HIGH);
							digitalWrite(BLANK, LOW);
						}
						led_panel.setPWM(programSequence[i].left_0 - ((left_abs_diff * programSequence[i].runtime) / programSequence[i].millis));
					}
				}
			}
			break;
		case EXPONENTIAL:
			{
				if (programSequence[i].gradient){
					unsigned short int left;
					unsigned short int right;
					if(programSequence[i].left_0 <= programSequence[i].left_1){
						left = ((uint16_t) (exp((programSequence[i].runtime) * programSequence[i].x) + programSequence[i].c - 1) );
					}
					else{
						left = ((uint16_t) (programSequence[i].left_0 - exp((programSequence[i].runtime) * programSequence[i].x) + programSequence[i].c - 1) );
					}
					if(programSequence[i].right_0 <= programSequence[i].right_1){
						right = ((uint16_t) (exp((programSequence[i].runtime) * programSequence[i].x_r) + programSequence[i].c_r - 1) );
					}
					else{
						right = ((uint16_t) (programSequence[i].right_0 - exp((programSequence[i].runtime) * programSequence[i].x_r) + programSequence[i].c_r - 1) );
					}

					if(orientation == UPSIDE_DOWN){
						unsigned short int temp = left;
						left = right;
						right = temp;
					}

					unsigned short int gradient[12];
					if(left <= right){
						for (int o=0; o<12; o++){
							gradient[o] = left + ((o * (right - left)) / 11);
						}
					}
					else{
						for (int o=0; o<12; o++){
							gradient[o] = left - ((o * (left - right)) / 11);
						}
					}

					if (!blank_state){
						digitalWrite(BLANK, HIGH);
						digitalWrite(BLANK, LOW);
					}
					led_panel.setGradient(gradient);
				}
				else{
					if(programSequence[i].left_0 <= programSequence[i].left_1){
						led_panel.setPWM((uint16_t) (exp((programSequence[i].runtime) * programSequence[i].x) + programSequence[i].c - 1) );
					}
					else{
						led_panel.setPWM((uint16_t) (programSequence[i].left_0 - exp((programSequence[i].runtime) * programSequence[i].x) + programSequence[i].c - 1) );
					}
				}
			}
			break;
		//case LOGARITHMIC:
		//	{
		//		if (programSequence[i].c == -1.0){
		//			programSequence[i].c = (double) (programSequence[i].left_1 - programSequence[i].left_0) / exp((double) programSequence[i].millis);
		//		}
		//		
		//		unsigned short int left_abs_diff = abs(programSequence[i].left_0 - programSequence[i].left_1);
		//		unsigned short int right_abs_diff = abs(programSequence[i].right_0 - programSequence[i].right_1);
		//
		//		if (!blank_state){
		//			digitalWrite(BLANK, HIGH);
		//			digitalWrite(BLANK, LOW);
		//		}
		//		led_panel.setPWM((uint16_t) programSequence[i].left_0 + programSequence[i].c * exp(programSequence[i].runtime));
		//	}
		//	break;
		default:
			break;
	}
	return 1;
}

void startProgram(){
	time_reference = millis();
	bt_send("  program started");
	currentEntry = 0;
	programSequence[currentEntry].start_time = millis();
	digitalWrite(BLANK, LOW);
	blank_state = 0;
	updateLEDs(currentEntry);
}

void stopProgram(){
	currentEntry = -1;
	entryCounter = 0;
	led_panel.setPWM(0);
	digitalWrite(BLANK, HIGH);
	blank_state = 1;
	bt_send("  program terminated");
	delay(400);
}

bool makeProgram(){
	char *entry;
	char *token;
	long extHash;
	long intHash;
	char entryBuffer[ENTRY_LENGTH+1];
	entryCounter = 0;
	int i = 0;
	char * pch;

	pch=strchr(btBuffer,'_');
	if(pch == NULL){
		//Serial.println("ERR: btBuffer contains no underscores");
		return false;
	}
	while (pch!=NULL){
		if(strchr(pch+1,'_') != NULL){
			pch=strchr(pch+1,'_');
		}
		else{
			break;
		}	
	}
	pch++;
	extHash = (long) atoi(pch); // "save" the hash
	*pch = NULL;
	intHash = hash(btBuffer);
	pch--;
	*pch = NULL;

	//Serial.print("extHash:\t");
	//Serial.println(extHash);
	//Serial.print("intHash:\t");
	//Serial.println(intHash);
	if(extHash != intHash){
		//Serial.println("ERR: external and internal hash are not identical");
		return false;
	}

	entry = strtok (btBuffer, "/"); // irrelevant
	for (entry = strtok (NULL, "/"); entry != NULL; entry = strtok (entry + strlen(entry)+1, "/") ){
		strncpy (entryBuffer, entry, sizeof (entryBuffer));
		//Serial.print("Entry: ");
		//Serial.println(entry);
		

		i = 0;
		for (token = strtok (entryBuffer, "_"); token != NULL; token = strtok (token + strlen (token) + 1, "_"))
		{
			switch (i){
				case 0:
					programSequence[entryCounter].slope = (unsigned short) atoi(token);
					break;
				case 1:
					programSequence[entryCounter].gradient = (unsigned short) atoi(token);
					break;
				case 2:
					programSequence[entryCounter].millis = (unsigned long) atoi(token) * 1000;
					break;
				case 3:
					if (programSequence[entryCounter].slope == JUMP){
						programSequence[entryCounter].remaining_jumps = (unsigned short) atoi(token);
					}else{
						programSequence[entryCounter].left_0 = ((unsigned long) atoi(token) * 4095 ) / 100;
					}
					break;
				case 4:
					programSequence[entryCounter].right_0 = ((unsigned long) atoi(token) * 4095 ) / 100;
					break;
				case 5:
					if (programSequence[entryCounter].slope == JUMP){
						programSequence[entryCounter].jump_index = (unsigned short) atoi(token);
					}else{
						programSequence[entryCounter].left_1 = ((unsigned long) atoi(token) * 4095 ) / 100;
					}
					break;
				case 6:
					programSequence[entryCounter].right_1 = ((unsigned long) atoi(token) * 4095 ) / 100;
					break;
				case 7:
					programSequence[entryCounter].acceleration = ((unsigned short) atoi(token));
					break;
				case 8:
					programSequence[entryCounter].accel = ((double) atof(token));
					break;
				case 9:
					programSequence[entryCounter].accel_millis = (unsigned long) atoi(token) * 1000;
					break;
				case 10:
					//Serial.println("ERROR: Received program sequence has incorrect format.");
					break;
				default:
					break;
			}
			i++;
		}
		programSequence[entryCounter].runtime  	= 0;
		if (programSequence[entryCounter].slope == EXPONENTIAL){
			double min_val = (double) min(programSequence[entryCounter].left_0, programSequence[entryCounter].left_1);
			double max_val = (double) max(programSequence[entryCounter].left_0, programSequence[entryCounter].left_1);
			programSequence[entryCounter].c = min(programSequence[entryCounter].left_0, programSequence[entryCounter].left_1);
			programSequence[entryCounter].x = log(max_val - min_val + 1.0)/((double) (programSequence[entryCounter].millis));
			if (programSequence[entryCounter].gradient == 1){
				double min_val2 = (double) min(programSequence[entryCounter].right_0, programSequence[entryCounter].right_1);
				double max_val2 = (double) max(programSequence[entryCounter].right_0, programSequence[entryCounter].right_1);
				programSequence[entryCounter].c_r = min(programSequence[entryCounter].right_0, programSequence[entryCounter].right_1);
				programSequence[entryCounter].x_r = log(max_val2 - min_val2 + 1.0)/((double) (programSequence[entryCounter].millis));
			}
		}
		entryCounter++;
	}
	return true;
}


void printProgram(){
	for(int i=0; i<entryCounter; i++){
		//Serial.print("––Entry ");
		//Serial.print(i);
		//Serial.println(":");
		//Serial.print("\t");
		//Serial.println(programSequence[i].slope);
		//Serial.print("\t");
		//Serial.println(programSequence[i].gradient);
		//Serial.print("\t");
		//Serial.println(programSequence[i].millis);
		//Serial.print("\t");
		//Serial.println(programSequence[i].left_0);
		//Serial.print("\t");
		//Serial.println(programSequence[i].right_0);
		//Serial.print("\t");
		//Serial.println(programSequence[i].left_1);
		//Serial.print("\t");
		//Serial.println(programSequence[i].right_1);
		//Serial.print("\t");
		//Serial.println(programSequence[i].acceleration);
		//Serial.print("\t");
		//Serial.println(programSequence[i].accel);
		//Serial.print("\t");
		//Serial.println(programSequence[i].accel_millis);
	}
}

long hash(char *str)
{
    long hash = 5381;
    int c;
    int i = 0;

    while (c = *str++){
    	i++;
        hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
    }

    //Serial.println(i);
    return hash;
}

void update_display(void)
{
	display.setRotation(orientation);
	
	if(currentEntry == -1){
		display.clearDisplay();
		time_inten ? display.setTextSize(3) : display.setTextSize(1);
		display.setCursor(0,0);
		//unsigned long int sec;
		if(reValue >= 114){
			unsigned int temp = reValue;
			temp = temp - 114;
			unsigned int min = 10 + temp ;
			//sec = min*60;
			display.print(min);
			display.println("min");
		}
		else if(reValue >= 60){
			unsigned int temp = reValue;
			temp = temp - 60;
			unsigned int min = 1 + (unsigned int) temp / 6;
			unsigned int sec = (temp % 6) * 10;
			display.print(min);
			display.print(":");
			if(sec == 0)display.print("0");
			display.print(sec);
			display.println("min");
		}
		else{
			display.print(reValue);
			display.print("sec");
		}
		time_inten ? display.setTextSize(1) : display.setTextSize(3);
		time_inten ? display.setCursor(0,24) : display.setCursor(0,12);
		display.print(intensity);
		display.println("%");
		display.display();
	}
	else if(currentEntry >= 0){
		display.clearDisplay();

		display.setTextSize(2);
		display.setCursor(0,0);
		display.print("Bluetooth");
		//display.setTextSize(2);
		display.setCursor(0,16);
		display.print("entry: ");
		display.println(currentEntry);
		display.display();
	}
	else{
		display.clearDisplay();

		display.setTextSize(2);
		display.setCursor(0,0);
		display.print("Illuminate");
		display.setCursor(0,16);
		display.print(intensity);
		display.println("%");
		display.display();
	}
}

void irupt_re_clk()
{
	rot_clk = digitalRead(RE_CLK);

	if(currentEntry == -1){
		if(time_inten){
			if(digitalRead(RE_DT) != rot_clk){
				if(reValue < 164)reValue++;
			}
			else{
				if(reValue > 0)reValue--;
			}
		}
		else{
			if(digitalRead(RE_DT) != rot_clk){
				if(intensity < 100)intensity++;
			}
			else{
				if(intensity > 0)intensity--;
			}
		}
	}
}

void irupt_re_sw()
{
	unsigned long int currentMillis = millis();

	if(digitalRead(RE_SW) == LOW){
		sw_pressed = currentMillis;
	}
	else{
		unsigned long int diff = currentMillis - sw_pressed;
		if(diff >= 500){
			//Serial.println("long");
			if(currentEntry != -1){
				stopProgram();
			}
			else{
				startSimpleProgram();
			}

		}
		else if(diff >= 15){
			//Serial.println("short");
			if(currentEntry != -1){
				stopProgram();
				if(currentEntry == -1){
					time_inten ? time_inten = 0 : time_inten = 1;
				}
			}
			else{
				time_inten ? time_inten = 0 : time_inten = 1;
			}
		}
	}
}

void startSimpleProgram(){
	time_end = millis() + (reValue_to_seconds()*1000);
	currentEntry = -2;
	digitalWrite(BLANK, LOW);
	blank_state = 0;
	led_panel.setPWM( (int)(4095*intensity)/100 );
}

unsigned long int reValue_to_seconds(){
	unsigned long int sec;
	if(reValue >= 114){
		unsigned int temp = reValue;
		temp = temp - 114;
		unsigned int min = 10 + temp;
		sec = min*60;
	}
	else if(reValue >= 60){
		unsigned int temp = reValue;
		temp = temp - 60;
		unsigned int min = 1 + (unsigned int) temp / 6;
		sec = (min*60) + (temp % 6);
	}
	else{
		sec = reValue;
	}
	return sec;
}
