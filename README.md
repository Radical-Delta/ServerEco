# ServerEco
This plugin allows for making specific plugins use certain economy accounts for their transaction!

Default config with explanations:
```
# If true, You will get information like the plugins ID displayed into the server console
debug=false
plugin{
  #Format is 'pluginID'='virtual account name' or use /se add <pluginID> <virtual account>
  servereco=Server
}
```

### Commands
```
'/se' or '/se help' - Display all commands - Permission: servereco.command.help
'/se add <plugin> <account>' - Add an account to the config - Permission: servereco.command.add
'/se del <plugin>' - Remove a plugin from the config - Permission: servereco.command.del
'/se list' - List all configured plugins - Permission: servereco.command.list
'/se debug (true|false)' - Switch debug status or toggle debug - Permission: servereco.command.debug
```

You can add as many plugins as you want, each with a different account if youd like!
