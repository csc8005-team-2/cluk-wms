# CLUK Warehouse Backend and Mobile App
*CSC8005 Team 2 Repository*
## Repository description
This repository contains code for REST-ful API for the
Warehouse Management System in its root directory and 
Android application for drivers in `googleMap1` directory.
## IDE Project description
Backend is created in IntelliJ IDEA Community set to use
`.classpath` and `.project` files for Eclipse compatibility. 
Mobile app is created in Android Studio.
## Backend description
Backend is created in Java EE using Jersey library as JAX-RS
implementation. To launch server, use `StartServer` class 
in `org.team2.cluk.backend` package. 
Package `org.team2.cluk.backend.webresources` contains
whole API logic and package `org.team2.cluk.backend.tools`
consists of useful objects used in the project.
## Used libraries
Apart from Jersey, project makes use of MySQL/J Connector,
javax.json package and Apache Common Lang 3.
## Contributions
### Backend
Business logic of the backend was designed by #teambackend:
* A. Azeez
* M. Lee
* W. Nujum
* A. Wood
Business logic was refactored and adjusted for Web workload by 
**M. Grabara**. During the process, server logic, logging 
system and supporting classes in `org.team2.cluk.backend.tools` 
package were created.
### Mobile app
The Android app was created by **W. Nujum**.