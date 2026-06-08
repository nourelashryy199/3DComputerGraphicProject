import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;


class Env {
    public static final float SIZE = 37f;  
}

public class Buzz_GLEventListener implements GLEventListener {

  private Camera camera; 

 private Light[] allLights; //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private Light spotlightLight; //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private Light generalLight;//new (Nour Elashry, nomelashry1@sheffield.ac.uk)

  private TextureLibrary textures; 

  private Model cubeFace; //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private Statue[] statues; //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private Flower[] flowers;//new (Nour Elashry, nomelashry1@sheffield.ac.uk)

  private Flower flowerA;//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private Flower flowerB;//new (Nour Elashry, nomelashry1@sheffield.ac.uk)

  private double beeStartTime;//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private double spotlightStartTime; //new (Nour Elashry, nomelashry1@sheffield.ac.uk)


  private Statue statueWood, statueStone, statueMetal;//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private Bee bee;//new (Nour Elashry, nomelashry1@sheffield.ac.uk)

  private SGNameNode worldRoot;
  private Spotlight spotlight; 
  private GeneralIllumination generalIllumination; //new (Nour Elashry, nomelashry1@sheffield.ac.uk)

  private boolean beeAnimation = false; //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private boolean spotlightAnimation = false; //new (Nour Elashry, nomelashry1@sheffield.ac.uk)

  private double startTime; //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private double spotlightStart;//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private float overlayAlpha = 0.0f; //new (Nour Elashry, nomelashry1@sheffield.ac.uk)

 

  private float spotlightBrightness = 1.0f;   //new (Nour Elashry, nomelashry1@sheffield.ac.uk)

  private static final float STATUE_FRONT = 4.0f;  //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private static final float BEE_GAP = 2.0f;      //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private static final float REACT_EPS = 0.25f;   //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private static final float X_THRESH = 1.2f;     //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  private static final float MIN_MOON_BRIGHTNESS = 0.25f;  //new (Nour Elashry, nomelashry1@sheffield.ac.uk)

  private float[] segmentSpeeds = { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
        0.20f, 
        0.20f, 
        0.20f, 
        0.27f, 
        0.27f, 
        0.27f 
    };

  public Buzz_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(0f, 20f, 45f));
    this.camera.setTarget(new Vec3(0f, 4f, 0f));
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3(); 

    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());

    gl.glClearColor(0f, 0f, 0f, 1f); 
    gl.glClearDepth(1f); 
    gl.glEnable(GL.GL_DEPTH_TEST); 
    gl.glDepthFunc(GL.GL_LESS); 
    gl.glFrontFace(GL.GL_CCW); 
    gl.glEnable(GL.GL_CULL_FACE); 
    gl.glCullFace(GL.GL_BACK);

    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    initialise(gl);

    startTime = getSeconds();
    spotlightStart = getSeconds();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);

    float aspect = (float) width / (float) height;
    Mat4 p = Mat4Transform.perspective(45, aspect);
    camera.setPerspectiveMatrix(p);
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {}

  public void initialise(GL3 gl) {
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    Shader sunShader = new Shader(gl, 
        "assets/shaders/vs_sun.txt",
        "assets/shaders/fs_sun.txt"
    );

    textures = new TextureLibrary();
    textures.add(gl, "backgroundfloor", "assets/textures/skybox2/FLOOR.jpg");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "backgroundwall",  "assets/textures/skybox2/BACK.jpg");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "backgroundleft", "assets/textures/skybox2/RIGHT.jpg");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "backgroundright", "assets/textures/skybox2/LEFT.jpg");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "backgroundup", "assets/textures/skybox2/CEILING.jpg");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)


    textures.add(gl, "white1x1", "assets/textures/white1x1.jpg");
    textures.add(gl, "black1x1", "assets/textures/black1x1.jpg");

    textures.add(gl, "stone", "assets/textures/statuestuff/stonestatic.png");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "wood",  "assets/textures/statuestuff/woodstatic.png");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "metal", "assets/textures/statuestuff/metalstatic.png");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)

    textures.add(gl, "stoneReaction", "assets/textures/statuestuff/stonereaction.png");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "woodReaction",  "assets/textures/statuestuff/woodreaction.png");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "metalReaction", "assets/textures/statuestuff/metalreaction.png");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "FloorNightSky", "assets/textures/skybox2/FloorNightSky.jpg");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "NightSky2", "assets/textures/skybox2/myCustomNightSky2.jpg");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "NightSky3", "assets/textures/skybox2/myCustomNightSky3.jpg");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "NightSky4", "assets/textures/skybox2/myCustomNightSky4.jpg");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    textures.add(gl, "CeilingNightSky", "assets/textures/skybox2/CeilingNightSky.jpg");
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  



//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    spotlightLight = new Light(gl, camera);
    Material spotMat = new Material();
    spotMat.setAmbient(0.05f, 0.05f, 0.05f);
    spotMat.setDiffuse(0.7f, 0.7f, 0.7f);
    spotMat.setSpecular(0.8f, 0.8f, 0.8f);
    spotMat.setShininess(64f);
    spotlightLight.setMaterial(spotMat);
    spotlightLight.setCutOff((float)Math.cos(Math.toRadians(20.0f)));
    spotlightLight.setOuterCutOff((float)Math.cos(Math.toRadians(40.0f)));
    spotlightLight.setSpotlight(true);
    spotlightLight.setPosition(-10f, 4f, 0f);
    spotlightLight.setDirection(new Vec3(0f, -1f, 0f));  
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    generalLight = new Light(gl, camera);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    Material sunMat = new Material();
    sunMat.setAmbient(0.15f, 0.15f, 0.15f);   
    sunMat.setDiffuse(1.0f, 1.0f, 1.0f);     
    sunMat.setSpecular(0.2f, 0.2f, 0.2f);
    sunMat.setShininess(32f);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    generalLight.setMaterial(sunMat);
    generalLight.setSpotlight(false);             
    generalLight.setDirection(new Vec3(0f, -1f, 0.3f)); 

//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    generalIllumination = new GeneralIllumination(
        gl, camera, generalLight, sunShader
    );


//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    allLights = new Light[]{ generalLight, spotlightLight };
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    spotlight = new Spotlight(
    gl,
    camera,
    generalLight,
    spotlightLight,
    textures.get("metal"),
    textures.get("white1x1"),
    -10.0f, 4.0f, 0.0f   
  );
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    cubeFace = makeCubeFace(gl, allLights, camera);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    statueWood  = new Statue(gl, camera, allLights,
                    textures.get("wood"),
                    ExpressionType.ANGRY);
    statueStone = new Statue(gl, camera, allLights,
                    textures.get("stone"), 
                    ExpressionType.HAPPY);
    statueMetal = new Statue(gl, camera, allLights,
                    textures.get("metal"), 
                    ExpressionType.NEUTRAL);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    bee = new Bee(gl, camera, allLights, 
                  textures.get("white1x1"), textures.get("black1x1"));


//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    statueWood.setPosition(new Vec3(-15f, 1f, -6f));
    statueStone.setPosition(new Vec3(  0f, 1f, -10f));
    statueMetal.setPosition(new Vec3( 15f, 1f, -6f));



    statues = new Statue[]{ statueMetal, statueStone, statueWood }; 
    
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    flowerA = new Flower(gl, camera, allLights);
    flowerB = new Flower(gl, camera, allLights);

    flowers = new Flower[]{flowerA, flowerB};
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
   SGTransformNode flowerATrans = new SGTransformNode(
    "flowerA_trans",
        Mat4.multiply(
            Mat4Transform.translate(-8f, 2.5f, 8f),
            Mat4Transform.scale(3f, 3f, 3f)   
        )
    );

    SGTransformNode flowerBTrans = new SGTransformNode(
        "flowerB_trans",
        Mat4.multiply(
            Mat4Transform.translate(8f, 2.5f, 8f),
            Mat4Transform.scale(3f, 3f, 3f)
        )
    );

    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    flowerA.setPetalColor(1f, 0.2f, 0.2f);   
    flowerB.setPetalColor(1f, 0.6f, 0.8f); 

    flowerATrans.addChild(flowerA.getRoot());
    flowerBTrans.addChild(flowerB.getRoot());
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    Vec3 s1 = statues[0].getWorldPosition();
    Vec3 start = new Vec3(
        s1.x + 5f,
        bee.getBaseHeight(),
        s1.z + STATUE_FRONT + BEE_GAP
    );
    bee.forcePosition(start.x, start.y, start.z);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    worldRoot = new SGNameNode("world-root");
    worldRoot.addChild(statueWood.getRoot());
    worldRoot.addChild(statueStone.getRoot());
    worldRoot.addChild(statueMetal.getRoot());
    worldRoot.addChild(bee.getRoot());
    worldRoot.addChild(spotlight.getRoot());
    worldRoot.addChild(flowerATrans);
    worldRoot.addChild(flowerBTrans);
    worldRoot.addChild(generalIllumination.getRoot());
    worldRoot.update();
  }

  private Model makeCubeFace(GL3 gl, Light[] lights, Camera camera) { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());

    Shader shader = new Shader(gl,
        "assets/shaders/vs_standard.txt",
        "assets/shaders/fs_standard_d.txt"
    );
    shader.use(gl);
    shader.setInt(gl, "diffuse_texture", 0);
    shader.setInt(gl, "overlay_texture", 1);

    shader.setInt(gl, "isWall", 0);
    shader.setFloat(gl, "overlay_alpha", 0f);
    shader.setInt(gl, "useTexture", 1);

    Material material = new Material();
    material.setAmbient(1f,1f,1f);
    material.setDiffuse(1f,1f,1f);
    material.setSpecular(0f,0f,0f);
    material.setShininess(1f);

    return new Model("cube-face", mesh, new Mat4(1), shader, material,
                     new Renderer(), lights, camera);
}


    private Mat4 M1_floor() { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
        float S = Env.SIZE * 2f;
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(Mat4Transform.scale(S, 1, S), m);
    

        return m;
    }


    private Mat4 M2_back() { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
        float S = Env.SIZE * 2f;
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(Mat4Transform.scale(S,1,S), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundX(-90), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundZ(180), m);
        m = Mat4.multiply(Mat4Transform.translate(0, Env.SIZE, -Env.SIZE), m);
        return m;
    }


    private Mat4 M3_right() { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
        float S = Env.SIZE * 2f;
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(Mat4Transform.scale(S,1,S), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundX(-90), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundY(90), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundZ(180), m);
        m = Mat4.multiply(Mat4Transform.translate(Env.SIZE, Env.SIZE, 0), m);
        return m;
    }

    private Mat4 M4_left() { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
        float S = Env.SIZE * 2f;
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(Mat4Transform.scale(S,1,S), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundX(-90), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundY(-90), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundZ(180), m);

        m = Mat4.multiply(Mat4Transform.translate(-Env.SIZE, Env.SIZE, 0), m);

        return m;
    }

    private Mat4 M6_ceiling() { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
        float S = Env.SIZE * 2f;
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(Mat4Transform.scale(S,1,S), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundX(180), m);
        m = Mat4.multiply(Mat4Transform.translate(0, 2*Env.SIZE, 0), m);
        

        return m;
    }



  public void render(GL3 gl) {
    gl.glEnable(GL.GL_BLEND);

    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    generalIllumination.update(gl);

//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    if (beeAnimation) {

        double elapsed = getSeconds() - startTime; //get how long animation has been running
        float time = (float) elapsed; //convert to float

        float h = bee.getBaseHeight(); //get height of bee

        Vec3 s1 = statues[0].getWorldPosition(); //world coordinates of statues
        Vec3 s2 = statues[1].getWorldPosition(); 
        Vec3 s3 = statues[2].getWorldPosition(); 

        Vec3 f1 = flowers[0].getWorldPosition(); //world coordinates of flowers
        Vec3 f2 = flowers[1].getWorldPosition();
        Vec3 start = new Vec3( //world coordinates of start position of movement of bee
        s1.x + 5f,
        h,
        s1.z + STATUE_FRONT + BEE_GAP   
    );

       //array comprised of five (main) points in the world space. 
       //this array is the path of the bee. 
        Vec3[] path = new Vec3[]{ 
            start,
            new Vec3(s1.x, h, s1.z + STATUE_FRONT + BEE_GAP),
            new Vec3(s2.x, h, s2.z + STATUE_FRONT + BEE_GAP),
            new Vec3(s3.x, h, s3.z + STATUE_FRONT + BEE_GAP),
            new Vec3(f1.x, h, f1.z + BEE_GAP),
            new Vec3(f2.x, h, f2.z + BEE_GAP),
            start
        };

        int sectionCount = path.length - 1; //getting the segments in between the five main points

        //getting full cycle time
        float totalDuration = 0f; 
        for (int s = 0; s < sectionCount; s++) { 
            totalDuration += 1f / segmentSpeeds[s]; 
        }
        //current seconds count into the current loop
        float cycleTime = time % totalDuration; 

//find which segment the bee is currently in
//i.e. given current time, which flight segment does the bee belong to
        float accumulated = 0f;
        int seg = 0;
        for (int s = 0; s < sectionCount; s++) {
            float duration = 1f / segmentSpeeds[s];  
            if (cycleTime < accumulated + duration) {
                seg = s;
                break;
            }
            accumulated += duration;
        }
//computes how far between point A and point B the bee should be right now
        float duration = 1f / segmentSpeeds[seg];
        float localT = (cycleTime - accumulated) / duration;
        if (localT < 0f) localT = 0f;
        if (localT > 1f) localT = 1f;

        if (seg <= 2) {
            bee.setFacingStatues();
        } else {
            bee.setFacingFlowers();
        }

        Vec3 p1 = path[seg];
        Vec3 p2 = path[seg + 1];

        float x = p1.x * (1 - localT) + p2.x * localT;
        float y = p1.y * (1 - localT) + p2.y * localT;
        float z = p1.z * (1 - localT) + p2.z * localT;

        bee.forcePosition(x, y, z); //updating bee position
        bee.update(elapsed);
        worldRoot.update();
        updateBeeInteractions();
    }

    
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    if (spotlightAnimation) {
        double t = getSeconds() - spotlightStart;
        spotlight.update((float)t);
    }


    gl.glDisable(GL.GL_BLEND); 
    gl.glDisable(GL.GL_CULL_FACE);   
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    Shader skyShader = cubeFace.getShader();
    cubeFace.getMaterial().setDiffuseMap(textures.get("backgroundfloor"));
    cubeFace.setModelMatrix(M1_floor());
    skyShader.use(gl);
    sendLightsToShader(gl, skyShader, allLights);
    skyShader.setInt(gl, "numLights", allLights.length);
    gl.glActiveTexture(GL.GL_TEXTURE0);
    textures.get("backgroundfloor").bind(gl);
    gl.glActiveTexture(GL.GL_TEXTURE1);
    textures.get("FloorNightSky").bind(gl);
    skyShader.setInt(gl, "overlay_texture", 1);
    skyShader.setInt(gl, "isWall", 1);
    skyShader.setFloat(gl, "overlay_alpha", overlayAlpha);
    cubeFace.render(gl);

    cubeFace.getMaterial().setDiffuseMap(textures.get("backgroundwall"));
    cubeFace.setModelMatrix(M2_back());
    skyShader.use(gl);
    sendLightsToShader(gl, skyShader, allLights);
    gl.glActiveTexture(GL.GL_TEXTURE0);
    textures.get("backgroundwall").bind(gl);
    gl.glActiveTexture(GL.GL_TEXTURE1);
    textures.get("NightSky2").bind(gl);
    skyShader.setInt(gl, "overlay_texture", 1);
    skyShader.setInt(gl, "isWall", 1);
    cubeFace.render(gl);

    cubeFace.getMaterial().setDiffuseMap(textures.get("backgroundright"));
    cubeFace.setModelMatrix(M3_right());
    skyShader.use(gl);
    sendLightsToShader(gl, skyShader, allLights);
    gl.glActiveTexture(GL.GL_TEXTURE0);
    textures.get("backgroundright").bind(gl);
    gl.glActiveTexture(GL.GL_TEXTURE1);
    textures.get("NightSky3").bind(gl);
    skyShader.setInt(gl, "isWall", 1);
    cubeFace.render(gl);

    cubeFace.getMaterial().setDiffuseMap(textures.get("backgroundleft"));
    cubeFace.setModelMatrix(M4_left());
    skyShader.use(gl);
    sendLightsToShader(gl, skyShader, allLights);
    gl.glActiveTexture(GL.GL_TEXTURE0);
    textures.get("backgroundleft").bind(gl);
    gl.glActiveTexture(GL.GL_TEXTURE1);
    textures.get("NightSky4").bind(gl);
    skyShader.setInt(gl, "isWall", 1);
    cubeFace.render(gl);

    cubeFace.getMaterial().setDiffuseMap(textures.get("backgroundup"));
    cubeFace.setModelMatrix(M6_ceiling());
    skyShader.use(gl);
    sendLightsToShader(gl, skyShader, allLights);
    gl.glActiveTexture(GL.GL_TEXTURE0);
    textures.get("backgroundup").bind(gl);
    gl.glActiveTexture(GL.GL_TEXTURE1);
    textures.get("CeilingNightSky").bind(gl);
    skyShader.setInt(gl, "isWall", 1);
    cubeFace.render(gl);

    gl.glEnable(GL.GL_CULL_FACE);   
    //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    skyShader.setInt(gl, "isWall", 0);

    worldRoot.update();

    bee.render(gl);
    worldRoot.draw(gl);

    }




private double getSeconds() {
    return System.currentTimeMillis() / 1000.0;
  }

private int currentStatue = -1;

//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
//purpose: decide which statue the bee is currently in front of and change 
// statue reaction/texture and bee reaction accordingly
private void updateBeeInteractions() {

    //getting bee x and z positions
    Vec3 bp = bee.getPosition();
    float bx = bp.x;
    float bz = bp.z;

    int nearest = -1; //default nearest statue set to none
    //because indexing is 0,1,2 for statues. 

    //get statue world position and set the z (depth) such that the bee is considered to bee 
    //"close" to the statue (this is because the bee flies in front of the statues again in the
    //scene but a bit further away z-wise)
    for (int i = 0; i < statues.length; i++) {
        Vec3 sp = statues[i].getWorldPosition();
        float targetZ = sp.z + STATUE_FRONT + BEE_GAP;


//setting x and z coordinates of bee position such that a reaction is 
//elicited from both bee and statue
        boolean correctZ = Math.abs(bz - targetZ) <= REACT_EPS; 
        boolean correctX = Math.abs(bx - sp.x) <= X_THRESH;
//if both are fulfilled, we know that the bee is now visiting statue i
        if (correctZ && correctX) {
            nearest = i;
            break;
        }
    }

//to exit interaction from statue.
    if (currentStatue != -1 && nearest != currentStatue) {
        applyNormalTexture(currentStatue);
        bee.setAlertCount(0);
        currentStatue = -1;
    }
//to enter interaction with statue
    if (nearest != -1 && nearest != currentStatue) {

        for (int j = 0; j < statues.length; j++) {
            if (j != nearest) applyNormalTexture(j);
        }

        applyReactionTexture(nearest);
        bee.setAlertCount(nearest + 1);

        currentStatue = nearest;
    }
}


public void startBee() { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    beeAnimation = true;
    beeStartTime = getSeconds();
}

public void stopBee() { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    beeAnimation = false;
}

public void startSpotlight() { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    spotlightAnimation = true;
    spotlightStartTime = getSeconds();
    spotlightLight.setSpotlight(true);
    setSpotlightBrightness(spotlightBrightness);
}


public void stopSpotlight() { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    spotlightAnimation = false;
    spotlightLight.setSpotlight(false);

    Material m = spotlightLight.getMaterial();
    m.setAmbient(0, 0, 0);
    m.setDiffuse(0, 0, 0);
    m.setSpecular(0, 0, 0);
}

//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
public void teleportBeeToStatue(int index) {
    beeAnimation = false; //stop animation of bee
// a workaround because i switched up the order of the statues 
// somewhere and its affecting the reactions
    int sIndex = index; 
    if (index == 0)      sIndex = 2;  
    else if (index == 2) sIndex = 0;  

    //get statue position
    Vec3 pos = statues[sIndex].getWorldPosition();

    //setting bee position
    float worldX = pos.x;
    float worldY = bee.getBaseHeight();
    float worldZ = pos.z + STATUE_FRONT + BEE_GAP;

    //setting bee position
    bee.forcePosition(worldX, worldY, worldZ);

    worldRoot.update();
    updateBeeInteractions(); //calling reaction method
}

private void forceInteractionUpdate() { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    updateBeeInteractions();
}

//apply appropriate reaction face texture (when bee is in front of the statue)
private void applyReactionTexture(int index) { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    if (index == 0) {
     
        statues[0].setStatueTexture(textures.get("metalReaction"));
    } else if (index == 1) {

        statues[1].setStatueTexture(textures.get("stoneReaction"));
    } else if (index == 2) {
    
        statues[2].setStatueTexture(textures.get("woodReaction"));
    }
}
//apply normal reaction texture (when bee is away)
private void applyNormalTexture(int index) { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    if (index == 0) {
        statues[0].setStatueTexture(textures.get("metal"));
    } else if (index == 1) {
        statues[1].setStatueTexture(textures.get("stone"));
    } else if (index == 2) {
        statues[2].setStatueTexture(textures.get("wood"));
    }
}

//method which serves a combined function of 
//changing opacity of overlay texture of cubes
//changing brightness of light in the sun/moon direction
public void setOverlayAlphaAndSunBrightness(float value) { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    overlayAlpha = value;
    if (overlayAlpha < 0f) overlayAlpha = 0f;
    if (overlayAlpha > 0.5f) overlayAlpha = 0.5f;
    float t = overlayAlpha / 0.5f;    
  
    float brightness = 1.0f * (1.0f - t) + MIN_MOON_BRIGHTNESS * t;

    Material m = generalLight.getMaterial();
    m.setAmbient(0.15f * brightness, 0.15f * brightness, 0.15f * brightness);
    m.setDiffuse(1.0f * brightness, 1.0f * brightness, 1.0f * brightness);
    m.setSpecular(0.2f * brightness, 0.2f * brightness, 0.2f * brightness);
}

private void sendLightsToShader(GL3 gl, Shader shader, Light[] lights) { //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    shader.use(gl);
    shader.setInt(gl, "numLights", lights.length);

    for (int i = 0; i < lights.length; i++) {
        Light L = lights[i];
        String base = "lights[" + i + "]";

        shader.setVec3(gl, base + ".position", L.getPosition());
        shader.setVec3(gl, base + ".ambient", L.getMaterial().getAmbient());
        shader.setVec3(gl, base + ".diffuse", L.getMaterial().getDiffuse());
        shader.setVec3(gl, base + ".specular", L.getMaterial().getSpecular());

        shader.setVec3(gl, base + ".direction", L.getDirection());
        shader.setFloat(gl, base + ".cutOff", L.getCutOff());
        shader.setFloat(gl, base + ".outerCutOff", L.getOuterCutOff());

        shader.setInt(gl, base + ".isSpotlight", L.isSpotlight() ? 1 : 0);
    }
}

public void setSpotlightBrightness(float brightness){ //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
    if(!spotlightAnimation){
        return;
    }
    spotlightBrightness = brightness;
    Material m = spotlightLight.getMaterial();
    m.setAmbient(0.1f * brightness, 0.1f * brightness, 0.1f * brightness);
    m.setDiffuse(1.0f * brightness, 1.0f * brightness, 1.0f * brightness);
    m.setSpecular(1.0f * brightness, 1.0f * brightness, 1.0f * brightness);
}


}


