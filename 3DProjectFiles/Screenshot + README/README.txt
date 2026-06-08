This assignment is an interactive 3D scene implemented in Java using JOGL.
The project demonstrates the use of scene graphs, hierarchical modeling, custom meshes, lighting, animation, and shader programming.
------------------------------------------------------------------------------------------------

*BEFORE READING THE PROVIDED SUMMARY OF THE SCENE, PLEASE READ THE FOLLOWING INSTRUCTIONS ON HOW TO RUN/INTERACT WITH THE SCENE. ALSO IN THIS OUTLINED SECTION YOU WILL FIND LIMITATIONS/DIFFICULTIES ENCOUNTERED IN THE PROJECT WHICH WERE CHALLENGING TO RESOLVE AS WELL AS A LIST OF ALL THE JAVA FILES AUTHORED BY ME/ARE NEW:

1. Ensure Java 17+ and JOGL are installed
2. Navigate to terminal, and compile and run Buzz.java using the following commands
(find . -name "*.class" -delete) use if necessary
javac -cp ".:jogamp-fat.jar" Buzz.java
java -cp ".:jogamp-fat.jar" Buzz  

3. A window will pop up that contains the scene and on its edges you will find several control panels and buttons

- "Continuous Mode" button starts the bee animation (please see the whole thing until the bee goes back to its starting point)
- "Pose Statue 1" button takes the bee out of "Continuous Mode"/taking bee from still position on screen to first statue on the right, where both the bee and the statue pose for each other
- "Pose Statue 2" and "Pose Statue 3" follow the same logic as "Pose Statue 1". 
- "Spotlight On" turns lamp-post on
- "Spotlight Off" turns lamp-post off
- Slider between "Spotlight On" and "Spotlight Off" buttons is used to control the intensity of the light emitted from lamp-post. Slider does not work when lamp-post is not on. 
- "Day --> Night" controls brightness coming out from the sun + opacity alpha value of overlay texture that we used to simulate night sky. 
- Other controls include: 
 - Dragging mouth on screen to control camera angle
 - Q to move up 
 - A to move left 
 - S to zoom out
 - W to zoom in 
 - E to move down
 - D to move right

ISSUES AND LIMITATIONS:
-----------------------

1. Backdrop Lighting Bypass:
----------------------------
The CubeFace backdrop uses the isWall shader path, which performs texture + overlay blending and returns early. Because this bypasses the Phong lighting loop, the spotlight and world lights do not illuminate the backdrop. This was necessary to prevent unstable brightness due to large planar normals.

2. Backdrop Distortion at Extreme Camera Angles:
-----------------------------------------------
CubeFaces are rendered as independent Two-Triangle quads. Under oblique viewing angles, the combination of thin geometry and fixed UVs can produce stretching or collapsing artifacts at the edges.

3. Spotlight Limited to Scene Objects:
-------------------------------------
Since the backdrop does not evaluate diffuse/specular terms, the spotlight only affects objects using the standard lighting path (statues, bee, flowers). This is an intentional constraint of the shader design.

4. Camera Near-Plane Clipping:
------------------------------
The camera uses a fixed near-plane distance from the exercise sheets. In a confined environment, this can lead to occasional clipping of the CubeFaces when the camera rotates very close to a wall.

FILES AUTHORED BY ME: 
---------------------
The assignment builds upon the folder pertaining to "Chapter 8: Many Robots" JOGL tutorial. However, many things were added/adjusted to fit the requirements of the assignment. 
Files authored by me from scratch are: 
1. Flower.java
2. PetalMesh.java
3. Circle.java
4. Bee.java
5. Statue.java
6. Spotlight.java
7. GeneralIllumination.java


------------------------------------------------------------------------------------------------

The final scene includes the following core components:

1. Scene Container

The environment is formed by five custom “cubeFace” models, each built using a Two-Triangle mesh. These faces construct a cube with no front face, creating an open container for the scene. The cube faces are placed in the world directly (not through scene-graph nodes) and textured via the fragment shader, supporting overlay blending.

2. Global Scene Graph

The global scene graph contains all major scene objects:
- Three statues (each with configurable emotional expressions).
- A lamp-post with an animated spotlight.
- A bee with hierarchical local animation.
- Two flowers created using a custom petal mesh.
- General illumination sphere representing the “sun” (could also be considered as the "moon" when user dims out the scene using the provided controls).  
- Each object is implemented as a self-contained scene-graph structure with its own root transform, scaling, and local modeling hierarchy.

3. Bee (Local and Global Scene Graph)

The bee is modeled using a detailed hierarchical local scene graph that includes:
- Body
- Eyes
- Antennae
- Left and right wings (each with its own pivot and flap animation + created using a custom Circle mesh) 
- Tail
- Alert Indicator (reaction to flying past the statues)

The bee’s local hierarchy is attached to a world transform node, making it part of the global scene graph.
The bee supports procedural animation, sinusoidal wing flapping, and world-space movement.

4. Statues (Local and Global Scene Graph)

Each statue is built from:
- A sphere mesh
- A scaling transform
- A translation and rotation hierarchy
- A material that can change at runtime based on a chosen facial expression (neutral, happy, angry)
Every statue has:
- Its own local scene graph defining shape and orientation
- A world transform node that positions it within the global scene

5. Additional Components

- General Illumination Node
- A spherical “sun” object moves in the world and updates the position of the directional world light. This uses a separate shader and does not receive lighting.
- Spotlight Lamp-Post: A hierarchical lamp-post composed of cubes and spheres.The bulb rotates via a world-time animation, updating:
  - The spotlight position
  - The spotlight direction
  - The pivot transforms
- Flowers. Each flower includes:
  - A custom PetalMesh (procedurally generated)
  - A spherical flower center
  - A cube-based stem
  - Its own world transform for positioning

6. Rendering and Shaders:

The scene uses a standard JOGL rendering pipeline with custom GLSL shaders. All objects except the sun use the same vertex/fragment shader pair, which supports:

- Per-fragment Phong lighting with multiple light sources
- Spotlight cone calculations (for lamp-post)
- Texture sampling and overlay blending (for cube faces)
- A procedural stripe effect for the bee body using model-space coordinates

The sun uses a separate shader that renders emissive color and radial glow, without receiving lighting from the scene.

7. Assets
All meshes and textures are included inside the assets/ folder:

- Meshes: Sphere.java, Cube.java, Circle.java, Petal.java
- Textures: Wall diffuse textures, overlay textures for night effect, statue textures, bee textures (black/white), flower colors, and "skybox-like" images. The bee’s body does not use a pre-made texture for its coloring. Instead, the yellow and black stripes are computed directly in the fragment shader by alternating colors based on vLocalPos.x, producing a procedural pattern.
- Shaders: vs_standard.txt, fs_standard_d.txt, vs_sun.txt, fs_sun.txt.






