package multiDimVisualization;


import ij.IJ;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author lbugnon
 * 
 * Manage the .config partner file of the image. It saves all the start up configuration and transfer functions data.
 *
 */
public class Archiver
{
  static String SavePath;
  static Properties SavingProperties;
	
  public static boolean SaveProperties(String archive, Properties prop)
  {
	//abrir archivo, sobreescribir properties
	
	  
	 try
	{
		 
		File out=new File(SavePath+archive+".conf");
		FileOutputStream sout=new FileOutputStream(out);
	
		prop.store(sout, "Properties of "+archive+" file");
	
		sout.close();
		
	} catch (IOException e)
	{
		e.printStackTrace();
	}
	
	return true;  
  }

  public static boolean LoadProperties(String archive, Properties prop)
  {
	//Try to adquire the path for this image name. If doesn't exists, ask.
	SavingProperties=new Properties();
	  try
		{
		 File in=new File("5DVIsualizer.conf");
		 if(in.exists())
		   {
//			SavePath=IJ.getString("Saving Directory Path", "'/home/user/...' or 'C:/Documents and settings/...");
//		   }
//		 else
//		  {
		   FileInputStream fis = new FileInputStream(in);
		   SavingProperties.load(fis);
		  }
		 }
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}  
	  
		SavePath="like 'c:/Documents and settings/'";
		if(SavingProperties.getProperty("SavePath")!=null) //path found
		 SavePath=SavingProperties.getProperty("SavePath");
		

		 SavePath=IJ.getString("Saving Directory Path", SavePath);
		 SavingProperties.put("SavePath", SavePath);
		
		//saving the path...
		 try
			{
			 File out=new File("5DVIsualizer.conf");
			 FileOutputStream sout=new FileOutputStream(out);
			 SavingProperties.store(sout, "Saving path's for 5DVisualizer");
			 sout.close();
				
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		
		
	//try to adquire properties for image rendering
	try
	{
	 File in=new File(SavePath+archive+".conf");
	 if(!in.exists())
	   return false;

	 FileInputStream fis = new FileInputStream(in);
	 prop.load(fis);
	
	 }
	catch (FileNotFoundException e)
	{
		e.printStackTrace();
	} catch (IOException e)
	{
		e.printStackTrace();
	}
  
	   
	return true;  
  }
/**MAGIC----DON'T TOUCH*/
public static void LoadTexture(float[] image)
{
	
	 short voxel;
 	 float[] voxelClasificado=new float[4];
 	 
 	 
 	 switch(D5Visualizer_.StackOrder)
 	 {
 	 case 0: //FORMATO XYCZT
 	 
 	 for(int z=0;z<D5Visualizer_.Nz;z++)
 	  for(int c=0;c<D5Visualizer_.Nc;c++)
 	  { 
 	   IJ.getImage().setSlice((Display.TActual-1)*D5Visualizer_.Nz*D5Visualizer_.Nc +D5Visualizer_.Nc*z+c+1);
 	  
 	   for(int y=0;y<D5Visualizer_.IHeight;y++)
 	    for(int x=0;x<D5Visualizer_.IWidth;x++)
 	      if(GeometricCalc.Interior(x,y,z))
  	  	  { 
 	    	voxel=(short) IJ.getImage().getProcessor().getPixel(x, y);
 	    	voxelClasificado=D5Visualizer_.Classifier.Clasificar(voxel,c);	
 			 	    	
 			  switch(D5Visualizer_.ClassificationMode)
 			  {
 			  //No classifying
 			  case 0:
 				 if(voxel/D5Visualizer_.MaxPixelValue> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*2+y*D5Visualizer_.IWidth*2+x*2])
 				     image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*2+y*D5Visualizer_.IWidth*2+x*2]=voxel/D5Visualizer_.MaxPixelValue;//(float) (voxel)/DVR_.ValMaximo;   //L	
 			 
 			  break;
 				  
 			  //GrayScale
 			  case 1:
 				 if(voxelClasificado[0]> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*2+y*D5Visualizer_.IWidth*2+x*2])
 				     image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*2+y*D5Visualizer_.IWidth*2+x*2]=voxelClasificado[0];//(float) (voxel)/DVR_.ValMaximo;   //L	
 			
 				 if(voxelClasificado[3]> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*2+y*D5Visualizer_.IWidth*2+x*2+1])	
 	    			 image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*2+y*D5Visualizer_.IWidth*2+x*2+1]=voxelClasificado[3]; //A
 			  break;
 			  //RGBA
 			  case 2:
 				  if(voxelClasificado[0]> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4])
 					  image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4]=voxelClasificado[0];   //R		
 					  
 				  if(voxelClasificado[1]> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+1])
 					  image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+1]=voxelClasificado[1];   //R		
 						  
 				  if(voxelClasificado[2]> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+2])
 					  image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+2]=voxelClasificado[2];   //R	
 				
 				  if(voxelClasificado[3]>image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+3])
 					  image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+3]=voxelClasificado[3]; //A		
 		      break;
 			  }
 			}
 	  
      } 
 	 break;
 	 case 1: //XYZCT 
 		for(int c=0;c<D5Visualizer_.Nc;c++)
 		 for(int z=0;z<D5Visualizer_.Nz;z++)
 		 	  { 
 		 	   IJ.getImage().setSlice((Display.TActual-1)*D5Visualizer_.Nz*D5Visualizer_.Nc +D5Visualizer_.Nz*c+z+1);
 		 	  
 		 	   for(int y=0;y<D5Visualizer_.IHeight;y++)
 		 	    for(int x=0;x<D5Visualizer_.IWidth;x++)
 		 	      if(GeometricCalc.Interior(x,y,z))
 		  	  	  { 
 		 			voxel=(short) IJ.getImage().getProcessor().getPixel(x, y);
 		 		    
 		 			  switch(D5Visualizer_.ClassificationMode)
 		 			  {
 		 			  //No classifying
 		 			  case 0:
 		 				 if(voxel/D5Visualizer_.MaxPixelValue> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth+y*D5Visualizer_.IWidth+x])
 		 				     image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth+y*D5Visualizer_.IWidth+x]=voxel/D5Visualizer_.MaxPixelValue;   //LUMINANCE	
 		 			  break;
 		 			  
 		 			 //GrayScale
 		 			  case 1:
 		 				voxelClasificado=D5Visualizer_.Classifier.Clasificar(voxel,c);	
 		 				 if(voxelClasificado[0]> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*2+y*D5Visualizer_.IWidth*2+x*2])
 		 				     image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*2+y*D5Visualizer_.IWidth*2+x*2]=voxelClasificado[0];   //L	
 		 			
 		 				 if(voxelClasificado[3]> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*2+y*D5Visualizer_.IWidth*2+x*2+1])	
 		 	    			 image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*2+y*D5Visualizer_.IWidth*2+x*2+1]=voxelClasificado[3]; //A
 		 			  break;
 		 			  //RGBA
 		 			  case 2:
 		 				  voxelClasificado=D5Visualizer_.Classifier.Clasificar(voxel,c);	
 		 				  if(voxelClasificado[0]> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4])
 		 					  image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4]=voxelClasificado[0];   //R		
 		 					  
 		 				  if(voxelClasificado[1]> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+1])
 		 					  image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+1]=voxelClasificado[1];   //R		
 		 						  
 		 				  if(voxelClasificado[2]> image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+2])
 		 					  image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+2]=voxelClasificado[2];   //R	
 		 				
 		 				  if(voxelClasificado[3]>image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+3])
 		 					  image[z*D5Visualizer_.IHeight*D5Visualizer_.IWidth*4+y*D5Visualizer_.IWidth*4+x*4+3]=voxelClasificado[3]; //A		
 		 		      break;
 		 			  }
 		 			}
 		 	  
 		      } 
 	 break;
 	 case 2: //XYZTC//TODO ARREGLAR
 	 break;
 	 
 	 }
    }
	
}
