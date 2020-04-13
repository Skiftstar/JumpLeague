# General Information

This is a free JumpLeague Plugin for your Minecraft Spigot Server.

This plugin is currently only tested on Spigot 1.12.2 and may contain some bugs as it's still in development (but it's already useable)

## Getting the plugin on your server

Download the latest jar from the [releases](https://github.com/Skiftstar/JumpLeague/releases) and put it in your plugins folder.
The Plugin will only work if you have [Vault](https://www.spigotmc.org/resources/vault.34315/) and an economy plugin (like [Aconomy](https://www.spigotmc.org/resources/aconomy-simple-vault-economy.64569/)) on your server as well!

# Setup - Ingame

### Setting up your worls

Currently, to use PixelLeague, you need [Multiverse](https://www.spigotmc.org/resources/multiverse-core.390/) and [VoidWorld](https://www.spigotmc.org/resources/voidworld.29807/). Generate a world name 'void' by executing the following command: `/mv create void normal -g VoidWorld` and then you're good to go!

## Permissions and Commands

The following permissions are important for staff members (commands are explained down below):
- pixelleague.* - Gives all permissions
- pixelLeague.setLobbySpawn - for the /setLobbySpawn command
- pixelLeague.setPvPSpawn - for the /setPvPSpawn command
- PixelLeague.savePart - for the /savepart command
- pixelLeague.leagueStart - for the /leagueStart command
- pixelLeague.partsList - for the /parts command
- pixelLeague.delpart - for the /delpart command
- pixelleague.removepvpspawn - for the /removePvPSpawn command

And here is a list of all of the commands in the plugin:
- /setLobbySpawn - Sets the coordinates for the lobby where the players will be teleported to when joining a server without an active game
- /setPvPSpawn - Adds the current Location to the possible PvPSpawns for the PvP Round
- /savepart [difficulty] [name] - adds the selected part to the parts config so that can be used when generating a parkour
- /parts - shows all parts with their names and difficulties
- /delpart [difficulty] [name] - removes the part from the provided difficulty if a part with that name exists
- /leaguestart - force starts a game with the players currently on the server
- removePvPSpawn [Number/all] - removes a given amount of (or all) PvPSpawns. Deletes from the newer ones first.

### Saving parts

Saving parts is pretty simple. Take a wooden shovel and left click to set the first postion, right click to set the second postion.

then use /savepart [difficulty] [name] (e.g. /savepart easy HouseJump1) to save the part.
Possible difficulties are: easy, medium, hard, hardcore

**!IMPORTANT! The part has to be oriented in Positive X or else the generator may produce some nonsense. Please keep this in mind when saving parts!**

# Setup - Configs

### Main Config

You can customize most things about the plugin in the main config (config.yml). Providing an invalid value for one of the options may cause the plugin to either crash or not work. If you delete one of the precreated options, the plugin will readd it once the server is either reloaded or restarted.
You can find the default config [here](https://github.com/Skiftstar/JumpLeague/blob/master/resources/config.yml)

### Chests config

The other customizable config is the chests config (chestItems.yml) - You can find a config with a working Item preset [here](https://github.com/Skiftstar/JumpLeague/blob/master/resources/chestItems.yml) if you don't want to add your own drop table for the chests.

When editing it, please use the preset as an orientation. Make sure that the difficulty names are the same as the ones in the parts config (parts.yml) or else the plugin doesn't know to what difficulty the drop tables belongs to and just ignores it.

Keep in mind that 'chance' is the chance of the item to appear in the chest. This means that even if you have two items with a chance of 50%, there still is a 25% that the chest will be empty.

Adding potions is a bit different than other items so I will give you an example here.

First of all, you have to create an extra block for potions for each difficulty, it has to be called 'Potions'.

Here is an example of adding three different types of potions with some comments that might help you out!
```
Potions:
  'potion1':
    minCount: 1
    maxCount: 1
    chance: 50
    potionType: 'INSTANT_HEAL' # The potionEffect
    potionUpgraded: true # If the potion is upgraded (e.g. Instant health 2)
    potionExtended: false # If the potion effect is longer than the base effect
    potionMaterial: POTION # The type of potion that it is (normal, lingering, splach)
  'potion2':
    minCount: 1
    maxCount: 1
    chance: 50
    potionType: 'REGEN' # This potion has regeneration
    potionUpgraded: false
    potionExtended: true # And the effect lasts longer than normal
    potionMaterial: SPLASH_POTION # This time we're gonna use a splash potion
  'potion3':
    minCount: 1
    maxCount: 3 # The stack can have up to 3 Potions
    chance: 20
    potionType: 'POISON' # Poison Potion this time
    potionUpgraded: false
    potionExtended: false
    potionMaterial: LINGERING_POTION # This time it's a lingering potion
```

You can find a list of potionTypes [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionType.html) (Please keep in mind that this list is for 1.15.2 so some potion effects may not exist in 1.12.2)

If you don't know the spigot Item Materials, you can find a list of them [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html) (once again, keep in mind that this list is for 1.15.2 so some items may not exits in 1.12.2)

### Messages Config

You can also customize all of the messages the plugin outputs.

To do this, just go into the messages config (messages.yml) and have fun. Keep in mind that color codes work as well
