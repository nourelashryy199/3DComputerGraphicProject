import gmaths.*;

public class SGTransformNode extends SGNode {

  private Mat4 transform;

  public SGTransformNode(String name, Mat4 t) {
    super(name);
    transform = new Mat4(t);
  }

  public void setTransform(Mat4 m) {
    transform = new Mat4(m);
  }

  @Override
  protected void update(Mat4 parentWorld) {
    Mat4 currentWorld = Mat4.multiply(parentWorld, transform);
    worldTransform = currentWorld;

    for (SGNode child : children) {
      child.update(currentWorld);
    }
  }


  /*public void print(int indent, boolean inFull) {
    System.out.println(getIndentString(indent)+"Name: "+name);
    if (inFull) {
      System.out.println("worldTransform");
      System.out.println(worldTransform);
      System.out.println("transform node:");
      System.out.println(transform);
    }
    for (int i=0; i<children.size(); i++) {
      children.get(i).print(indent+1, inFull);
    }
  }*/
  
}