package multiDimVisualization;



public class TransferFunction {

	//Atributos***********************************************************
	 
	//--------------------------------------------------------------------------------------------------------------  
	public short Umbral1,Umbral2;
	public float M1,M2; //m1 y m2 son pendientes de la f. de transferencia 
	
	public float[] ValoresTejido; //campos de RGBA float
	String Tipo;
	int Channel=0; //this is the default value if Nc=1;
	//--------------------------------------------------------------------
	
	//M�todos*************************************************************
	
	//--------------------------------------------------------------------
	public TransferFunction() 
	{
	 Umbral1=0;
	 Umbral2=0;
	 M1=0;
	 M2=0;
	 Channel=0; 
     ValoresTejido=new float[4];
	 ValoresTejido[0]=0;
	 ValoresTejido[1]=0;
	 ValoresTejido[2]=0;
	 ValoresTejido[3]=0;
	}
	
	/**
	 * Transfer function for a rgb or Gray scale image. Each transfer function only have components for one
	 * channel. In the Gray scale case, the rgb components are equal. This function returns the  
	 * values on the resultant vector.
	 * @param intensity
	 * @param c
	 * @return result (RGBA) of applying the TF
	 */
	
	public float[] ApplyTransfer(short intensity,int c) 
	{
	 float[] result={0,0,0,0};	
	
	 if( D5Visualizer_.VisualizationMode==1)	//DVR
	 if(c==Channel) 
	 {
		 if(intensity<Umbral1 && (M1*(intensity-Umbral1)+1)>0)
		 {       
			 if(D5Visualizer_.ClassificationMode==2) //rgb
			 {
			 result[0]=ValoresTejido[0]*(M1*(intensity-Umbral1)+1);
			 result[1]=ValoresTejido[1]*(M1*(intensity-Umbral1)+1);
			 result[2]=ValoresTejido[2]*(M1*(intensity-Umbral1)+1);
			 result[3]=ValoresTejido[3]*(M1*(intensity-Umbral1)+1);
			 }
			 
			 if(D5Visualizer_.ClassificationMode==1) //grayscale
			 {
			 result[0]=intensity/D5Visualizer_.MaxPixelValue;; 
			 result[3]=ValoresTejido[3]*(M1*(intensity-Umbral1)+1);
			 }
			
		 }
		 
		 if(intensity>=Umbral1 && intensity<=Umbral2)
		 {
			 if(D5Visualizer_.ClassificationMode==2) //rgb
			 {
			 result[0]=ValoresTejido[0];
			 result[1]=ValoresTejido[1];
			 result[2]=ValoresTejido[2];
			 result[3]=ValoresTejido[3];
			 }
			 if(D5Visualizer_.ClassificationMode==1) //grayscale
			 {
			  result[0]=intensity/D5Visualizer_.MaxPixelValue;
			  result[3]=ValoresTejido[3];
			 }
			
	     }
		
		 if(intensity>Umbral2 && (M2*(-intensity+Umbral2)+1)>0)
		 {    
			 if(D5Visualizer_.ClassificationMode==2) //rgb
			 {
			 result[0]=ValoresTejido[0]*(M2*(-intensity+Umbral2)+1);
			 result[1]=ValoresTejido[1]*(M2*(-intensity+Umbral2)+1);
			 result[2]=ValoresTejido[2]*(M2*(-intensity+Umbral2)+1);
			 result[3]=ValoresTejido[3]*(M2*(-intensity+Umbral2)+1);
			 }
			 
			 if(D5Visualizer_.ClassificationMode==1) //grayscale
			 {
			 result[0]=intensity/D5Visualizer_.MaxPixelValue;; 
			 result[3]=ValoresTejido[3]*(M2*(-intensity+Umbral2)+1);
			 }
		 }

	 }
	 
	 
	 
	 if( D5Visualizer_.VisualizationMode==0)	//MIP
		 if(c==Channel) 
		 {
		 if(D5Visualizer_.ClassificationMode==1) //grayscale
		 {
		  if(intensity>Umbral1 && intensity<Umbral2)
		   result[0]=intensity/D5Visualizer_.MaxPixelValue;	 
		  else
		   result[0]=0;
		 }
			
		 if(D5Visualizer_.ClassificationMode==2) //RGB
		 {
		  if(intensity>Umbral1 && intensity<Umbral2)
			 {
			 result[0]=ValoresTejido[0]*intensity/D5Visualizer_.MaxPixelValue;
			 result[1]=ValoresTejido[1]*intensity/D5Visualizer_.MaxPixelValue;
			 result[2]=ValoresTejido[2]*intensity/D5Visualizer_.MaxPixelValue;
			 result[3]=1;
			 }
		  else
		  {
			  result[0]=0;
			  result[1]=0;
			  result[2]=0;
			  result[3]=0;  
		  }
		 }
		 
		 }
	 
	 return result;
	}
	
}
