import gmaths.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
/* I declare that this code is my own work.
   Author: Nour Elashry (nomelashry1@sheffield.ac.uk)
*/
public class Statue{

    private SGNode basicStatueRoot;
    private Material statueMaterial;
    private Vec3 scale;

    private SGTransformNode worldTransform;
    private Shader shader;

    public Statue(GL3 gl, Camera camera, Light [] lights, Texture statueTexture,
            ExpressionType expressionType){

            this.shader = new Shader(gl,
            "assets/shaders/vs_standard.txt",
            "assets/shaders/fs_standard_d.txt"
            );
            Renderer renderer = new Renderer();


        
        

            Mesh sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

            this.statueMaterial = new Material();

            this.statueMaterial.setDiffuseMap(statueTexture);

            Model statueModel = new Model(
                "statue",
                sphereMesh,
                new Mat4(1),
                shader,
                this.statueMaterial,
                renderer,
                lights,
                camera);

            basicStatueRoot = new SGNameNode("statue-root");
           
            this.scale = new Vec3(7f, 18f, 7f);
            SGTransformNode statueScale = new SGTransformNode(
                "statue-scale",
                Mat4Transform.scale(scale.x, scale.y, scale.z));


            SGTransformNode statueTranslate = new SGTransformNode(
                "statue-translate",
                Mat4Transform.translate(0f, 5.0f, 0f)
            );

            SGTransformNode statueRotate = new SGTransformNode(
                "statue-rotate",
                Mat4Transform.rotateAroundY(180f)
            );


            SGModelNode statueNode = new SGModelNode("statue-node", statueModel);
            basicStatueRoot.addChild(statueTranslate);
                statueTranslate.addChild(statueScale);
                statueScale.addChild(statueRotate);
                    statueRotate.addChild(statueNode);

            worldTransform = new SGTransformNode(
                "statue-world-transform", 
                Mat4Transform.translate(0,0,0));

            worldTransform.addChild(basicStatueRoot);
            worldTransform.update();
            update(expressionType);
    }
public float getHalfWidth(){
    return scale.x/2;
}
public Vec3 getScale(){
    return scale;
}
public float getWidth(){
    return scale.x;
}


    public void update(ExpressionType expressionType) {
    if (expressionType == ExpressionType.NEUTRAL) {
       statueMaterial.setAmbient(0.45f, 0.45f, 0.45f);   
       statueMaterial.setDiffuse(0.70f, 0.70f, 0.70f);   
       statueMaterial.setSpecular(0.00f, 0.00f, 0.00f);  
       statueMaterial.setShininess(1f);                

    }
    else if (expressionType == ExpressionType.HAPPY) {
        statueMaterial.setAmbient(0.60f, 0.35f, 0.20f);   
        statueMaterial.setDiffuse(1.0f, 1.00f, 1.00f);   
        statueMaterial.setSpecular(0.1f, 0.1f, 0.1f); 
        statueMaterial.setShininess(8f);                

    }
    else if (expressionType == ExpressionType.ANGRY) {
     statueMaterial.setAmbient(0.15f, 0.15f, 0.15f);   
statueMaterial.setDiffuse(1.0f, 1.00f, 1.00f);   
statueMaterial.setSpecular(1.7f, 1.7f, 1.7f);  
statueMaterial.setShininess(128f);                

    }

    if (worldTransform != null) worldTransform.update();
}


    public void render(GL3 gl){
        shader.use(gl);
        shader.setInt(gl, "isStatue", 1);
        shader.setInt(gl, "isWall", 0);
        shader.setInt(gl, "isBeeBody", 0);
        shader.setFloat(gl, "overlay_alpha", 0f);
        shader.setInt(gl, "useTexture", 1);

        Vec3 amb = statueMaterial.getAmbient();
        Vec3 diff = statueMaterial.getDiffuse();
        Vec3 spec = statueMaterial.getSpecular();
        float shin = statueMaterial.getShininess();

        shader.setVec3(gl, "material.ambient", amb);
        shader.setVec3(gl, "material.diffuse", diff);
        shader.setVec3(gl, "material.specular", spec);
        shader.setFloat(gl, "material.shininess", shin);

        if(worldTransform != null){
            worldTransform.draw(gl);
        }


    }
    public SGNode getRoot() {
        return worldTransform;
    }

public void setStatueTexture(Texture newTexture) {
    if (statueMaterial != null) {
        statueMaterial.setDiffuseMap(newTexture);
    }
}
public void setPosition(Vec3 p){
    worldTransform.setTransform(
        Mat4Transform.translate(p.x, p.y, p.z)
    );
    worldTransform.update();
}
public Vec3 getWorldPosition(){
    float[] a = worldTransform.worldTransform.toFloatArrayForGLSL();
    float x = a[12];
    float y = a[13];
    float z = a[14];

    return new Vec3(x,y,z);
}

}