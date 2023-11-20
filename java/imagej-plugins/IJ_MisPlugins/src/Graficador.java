import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import java.nio.ByteBuffer;




 public class Graficador implements GLEventListener 
 
  {
	static GL2 gl;
	static float zoom=1;
	static boolean reset=false;
	static float deltaZ=9;
	static int umbral=0;

	static int zoomFogZ=0;
	static int zoomFogX=0;
	static int zoomFogY=0;
	
	static float atenuacion_distancia=1;
	
	static int[] texturas=new int[MIP_.imp.getNSlices()];

	
	//0-MIP ESTADAR 1-DMIP 2-LMIP
	static int MIP=0;
	static boolean cambio=false;

	//modo_fog 0=linear 1=exp 2=exp2 
	static int modo_fog=0;
	static float densidad_fog=0.3f; //entre 0 y 1
	
	//transformaciones
	static int azimut=0;
	static int elevacion=0;
	static boolean moviendose=false;
	static int X;
	static int Y;
	 
	
//para dibujar sin texturas y sin especificar de a uno los vertex
//que desaprovecha capacidad de hardware
 /*
	void dibujar_VertexArray(GL gl) 
  {
	gl.glEnableClientState(GL.GL_COLOR_ARRAY);
	gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
    byte[] imagen=new byte[MIP_.alto*MIP_.ancho];	
    
	float[] vertex=new float[MIP_.ancho*MIP_.alto*3];
    int contador=0;
    int z=0;
	for(int y=0;y<MIP_.alto;y++)
	  for(int x=0;x<MIP_.ancho;x++)
	  {
		vertex[contador]=x;
		vertex[contador+1]=y;
		vertex[contador+2]=z;
	    contador+=3;
	  }		
	
   //recorre las imagenes, obtiene los pixeles y los tira juntos	 
	for(int i=1;i<=MIP_.imp.getNSlices();i++)
	{
	 MIP_.imp.setSlice(i);
	 imagen=(byte[])(MIP_.imp.getProcessor().getPixels());

	 
	 //actualiza la parte z de los vertices
	 z=i-1;
	 for(int ind=2;i<vertex.length;i+=3) vertex[ind]=z;
	
	 gl.glColorPointer(1, GL.GL_BYTE, 0, Buffer);
	 gl.glVertexPointer(3, GL.GL_FLOAT, 0, FloatBuffer.wrap(vertex));
	 
	 //dibuja
	 
	 gl.glPushMatrix();
	 
	 gl.glRotatef(azimut, 0, 1, 0);
	 gl.glRotatef(elevacion, 1, 0, 0);
	 
	 gl.glTranslatef(-MIP_.ancho/2.0f+ zoomFogX, -MIP_.alto/2.0f+ zoomFogY, -MIP_.imp.getNSlices()*deltaZ/2+ zoomFogZ);
 	
	 gl.glBegin(GL.GL_POINT);
	//  gl.glArrayElement(1);
	 gl.glEnd();	
		
	 gl.glPopMatrix();
	}
	
	
	
	  
  }
	*/
	
	
 static void limpiar(GL2 gl)
 {
	
	texturas=new int[MIP_.imp.getNSlices()]; 
 }
	
	
  void Cargar_Texturas(GL2 gl)
  {
	 
	  for(int i=1;i<=texturas.length;i++)
	   {
		texturas[i-1]=i;
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texturas[i-1]);
		
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	    gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	    gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,GL2.GL_CLAMP);
	    gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);

	    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_LUMINANCE, 
			 			MIP_.ancho, MIP_.alto, 0,GL2.GL_LUMINANCE, GL2.GL_UNSIGNED_BYTE,
			 			ByteBuffer.wrap((byte[])(MIP_.imp.getStack().getPixels(i))));
	
	    gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE); 
		
	     
	   }  
		
	  gl.glEnable(GL2.GL_TEXTURE_2D);
	  
  }
    
  
  void Configurar_MIP(GL2 gl)
	{
//	  reset_transformaciones(gl);
	  gl.glBlendEquation(GL2.GL_MAX);
	//  gl.glBlendFunc(GL2.GL_ONE,GL2.GL_ONE);
	  gl.glEnable(GL2.GL_BLEND);
	  
	}
  	
  void Configurar_DMIP(GL2 gl)
	{
	//  reset_transformaciones(gl);
	  gl.glEnable(GL2.GL_FOG); 
      gl.glHint(GL2.GL_FOG_HINT,GL2.GL_NICEST);
	  switch(modo_fog)
      {
	   case 0: gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);zoom=1;atenuacion_distancia=1;break;
	   case 1: gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP);zoom=100;atenuacion_distancia=0.01f;break;	
	   case 2: gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP2);zoom=100;atenuacion_distancia=0.01f;break;
	  }
	  gl.glFogf(GL2.GL_FOG_DENSITY, densidad_fog);
	  gl.glFogf(GL2.GL_FOG_START,0);
	  gl.glFogf(GL2.GL_FOG_END, deltaZ*(texturas.length/2));
	 
	  gl.glBlendEquation(GL2.GL_MAX);
	 // gl.glBlendFunc(GL2.GL_ONE,GL2.GL_ONE);
	  gl.glEnable(GL2.GL_BLEND);
	}

  void Configurar_LMIP(GL2 gl)
  {
	  reset_transformaciones(gl);
		 
	  gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	  
	  gl.glEnable(GL2.GL_BLEND);
	  gl.glBlendEquation(GL2.GL_MAX);
	 
	  gl.glEnable(GL2.GL_DEPTH_TEST);
	     gl.glEnable(GL2.GL_ALPHA_TEST);
		 gl.glAlphaFunc(GL2.GL_GEQUAL, umbral);
	     gl.glDepthFunc(GL2.GL_LEQUAL);
	   
	  
	}
  
	static void reset_transformaciones(GL2 gl)
	{
	 azimut=0;
	 elevacion=0;
	 zoom=1;
	 zoomFogX=0;
	 zoomFogY=0;
	 zoomFogZ=0;

	 if(MIP==1 && modo_fog!=0)
	 	zoom=100;
	
	//actualiza display
	 gl.glMatrixMode ( GL2.GL_PROJECTION );
     gl.glLoadIdentity ();
     gl.glOrtho (-atenuacion_distancia*MIP_.ancho/2.0f, atenuacion_distancia*MIP_.ancho/2.0f, -atenuacion_distancia*MIP_.alto/2.0f, atenuacion_distancia*MIP_.alto/2.0f,-atenuacion_distancia*deltaZ*texturas.length,atenuacion_distancia*deltaZ*texturas.length);
   
     gl.glMatrixMode ( GL2.GL_MODELVIEW );
     gl.glLoadIdentity();
	
	}
	   
    public void init(GLAutoDrawable gLDrawable) {
          gl=(GL2) gLDrawable.getGL();
          limpiar(gl);
         
      //  gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        gl.glHint(GL2.GL_POINT_SMOOTH_HINT,GL2.GL_NICEST);
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
         
         Cargar_Texturas(gl);         
         
         //por defecto
         Configurar_MIP(gl);
         
     }
    public void display(GLAutoDrawable gLDrawable)
    {
      gl = (GL2) gLDrawable.getGL();

      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
     gl.glLoadIdentity();	
    
     if(reset) reset_transformaciones(gl);reset=false;
     
     if(cambio)
     {
      gl.glDisable(GL2.GL_BLEND);
      gl.glDisable(GL2.GL_FOG);
      gl.glDisable(GL2.GL_DEPTH_TEST);
      gl.glDisable(GL2.GL_ALPHA_TEST);
      
       switch(MIP)
        {
         case 0: Configurar_MIP(gl);break; 
         case 1: Configurar_DMIP(gl);break; 
         case 2: Configurar_LMIP(gl);break;
         default: break;
        }
     
       cambio=false;
      }
 
     	

     
   gl.glScalef(zoom,zoom,1);
 
   
   
   
     for(int i=1;i<=texturas.length;i++)
       {
    	 gl.glBindTexture(GL2.GL_TEXTURE_2D, texturas[i-1]);
    	 gl.glPushMatrix();
    	 
    	 gl.glRotatef(azimut, 0, 1, 0);
    	 gl.glRotatef(elevacion, 1, 0, 0);
    	 
    	 gl.glTranslatef(-atenuacion_distancia*MIP_.ancho/2.0f+ atenuacion_distancia*zoomFogX, -atenuacion_distancia*MIP_.alto/2.0f+ atenuacion_distancia*zoomFogY, -atenuacion_distancia*texturas.length*deltaZ/2+ atenuacion_distancia*zoomFogZ);
     	
    	   gl.glBegin(GL2.GL_QUADS);
    	 
       	   	 gl.glTexCoord2f(0,0); gl.glVertex3f(0, 0,atenuacion_distancia*i*deltaZ);
       	     gl.glTexCoord2f(1,0); gl.glVertex3f(atenuacion_distancia*MIP_.ancho, 0,atenuacion_distancia*i*deltaZ);
        	 gl.glTexCoord2f(1,1); gl.glVertex3f(atenuacion_distancia*MIP_.ancho, atenuacion_distancia*MIP_.alto,atenuacion_distancia*i*deltaZ);
         	 gl.glTexCoord2f(0,1); gl.glVertex3f(0, atenuacion_distancia*MIP_.alto,atenuacion_distancia*i*deltaZ);
   	       	   	 
    	   gl.glEnd();
    	 
    	 gl.glPopMatrix();
       }
   
   

     //EJES DE AYUDA
     
     gl.glDisable(GL2.GL_BLEND);
    // gl.glDisable(GL2.GL_FOG);
     
     gl.glRotatef(azimut, 0, 1, 0);
	 gl.glRotatef(elevacion, 1, 0, 0);
     gl.glColor3f(0.5f, 0.5f, 0);
     gl.glBegin(GL2.GL_LINES);
       gl.glVertex3f(-atenuacion_distancia*MIP_.ancho/2, 0, 0);
       gl.glVertex3f( atenuacion_distancia*MIP_.ancho/2,0 , 0);
       
       gl.glVertex3f(0,-atenuacion_distancia*MIP_.alto/2, 0);
       gl.glVertex3f(0, atenuacion_distancia*MIP_.alto/2, 0);
       
       gl.glVertex3f(0, 0, -atenuacion_distancia*deltaZ*texturas.length/2);
       gl.glVertex3f(0, 0, atenuacion_distancia*deltaZ*texturas.length/2);
     gl.glEnd();
     
     gl.glEnable(GL2.GL_BLEND);
     
     
   
     
    //gl.glFlush();	 
     
    }
    public void displayChanged(GLAutoDrawable gLDrawable, 
      boolean modeChanged, boolean deviceChanged) {
    }
    public void reshape(GLAutoDrawable gLDrawable, int x, 
    int y, int width, int height) 
    {
         gl=(GL2) gLDrawable.getGL();
    	
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode ( GL2.GL_PROJECTION );
        gl.glLoadIdentity ();
        gl.glOrtho (-atenuacion_distancia*MIP_.ancho/2.0f, atenuacion_distancia*MIP_.ancho/2.0f, -atenuacion_distancia*MIP_.alto/2.0f, atenuacion_distancia*MIP_.alto/2.0f,-atenuacion_distancia*deltaZ*texturas.length,atenuacion_distancia*deltaZ*texturas.length);
       
        gl.glMatrixMode ( GL2.GL_MODELVIEW );
        gl.glLoadIdentity ();
        
               
    }


	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}




   
    
    
    
  
    
    
    
 }