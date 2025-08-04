# Dungeon Generator

It's a program written in Java that generates dungeon structures.
It has three generating algorithms, can save the dungeon as an image, and allows the user to observe or speed up the dungeon generation.

## Inputs
  - Size of map - Height and width of the dungeon.
  - Size of dungeon - Number of squares that the dungeon should at least have.
  - Seed - Seed for random generation.
  - Dungeon algorithm - Type of algorithm that will be used to generate the dungeon.

## Buttons (Reading from the top right corner)

  - Start - Start generating.
  - Stop - Stop generating.
  - Next - Make the next step if stopped.
  - Reset - Clear the screen of the previous dungeon.
  - Save - Save the dungeon as an image.
  - Speed up - Skip the generating process to the end.
  - Resize - Center and zoom in the dungeon.
  - Draw walls - Draw the walls of the dungeon.
  - Draw entries - Draw entries to the dungeon.
  - Draw treasures - Draw a few treasures.
  - Draw enemies - Draw a few enemies.
  - Draw doors and keys - Draw a few doors and keys.
  - Drew safe room - Drew room safe from enemies and with friendly NPC


## Colors
  - Black - Outside of the dungeon.
  - White - Floors.
  - Gray - Walls.
  - Red - Enemies.
  - Orange - Entries (Entry and exit).
  - Blue - Treasures.
  - Yellow - Doors (Blocking passage inside the dungeon).
  - Cyan - Keys.
  - Green - Safe room doors.
  - Magenta - Friendly NPC.

## Graphics
Icons used in the project are from https://game-icons.net/.
  - Bully minion icon, Clockwise rotation icon, Contract icon, Fast forward button icon, Highlighter icon, Next button icon, Save icon, Shop icon were created by Delapouite.
  - Key icon was created by sbed.
  - Pause button icon, Play button icon were created by Guard13007.
  - Open chest icon was created by Skoll.
  - Wooden door icon was created by Lorc.

## Used modules
Program use module [TOOLBOX_Delaunay_Triangulation_JAVA](https://github.com/wuga214/TOOLBOX_Delaunay_Triangulation_JAVA).