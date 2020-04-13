# General Information

This is a free JumpLeague Plugin for your Minecraft Spigot Server.
The plugin is currently only tested on Spigot 1.12.2 and may contain some bugs as it's still in devlopment (but it's already useable)

## Getting the plugin on your server

Download the latest jar from the [releases](https://github.com/Skiftstar/JumpLeague/releases) and put it in your plugins folder.
The Plugin will only work if you have [Vault](https://www.spigotmc.org/resources/vault.34315/) and an economy plugin (like [Aconomy](https://www.spigotmc.org/resources/aconomy-simple-vault-economy.64569/)) on your server as well!

# Setup - Configs

You can customize most things about the plugin in the main config (config.yml). Providing an invalid value for one of the options may cause the plugin to either crash or not work. If you delete one of the precreated options, the plugin will readd it once the server is either reloaded or restarted.
You can find the default config [here](https://github.com/Skiftstar/JumpLeague/blob/master/resources/config.yml)

The other customizable config is the chests config (chestItems.yml) - You can find a config with a working Item preset here(ADD LINK!) if you don't want to add your own drop table for the chests.

When editing it, please use the preset one as an orientation. Make sure that the difficulty names are the same as the ones in the parts config (parts.yml) or else the plugin doesn't know to what difficulty the drop tables belongs to and just ignores it.

When adding potions to the drop table, please keep in mind that you have to provided extra value such as potionType, potionExtended and potionUpgraded.

potionType is the type of potion, you can find a list of potionTypes [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionType.html) (Please keep in mind that this list is for 1.15.2 so some potion effects may not exist in 1.12.2)

potionExtended (true or false) tells the plugin if the potion effect is longer than the base potion

potionUpgraded (true or false) tells the plugin if the potion effect is upgraded (for example instant heal 2)

If you don't know the spigot Item Materials, you can find a list of them [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html) (once again, keep in mind that this list is for 1.15.2 so some items may not exits in 1.12.2)

# Setup - Ingame

The following permissions are important for staff members (commands are explained down below):
- pixelLeague.setLobbySpawn - for the /setLobbySpawn command
- pixelLeague.setPvPSpawn - for the /setPvPSpawn command
- PixelLeague.savePart - for the /savepart command
- pixelLeague.leagueStart - for the /leagueStart command

And here is a list of all of the commands in the plugin:
- /setLobbySpawn - Sets the coordinates for the lobby where the players will be teleported to when joining a server without an active game
- /setPvPSpawn - Adds the current Location to the possible PvPSpawns for the PvP Round
- /savepart [difficulty] - adds the selected part to the parts config so that can be used when generating a parkour
- /leaguestart - force starts a game with the players currently on the server

### Saving parts

Saving parts is pretty simple. Take a wooden shovel and left click to set the first postion, right click to set the second postion.

then use /savepart [difficulty] (e.g. /savepart easy) to save the part.
Possible difficulties are: easy, medium, hard, hardcore

**!IMPORTANT! The part has to be oriented in Positive X or else the generator may produce some nonsense. Please keep this in mind when saving parts!**
