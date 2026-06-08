import gmaths.*;
import java.util.ArrayList;
import com.jogamp.opengl.*;

public class SGNode {

  protected String name;
  protected ArrayList<SGNode> children;
  protected Mat4 worldTransform;

  public SGNode(String name) {
    children = new ArrayList<>();
    this.name = name;
    worldTransform = new Mat4(1);
  }

  public Mat4 getWorldTransform() { 
    return worldTransform;
  }

  public void addChild(SGNode child) {
    children.add(child);
  }

  public void update() {
    update(new Mat4(1));  
  }

  protected void update(Mat4 parentWorld) {
    worldTransform = parentWorld;  
    for (SGNode child : children) {
        child.update(parentWorld);
    }
  }

  public void draw(GL3 gl) {
    for (SGNode child : children)
      child.draw(gl);
  }
}
