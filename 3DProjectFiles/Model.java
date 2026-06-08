import gmaths.*;
import com.jogamp.opengl.*;

public class Model {
  
  protected String name;
  protected Mesh mesh;
  protected Mat4 modelMatrix;
  protected Shader shader;
  protected Material material;
  protected Camera camera;
  protected Light[] lights;      
  protected Renderer renderer;

  protected boolean isBeeBody = false;

  public Model() {
    name        = null;
    mesh        = null;
    modelMatrix = null;
    material    = null;
    shader      = null;
    renderer    = null;
    lights      = null;   
    camera      = null;
  }
  
  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader,
               Material material, Renderer renderer,
               Light[] lights, Camera camera) {

    this.name        = name;
    this.mesh        = mesh;
    this.modelMatrix = modelMatrix;
    this.shader      = shader;
    this.material    = material;
    this.renderer    = renderer;
    this.lights      = lights;
    this.camera      = camera;
  }

  public void setName(String s) { this.name = s; }
  public void setMesh(Mesh m)   { this.mesh = m; }
  public Mesh getMesh()         { return mesh; }

  public void setModelMatrix(Mat4 m) { modelMatrix = m; }

  public void setMaterial(Material material) { this.material = material; }
  public Material getMaterial()             { return material; }

  public void setShader(Shader shader) { this.shader = shader; }
  public Shader getShader()            { return shader; }

  public void setLights(Light[] lights) { this.lights = lights; }
  public Light[] getLights()            { return lights; }

  public void setBeeBody(boolean b) { this.isBeeBody = b; }
  public boolean isBeeBody()        { return isBeeBody; }

  public void displayName(GL3 gl) {
    System.out.println("Name = " + name);
  }

  private boolean mesh_null() {
    return (mesh == null);
  }

  public void render(GL3 gl) {
    shader.use(gl);
    shader.setInt(gl, "isSkyBox", 0);
    // AUTO-DETERMINE WHETHER THIS MODEL USES A TEXTURE
if (material.getDiffuseMap() != null) {
    shader.setInt(gl, "useTexture", 1);   
} else {
    shader.setInt(gl, "useTexture", 0);  
}

    renderer.render(gl, mesh, modelMatrix, shader, material, lights, camera);
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    if (mesh_null()) {
      System.out.println("Error: null mesh in model render");
      return;
    }
    shader.use(gl);
    shader.setInt(gl, "isSkyBox", 0);
if (material.getDiffuseMap() != null) {
    shader.setInt(gl, "useTexture", 1);   
} else {
    shader.setInt(gl, "useTexture", 0);   
}

    renderer.render(gl, mesh, modelMatrix, shader, material, lights, camera);
  }
}
