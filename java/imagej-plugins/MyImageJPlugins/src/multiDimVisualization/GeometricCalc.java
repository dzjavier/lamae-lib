package multiDimVisualization;


public class GeometricCalc
{
	  static boolean Interior(float x, float y, float z) 
	    {
		 /*z*=DVR_.Vra.dz;  //including the dimensional factor VRA to the voxel size.
		 y*=DVR_.Vra.dy;
		 x*=DVR_.Vra.dx;
		 
		 
		 boolean valido=true; 
		 for(int i=0;i<ClipPlanes.length;i++)
		  {
			//ax+by+cz+d=0 ;  
		   if(ClipPlanes[i].c!=0.0)  
		    {
			 float zp=-(x*ClipPlanes[i].a/ClipPlanes[i].c+y*ClipPlanes[i].b/ClipPlanes[i].c
					    +ClipPlanes[i].d/ClipPlanes[i].c);
		    
		     if(ClipPlanes[i].c>0 && z>zp) valido=false;
		     if(ClipPlanes[i].c<0 && z<zp) valido=false;
		     
		    }
		  
		  }*/
			return true;
		}
	  
	  static int OptimizeOrientation(float Elevation,float Azimut) 

	  {
		 float[] zp={0,0,1};
		 float[] xp={1,0,0};
		 float[] zn={0,0,-1};
		 float[] xn={-1,0,0};
		 float[] yp={0,1,0};
		 float[] yn={0,-1,0};
		
		 float[] pos={(float) (Math.sin(Azimut*2*Math.PI/360.0f)),
					    (float) (Math.sin(Elevation*2*Math.PI/360.0f)*Math.cos(Azimut*2*Math.PI/360.0f)),
					    (float) (Math.cos(Elevation*2*Math.PI/360.0f)*Math.cos(Azimut*2*Math.PI/360.0f))};
		 
		 //choose the grater point product between the unitary vectors;
		 
		 float prod0=zp[0]*pos[0]+zp[1]*pos[1]+zp[2]*pos[2];
		 float prod1=xp[0]*pos[0]+xp[1]*pos[1]+xp[2]*pos[2];
		 float prod2=zn[0]*pos[0]+zn[1]*pos[1]+zn[2]*pos[2];
		 float prod3=xn[0]*pos[0]+xn[1]*pos[1]+xn[2]*pos[2];
		 float prod4=yp[0]*pos[0]+yp[1]*pos[1]+yp[2]*pos[2];
		 float prod5=yn[0]*pos[0]+yn[1]*pos[1]+yn[2]*pos[2];
		
		 float prod=prod0;
		 int orientation=0;
		 if(prod1>prod)
		  {prod=prod1;orientation=1;}
		 if(prod2>prod)
		  {prod=prod2;orientation=2;}
		 if(prod3>prod)
		  {prod=prod3;orientation=3;}
		 if(prod4>prod)
		  {prod=prod4;orientation=4;}
		 if(prod5>prod)
		  {prod=prod5;orientation=5;}
		 
		 return orientation;
		}
	  
	  static void DimensionUpdate()
		{
	    	Display.MaxDim=(float) Math.sqrt(Display.GWidth*Display.GWidth + Display.GHeight*Display.GHeight+Display.GDepth*Display.GDepth);
		
	    	Display.MaxPix=(float) Math.sqrt(D5Visualizer_.IHeight*D5Visualizer_.IHeight+ D5Visualizer_.IWidth*D5Visualizer_.IWidth +
	                D5Visualizer_.Nz*D5Visualizer_.Nz);
	    	Display.SizeChange=false;
	    	
	    	Display.GWidth=D5Visualizer_.IWidth*D5Visualizer_.Vra.dx;
	    	Display.GHeight=D5Visualizer_.IHeight*D5Visualizer_.Vra.dy;
	    	Display.GDepth=D5Visualizer_.Nz*D5Visualizer_.Vra.dz;
		}	
}
