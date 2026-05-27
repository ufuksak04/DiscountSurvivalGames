### DiscountSurvivalGames

This is a Minecraft Java plugin made for Spigot 1.21.8 (ver 1.21.8-R0.1-SNAPSHOT) that aims to clone the classic minigame Survival Games (or Hunger Games). It is meant as a learning project as at the start of making this project I had zero Java knowledge.

## Index

  - [Features](#Features)
  - [Development Notes](#Development_Notes)
  - [Setup](#Setup)
  - [License](#License)

## Features

  - Automatic game lifecycle - the plugin automatically queues and creates games with resetting maps for multiple concurrent games
  - Custom spectator state - eliminated players are put inside a custom spectator mode that does not utilize Minecraft's native spectator gamemode, instead handling events for players still in survival mode. This includes packet handling so the eliminated players cannot interact with players who are still in the game, including any packets that create sound in the game world
  - Persistent player data - player statistics like win or kill count are stored in a PostgreSQL database or JSON (if database is unavailable)
  - Custom commands - Many custom commands are implemented to configure the plugin or influence the game lifecycle such as skipping to the next event on the game timer with `/game skip`
  - Event-driven player handling - a vast range of event listeners are used to view or influence player activity using the Bukkit API

## Development Notes

  The project was meant as a learning experience and a personal project, which lead to some implementation deferrals. I will talk about them and reflect on what could be done better here:

  - The GameManager class splits games into functions of the GameManager class and the GameControllerTask (a BukkitTask child class). I would like to refactor this code to have a single unified `Game` class that creates and handles all BukkitTask logic within its own instance.
  - For simplicity's sake, I used world generation per game rather than creating an isolated server instance for each game. I recognize that this is a limitation as it causes a massive lag spike on the server that hosts all games as a shared resource, and also increases the creation time for each game.
  - The codebase does not consistently implement programming good practice.

  I've written these here as reflection on the project and what I'd do differently, not as a warning for production use.

## Setup

  # Requirements
  - a server running Spigot 1.21.8-R0.1-SNAPSHOT
  - JDK: jdk-21.0.10+7
  - optionally a PostgreSQL database

  For setup, you must download the latest version of Spigot for 1.21.8 from their webpage and set up a server to run it using the listed JDK version. Dropping the plugin .jar file into the /plugins folder will be enough to get it working theoretically, however as it stands the plugin is bespoke and needs further action on the server to get fully running. The plugin was not made for distribution and no system was put in place to support it.

## License
MIT
