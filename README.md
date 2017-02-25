# ServerEco
This plugin allows for making specific plugins use certain economy accounts for their transaction!

Basic config:
```
# If true, You will get information like the plugins ID displayed into the server console
debug=false
plugin {
    servereco {
        # This is an example of how to configure this plugin, Just add more like this below to configure another plugin to use an account
        account=Server
    }
}
# DO NOT TOUCH or your config will go poof
version=1
```
You can add as many plugins as you want, each with a different account if youd like!
