import java.awt.*;
import java.awt.event.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.JLabel;


public class Buzz extends JFrame implements ActionListener {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private Buzz_GLEventListener glEventListener;
  private final FPSAnimator animator; 

  public static void main(String[] args) {
    Buzz b1 = new Buzz("Buzz");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
    b1.canvas.requestFocusInWindow();
  }

  public Buzz(String textForTitleBar) {

    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    Camera camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Buzz_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);

//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JPanel bottomContainer = new JPanel(); 
bottomContainer.setLayout(new GridLayout(2, 1)); 
this.add(bottomContainer, BorderLayout.SOUTH); 
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JPanel beePanel = new JPanel(new BorderLayout());
JLabel beeTitle = new JLabel("Continuous vs Pose Mode", JLabel.CENTER);
beePanel.add(beeTitle, BorderLayout.NORTH);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JButton continuousBtn = new JButton("Continuous Mode");
continuousBtn.setActionCommand("continuous");
continuousBtn.addActionListener(this);
beePanel.add(continuousBtn, BorderLayout.WEST);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JPanel posePanel = new JPanel(new FlowLayout());
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JButton pose1 = new JButton("Pose Statue 1");
pose1.setActionCommand("pose1");
pose1.addActionListener(this);
posePanel.add(pose1);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JButton pose2 = new JButton("Pose Statue 2");
pose2.setActionCommand("pose2");
pose2.addActionListener(this);
posePanel.add(pose2);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JButton pose3 = new JButton("Pose Statue 3");
pose3.setActionCommand("pose3");
pose3.addActionListener(this);
posePanel.add(pose3);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
beePanel.add(posePanel, BorderLayout.EAST);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
bottomContainer.add(beePanel);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JPanel spotlightPanel = new JPanel(new BorderLayout());
JLabel spotlightTitle = new JLabel("Spotlight Controls", JLabel.CENTER);
spotlightPanel.add(spotlightTitle, BorderLayout.NORTH);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JButton spotOn = new JButton("Spotlight ON");
spotOn.setActionCommand("spot_on");
spotOn.addActionListener(this);
spotlightPanel.add(spotOn, BorderLayout.WEST);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JSlider spotBrightness = new JSlider(0, 200, 100);
spotBrightness.addChangeListener(e -> {
    float value = spotBrightness.getValue() / 100f;
    glEventListener.setSpotlightBrightness(value);
});
spotlightPanel.add(spotBrightness, BorderLayout.CENTER);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JButton spotOff = new JButton("Spotlight OFF");
spotOff.setActionCommand("spot_off");
spotOff.addActionListener(this);
spotlightPanel.add(spotOff, BorderLayout.EAST);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
bottomContainer.add(spotlightPanel);

    

//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JLabel overlayLabel = new JLabel("<html><center>Day<br>↓<br>Night</center></html>");
JSlider overlaySlider = new JSlider(JSlider.VERTICAL, 0, 50, 0);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
overlaySlider.addChangeListener(e ->{
  float value = overlaySlider.getValue()/100f;
  glEventListener.setOverlayAlphaAndSunBrightness(value);
});
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
JPanel overlayPanel = new JPanel(new BorderLayout());
overlayPanel.add(overlayLabel, BorderLayout.NORTH);
overlayPanel.add(overlaySlider, BorderLayout.CENTER);
//new (Nour Elashry, nomelashry1@sheffield.ac.uk)
this.add(overlayPanel, BorderLayout.EAST);


    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }
  
  //new (Nour Elashry, nomelashry1@sheffield.ac.uk)
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand(); 

    switch (cmd) {


        case "continuous":
            glEventListener.startBee();
            break;


        case "pose1":
            glEventListener.stopBee();
            glEventListener.teleportBeeToStatue(2);
            break;

        case "pose2":
            glEventListener.stopBee();
            glEventListener.teleportBeeToStatue(1);
            break;

        case "pose3":
            glEventListener.stopBee();
            glEventListener.teleportBeeToStatue(0);
            break;


        case "spot_on":
            glEventListener.startSpotlight();
            break;

        case "spot_off":
            glEventListener.stopSpotlight();
            break;
    }
}


}

class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;
  
  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }
  
  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    if (e.getModifiersEx() == java.awt.event.InputEvent.SHIFT_DOWN_MASK)
      switch (e.getKeyCode()) {
        case KeyEvent.VK_W: m = Camera.Movement.FAST_FORWARD;  break;
        case KeyEvent.VK_S: m = Camera.Movement.FAST_BACK;  break;
        case KeyEvent.VK_A: m = Camera.Movement.FAST_LEFT;  break;
        case KeyEvent.VK_D: m = Camera.Movement.FAST_RIGHT; break;
        case KeyEvent.VK_Q: m = Camera.Movement.FAST_UP;  break;
        case KeyEvent.VK_E: m = Camera.Movement.FAST_DOWN;  break;
      }
    else
      switch (e.getKeyCode()) {
        case KeyEvent.VK_W: m = Camera.Movement.FORWARD;  break;
        case KeyEvent.VK_S: m = Camera.Movement.BACK;  break;
        case KeyEvent.VK_A: m = Camera.Movement.LEFT;  break;
        case KeyEvent.VK_D: m = Camera.Movement.RIGHT; break;
        case KeyEvent.VK_Q: m = Camera.Movement.UP;  break;
        case KeyEvent.VK_E: m = Camera.Movement.DOWN;  break;
      }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;
  
  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }
  
    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    // need to include shift key here as used as a modifier with key presses as well
    int mask = MouseEvent.BUTTON1_DOWN_MASK & MouseEvent.SHIFT_DOWN_MASK;
    if (e.getModifiersEx()==MouseEvent.BUTTON1_DOWN_MASK
        || (e.getModifiersEx() & mask) == mask) {
      camera.updateYawPitch(dx, -dy);
    }
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }

}