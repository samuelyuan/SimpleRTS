SimpleRTS
======

SimpleRTS a real time strategy game in Java.

<div style="display:inline-block;">
<img src="https://github.com/samuelyuan/SimpleRTS/raw/master/screenshots/game1.png" alt="Game1" width="400" height="300" />
<img src="https://github.com/samuelyuan/SimpleRTS/raw/master/screenshots/game2.png" alt="Game2" width="400" height="300" />
</div>

The game contains a short single player campaign with multiple different maps. In each map, the objective is to capture the enemy base by defeating the opposing army and taking control of the all the control points. These control points are represented with a flag and a color, which can change depending on which side is in control. The player starts off with a small number of units, but reinforcements will spawn daily near all player control points. 

Getting Started
---
1. Clone the project
2. Compile project
   ```
   gradle build
   ```
3. Run project
   ```
   gradle run
   ```
4. Run unit tests and generate code coverage
   ```
   gradle test jacocoTestReport
   ```
This will run the tests and generate a code coverage report, which can be found in `build/reports/jacoco/test/html/index.html`.
