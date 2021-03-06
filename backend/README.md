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

Package `org.team2.cluk.backend.unprocessed` contains classes 
`Driver` and `WorkingHours` that have not been yet integrated
with the rest of the Web API.
## Used libraries
Apart from Jersey, project makes use of MySQL/J Connector,
javax.json package and Apache Common Lang 3. Required dependencies 
are in `lib` directory. Depending on the IDE, you might need to 
add it to the build/execution path manually.

## Requirements
Backend runs on Oracle JDK or OpenJDK 12. Root permissions 
might bre required to run the main method.

URIs and API keys might be used in the source code. They are not valid and please replace them with your own.
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
