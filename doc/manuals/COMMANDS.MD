# Toast Commands  

## Legend
**bold** : A variable or argument title  
*italic* : An optional variable or argument  
\[param_name\] : Replace with your own string  

##  Commands  

### USB  
<a name="cmd_usb"></a>
**usb** : Used for interaction between USB Mass Storage Devices
- **generate** : Generates a default toast_autorun.conf file for all Invalid USB Drives connected
- **dump** :  Will copy all local Toast files to a USB Device  
  - *[drive_name]* :  Specify a USB Device to dump to. If not set, it will dump to all connected USB devices.
- **load** : Copy all Toast files from a USB Drive to the local Toast Directory (i.e. reload from backup)
  - *[drive_name]* : Specify a USB Device to load from. If not set, it will load from all connected USB devices

### Toast Thread Pool
<a name="cmd_threads"></a>
**threads** : Echos a debugging message regarding the status of the default Toast Thread Pool, including the number of active threads, core threads, active jobs, completed jobs and pending jobs, as well as the status of the ThreadPool(Shutdown/Terminated/Terminating)

No arguments or parameters

### Groovy Scripting
<a name="cmd_script"></a>
**script** : Used for On-The-Fly groovy scripting.
- *-c* : Execute the Script in the Toast Thread Pool. This will make the script run concurrently.
- *Any further arguments are taken as the groovy script to execute*
