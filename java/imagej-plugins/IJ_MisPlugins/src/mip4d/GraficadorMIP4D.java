 package mip4d;

import javax.media.opengl.GL2;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLAutoDrawable;

//import java.nio.Buffer;
import java.nio.ByteBuffer;
//import java.nio.IntBuffer;
//import java.nio.FloatBuffer;




 public class GraficadorMIP4D implements GLEventListener 
 
  {
	static GL2 gl;
	static int NSlices;
	static int NFrames;
	static int NChanels;
	static int NTexturas;
	static int[] Rtexturas;
	static int[] Gtexturas;
	static int[] Btexturas;
   //transformaciones
	static int azimut=0;
	static int elevacion=0;
	static boolean moviendose=false;
	static int X;
	static int Y;
	static int t = 1; 
	
	void Cargar_Texturas(GL2 gl) {
	   
	   
	   
	   NSlices=MIP4D_.cortes;
	   NFrames=MIP4D_.cuadros;
	   NChanels=MIP4D_.impR.getNChannels();
	   NTexturas=NSlices*NFrames;
	   
	   Rtexturas=new int[NSlices*NFrames];
	   Gtexturas=new int[NSlices*NFrames];
	   //Btexturas=new int[NSlices*NFrames];
	  for(int i=1;i<=NTexturas;i++){
		Rtexturas[i-1]=i;
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Rtexturas[i-1]);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	    gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	    gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
	    gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
	    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, 
			 			MIP4D_.ancho, MIP4D_.alto, 0,GL2.GL_RED, GL2.GL_UNSIGNED_BYTE,
			 			ByteBuffer.wrap((byte[])(MIP4D_.impR.getStack().getPixels(i))));
	    gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_ADD);
	    Gtexturas[i-1]=i;
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Gtexturas[i-1]);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	    gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	    gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
	    gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
	    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, 
			 			MIP4D_.ancho, MIP4D_.alto, 0,GL2.GL_GREEN, GL2.GL_UNSIGNED_BYTE,
			 			ByteBuffer.wrap((byte[])(MIP4D_.impG.getStack().getPixels(i))));
	    gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_ADD); 
	    	
	    }  
		
	  gl.glEnable(GL2.GL_TEXTURE_2D);
	  
  }
      
  	public void init(GLAutoDrawable gLDrawable) {
          gl=(GL2) gLDrawable.getGL();
            
      //  gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
        gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT,GL2.GL_NICEST);
        //gl.glEnable(GL2.GL_MULTISAMPLE);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Cargar_Texturas(gl);         
                 
     }
    public void display(GLAutoDrawable gLDrawable)
    {
      gl = (GL2) gLDrawable.getGL();
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
      gl.glLoadIdentity();
      gl.glRotatef(azimut, 0, 1, 0);
 	  gl.glRotatef(elevacion, 1, 0, 0);
 	  
 	  
 	  gl.glScalef(1.0f, 1.0f, 0.3f);
 	  gl.glTranslatef(-0.5f,-0.5f,-0.5f);
 	  
 	  gl.glEnable(GL2.GL_BLEND);
	  gl.glBlendEquation(GL2.GL_MAX);
	  //t=1;
 	  for (int i=1;i<=NSlices;i++){
 		//for(int i=1;i<=texturas.length;i++){
    	 //gl.glBindTexture(GL2.GL_TEXTURE_2D, texturas[i-1]);
    	 gl.glBindTexture(GL2.GL_TEXTURE_2D, Rtexturas[t-1]);
         gl.glBegin(GL2.GL_QUADS);
          
          gl.glTexCoord2f(0,0); gl.glVertex3f(0,0,(float) (1.0*i/30));
          gl.glTexCoord2f(1,0); gl.glVertex3f(1,0,(float) (1.0*i/30));
          gl.glTexCoord2f(1,1); gl.glVertex3f(1,1,(float) (1.0*i/30));
          gl.glTexCoord2f(0,1); gl.glVertex3f(0,1,(float) (1.0*i/30));
         gl.glEnd();
         
         gl.glBindTexture(GL2.GL_TEXTURE_2D, Gtexturas[t-1]);
          gl.glBegin(GL2.GL_QUADS);
           gl.glTexCoord2f(0,0); gl.glVertex3f(0,0,(float) (1.0*i/30));
           gl.glTexCoord2f(1,0); gl.glVertex3f(1,0,(float) (1.0*i/30));
           gl.glTexCoord2f(1,1); gl.glVertex3f(1,1,(float) (1.0*i/30));
           gl.glTexCoord2f(0,1); gl.glVertex3f(0,1,(float) (1.0*i/30));
          gl.glEnd();
         
          if (t>=NSlices*NFrames) t=1;
    	  else t++;
          }
 	  
 	  //}
 	 //EJES     
     gl.glDisable(GL2.GL_BLEND);
     gl.glFlush();	 
     
    }
    public void displayChanged(GLAutoDrawable gLDrawable, 
      boolean modeChanged, boolean deviceChanged) {
    }
    public void reshape(GLAutoDrawable gLDrawable, int x, 
    int y, int width, int height) 
    {
        gl=(GL2) gLDrawable.getGL();
    	gl.glViewport(0,0,width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION );
        gl.glLoadIdentity();
        gl.glOrtho (-0.75f,0.75f,-0.75f,0.75f,-0.75f,0.75f);
        gl.glMatrixMode ( GL2.GL_MODELVIEW );
        gl.glLoadIdentity ();
        
               
    }

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
 }