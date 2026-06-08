import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Light {
  
  private Material material;
  private Vec3 position;
  private Mat4 modelMatrix;
  private Shader shader;
  private Camera camera;
  private Mesh mesh;


  private Vec3 direction   = new Vec3(0, -1, 0);
  private float cutOff     = (float)Math.cos(Math.toRadians(12.5));
  private float outerCutOff= (float)Math.cos(Math.toRadians(17.5));
  private boolean isSpotlight = false;
  private boolean isGeneralIllumination = false;

  public Light(GL3 gl, Camera camera) {
    this(gl, MaterialConstants.dullWhiteLightSource, new Vec3(3f,2f,1f), camera);
  }

  public Light(GL3 gl, Material material, Vec3 position, Camera camera) {
    this.material = material;
    this.position = position;
    this.camera   = camera;

    modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), modelMatrix);

    shader = new Shader(gl, "assets/shaders/vs_light_01.txt", "assets/shaders/fs_light_01.txt");
    mesh   = new Mesh(gl, Sphere.vertices, Sphere.indices);
  }
  
  public void setPosition(Vec3 v) {
    position.x = v.x;
    position.y = v.y;
    position.z = v.z;
  }
  
  public void setPosition(float x, float y, float z) {
    position.x = x;
    position.y = y;
    position.z = z;
  }
  
  public Vec3 getPosition() {
    return position;
  }
  
  public void setMaterial(Material m) {
    material = m;
  }
  
  public Material getMaterial() {
    return material;
  }
  
  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  

  public void render(GL3 gl) {
  }

  public void setDirection(Vec3 d) { direction = d; }
  public Vec3 getDirection()       { return direction; }

  public void setCutOff(float c)        { cutOff = c; }
  public float getCutOff()              { return cutOff; }

  public void setOuterCutOff(float oc)  { outerCutOff = oc; }
  public float getOuterCutOff()         { return outerCutOff; }

  public void setSpotlight(boolean s)   { isSpotlight = s; }
  public boolean isSpotlight()          { return isSpotlight; }

  public void setGeneralIllumination (boolean s) {isGeneralIllumination = s;}
  public boolean isGeneralIllumination() {return isGeneralIllumination;}

}
