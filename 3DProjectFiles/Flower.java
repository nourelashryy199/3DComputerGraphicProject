import com.jogamp.opengl.*;
import gmaths.*;

/* I declare that this code is my own work.
   Author: Nour Elashry (nomelashry1@sheffield.ac.uk)
*/

public class Flower {

    private SGNode flowerRoot;
    private SGTransformNode flowerWorldTransform;  
    private SGTransformNode stemTransform;

    private Shader shader;
    private Renderer renderer;

    private Material petalMaterial;
    private Material centerMaterial;
    private Material stemMaterial;

    public Flower(GL3 gl, Camera camera, Light[] lights) {

        shader = new Shader(
            gl,
            "assets/shaders/vs_standard.txt",
            "assets/shaders/fs_standard_d.txt"
        );
        shader.use(gl);

        shader.setInt(gl, "isBeeBody", 0);
        shader.setInt(gl, "isWall", 0);
        shader.setInt(gl, "isStatue", 0);
        shader.setFloat(gl, "overlay_alpha", 0f);
        shader.setInt(gl, "useTexture", 0);  

        renderer = new Renderer();

        
        petalMaterial = new Material();
        petalMaterial.setAmbient(0.9f, 0.4f, 0.4f);
        petalMaterial.setDiffuse(1.0f, 0.6f, 0.6f);
        petalMaterial.setSpecular(0.3f, 0.3f, 0.3f);
        petalMaterial.setShininess(32f);

        centerMaterial = new Material();
        centerMaterial.setAmbient(0.8f, 0.8f, 0.1f);
        centerMaterial.setDiffuse(1.0f, 1.0f, 0.2f);
        centerMaterial.setSpecular(0.3f, 0.3f, 0.3f);
        centerMaterial.setShininess(16f);

        stemMaterial = new Material();
        stemMaterial.setAmbient(0.2f, 0.6f, 0.2f);
        stemMaterial.setDiffuse(0.3f, 0.9f, 0.3f);
        stemMaterial.setSpecular(0.05f, 0.05f, 0.05f);
        stemMaterial.setShininess(4f);

        flowerRoot = new SGNameNode("flower_root");

        flowerWorldTransform = new SGTransformNode(
            "flower_world_transform",
            Mat4Transform.translate(0f, 0f, 0f)   
        );
        flowerRoot.addChild(flowerWorldTransform);

        Mesh petalMesh = new PetalMesh().buildMesh(gl);
        SGNameNode petalsRoot = new SGNameNode("petals_root");

        int numPetals = 10;
        float angleStep = 360f / numPetals;

        for (int i = 0; i < numPetals; i++) {

            float angle = i * angleStep;

            SGTransformNode rotate = new SGTransformNode(
                "petal_rotate_" + i,
                Mat4Transform.rotateAroundY(angle)
            );

            SGTransformNode translate = new SGTransformNode(
                "petal_translate_" + i,
                Mat4Transform.translate(0f, 0.15f, 0.32f)
            );

            Model petalModel = new Model(
                "petal_" + i,
                petalMesh,
                new Mat4(1),
                shader,
                petalMaterial,
                renderer,
                lights,
                camera
            );

            SGModelNode petalNode = new SGModelNode("petal_node_" + i, petalModel);

            petalsRoot.addChild(rotate);
            rotate.addChild(translate);
            translate.addChild(petalNode);
        }

    
        Mesh sphereMesh = new Mesh(
            gl,
            Sphere.vertices.clone(),
            Sphere.indices.clone()
        );

        Model centerModel = new Model(
            "flower_center",
            sphereMesh,
            new Mat4(1),
            shader,
            centerMaterial,
            renderer,
            lights,
            camera
        );

        SGTransformNode centerTransform = new SGTransformNode(
            "center_transform",
            Mat4Transform.scale(0.22f, 0.22f, 0.22f)
        );

        SGModelNode centerNode = new SGModelNode("center_node", centerModel);
        centerTransform.addChild(centerNode);

 
        Mesh cubeMesh = new Mesh(
            gl,
            Cube.vertices.clone(),
            Cube.indices.clone()
        );

        Model stemModel = new Model(
            "stem",
            cubeMesh,
            new Mat4(1),
            shader,
            stemMaterial,
            renderer,
            lights,
            camera
        );

        Mat4 stemMatrix = Mat4.multiply(
            Mat4Transform.translate(0f, -0.8f, 0f),
            Mat4Transform.scale(0.1f, 1.6f, 0.1f)
        );

        stemTransform = new SGTransformNode("stem_transform", stemMatrix);

        SGModelNode stemNode = new SGModelNode("stem_node", stemModel);
        stemTransform.addChild(stemNode);

        flowerWorldTransform.addChild(petalsRoot);
        flowerWorldTransform.addChild(centerTransform);
        flowerWorldTransform.addChild(stemTransform);

        flowerRoot.update();
    }



    public SGNode getRoot() {
        return flowerRoot;
    }

    public void setPetalColor(float r, float g, float b){
        petalMaterial.setAmbient(r * 0.8f, g * 0.4f, b * 0.4f);
        petalMaterial.setDiffuse(r, g, b);
    }

    public Vec3 getWorldPosition() {
        float[] a = flowerWorldTransform.worldTransform.toFloatArrayForGLSL();
        return new Vec3(a[12], a[13], a[14]);
    }

    public void setPosition(Vec3 p) {
        flowerWorldTransform.setTransform(
            Mat4Transform.translate(p.x, p.y, p.z)
        );
        flowerRoot.update();
    }
}
