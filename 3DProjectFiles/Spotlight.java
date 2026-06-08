import gmaths.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

/* I declare that this code is my own work.
   Author: Nour Elashry (nomelashry1@sheffield.ac.uk)
*/

public class Spotlight {

  private SGNameNode root;

  private SGTransformNode poleTranslate;
  private SGTransformNode poleScale;
  private SGModelNode poleModelNode;

  private SGTransformNode bulbPivotTranslate;
  private SGTransformNode bulbRotate;
  private SGTransformNode bulbCenterTranslate;
  private SGTransformNode bulbScale;
  private SGModelNode bulbModelNode;

  private SGTransformNode bulbTipTranslate;
  private SGTransformNode bulbTilt;


  private Shader shader;
  private Renderer renderer;
  private Model poleModel;
  private Model bulbModel;

  private Mesh cubeMesh;
  private Mesh sphereMesh;

  private Light spotlightLight;
  private Light generalLightLight;
  private Camera camera;


  private final float poleHeight = 20.0f;
  private final float poleWidth  = 0.50f;
  private final float bulbLength = 4.5f;
  private final float bulbThickness = 1.0f;
  private final float rotationSpeedDegPerSec = 60f;

  private float worldX, worldZ, worldYBase;
  private Vec3 pivotWorldPosition;

  private float currentAngleDeg = 0f;

  public Spotlight(GL3 gl, Camera camera, Light   generalLightLight, Light spotLightLight,
                   Texture poleTexture, Texture bulbTexture,
                   float worldX, float worldZ, float baseY) {

    this.camera         = camera;
    this.generalLightLight = generalLightLight;
    this.spotlightLight = spotLightLight;
    this.worldX         = worldX;
    this.worldZ         = worldZ;
    this.worldYBase     = baseY;

    spotlightLight.setSpotlight(true);

    shader   = new Shader(gl, "assets/shaders/vs_standard.txt",
                             "assets/shaders/fs_standard_d.txt");
    renderer = new Renderer();

    cubeMesh   = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

    Material poleMaterial = new Material();
    poleMaterial.setAmbient(0.7f, 0.4f, 0.1f);
    poleMaterial.setDiffuse(0.9f, 0.5f, 0.1f);
    poleMaterial.setSpecular(0.2f, 0.2f, 0.2f);
    poleMaterial.setShininess(16f);
    if (poleTexture != null) poleMaterial.setDiffuseMap(poleTexture);

    Material bulbMaterial = new Material();
    bulbMaterial.setAmbient(1.0f, 0.9f, 0.6f);
    bulbMaterial.setDiffuse(1.0f, 0.9f, 0.6f);
    bulbMaterial.setSpecular(1.0f, 1.0f, 1.0f);
    bulbMaterial.setShininess(64f);
    if (bulbTexture != null) bulbMaterial.setDiffuseMap(bulbTexture);

  
    poleModel = new Model("lamp-pole", cubeMesh, new Mat4(1),
                          shader, poleMaterial, renderer,
                          new Light[]{ generalLightLight }, camera);

    bulbModel = new Model("lamp-bulb", sphereMesh, new Mat4(1),
                          shader, bulbMaterial, renderer,
                          new Light[]{ generalLightLight }, camera);

    root = new SGNameNode("spotlight-root");

    poleTranslate = new SGTransformNode("pole-translate",
        Mat4Transform.translate(worldX, worldYBase + poleHeight * 0.5f, worldZ));
    poleScale = new SGTransformNode("pole-scale",
        Mat4Transform.scale(poleWidth, poleHeight, poleWidth));
    poleModelNode = new SGModelNode("pole-model", poleModel);

    root.addChild(poleTranslate);
      poleTranslate.addChild(poleScale);
        poleScale.addChild(poleModelNode);

    float pivotY = worldYBase + poleHeight;
    pivotWorldPosition = new Vec3(worldX, pivotY, worldZ);

    bulbPivotTranslate = new SGTransformNode("bulb-pivot-translate",
        Mat4Transform.translate(worldX, pivotY, worldZ));

    bulbRotate = new SGTransformNode("bulb-rotate",
        Mat4Transform.rotateAroundY(0f));

    float half = bulbLength * 0.5f;

    bulbCenterTranslate = new SGTransformNode("bulb-center-translate",
        Mat4Transform.translate(half, 0, 0));

    bulbTipTranslate = new SGTransformNode("bulb-tip-translate",
        Mat4Transform.translate(half, 0, 0));

    bulbScale = new SGTransformNode("bulb-scale",
        Mat4Transform.scale(bulbLength, bulbThickness, bulbThickness));

    bulbModelNode = new SGModelNode("bulb-model", bulbModel);

    root.addChild(bulbPivotTranslate);
      bulbPivotTranslate.addChild(bulbRotate);
    bulbTilt = new SGTransformNode("bulb-tilt",
        Mat4Transform.rotateAroundZ(-30));  

    bulbRotate.addChild(bulbTilt);
    bulbTilt.addChild(bulbCenterTranslate);

    bulbCenterTranslate.addChild(bulbScale);
        bulbScale.addChild(bulbModelNode);

    bulbCenterTranslate.addChild(bulbTipTranslate);


    root.update();

  }

  public SGNode getRoot() {
    return root;
  }

  public void update(float t) {
    currentAngleDeg = (t * rotationSpeedDegPerSec) % 360f;

    bulbRotate.setTransform(Mat4Transform.rotateAroundY(currentAngleDeg));
    root.update();

    Mat4 tipWorldMatrix = bulbTipTranslate.getWorldTransform();
    float[] wTip = tipWorldMatrix.toFloatArrayForGLSL();

    float tx = wTip[12];
    float ty = wTip[13];
    float tz = wTip[14];

    spotlightLight.setPosition(new Vec3(tx, ty, tz));

    Mat4 rotWorld = bulbTilt.getWorldTransform();

    float[] wRot = rotWorld.toFloatArrayForGLSL();

    float fx = wRot[0];  
    float fy = wRot[1];
    float fz = wRot[2];

    Vec3 forward = new Vec3(fx, fy, fz);
    forward.normalize();

    spotlightLight.setDirection(forward);
}



  

}
