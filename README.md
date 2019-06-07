# LEDpanel
A multifunctional, bluetooth controlled LED panel for illuminating 96-well microtiter plates containing biological or chemical samples. The first version of this hardware was developed as part of the iGEM project from team Bielefeld CeBiTec 2017. For a detailed description of this first model and its applications in our project, see the [Hardware page](2017.igem.org/Team:Bielefeld-CeBiTec/Hardware) of our project wiki.

## Disclaimer

The hardware described on this github repository is still in prototype phase and both software and hardware may contain design faults. Furthermore, I do not take any responsibility and I am not liable for any damage that is caused by handling of electrical components or devices based on designs, instructions and information in this repository.

## Some Hints for Assembly

* In principal, any LEDs can be mounted on the LED panel. The constant current flowing through the LEDs is adjustable with potentiometers on the PCB itself. The LED panel is designed to be configurable to either control the current of all LEDs with a single potentiometer, or two groups of LEDs with an individual potentiometer for each group. Therefore, it is possible to mount two different groups of LEDs on the LED panel. In any case, I highly recommend to adapt the footprint in the PCB design files according to the instructions in the LED's technical sheets.

* Please adapt the file "model_slim_microtiter_adapter.stl" to fit the microtiter plates that are used in your laboratory.

* The parts list is probably outdated and is missing some required parts. Anyways, it might give some orientation in choosing the right parts.

* The LED panel requires two different voltages supplied over a micro usb cable: 3.3 V for the micro controller and logic, and a seperate LED supply voltage that should be at least 0.4 volts above the supply voltage of the LEDs in use. I used two LM2596 based DC-DC voltage regulator boards in a seperate 3d printed case (voltage_regulators_case.stl) to supply the required voltages from a standard 5V DC mains adapter with a barrel connector.