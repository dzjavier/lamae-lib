package multiDimVisualization;


import java.awt.Dimension;
import java.util.Properties;

import javax.media.opengl.GLProfile;


import ij.*;
import ij.gui.GenericDialog;
import ij.process.*;
import ij.plugin.filter.*;


 public class D5Visualizer_ implements PlugInFilter 
 {
	 static
	 {
		 GLProfile.initSingleton(true);
	 }
	
	 
  //Atributos*****************************************************************************************************
	public static Classifier Classifier;
  //Variables de imagen-------------------------------------------------------------------------------------------
    public static int IWidth;
    public static int IHeight;
  //  public static Dimension IDim;
    
    public class VRA{float dx,dy,dz;}; //voxel ratio aspect (relative size of a voxel);  
    public static VRA Vra;
    public static int Nz; //number of silices
    public static int Nc; //number of channels.
    public static int Nt; //number of frames
    /**
     *  Image order in the stack. 0-XYCZT 1-XYZCT 2-XYZTC
     */
    public static int StackOrder;
    private static String imageName;
    /**
     *  Define reconstruction method; 0-MIP 1-DVR
     */
    static int VisualizationMode;
    /**
     *  Define segmentation method; 0-don't classify 1-original Gray scale 2-RGB
     */
    static int ClassificationMode;  
    /** Contains all the init configuration of the rendered image (classification mode, volume ratio aspect, etc) 
     *  and all the transfer functions that users defined in the last session. 
     */
    private static Properties imageProperties;
    
    public static float MaxPixelValue; 
    
    public int setup(String arg, ImagePlus imp_nuevo)
    {
    	
    	//JVM VERSION
    	 String version=System.getProperty("java.version");
         //version JVM minima = 1.6
          if( version.charAt(0)<1 || (version.charAt(0)==1 && version.charAt(2) <6))
          {
          version+="\n Old Java JRE Version";	
          IJ.showMessage(version);System.exit(0);
          }	
      /* 	 //IJ VERSION //TODO
          System.out.println(IJ.getVersion());*/
       
        Classifier=new Classifier();
        Vra=new VRA();
        
        //properties init
        imageProperties=new Properties();
        imageProperties.put("dx", "1");
        imageProperties.put("dy", "1");
        imageProperties.put("dz", "1");
        imageProperties.put("nz", "1");
        imageProperties.put("nc", "1");
        imageProperties.put("nt", "1");
        imageProperties.put("visualizMode", "1");
        imageProperties.put("classifMode", "1");
        imageProperties.put("StackOrder", "1"); //TODO 0-XYCZT 1-XYZCT 2-XYZTC
     
        
        imageProperties.put("ntf", "1");
        imageProperties.put("1u1", "0");
        imageProperties.put("1u2", "0");
        imageProperties.put("1sl1", "0");
        imageProperties.put("1sl2", "0");
        imageProperties.put("1r", "0");
        imageProperties.put("1g", "0");
        imageProperties.put("1b", "0");
        imageProperties.put("1a", "0");
        imageProperties.put("1ch", "0");
        
        imageProperties.put("zoom", "1");
        imageProperties.put("azimut", "0");
        imageProperties.put("elevation", "0");
        imageProperties.put("fogMode", "0");
        imageProperties.put("fogDensity", "0");
        imageProperties.put("fogEnd", "0");
        imageProperties.put("interpMode", "0");	
    	
        imageName=IJ.getImage().getTitle();
        IWidth=IJ.getImage().getWidth();
        IHeight=IJ.getImage().getHeight();
                	
        return DOES_ALL;

    }
    //-------------------------------------------------------------------------------------------------------------- 
    
    //-------------------------------------------------------------------------------------------------------------- 
      public void run(ImageProcessor ip)
      {
       Archiver.LoadProperties(imageName, imageProperties);
       importProperties();	   
      	
       Ventana ventana=new Ventana();
       Inicializar();
       ventana.CrearVentana(imageName,new Dimension(IWidth,IHeight));
      }

      static void endRun()
      {
    	  exportProperties();
          Archiver.SaveProperties(imageName, imageProperties);
          
         
        //TODO:   Runtime.getRuntime().runFinalization();
           
      }
  //--------------------------------------------------------------------------------------------------------------

  //--------------------------------------------------------------------------------------------------------------
    private void Inicializar()
    {  	
     ConfigDialog();
    //setup de clasificador
    if(ClassificationMode!=0)
     TFuncConfig();
 
  
 /*   if(IJ.getImage().getBytesPerPixel()==1)
     MaxPixelValue=(float) 256;
    if(IJ.getImage().getBytesPerPixel()==2)
     MaxPixelValue=(float) 256*256;*/
    
    MaxPixelValue=(float)IJ.getImage().getProcessor().getMax();
          
    /* //clipplanes, prueba
     Display.ClipPlanes=new PlaneCoord [1];
     Display.ClipPlanes[0]=new PlaneCoord();
     
     Display.ClipPlanes[0].xo=0;   //Asume that xo,yo,zo are the real sizes(voxel*vra)
     Display.ClipPlanes[0].yo=0;
     Display.ClipPlanes[0].zo=(int) (Nz*DVR_.Vra.dz);
     Display.ClipPlanes[0].a=0f;
     Display.ClipPlanes[0].b=0f;
     Display.ClipPlanes[0].c=1f;
     Display.ClipPlanes[0].d=-(Display.ClipPlanes[0].xo*Display.ClipPlanes[0].a+Display.ClipPlanes[0].yo*Display.ClipPlanes[0].b
                          +Display.ClipPlanes[0].zo*Display.ClipPlanes[0].c);  
     */
    /* 
    if(Nc==1) 
    {//hueso
	     Classifier.CargarTejido((short)1500,(short)2500,0.005f,0.01f,0.7f,0.7f,0.0f,0.8f,0);
	    //sangre
	     Classifier.CargarTejido((short)1200,(short)1280,0.3f,0.3f,0.9f,0.0f,0.0f,0.9f,0);
	  
    }  
    else
    {
	     //Canal rojo                 // A     B       M1  M2 R  G    B    A   c
	     Classifier.CargarTejido((short)40,(short)250,0.1f,1f,0.5f,0.0f,0.0f,0.02f,0);
	     //Canal Verde
	     Classifier.CargarTejido((short)60,(short)250,0.1f,1f,0.0f,0.5f,0.0f,0.02f,1);
    }    
    */
   /*  
     //otro plano
     
     Display.CoordPlanos[6]=200;
     Display.CoordPlanos[7]=200;
     Display.CoordPlanos[8]=5;
     Display.CoordPlanos[9]=0.2f;
     Display.CoordPlanos[10]=0.2f;
     Display.CoordPlanos[11]=-2f;
     */
    }
    
  public static void TFuncConfig() 
  {
	Ventana.Animator.pause();
	GenericDialog  dialog=new GenericDialog("Classifier configuration");
	
	dialog.addMessage("This configuration window define a \n set of N simple transfer functions \n to a better volume visualization");
	//dialog.addHelp("www.ayuda.com"); //TODO esto puede ser util
	
	dialog.addNumericField("Number of Transfer Functions", Classifier.GetNtf(), 1);
	
	dialog.showDialog();
	
	int Ntf=(int)dialog.getNextNumber();

	//CLassifier backup
	TransferFunction[] tfs=new TransferFunction[Ntf];
	for(int i=0;i<Ntf;i++)
		{
		if(Classifier.GetNtf()>i)
		 tfs[i]=Classifier.GetTf(i);
		else
		 tfs[i]=new TransferFunction();
		}
	Classifier.Reset();
	Classifier.SetNtf(Ntf);
	
	
	for(int i=0;i<Ntf;i++)
	{
	 dialog=new GenericDialog("Transfer Function"+ (i+1));
	  dialog.setAlwaysOnTop(false);	
		
	switch(VisualizationMode)
	{
	  case 0: //MIP// This only make threshold segmentation without attenuation.
	   dialog.addNumericField("Umbral 1", tfs[i].Umbral1, 4);
	   dialog.addNumericField("Umbral 2",  tfs[i].Umbral2, 4);
	
	   if(ClassificationMode==2) //RGB
	   {
		dialog.addNumericField("Ouput:red", tfs[i].ValoresTejido[0], 3);
		dialog.addNumericField("Ouput:green", tfs[i].ValoresTejido[1], 3);
		dialog.addNumericField("Ouput:blue", tfs[i].ValoresTejido[2], 3);   
	   }
	   
	   dialog.addNumericField("Input Channel", tfs[i].Channel, 1);   
		
	   
	   
	   dialog.showDialog();
	  	  
		  tfs[i].Umbral1=(short) dialog.getNextNumber();
		  tfs[i].Umbral2=(short) dialog.getNextNumber();
		  if(ClassificationMode==2)
		  {
		  tfs[i].ValoresTejido[0]=(float) dialog.getNextNumber();
		  tfs[i].ValoresTejido[1]=(float) dialog.getNextNumber();
		  tfs[i].ValoresTejido[2]=(float) dialog.getNextNumber();
		  }
		/*  if(ClassificationMode==1) //grayscale
		  {
		  tfs[i].ValoresTejido[0]=1;
		  tfs[i].ValoresTejido[1]=1;
		  tfs[i].ValoresTejido[2]=1;  
		  }*/
		  tfs[i].M1=1;
		  tfs[i].M2=1;
		  tfs[i].Channel=(int) dialog.getNextNumber();
	   
	  break;
	  
	  case 1://DVR
		  
		  dialog.addNumericField("Umbral 1", tfs[i].Umbral1, 4);
		  dialog.addNumericField("Umbral 2",  tfs[i].Umbral2, 4);
		  dialog.addNumericField("Slope 1",  tfs[i].M1, 3);
		  dialog.addNumericField("Slope 2", tfs[i].M2, 3);
		  if(ClassificationMode==2)
		  {
		   dialog.addNumericField("Ouput:red", tfs[i].ValoresTejido[0], 3);
		   dialog.addNumericField("Ouput:green", tfs[i].ValoresTejido[1], 3);
		   dialog.addNumericField("Ouput:blue", tfs[i].ValoresTejido[2], 3);
		  }
		 
		   dialog.addNumericField("Ouput:alpha", tfs[i].ValoresTejido[3], 3);
		   dialog.addNumericField("Input Channel", tfs[i].Channel, 1);
		   
		   dialog.showDialog();
		  	  
			  tfs[i].Umbral1=(short) dialog.getNextNumber();
			  tfs[i].Umbral2=(short) dialog.getNextNumber();
			  tfs[i].M1=(float) dialog.getNextNumber();
			  tfs[i].M2=(float) dialog.getNextNumber();
			  if(ClassificationMode==2)
			  {
			  tfs[i].ValoresTejido[0]=(float) dialog.getNextNumber();
			  tfs[i].ValoresTejido[1]=(float) dialog.getNextNumber();
			  tfs[i].ValoresTejido[2]=(float) dialog.getNextNumber();
			  }
			  tfs[i].ValoresTejido[3]=(float) dialog.getNextNumber();
			  tfs[i].Channel=(int) dialog.getNextNumber();
		   
		   
	  break;
		
		
	}
	
	  Classifier.SetTf(tfs[i],i);
		
	 }
	Display.Actualize();
	
	
/*	for(int i=0;i<Classifier.GetNtf();i++)
	{
	 System.out.println("u1: "+Classifier.GetTf(i).Umbral1+"u2: "+Classifier.GetTf(i).Umbral2+"c: "+Classifier.GetTf(i).Channel);	
		
	}*/
	
	
	Ventana.Animator.resume();
   }

private void ConfigDialog() 
  {
 	  GenericDialog  dialog = new GenericDialog("Configuration");

 	  	String[] s=new String[2];
 	  	s[0]="MIP";
 	  	s[1]="DVR";
 	  	dialog.addChoice("Visualization Mode", s, s[VisualizationMode]);     
 	  	
	     dialog.addMessage("Volume Ratio Aspect:");
	  
	  	 dialog.addNumericField("dx", Vra.dx, 1);
	     dialog.addNumericField("dy", Vra.dy, 1);
	     dialog.addNumericField("dz", Vra.dz, 1);
	     
	     dialog.addMessage("");
	     dialog.addNumericField("Number of Slices (Z)", Nz, 0);
		 dialog.addNumericField("Number of Chanels (C)", Nc, 0);
		 dialog.addNumericField("Number of Frames (T)", Nt, 0);
	  	
		 s=new String[3]; //here we can choose the classifying method
	 	 s[0]="XYCZT";
	 	 s[1]="XYZCT";
	 	 s[2]="XYZTC";
	 	 
		 dialog.addChoice("Stack order", s, s[StackOrder]);
		 
		 s=new String[3]; //here we can choose the pixel classifying method
		 
		 s[0]="Don't classify";
	 	 s[1]="GrayScale Classifying";
	 	 s[2]="RGB Classifying";
	 	
	 	 dialog.addChoice("Classification task", s, s[ClassificationMode]);
	 	 
	 //	 dialog.addStringField("Configuration file path", "")
	 	 
	 	 dialog.setAlwaysOnTop(false);
	 	 dialog.showDialog();
	    
	 	 VisualizationMode=dialog.getNextChoiceIndex();
	  	 Vra.dx=(float) dialog.getNextNumber();
	  	 Vra.dy=(float) dialog.getNextNumber();
	  	 Vra.dz=(float) dialog.getNextNumber();
	     Nz=(int)dialog.getNextNumber();
	     Nc=(int)dialog.getNextNumber();
	     Nt=(int)dialog.getNextNumber();
	     StackOrder=dialog.getNextChoiceIndex();
	    
	     ClassificationMode=dialog.getNextChoiceIndex();
	     
	     
	     if(dialog.wasCanceled()) //TODO VERIFICAR
	    	 Runtime.getRuntime().exit(0);
	    	
   } 
 
    
    
private void importProperties()
{
	//General values
	Vra.dx= Float.valueOf(imageProperties.getProperty("dx"));
	Vra.dy= Float.valueOf(imageProperties.getProperty("dy"));
	Vra.dz= Float.valueOf(imageProperties.getProperty("dz"));
	Nz= Integer.valueOf(imageProperties.getProperty("nz"));
	Nc= Integer.valueOf(imageProperties.getProperty("nc"));
	Nt= Integer.valueOf(imageProperties.getProperty("nt"));
	StackOrder= Integer.valueOf(imageProperties.getProperty("StackOrder") ); //TODO /XYCZT / XYZCT / XYZTC
	   
	VisualizationMode=Integer.valueOf(imageProperties.getProperty("visualizMode"));
	ClassificationMode=Integer.valueOf(imageProperties.getProperty("classifMode"));
	
	
	//Transfer Functions
	Classifier.SetNtf(Integer.valueOf(imageProperties.getProperty("ntf")) );
	
	TransferFunction t;
	
	for(int i=0;i<Classifier.GetNtf();i++)
	{
		t=new TransferFunction();
		t.Umbral1=Short.valueOf(imageProperties.getProperty(String.valueOf(i+1)+"u1"));
		t.Umbral2=Short.valueOf(imageProperties.getProperty(String.valueOf(i+1)+"u2"));
		t.M1=Float.valueOf(imageProperties.getProperty(String.valueOf(i+1)+"sl1"));
		t.M2=Float.valueOf(imageProperties.getProperty(String.valueOf(i+1)+"sl2"));
		t.ValoresTejido[0]=Float.valueOf(imageProperties.getProperty(String.valueOf(i+1)+"r"));
		t.ValoresTejido[1]=Float.valueOf(imageProperties.getProperty(String.valueOf(i+1)+"g"));
		t.ValoresTejido[2]=Float.valueOf(imageProperties.getProperty(String.valueOf(i+1)+"b"));
		t.ValoresTejido[3]=Float.valueOf(imageProperties.getProperty(String.valueOf(i+1)+"a"));
		t.Channel=Integer.valueOf(imageProperties.getProperty(String.valueOf(i+1)+"ch"));
		
		Classifier.SetTf(t, i);
	}
	
	//display atributes
	
	Display.Zoom=Float.valueOf(imageProperties.getProperty("zoom"));
	Display.Azimut=Integer.valueOf(imageProperties.getProperty("azimut"));
	Display.Elevation=Integer.valueOf(imageProperties.getProperty("elevation"));
	Display.FogMode=Integer.valueOf(imageProperties.getProperty("fogMode"));
	Display.FogExpDensity=Float.valueOf(imageProperties.getProperty("fogDensity"));
	Display.FogLinearEnd=Integer.valueOf(imageProperties.getProperty("fogEnd"));
	Display.InterpolationMode=Integer.valueOf(imageProperties.getProperty("interpMode"));
	
  //cliplanes save:
}

private static void exportProperties()
{
	imageProperties.clear();
	
    imageProperties.put("dx", String.valueOf(Vra.dx));
    imageProperties.put("dy", String.valueOf(Vra.dy));
    imageProperties.put("dz", String.valueOf(Vra.dz));
    imageProperties.put("nz", String.valueOf(Nz));
    imageProperties.put("nc", String.valueOf(Nc));
    imageProperties.put("nt", String.valueOf(Nt));
    imageProperties.put("classifMode", String.valueOf(ClassificationMode));
    imageProperties.put("ntf", String.valueOf(Classifier.GetNtf()));
    imageProperties.put("StackOrder", String.valueOf(StackOrder)); //TODO /XYCZT / XYZCT / XYZTC
    imageProperties.put("visualizMode", String.valueOf(VisualizationMode));
    //Transfer function save:
    for(int i=0;i<Classifier.GetNtf();i++)
    {
    	imageProperties.put(String.valueOf(i+1)+"u1",String.valueOf(Classifier.GetTf(i).Umbral1) );
        imageProperties.put(String.valueOf(i+1)+"u2", String.valueOf(Classifier.GetTf(i).Umbral2));
        imageProperties.put(String.valueOf(i+1)+"sl1", String.valueOf(Classifier.GetTf(i).M1));
        imageProperties.put(String.valueOf(i+1)+"sl2", String.valueOf(Classifier.GetTf(i).M2));
        imageProperties.put(String.valueOf(i+1)+"r", String.valueOf(Classifier.GetTf(i).ValoresTejido[0]));
        imageProperties.put(String.valueOf(i+1)+"g", String.valueOf(Classifier.GetTf(i).ValoresTejido[1]));
        imageProperties.put(String.valueOf(i+1)+"b", String.valueOf(Classifier.GetTf(i).ValoresTejido[2]));
        imageProperties.put(String.valueOf(i+1)+"a", String.valueOf(Classifier.GetTf(i).ValoresTejido[3]));
        imageProperties.put(String.valueOf(i+1)+"ch", String.valueOf(Classifier.GetTf(i).Channel));	
    }
    
    //display atributes

    imageProperties.put("zoom", String.valueOf(Display.Zoom));
    imageProperties.put("azimut", String.valueOf(Display.Azimut));
    imageProperties.put("elevation", String.valueOf(Display.Elevation));
    imageProperties.put("fogMode", String.valueOf(Display.FogMode));
    imageProperties.put("fogDensity", String.valueOf(Display.FogExpDensity));
    imageProperties.put("fogEnd", String.valueOf(Display.FogLinearEnd));
    imageProperties.put("interpMode", String.valueOf(Display.InterpolationMode));
     
  //cliplanes save:
    
}
     
 
  
 }