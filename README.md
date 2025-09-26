![DNDSheets](https://media.forgecdn.net/attachments/description/null/description_a4b20240-7c01-4f37-bb19-077c88bdb478.png "Optional Title")

## Description


D&D Sheets is a utility and QoL mod intended for tabletop players. It gives the player the ability to manage and roll from a character sheet accessible with the press of a key. You can set ability scores, customize skill checks and saving throws, and even make your own attack and damage rolls.

 

## What is this for?
You've probably never thought about using Minecraft for tabletop; after all, why would you when Foundry and Roll20 exist? There's even VTTs for 3D environments. You'd never think Minecraft.

But as it turns out, Minecraft is an excellent way to create an immersive experience. It is, however, difficult to use for tabletop normally. After all, you will have to ask your players to make rolls and manage a sheet outside of Minecraft. This adds unnecessary micromanagement on the players and DM both, takes up additional system resources, and demands constant alt tabbing to be efficient.

This mod seeks to change that.

Essentially, this mod is designed for DMs and players alike that wish to play D&D 5e using Minecraft as their VTT of choice.


## Current Features
- Fillable character sheets for each player on the server, accessible with a keypress.
- Buttons from the character sheet to make easy dice rolls, which output rolls server-wide.
- Customizable roll expressions, allowing players to fine-tune how the buttons on their sheet work.
- A dedicated tab for attack and damage rolls, not technically limited to the aforementioned, allowing you to make your own rolls for any need and any context.
- A simple /roll (shorthand /r) command to make dice rolls, using standard dice notation

## Planned Features
- A dedicated spells tab with spell slots.
- DMing tools, allowing DMs to manage initiative from a GUI ingame.
- The ability to store and manage multiple sheets per player.
- Placeable blocks which project visible areas-of-effect, such as spheres and cones.
- Measuring tools to tell distances easily and help players judge the movement they're using.
- Localization to other languages.

## Known Bugs
- Roll expressions don't work correctly when faced with multiple dice groups (i.e. 1d20 + 1d4). Multiple dice in one group (i.e. 4d4) do, however, work. In complex expressions, you can substitute multiple dice groups for now by using the "Extra Roll" section (thereby allowing up to two).

## Technical Details
This mod needs to be on both the client and server. In multiplayer, character sheets are kept for each player on the server, associated by UUID and saved as JSON on the server end. This can be seen in a "charactersheets" folder in the server instance. This allows server owners (likely the DM) to see the sheets themselves. These files are loaded when a client joins, and are saved to in real time when players make changes to their sheets. 

While technically usable, this mod is not intended for singleplayer. You won't get much use out of it. This mod also does not have any functions which impact Minecraft's normal gameplay. It does not do anything on its own, in other words. So you're not suddenly gonna be making dice rolls when attacking mobs. It would be beyond the scope of the mod and wouldn't fit its intended purpose anyway. 

 

## I don't know how to play D&D, does this mod teach me? Can you make a version for Pathfinder? Can you update to 1.21.1? Will I always be a DM?
Please note that while this mod makes playing D&D much easier, it does not contain any resources to play the game with. You will need to legally obtain those yourself. 

There are also no plans to expand to other tabletop systems or update to newer versions of Minecraft. But you're more than welcome to do so yourself! Feel free to make pull requests to the project GitHub if you'd like to take up the task.

You will always be a DM.
