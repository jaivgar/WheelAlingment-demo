# WheelAlingment-demo
Demo for the Far-Edge project using Arrowhead

Overview information about the use case can be seen in word document "SosD ...", although not yet
fully implemented, missing buffer cloud and Virtual Workstation.

Demo performed using code from branch "feature/data_manager" in repository "arrowhead-f/core-java" 
for the Local Clouds (LC). 
Refer to arrowhead-f/core-java for how to deploy Local Clouds, and similar procedure for the consumer 
and provider services inside the local clouds. 

Modifications have been done in starting scripts, so the scripts to create databases are customized
for the application systems and consumers used in the use case. Also the core systems have a script
to run them, remember to check "start_insec..." for what system are started manually 
(service_registry first, then start_insec.. scripts and finally data_manager). This is the 
configuration at the moment that is good for checking that everything is running properly, but could 
be manually changed keeping the order of initalization.

In addition to the customized databases scripts, it is needed to manually add the authorization rules
so that the systems can communicate, and example is shown in picture "Add ERP_PLM system for 
authorization", general explanations can be found in the Arrowhead documentation. This is needed for
every application system that needs to communicate with another application or core system.
