System Execution Instructions
=============================

Full System Execution (GUI): To use the system through the graphical interface, you must first
launch ClubServer via its entry point. This initializes the database and enables communication
through commands triggered by GUI interactions. Once the server is running, execute the entry
point of the App class located in the com.example.clubapp package. After completing both steps,
the system is ready for real-user interaction. You may launch the App class multiple times to
test the system's behavior with multiple concurrent users.

Simulation Mode: To view the results of a simple simulation (as specified in the project
requirements), execute only the entry point of the SimulationRunner class. Once the execution
is finished, you can verify the results in the simulation.txt file.