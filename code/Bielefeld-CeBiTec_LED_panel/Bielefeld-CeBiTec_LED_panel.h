#ifndef _BIELEFELD_CEBITEC_LED_PANEL
#define _BIELEFELD_CEBITEC_LED_PANEL

#include <Arduino.h>

class TLC5947 {
	public:
 		TLC5947(uint8_t c, uint8_t d, uint8_t l);

 		boolean begin(void);
 		void setPWM(uint16_t chan, uint16_t pwm);
 		void write(void);
	private:
 		uint16_t *pwmbuffer;
 		uint8_t _clk, _dat, _lat;

};

class LED_panel {
	public:
 		LED_panel();
 		boolean begin(void);
 		void setPWM(uint16_t pwm);
 		void testGradient(unsigned short int pwm[12]);
 		void setGradient(unsigned short int pwm[12]);

	private:
 		TLC5947* _tlcs[4];
 		uint8_t blank;
};

#endif