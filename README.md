# STDISCM-Prime-Checker

IMPORTANT: Make sure that your IDE runs on multiple instances for the SlaveServer.java ONLY.

1. Run MasterServer.java
2. Run ClientServer.java

Conditional:
If only 1 slave is needed, Run SlaveServer.Java.
Else, if more than 1, make sure that it has different ports...:
Change the first slave port to 5003, second slave to 5004, and so on and so forth. 

Not uniquely assigning the port will cause the masterserver to not recognize the slaveservers created.
