#include <Bielefeld-CeBiTec_LED_panel.h>

uint16_t ledOrder[6][4] = {{2,3,4,5},
						  {1,6,7,8},
						  {0,9,10,11},
						  {23,14,13,12},
						  {22,17,16,15},
						  {21,20,19,18}};

TLC5947::TLC5947(uint8_t c, uint8_t d, uint8_t l) {
	_clk = c;
	_dat = d;
	_lat = l;

	pwmbuffer = (uint16_t *)malloc(2 * 24);
	memset(pwmbuffer, 0, 2*24);
}

LED_panel::LED_panel() {
	uint8_t c = 13;
	uint8_t d = 11;
	uint8_t b = 3;
	uint8_t l1 = 8;
	uint8_t l2 = 16;
	uint8_t l3 = 7;
	uint8_t l4 = 17;
	_tlcs[0] = new TLC5947(c, d, l1);
	_tlcs[1] = new TLC5947(c, d, l2);
	_tlcs[2] = new TLC5947(c, d, l3);
	_tlcs[3] = new TLC5947(c, d, l4);

	blank = b;
}

void TLC5947::write(void) {
	digitalWrite(_lat, LOW);
	// 24 channels per TLC5947
	for (int16_t c=23; c >= 0 ; c--) {
		// 12 bits per channel, send MSB first
		for (int8_t b=11; b>=0; b--) {
			digitalWrite(_clk, LOW);
			if (pwmbuffer[c] & (1 << b)) 
				digitalWrite(_dat, HIGH);
			else digitalWrite(_dat, LOW);
			digitalWrite(_clk, HIGH);
		}
	}
	digitalWrite(_clk, LOW);
	digitalWrite(_lat, HIGH);	
	digitalWrite(_lat, LOW);
}

void TLC5947::setPWM(uint16_t chan, uint16_t pwm) {
	if (pwm > 4095) pwm = 4095;
	if (chan > 24) return;
	pwmbuffer[chan] = pwm;	
}


boolean TLC5947::begin() {
	if (!pwmbuffer) return false;
	pinMode(_clk, OUTPUT);
	pinMode(_dat, OUTPUT);
	pinMode(_lat, OUTPUT);
	digitalWrite(_lat, LOW);
	return true;
}

void LED_panel::setPWM(uint16_t pwm){
	for (int16_t c=0 ; c <4 ; c++){
		for (int16_t l=23; l >= 0 ; l--){
			_tlcs[c]->setPWM(l, pwm);
		}
		_tlcs[c]->write();
	}
}

void LED_panel::setGradient(unsigned short int pwm[12]){
	for (int16_t c=0 ; c <4 ; c+=2){
		for (int16_t l=0; l < 6 ; l++){
			for (int16_t o=0; o < 4; o++){
				_tlcs[c]->setPWM(ledOrder[l][o], pwm[l + ((c/2)*6)] );
				_tlcs[c+1]->setPWM(ledOrder[5-l][o], pwm[l + ((c/2)*6)] );
			}
		}
		_tlcs[c]->write();
		_tlcs[c+1]->write();
	}
}

void LED_panel::testGradient(unsigned short int pwm[12]){
	for (int16_t c=0 ; c <4 ; c+=2){
		for (int16_t l=0; l < 6 ; l++){
			for (int16_t o=0; o < 4; o++){
				_tlcs[c]->setPWM(ledOrder[l][o], pwm[l + ((c/2)*6)] );
				_tlcs[c+1]->setPWM(ledOrder[5-l][o], pwm[l + ((c/2)*6)] );
			}
			_tlcs[c]->write();
			_tlcs[c+1]->write();
			delay(200);
		}
		_tlcs[c]->write();
	}
}

boolean LED_panel::begin(){
	pinMode(blank, OUTPUT);
	digitalWrite(blank, HIGH);

	for (int16_t c=0 ; c <4 ; c++){
		if (!(_tlcs[c]->begin())) return false;
	}
	return true;
}
