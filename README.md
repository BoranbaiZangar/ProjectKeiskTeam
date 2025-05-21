🌌 Space Escape
Space Escape is a dynamic 2D platformer built with Java and JavaFX. Embark on an epic space adventure where you battle enemies, collect ammo, open doors, and navigate through portals to complete levels. Control your hero, wield various weapons (bullets, rockets, lasers), and avoid traps to reach your goal!

📖 Overview
In Space Escape, you control a character navigating through levels filled with platforms, spikes, disappearing blocks, and enemies. Your mission is to collect keys, activate buttons to open doors, and reach portals to progress to the next level. The game features an inventory system, multiple weapon types, health and score tracking, and progress saving.
Key Features:

🎮 Platformer Gameplay: Jump across platforms, dodge spikes, and fight enemies.
🔫 Variety of Weapons: Use bullets, rockets, and lasers with different damage values.
🚪 Interactive Elements: Activate buttons to open doors and collect keys for points.
🌀 Portals: Travel to the next level via portals.
💾 Progress Saving: Your progress is automatically saved.
🎵 Soundtrack: Immersive music and sound effects (with mute options).

🛠 Requirements
To run the game, you need:

Java 8 or higher (Java 11+ recommended).
JavaFX SDK (included in some Java distributions or installed separately).
Development Environment (e.g., IntelliJ IDEA, Eclipse, or NetBeans) or command-line compilation.
Operating System: Windows, macOS, or Linux.

📦 Installation

Clone the Repository:
git clone https://github.com/yourusername/space-escape.git
cd space-escape


Set Up JavaFX:

Ensure JavaFX SDK is installed. Download from the official site.
Add JavaFX to your project:
For IntelliJ IDEA: File -> Project Structure -> Libraries -> Add -> JavaFX SDK.
For command-line, use --module-path and --add-modules flags (see below).




Compile and Run:
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml main/Main.java
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml main.Main

Replace /path/to/javafx-sdk/lib with the path to your JavaFX SDK libraries.

Verify Resources:

Ensure the resources folder contains level files (levels/level1.txt, level2.txt, etc.), images (images/), and sounds (sounds/).



🎮 How to Play

Controls:

W, ↑, or Space: Jump.
A or ←: Move left.
D or →: Move right.
F: Shoot (if a weapon is selected).
1, 2, 3: Select weapon (bullets, rockets, lasers).
E: Open inventory.
H: Reveal hidden platforms.
Pause: Pause button in the top-right corner.


Objective:

Collect keys and ammo.
Activate buttons to open doors.
Reach the portal to advance to the next level.
Avoid spikes and enemy attacks to preserve health.


Inventory:

Open the inventory (E) to use items (e.g., health packs or ammo).
Weapons consume ammo; collect more ammo on levels.



🗺 Level Structure
Levels are stored in text files (levels/level1.txt, level2.txt, etc.) using the following symbols:

#: Platform.
^: Spikes.
~: Ice.
=: Disappearing platform.
D: Door.
B: Button.
S: Player start position.
P: Portal.
X: Enemy.
K, A, R, L: Key, bullet ammo, rocket ammo, laser ammo.

Example level file:
##########
#S.......#
#...D....#
#...B....#
#P.......#
##########

🛠 Project Structure
space-escape/
├── src/
│   └── main/
│       ├── Main.java
│       ├── Player.java
│       ├── Level.java
│       ├── Door.java
│       ├── Button.java
│       └── ... (other classes)
├── resources/
│   ├── images/
│   │   ├── player.png
│   │   ├── door.png
│   │   └── ... (other images)
│   ├── sounds/
│   │   ├── jump.wav
│   │   ├── background-music.mp3
│   ├── levels/
│   │   ├── level1.txt
│   │   ├── level2.txt

🔧 Development and Customization

Adding New Levels:

Create a new .txt file in the resources/levels/ folder and add it to the levelNames list in Main.java.
Use the symbols from the "Level Structure" section to design the map.


Adding New Weapons:

Create a new class extending Projectile (e.g., NewWeapon.java).
Add a new ammo type extending PickupItem.
Update Player.java and Main.java to support the new weapon.



🐛 Known Issues

Multiple buttons and doors are correctly linked by index in the current version of Level.java, ensuring each button opens its corresponding door.

🤝 Contributing
Want to contribute? Follow these steps:

Fork the repository.
Create a feature branch (git checkout -b feature/new-feature).
Commit your changes (git commit -m "Add new feature").
Push to your fork (git push origin feature/new-feature).
Create a Pull Request.

📜 License
This project is licensed under the MIT License. See the LICENSE file for details.
🙌 Acknowledgments

Thanks to the JavaFX team for an excellent framework for game development!
Icons and emojis: Twemoji.


🚀 Launch Space Escape and embark on a cosmic adventure!
