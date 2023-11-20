
import ij.*;
import ij.process.*;
import ij.plugin.filter.*;
  
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.awt.GLCanvas;




 public class MIP_ implements PlugInFilter {
     static boolean bQuit = false; 
                                   
     static protected ImagePlus imp;
     
     
     static int ancho;
     static int alto;
     
     static Frame frame;

     public int setup(String arg, ImagePlus imp_nuevo) {
    	 imp = imp_nuevo;
    	 return DOES_8G;
    	 }
        
     public void run(ImageProcessor ip) {
    	
    	//oculta ventana de imagen (bastante molesta)
 	   	 imp.hide();
    	
    	 String Nombre=imp.getTitle();
    	
    	frame = new Frame(Nombre);
        
    	
    	
    	//todas las imagenes del stack tienen
    	//el mismo tamaño
        ancho=imp.getWidth();
        alto=imp.getHeight();
    	//busca el tamaño minimo para q entren las imagenes
        
        
        
    	frame.setSize(ancho,alto);
      
      
        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(new Graficador());
        canvas.addKeyListener(new Teclado());
        canvas.addMouseListener(new Mouse());
        canvas.addMouseMotionListener(new MouseMov());
        
        frame.add(canvas);

     
        frame.addWindowListener(new WindowAdapter() 
                        {
                         public void windowClosing(WindowEvent e) 
                           {bQuit=true;}
                        }
                       );


        frame.setVisible(true);
        canvas.requestFocus();
    
        
       
     
        while( !bQuit ) 
       	 canvas.display();
       	 
       
        
        frame.dispose();
        bQuit=false; 
        
        
        //imp.killProcessor();
        imp.killStack();
        imp.close();
    //    imp.flush();
        
    //   IJ.freeMemory();
		}
     
 
  
 }