package multiDimVisualization;

import java.util.Vector;

/**
 * Preprocesamiento para generar la textura que se graficar�
 * 
 * @author USUARIO
 *
 */
public class Classifier 
 {
  //Primer intento: segmentacion por umbrales: Para cada intervalo 
  // alto-bajo corresponde un valor RGBA
  private Vector<TransferFunction> TransferFs; //Funciones de transferencia trapezoidales simples
  //M�TODOS***************************************************************

  //----------------------------------------------------------------------
  Classifier()
  {
   TransferFs=new Vector<TransferFunction>();
  }
 
/**
 * Classification in function of intensity and channel
 * @param voxel
 * @param c
 * @return
 */
public float[] Clasificar(short intensity, int c) 
{
  float[]result={0,0,0,0};
  float[]aux={0,0,0,0};
  
 //this accumulate the impact of each TF on the (intensity,channel) value. The same channel values aren't summ, but
  // it takes the grater value obteined from Transfer. This method can use any TF that take intensity-chanel value and
  // returns the rgba result
  for(int i=0;i<TransferFs.size();i++)        
	{
	  aux=TransferFs.elementAt(i).ApplyTransfer(intensity, c);
	  
	  if(aux[0]>result[0])result[0]=aux[0];
	  if(aux[1]>result[1])result[1]=aux[1];
	  if(aux[2]>result[2])result[2]=aux[2];
	  if(aux[3]>result[3])result[3]=aux[3];
	}

  return result;

}

public void Reset() 
{

TransferFs.clear();
	
}

public void SetNtf(int ntf) 
{
TransferFs.setSize(ntf);	
}
  
public int GetNtf(){return TransferFs.size();}

public TransferFunction GetTf(int i) {
	return TransferFs.get(i);
}

public void SetTf(TransferFunction t, int i) 
{
 TransferFs.set(i, t);
	
}
	
 }



