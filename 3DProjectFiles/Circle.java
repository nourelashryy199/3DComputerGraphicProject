import com.jogamp.opengl.*;
import gmaths.*;
/* I declare that this code is my own work.
   Author: Nour Elashry (nomelashry1@sheffield.ac.uk)
*/
public final class Circle {
    private static final int SEGMENTS = 40;
    public static final float RADIUS = 0.5f;
    private static final int STRIDE = 8;
    public static final float[] vertices = createVertices();
    public static final int[] indices = createIndices();
    private static float[] createVertices() {
        float[] verts = new float[(SEGMENTS + 1) * STRIDE];
        int ptr = 0;
        verts[ptr++] = 0f;  
        verts[ptr++] = 0f;  
        verts[ptr++] = 0f;  
        verts[ptr++] = 0f;  
        verts[ptr++] = 1f;  
        verts[ptr++] = 0f;  
        verts[ptr++] = 0.5f; 
        verts[ptr++] = 0.5f;


        for (int i = 0; i < SEGMENTS; i++) {

         
            double angle = 2 * Math.PI * i / SEGMENTS;

  
            float x = RADIUS * (float)Math.cos(angle);
            float z = RADIUS * (float)Math.sin(angle);
            

            verts[ptr++] = x;     
            verts[ptr++] = 0f;    
            verts[ptr++] = z;      

            verts[ptr++] = 0f;
            verts[ptr++] = 1f;
            verts[ptr++] = 0f;
            verts[ptr++] = 0.5f + x / (2 * RADIUS);
            verts[ptr++] = 0.5f + z / (2 * RADIUS);
        }


        return verts;
    }

    private static int[] createIndices() {

    
        int[] idx = new int[SEGMENTS * 3];

        for (int i = 0; i < SEGMENTS; i++) {

  
            idx[i * 3] = 0;

            idx[i * 3 + 1] = i + 1;

            idx[i * 3 + 2] = (i + 2 > SEGMENTS ? 1 : i + 2);
        }

        return idx;
    }
    public float getRadius(){
        return this.RADIUS;
    }
}
