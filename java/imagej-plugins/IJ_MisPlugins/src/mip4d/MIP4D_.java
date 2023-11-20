package mip4d;
import ij.*;

import ij.gui.GenericDialog;
import ij.process.*;
import ij.plugin.filter.*;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.awt.GLCanvas;

 public class MIP4D_ implements PlugInFilter {
     static boolean oglbQuit = false;
     static boolean ctrlbQuit = false;
     static protected ImagePlus impR;
     static protected ImagePlus impG;
     static protected ImagePlus impB;
     static int ancho;
     static int alto;
     static int cortes;
     static int cuadros;
     static Frame oglframe;
     static GenericDialog ctrlframe;

     public int setup(String arg, ImagePlus imp_nuevo) {
    	 impR = imp_nuevo;
    	 return DOES_8G+STACK_REQUIRED;
    	 }
        
     public void run(ImageProcessor ip) {
    	//impR.hide();
    	 
  		int[] wList = WindowManager.getIDList();
         
  		if (wList == null){
             IJ.noImage();
             return;
         }
         String[] titles = new String[wList.length];
         for (int i = 0; i < wList.length; i++){
             ImagePlus imp = WindowManager.getImage(wList[i]);
             if (imp != null)
                 titles[i] = imp.getTitle();
             else
                 titles[i] = "";
         }
 		String titleImage = Prefs.get("iterativedeconvolve3d.titleImage", titles[0]);
 		int imageChoice = 0;
         for (int i = 0; i < wList.length; i++){
 			if(titleImage.equals(titles[i])){
 				imageChoice = i;
 				break;
 			}
 		}
 		String titlePSF = Prefs.get("iterativedeconvolve3d.titlePSF", titles[0]);
 		int psfChoice = 0;
         for (int i = 0; i < wList.length; i++){
 			if(titlePSF.equals(titles[i])){
 				psfChoice = i;
 				break;
 			}
 		}
         oglframe = new Frame("MIP 4D");
     	 ancho = impR.getWidth();
         alto = impR.getHeight();
     	
         //busca el tama�o minimo para q entren las imagenes
         oglframe.setSize(ancho,alto);
         oglframe.setLocation(100, 100);
         	
 		GenericDialog ctrlframe  = new GenericDialog("Fijar par�metros", IJ.getInstance());
 		ctrlframe.setLocation(500, 500);
 		ctrlframe.addChoice("Canal Verde ",titles,titles[imageChoice]);
        ctrlframe.addChoice("Canal Rojo ",titles,titles[psfChoice]);  
    	
        ctrlframe.addNumericField("Cortes:",impR.getNSlices(),0);
        ctrlframe.addNumericField("Muestras temporales:",impR.getNFrames(),0);
        ctrlframe.addNumericField("Canales:",impR.getNChannels(),0);
        ctrlframe.showDialog();
        
        impR = WindowManager.getImage(wList[ctrlframe.getNextChoiceIndex()]);
        impG = WindowManager.getImage(wList[ctrlframe.getNextChoiceIndex()]);        
        
        if (ctrlframe.wasCanceled()) return;
        
        cortes = (int) ctrlframe.getNextNumber();
        cuadros = (int) ctrlframe.getNextNumber();
        

        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(new GraficadorMIP4D());
        canvas.addKeyListener(new Teclado());
        canvas.addMouseListener(new Mouse());
        canvas.addMouseMotionListener(new MouseMov());
        oglframe.add(canvas);
        
        oglframe.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) 
                           {oglbQuit=true;}
                        } );
      
        oglframe.setVisible(true);
        while(!oglbQuit){
       	 canvas.display();
       	 IJ.wait(100); 
       	 }
       	
        oglframe.dispose();
        ctrlframe.dispose();
        oglbQuit=false;
        ctrlbQuit=false;
        
        //imp.killProcessor();
        //imp.killStack();
        //imp.close();
    //    imp.flush();
        
        //IJ.freeMemory();
       		}
     }