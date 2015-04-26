# Network Delegate Bindings
A list of the default Network Delegates made available by Toast for you to connect to.  

## Delegates

### Logger
<a name="del_logger"></a>
**DelegateID:** TOAST_logger  
**Connection:** One Way (Send to Client)  
**Password:** Optional (Set in Toast config)

The Logger Delegate will simply accept connections from any client that wishes to connect. Upon connection, the Logger Delegate will echo the entire backlog of the Console Output from the robot. Additionally, any further logging messages sent from the 'Logger' class will also be broadcast to any connected clients. This functions as a remote console for the logging output of Toast and it's modules.  

### Command Bus
<a name="del_command"></a>
**DelegateID:** TOAST_command  
**Connection:** One Way (Receive from Client)  
**Password:** Optional (Set in Toast config)

The Command Delegate will simply accept connections from any client that wishes to connect. The Command Delegate will listen for incoming messages from clients and will interpret the message as a Command to be sent to the command bus. The incoming message must create a new line per command. The new line must follow the '\\n' standard (not '\\r\\n'). Commands will not be interpreted if they do not end in a new line. The command response will not be sent back to the client, but instead will be sent to the [Logger Delegate](#del_logger)
