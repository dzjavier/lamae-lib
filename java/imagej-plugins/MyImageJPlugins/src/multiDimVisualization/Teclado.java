package multiDimVisualization;



import ij.IJ;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Teclado implements KeyListener {

public void keyPressed(KeyEvent e) {
    	
    	int key=e.getKeyCode();
    	
    	switch(key)
    	{
    	
    	case KeyEvent.VK_RIGHT: 
    	Display.Azimut+=5;
    	Ventana.Actualizar(Display.Azimut, Display.Elevation);
    	break;
    	
    	case KeyEvent.VK_LEFT:  
    	Display.Azimut-=5;
    	Ventana.Actualizar(Display.Azimut, Display.Elevation)
    	;
        break;
    	
    	case KeyEvent.VK_UP:    Display.Elevation+=5; 
    	Ventana.Actualizar(Display.Azimut, Display.Elevation);
    	break;
    	case KeyEvent.VK_DOWN:  
    		Ventana.Actualizar(Display.Azimut, Display.Elevation);
        	Display.Elevation-=5; break;
      
    							
    	//transformaciones sobre el panel e imagen seleccionada						
    	case KeyEvent.VK_A: Display.Zoom+=0.1;break;
    	case KeyEvent.VK_Z: Display.Zoom-=0.1;break;
    	
    	//CAMBIAMOS PARAMETROS INICIO Y FIN DE FOG
    	
    	case KeyEvent.VK_X: Display.FogLinearEnd--;Display.FogChange=true;break;
    	case KeyEvent.VK_C: Display.FogLinearEnd++;Display.FogChange=true;break;
    	
  
    	//modifica deltaZ
    	case KeyEvent.VK_L: 
    		D5Visualizer_.Vra.dz=(float)IJ.getNumber("dz", D5Visualizer_.Vra.dz);
    		D5Visualizer_.Vra.dy=(float)IJ.getNumber("dy", D5Visualizer_.Vra.dy);
    		D5Visualizer_.Vra.dx=(float)IJ.getNumber("dx", D5Visualizer_.Vra.dx);
    	    GeometricCalc.DimensionUpdate();
    		break;
    	
        	
    	case KeyEvent.VK_SPACE: 
    		Display.Reset=true;
    	break;
    	
    	case KeyEvent.VK_K: Display.Azimut=(int) IJ.getNumber("azimut",Display.Azimut );break;
    	case KeyEvent.VK_J: Display.Elevation=(int) IJ.getNumber("elevacion",Display.Elevation );break;
    	
    	case KeyEvent.VK_0:Display.FogMode=0;Display.FogChange=true;break;
    	case KeyEvent.VK_1:Display.FogMode=1;Display.FogChange=true;break;
    	case KeyEvent.VK_2:Display.FogMode=2;Display.FogChange=true;break;
    	case KeyEvent.VK_3:Display.FogMode=3;Display.FogChange=true;break;
    	
   
    	case KeyEvent.VK_T: //TF
    		
    		D5Visualizer_.TFuncConfig();
    		 
    	break;
    		
    	
    	
    	default: break;
    	}
    	
    	
       
    }
    public void keyReleased(KeyEvent e) {
    }
    public void keyTyped(KeyEvent e) {
    }
    

}
