import com.jogamp.opengl.*;
import gmaths.*;

/* I declare that this code is my own work.
   Author: Nour Elashry (nomelashry1@sheffield.ac.uk)
*/
public class PetalMesh {

    private int slices = 20;        
    private float height = 0.6f;    
    private float width  = 0.25f;   

    public Mesh buildMesh(GL3 gl) {

        int vertsPerRow = 2;
        int totalVerts = (slices + 1) * vertsPerRow;
        float[] packed = new float[totalVerts * 8];  
        int pi = 0; 

        for (int i = 0; i <= slices; i++) {

            float v = (float)i / slices;
            float y = v * height;
            float curve = (float)Math.sin(v * (float)Math.PI) * width;

            float xLeft  = -curve;
            float xRight =  curve;

     
            packed[pi++] = xLeft;  
            packed[pi++] = y;      
            packed[pi++] = 0f;     

            packed[pi++] = 0f;     
            packed[pi++] = 0f;     
            packed[pi++] = 1f;     

            packed[pi++] = 0f;     
            packed[pi++] = v;      


            packed[pi++] = xRight;
            packed[pi++] = y;
            packed[pi++] = 0f;

            packed[pi++] = 0f;
            packed[pi++] = 0f;
            packed[pi++] = 1f;

            packed[pi++] = 1f;
            packed[pi++] = v;
        }

        int[] indices = new int[slices * 6];
        int ii = 0;

        for (int i = 0; i < slices; i++) {

            int L1 = i * 2;
            int R1 = L1 + 1;
            int L2 = (i + 1) * 2;
            int R2 = L2 + 1;

            // Triangle 1
            indices[ii++] = L1;
            indices[ii++] = R1;
            indices[ii++] = L2;

            // Triangle 2
            indices[ii++] = L2;
            indices[ii++] = R1;
            indices[ii++] = R2;
        }

        return new Mesh(gl, packed, indices);
    }
}
