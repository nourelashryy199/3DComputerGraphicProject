import gmaths.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

/* I declare that this code is my own work.
   Author: Nour Elashry (nomelashry1@sheffield.ac.uk)
*/

public class GeneralIllumination {

    private SGNameNode root;

    private SGTransformNode sphereTranslate;
    private SGTransformNode sphereScale;
    private SGModelNode sphereModelNode;

    private Mesh sphereMesh;
    private Shader sunShader;
    private Renderer renderer;
    private Model sphereModel;

    private Camera camera;
    private Light generalLight;

    public GeneralIllumination(
    GL3 gl,
    Camera camera,
    Light generalLight,
    Shader sunShader
)
 {
        this.camera = camera;
        this.generalLight = generalLight;
        this.sunShader = sunShader;

        Material giMat = new Material();
       giMat.setAmbient(new Vec3(0.12f, 0.115f, 0.10f));
       giMat.setDiffuse(new Vec3(1.8f, 1.75f, 1.6f));
       giMat.setSpecular(new Vec3(1.2f, 1.2f, 1.1f));
  


        sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        renderer = new Renderer();

        Material sphereMat = new Material();
       sphereMat.setAmbient(new Vec3(1.0f, 0.7f, 0.2f));
       sphereMat.setDiffuse(new Vec3(1.0f, 0.7f, 0.2f));
       sphereMat.setSpecular(new Vec3(0.0f, 0.0f, 0.0f));

        sphereMat.setShininess(1f);
        

        sphereModel = new Model(
            "sun-sphere",
            sphereMesh,
            new Mat4(1),
            sunShader,
            sphereMat,
            renderer,
            new Light[]{},   
            camera
        );
        sphereModel.setShader(sunShader);


        Vec3 spherePos = new Vec3(0f, 60f, -25f);


        root = new SGNameNode("gi-root");

        sphereTranslate = new SGTransformNode("sun-translate",
                Mat4Transform.translate(spherePos));

        sphereScale = new SGTransformNode("sun-scale",
                Mat4Transform.scale(10f, 10f, 10f));

        sphereModelNode = new SGModelNode("sun-model", sphereModel);

        root.addChild(sphereTranslate);
            sphereTranslate.addChild(sphereScale);
                sphereScale.addChild(sphereModelNode);

        root.update();
    }


    public SGNode getRoot() {
        return root;
    }


    public void update(GL3 gl) {
    root.update();

    Mat4 wm = sphereModelNode.getWorldTransform();
    Mat4 view = camera.getViewMatrix();
    Mat4 proj = camera.getPerspectiveMatrix();
    Mat4 mvp  = Mat4.multiply(proj, Mat4.multiply(view, wm));


    float lx = wm.get(0,3);
    float ly = wm.get(1,3);
    float lz = wm.get(2,3);
    generalLight.setPosition(new Vec3(lx, ly, lz));

    sunShader.use(gl);
    sunShader.setFloatArray(gl, "mvpMatrix", mvp.toFloatArrayForGLSL());
    sunShader.setVec3(gl, "sunColor", new Vec3(1.2f, 1.05f, 0.55f));
    sunShader.setFloat(gl, "emissionStrength", 1.7f);
    sunShader.setFloat(gl, "glowStrength", 7.0f);
    sunShader.setFloat(gl, "glowRadius", 7.0f);

    gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

    sphereMesh.render(gl);
}

    public Light getLight() {
        return generalLight;
    }
}
