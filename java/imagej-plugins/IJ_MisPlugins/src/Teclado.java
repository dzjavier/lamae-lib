 import ij.IJ;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class Teclado implements KeyListener {

public void keyPressed(KeyEvent e) {
    	
    	int key=e.getKeyCode();
    	
    	switch(key)
    	{
    	case KeyEvent.VK_ESCAPE: MIP_.bQuit = true;break;
    	
    	
      //mueven recuadro marcador	
    	case KeyEvent.VK_RIGHT: Graficador.azimut+=5; break;
    	case KeyEvent.VK_LEFT:  Graficador.azimut-=5; break;
    	case KeyEvent.VK_UP:    Graficador.elevacion+=5; break;
    	case KeyEvent.VK_DOWN:  Graficador.elevacion-=5; break;
    							
    	//transformaciones sobre el panel e imagen seleccionada						
    	case KeyEvent.VK_A: Graficador.zoom+=0.1;break;
    	case KeyEvent.VK_Z: Graficador.zoom-=0.1;break;

    	//cambios
    	case KeyEvent.VK_0: Graficador.cambio=true;
    						Graficador.MIP=0;break;
    	case KeyEvent.VK_1: Graficador.cambio=true;
    						//por defecto
    						Graficador.modo_fog=0;
    						Graficador.MIP=1;break;
    	case KeyEvent.VK_2: Graficador.cambio=true;
							Graficador.MIP=2;break;
    	
    	//acercamiento con fog
		
    	case KeyEvent.VK_S: Graficador.zoomFogZ++;break;
    	case KeyEvent.VK_X: Graficador.zoomFogZ--;break;
    	
    	case KeyEvent.VK_W: Graficador.zoomFogX++;break;
    	case KeyEvent.VK_E: Graficador.zoomFogX--;break;
    	
    	case KeyEvent.VK_R: Graficador.zoomFogY++;break;
    	case KeyEvent.VK_T: Graficador.zoomFogY--;break;
    	
    	
  
    	//modifica deltaZ
    	case KeyEvent.VK_L: Graficador.deltaZ=(float)IJ.getNumber("Distancia entre placas", Graficador.deltaZ);
    	                     break;
    	//modos de fog
    	case KeyEvent.VK_F: Graficador.modo_fog=(int)IJ.getNumber("0-Linear 1-Exp 2-Exp2", Graficador.modo_fog);
        Graficador.cambio=true;break;
    	//densidad de fog
    	case KeyEvent.VK_D: Graficador.densidad_fog=(float)IJ.getNumber("entre 0 y 1", Graficador.densidad_fog);
    	Graficador.cambio=true;break;
        
        
    	case KeyEvent.VK_PLUS: Graficador.umbral++;break;					
							
		
    	
    	
    	//reset transformaciones
    	
    	case KeyEvent.VK_SPACE: Graficador.reset=true;break;
    	
   
    	default: break;
    	}
    	
    	
       
    }
    public void keyReleased(KeyEvent e) {
    }
    public void keyTyped(KeyEvent e) {
    }
    

}
