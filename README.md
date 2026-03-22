# Platformer
 
A fully playable 2D platformer game built in Java, featuring a custom-built level editor. 2,500+ lines of code.
 
## Features
 
- **Game Loop** — Smooth, frame-based game loop with consistent update/render cycle
- **Collision Detection** — Precise tile-based collision for players, enemies, and objects
- **Sprite Rendering** — Custom sprite rendering system for all game entities
- **Level Editor** — Built-in level editor to design and save custom levels
- **Save/Load System** — User-created levels can be saved and reloaded between sessions
- **Enemies** — Multiple enemy types (`Enemy.java`, `SmallEnemy.java`) with basic AI
- **Collectibles & Hazards** — Coins, spikes, and checkpoints
- **Music Player** — Background music support via `MusicPlayer.java`
- **Menus** — Main menu and UI via the `menuclasses` package
 
## Tech Stack
 
- **Language:** Java
- **UI/Rendering:** Java Swing (`GameFrame`, `GamePanel`)
- **Architecture:** Object-Oriented Programming
 
## Project Structure
 
| File | Description |
|---|---|
| `PlatformerGame.java` | Entry point |
| `GamePanel.java` | Main game loop and rendering |
| `Player.java` | Player logic and controls |
| `Level.java` / `LevelGenerator.java` | Level loading and generation |
| `Enemy.java` / `SmallEnemy.java` | Enemy behavior |
| `Tile.java` / `Thing.java` | Base classes for tiles and objects |
| `levels/` | Saved level files |
| `music/` | Background music assets |
 
## How to Run
 
1. Clone the repository
2. Compile all `.java` files
3. Run `PlatformerGame.java`
 
```bash
javac *.java
java PlatformerGame
```
 
