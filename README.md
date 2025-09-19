# simpleservermessage
Simple Server Message is a plugin which can be used to periodically send messages in a server and also register commands to send messages. 

## Features
- A messenger which sends messages to the players on a set interval. The order can be configured to be randomized.
- Create your own message commands that send text back, for example /discord or /map.
- All commands can have aliasses, hover text and be made clickable to open lins. 
- If the floodgate plugin is installed, you can set specific messages for bedrock players. 

## commands
The only command this plugin has out of the box is /ssmreload. Which reloads the config file. You need the permission SimpleServerMessage.ssmReload to use it. 

## known issues
It's impossible to fully remove commands created via the config. If a command gets removed from the config file and the plugin gets reloaded only with /ssmreload, it gets actually replaced by a message mocking the unknown command error.
Removing Aliasses from a command also doesn't work for this very same reason.
Restarting the server does completely remove these commands. 
