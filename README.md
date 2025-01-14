# social-friends-plugin (1.21)
A plugin to allow a player to add / remove friends on Minecraft 1.21 using Plugins feature.

This plugin save player's friends and friends request into a "[player's UUID].yml" file in player_data directory.
It also save player's last connection date.
In game you can use various commands such as : /friends add <player> | /friends 

This is my first public plugin, this serves more of a display project rathen than a commercial project.

Use this plugin at your own risk, the plugin might encounter bugs. It should work tho. 

## Permission
| Permission Node | Default | Description |
| ------------------------- | ---------- | ---------------- |
| social.friends | Everyone | Allows use of all Social-Friends commands. |

## Commands
| Commands | Description |
| ------------------------- | ---------------- |
| /friends add <player> | Send a friend request to a player. |
| /friends remove <player> | Remove a friend. |
| /friends accept <player> | Accept a friend request, if it exist. |
| /friends deny <player> | Deny a friend request, if it exist. |
| /friends list | Show all player's friends with the last time they log in. |
| /friends | Show the list of all commands. |

## Tools

Tools used to make and test this plugin: Java IDE 'IntelliJ IDEA Community Edition' by JetBrains, Maven, Spigot, Minecraft and MultiMC.
