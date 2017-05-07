# traffic_model
Agent-based traffic wave model written in Java.

README

This application aims to model traffic congestion in a one directional road circuit using an agent-based traffic wave model with lights.


GENERAL USAGE NOTES
---------------------

-The application's gui contains 3 panels: the model area(right hand side) where the road, vehicles and traffic lights are visualised, the control panel(top left hand side) where the parameters are set for the model and the buttons are contained, and the statistics panel(bottom left hand side) which show some statistics associated with the model.

-The model runs on a basis of 'ticks'. One tick represents one lapse of time whereby each vehicle moves once. 

-On creation, traffic lights are given a random number of ticks they will stay off for. This number is between 10 and 50. This is meant to simulate the fact that some traffic lights are more used/stay on green light for longer than others.



OPERATING INSTRUCTIONS
---------------------

IMPORTING A CUSTOM BUILT ROAD NETWORK/MODEL ENVIRONMENT:
-Model environments must be in the form of a txt file. It must be a rectangular map of numbers. It is recommended that maps be of 100x100 in size for the best visual representation within the model panel. 
-Each element/tile within the txt file must be numbered with one of the following numbers depending on what it represents:
0 : Empty space
1 : A north facing road.
2 : An east facing road.
3 : A south facing road.
4 : A west facing road.
-I.e. A north facing road is a road where the vehicles move north on that road.
-Failing to import a complete road circuit that does not use the correct road values will crash the application when the model is ran.

RUNNING A MODEL
-Import the road network using file --> open. The file must be a txt.
-To place a vehicle on the imported road nextwork left click on a road tile in the model panel. Alternativley, click the 'auto add vehicles' button in the control panel. This adds a vehicle on every 7th tile. Press the button multiple times for more vehicles.
-To place a traffic light on the road network right click a road tile.
-In the control panel set the desired parameters which includes the number of ticks for the model to run for, the number of ticks for the traffic lights to stay on for and the animation speed.
-Click the start button in the control panel.
-Click the invert colours button to invert the road and background colours if desired.
-Press the reset button if you would like to start from the originally imported model environment.

SAVING THE MODEL'S CURRENT STATE AND STATISTICS
-While the model window is currently displaying an environment select file --> save to save the current state. This state can be then loaded into the application and is treated as the environment's original state (the reset button will reset to that).
-To save the currently displayed overall statistics selcet file --> save statistics. This will save just the overall/average statistics as a txt file.