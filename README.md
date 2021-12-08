# Mobile Sensor Data Collection

The python code is give in "IoT Project.ipynb" file. It is self contained and comments are given so that anyone can understand it easily. Initially, we load the data, perform feature selection, and apply ML algorithm.

We then load the UCI data, perform same feature selection, and apply ML algorithms

The android application code to collect the sensor data is given in "Android Application.zip" file. It is also self contained with comments

All the data including the one that we collected and the UCI data is in the "Data" folder.

The android application look like this
![android](https://user-images.githubusercontent.com/8347760/145128533-0cc9d36a-2d03-404d-9c07-af92b58f7708.png)

It collect data from 3 sensors and save it in csv formate in the download folder. Each csv file contains x-, y-, and z-axis values. The app record the sensor data for 60 seconds and the data collection automatically terminates after that. App has the functionality to run in the background if we minimize it.
