import gmaths.*; 
import com.jogamp.opengl.*; 
import com.jogamp.opengl.util.texture.Texture; 
/* I declare that this code is my own work.
   Author: Nour Elashry (nomelashry1@sheffield.ac.uk)
*/


public class Bee {

    private Camera camera;
    private Light[] lights;
    
    private Model sphereBody, sphereBlack, sphereWhite; 
    
    private Model wingModel; 

    private SGNode beeRoot; 
    private SGTransformNode rootTranslate; 
    private SGTransformNode rootRotate; 
    private SGTransformNode leftWingRotate, rightWingRotate; 

    private float wingAngleDeg = 0f;
    private float flapSpeedDegPerSec = 720f;   
    private float flapAmplitude = 35f;         
    private double lastUpdateTime = 0;

    private Shader bodyShader; 
    private Shader partShader;




    private float baseHeight = 3.8f; 
    private float moveSpeed  = 1.2f; 

    private SGTransformNode[] exMarks = new SGTransformNode[3]; 


    public Bee(GL3 gl, Camera camera, Light[] lights, Texture whiteTexture, Texture blackTexture) { 

    this.camera = camera; 
    this.lights = lights;

    Material bodyMat = new Material(); 
    bodyMat.setAmbient(1,1,1);
    bodyMat.setDiffuse(1,1,1); 
    bodyMat.setSpecular(0.3f,0.3f,0.3f);
    bodyMat.setShininess(32);

    Material blackMat = new Material(); 
    blackMat.setAmbient(0.05f,0.05f,0.05f);
    blackMat.setDiffuse(0.05f,0.05f,0.05f);
    blackMat.setSpecular(0.1f,0.1f,0.1f);
    blackMat.setShininess(16);
    blackMat.setDiffuseMap(blackTexture);

    Material whiteMat = new Material(); 
    whiteMat.setAmbient(1,1,1);     
    whiteMat.setDiffuse(1,1,1);     
    whiteMat.setSpecular(0,0,0);    
    whiteMat.setShininess(1);       
    whiteMat.setDiffuseMap(whiteTexture);

    this.bodyShader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_d.txt"); 
    this.partShader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_d.txt");
   

    Renderer renderer = new Renderer(); 
    Mesh sphere = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone()); 
    Mesh circle = new Mesh(gl, Circle.vertices, Circle.indices); 

    wingModel = new Model("wing", circle, new Mat4(1),
                      partShader, whiteMat, renderer,
                      lights, camera);

    SGModelNode leftWingModelNode = new SGModelNode("left-wing-model-node", wingModel);
    SGModelNode rightWingModelNode = new SGModelNode("right-wing-model-node", wingModel);


    
    bodyShader.use(gl); 
    bodyShader.setInt(gl, "isBeeBody", 1);

    sphereBody  = new Model("body", sphere, new Mat4(1),
                             bodyShader, bodyMat, renderer, lights, camera);

    sphereBlack = new Model("blk",  sphere, new Mat4(1),
                             partShader, blackMat, renderer, lights, camera);

    sphereWhite = new Model("wht",  sphere, new Mat4(1),
                             partShader, whiteMat, renderer, lights, camera);

    beeRoot = new SGTransformNode("beeRoot", new Mat4(1));

    rootTranslate = new SGTransformNode("beeTranslate",
            Mat4Transform.translate(8f, baseHeight, 0f)); 

    rootRotate = new SGTransformNode("beeRotate", new Mat4(1));


        beeRoot.addChild(rootTranslate);
        rootTranslate.addChild(rootRotate);

        SGNameNode bodyRoot = new SGNameNode("bodyRoot");

        SGTransformNode bodyScale = new SGTransformNode("bodyScale",
                Mat4Transform.scale(3.0f, 2.0f, 2.0f)); 

        SGModelNode bodyModel = new SGModelNode("bodyModel", sphereBody);

        rootRotate.addChild(bodyRoot);
        bodyRoot.addChild(bodyScale);
        bodyScale.addChild(bodyModel);

        addWings(bodyRoot, leftWingModelNode, rightWingModelNode);
        addEyes(bodyRoot);
        addAntennas(bodyRoot);
        addTail(bodyRoot);

        createAlertSigns(gl);


        beeRoot.update();
    }

   
    private void addWings(SGNameNode bodyRoot, SGModelNode ltWingModelNode, SGModelNode rtWingModelNode) {

    float offX = 0f;
    float sideOffset = 0.8f;

    float pivotZ = 0.375f;   


    SGNameNode LW = new SGNameNode("LW");

    SGTransformNode lwPos = new SGTransformNode("lwPos",
            Mat4Transform.translate(offX, 1.5f, sideOffset));

    SGTransformNode lwPivot = new SGTransformNode("lwPivot",
            Mat4Transform.translate(0f, 0f, -pivotZ));

    SGTransformNode lwScale = new SGTransformNode("lwScale",
            Mat4Transform.scale(1.75f, 3.2f, 0.75f));

    SGTransformNode lwOrient = new SGTransformNode("lwOrient",
            Mat4Transform.rotateAroundX(-45));

    leftWingRotate = new SGTransformNode("lwFlap",
            Mat4Transform.rotateAroundY(0));


    bodyRoot.addChild(LW);
        LW.addChild(lwPos);
            lwPos.addChild(lwPivot);
                lwPivot.addChild(lwScale);
                    lwScale.addChild(lwOrient);
                        lwOrient.addChild(leftWingRotate);
                            leftWingRotate.addChild(ltWingModelNode);



    SGNameNode RW = new SGNameNode("RW");

  
    SGTransformNode rwPos = new SGTransformNode("rwPos",
            Mat4Transform.translate(offX, 1.5f, -sideOffset));


    SGTransformNode rwPivot = new SGTransformNode("rwPivot",
            Mat4Transform.translate(0f, 0f, pivotZ));


    SGTransformNode rwScale = new SGTransformNode("rwScale",
            Mat4Transform.scale(1.75f, 3.2f, 0.75f));


    SGTransformNode rwOrient = new SGTransformNode("rwOrient",
            Mat4Transform.rotateAroundX(45));


    rightWingRotate = new SGTransformNode("rwFlap",
            Mat4Transform.rotateAroundY(0));



    bodyRoot.addChild(RW);
        RW.addChild(rwPos);
            rwPos.addChild(rwPivot);
                rwPivot.addChild(rwScale);
                    rwScale.addChild(rwOrient);
                        rwOrient.addChild(rightWingRotate);
                            rightWingRotate.addChild(rtWingModelNode);
}



    private void addEyes(SGNameNode bodyRoot) {

        float x = -1.5f;


        SGNameNode le = new SGNameNode("LE");
        SGTransformNode leT = new SGTransformNode("leT",
                Mat4Transform.translate(x, 0.3f, 0.55f));
        SGTransformNode leS = new SGTransformNode("leS",
                Mat4Transform.scale(0.28f,0.28f,0.28f));
        SGModelNode leM = new SGModelNode("leM", sphereBlack);

        bodyRoot.addChild(le);
        le.addChild(leT);
        leT.addChild(leS);
        leS.addChild(leM);


        SGNameNode re = new SGNameNode("RE");
        SGTransformNode reT = new SGTransformNode("reT",
                Mat4Transform.translate(x, 0.3f, -0.55f));
        SGTransformNode reS = new SGTransformNode("reS",
                Mat4Transform.scale(0.28f,0.28f,0.28f));
        SGModelNode reM = new SGModelNode("reM", sphereBlack);

        bodyRoot.addChild(re);
        re.addChild(reT);
        reT.addChild(reS);
        reS.addChild(reM);
    }
    private void addAntennas(SGNameNode bodyRoot) {

        float x = -1.5f;
        SGNameNode la = new SGNameNode("LA");
        SGTransformNode laT = new SGTransformNode("laT",
                Mat4Transform.translate(x, 1.0f, 0.3f));
        SGTransformNode laS = new SGTransformNode("laS",
                Mat4Transform.scale(0.15f,0.55f,0.15f));
        SGModelNode laM = new SGModelNode("laM", sphereBlack);

        SGTransformNode laTipT = new SGTransformNode("laTipT",
                Mat4Transform.translate(0,0.7f,0));
        SGTransformNode laTipS = new SGTransformNode("laTipS",
                Mat4Transform.scale(0.20f,0.20f,0.20f));
        SGModelNode laTipM = new SGModelNode("laTipM", sphereBlack);

        bodyRoot.addChild(la);
        la.addChild(laT);
        laT.addChild(laS);
        laS.addChild(laM);
        laS.addChild(laTipT);
        laTipT.addChild(laTipS);
        laTipS.addChild(laTipM);

        // RIGHT ANTENNA
        SGNameNode ra = new SGNameNode("RA");
        SGTransformNode raT = new SGTransformNode("raT",
                Mat4Transform.translate(x, 1.0f, -0.3f));
        SGTransformNode raS = new SGTransformNode("raS",
                Mat4Transform.scale(0.15f,0.55f,0.15f));
        SGModelNode raM = new SGModelNode("raM", sphereBlack);

        SGTransformNode raTipT = new SGTransformNode("raTipT",
                Mat4Transform.translate(0,0.7f,0));
        SGTransformNode raTipS = new SGTransformNode("raTipS",
                Mat4Transform.scale(0.20f,0.20f,0.20f));
        SGModelNode raTipM = new SGModelNode("raTipM", sphereBlack);

        bodyRoot.addChild(ra);
        ra.addChild(raT);
        raT.addChild(raS);
        raS.addChild(raM);
        raS.addChild(raTipT);
        raTipT.addChild(raTipS);
        raTipS.addChild(raTipM);
    }


    private void addTail(SGNameNode bodyRoot) {
        SGNameNode t = new SGNameNode("Tail");
        SGTransformNode tT = new SGTransformNode("tT",
                Mat4Transform.translate(1.7f,0,0));
        SGTransformNode tS = new SGTransformNode("tS",
                Mat4Transform.scale(0.38f,0.38f,0.38f));
        SGModelNode tM = new SGModelNode("tM", sphereBlack);

        bodyRoot.addChild(t);
        t.addChild(tT);
        tT.addChild(tS);
        tS.addChild(tM);
    }

   

public void update(double t) {
    float deltaTime = (float)(t - lastUpdateTime);
    lastUpdateTime = t;

    wingAngleDeg += deltaTime * flapSpeedDegPerSec;
    if (wingAngleDeg > 360f) wingAngleDeg -= 360f;

float flapAngle = 20f * (float)Math.sin(Math.toRadians(wingAngleDeg));

    leftWingRotate.setTransform(Mat4Transform.rotateAroundX(flapAngle));
rightWingRotate.setTransform(Mat4Transform.rotateAroundX(-flapAngle));


    leftWingRotate.update();
    rightWingRotate.update();
}

public void forcePosition(float x, float y, float z) {
    rootTranslate.setTransform(Mat4Transform.translate(x, y, z));
    rootTranslate.update();
}




    public void render(GL3 gl) {

        gl.glDisable(GL.GL_CULL_FACE);
        bodyShader.use(gl);
        bodyShader.setInt(gl, "isBeeBody", 1);
        bodyShader.setInt(gl, "isWall", 0);
        bodyShader.setInt(gl, "isStatue", 0);
        bodyShader.setFloat(gl, "overlay_alpha", 0);

        partShader.use(gl);
        partShader.setInt(gl, "isBeeBody", 0);
        partShader.setInt(gl, "isWall", 0);
        partShader.setInt(gl, "isStatue", 0);
        partShader.setFloat(gl, "overlay_alpha", 0);


        beeRoot.draw(gl);

    

    }
    public SGNode getRoot() {
        return beeRoot;
    }
   
public Vec3 getPosition() {
    try {
        Mat4 world = rootTranslate.getWorldTransform();
        if (world != null) {
            float[] f = world.toFloatArrayForGLSL();
            if (f != null && f.length >= 16) return new Vec3(f[12], f[13], f[14]);
        }
    } catch (Throwable ignore) {}
    return new Vec3(0f, 0f, 0f);
}
private static class ExMark {
    SGTransformNode root;   
    SGTransformNode pos;    
}


    private void createAlertSigns(GL3 gl) {
    Material redMat = new Material();
    redMat.setAmbient(1,0,0);
    redMat.setDiffuse(1,0,0);
    redMat.setSpecular(0.2f,0.2f,0.2f);
    redMat.setShininess(32);

    Shader sh = new Shader(gl, "assets/shaders/vs_standard.txt",
                                "assets/shaders/fs_standard_d.txt");
    Renderer r = new Renderer();
    Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mesh circle = new Mesh(gl, Circle.vertices,Circle.indices);

    
   for (int i = 0; i < 3; i++) {


    ExMark m = createExMark(gl, sh, r, redMat);

exMarks[i] = m.root;   

m.pos.setTransform(Mat4Transform.translate(-i * 1.2f, 3f, 0));

rootRotate.addChild(m.root);

}

    }
    
    public void setAlertCount(int n) {
        for (int i = 0; i < 3; i++) {
            if (i < n)
                exMarks[i].setTransform(Mat4Transform.scale(1,1,1));
            else
                exMarks[i].setTransform(Mat4Transform.scale(0,0,0));
        }
        for (SGTransformNode t : exMarks) t.update();
    }
   
    public float getWorldX() {
    return getPosition().x;
}
public SGNode getWorldRootofBee(){
    return beeRoot;
}
public SGNode getAnimationRoot() {
    return rootTranslate;
}
public SGNode getAnimatedNode() {
    return rootTranslate;
}
private ExMark createExMark(GL3 gl, Shader sh, Renderer r, Material redMat) {

    ExMark group = new ExMark();


    SGTransformNode root = new SGTransformNode("exMarkRoot", Mat4Transform.scale(0,0,0));
    group.root = root;


    SGTransformNode pos = new SGTransformNode("ex-pos", Mat4Transform.translate(0, 3f, 0));
    group.pos = pos;

    root.addChild(pos);


    Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    SGTransformNode barRoot = new SGTransformNode("barRoot", Mat4Transform.translate(0, 0.4f, 0));
    SGTransformNode barScale = new SGTransformNode("barScale", Mat4Transform.scale(0.25f, 1f, 0.25f));
    SGModelNode barModel = new SGModelNode("barModel",
        new Model("bar", cube, new Mat4(1), sh, redMat, r, lights, camera));

    pos.addChild(barRoot);
        barRoot.addChild(barScale);
            barScale.addChild(barModel);


    Mesh sphere = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    SGTransformNode dotRoot = new SGTransformNode("dotRoot", new Mat4(1));
    SGTransformNode dotTrans = new SGTransformNode("dotTrans",
        Mat4Transform.translate(0f, -0.7f, 0f));
    SGTransformNode dotScale = new SGTransformNode("dotScale",
        Mat4Transform.scale(0.35f, 0.35f, 0.35f));
    SGModelNode dotModel = new SGModelNode("dotModel",
        new Model("dot", sphere, new Mat4(1), sh, redMat, r, lights, camera));

    pos.addChild(dotRoot);
        dotRoot.addChild(dotTrans);
            dotTrans.addChild(dotScale);
                dotScale.addChild(dotModel);

    return group;
}
public float getBaseHeight() {
    return baseHeight;
}


public float getRootOffsetX() {
    float[] f = rootTranslate.getWorldTransform().toFloatArrayForGLSL();
    return f[12];
}


public void setFacingStatues() {

    rootRotate.setTransform(new Mat4(1));
    rootRotate.update();
}
public void setFacingFlowers() {
 
    rootRotate.setTransform(Mat4Transform.rotateAroundY(180));
    rootRotate.update();
}




}


