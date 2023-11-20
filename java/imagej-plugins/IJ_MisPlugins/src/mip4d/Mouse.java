package mip4d;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class Mouse implements MouseListener{

	@Override
	public void mouseClicked(MouseEvent e) {
	
		
		
		/*
		
		//e.getx y e.gety devuelven distancia
		//a esquina sup-izq en escala de ancho-alto
		//de ventana
		float x=e.getX();
		float y=e.getY();
		
		//como ancho y alto panel son grandes, uso float 
		
		//escala de pantalla/ejes de dibujo
		float escalax=(float)Panel_resonancias.ancho/(float)Graficador.ancho_panel;
		float escalay=(float)Panel_resonancias.alto/(float)Graficador.alto_panel;
		//offset(para llevar el 0,0 del mouse al -ancho_panel/2,-alto_panel/2 de los ejes GL(en unidades [pixeles del dibujo])
	//	offx=0
		float offy=(float)Graficador.alto_panel;
		
		Graficador.X =(int) (x/escalax);
		Graficador.Y =(int) -(y/escalay-offy);
		
	   if(!Graficador.imagenenPantalla)
		{Graficador.mouse=true;
		 Graficador.N_actualMouse=Graficador.Y/Graficador.alto*Graficador.imagenesXfila+Graficador.X/Graficador.ancho+1;
		}
	   else
	   {
		 //primero hay q escalar todo en funcion a tamaño de imagn
		   x=e.getX();
		   y=e.getY();
		   escalax=(float)Panel_resonancias.ancho/(float)Graficador.ancho;
		   escalay=(float)Panel_resonancias.alto/(float)Graficador.alto;
		   offy=(float)Graficador.alto;
		   
		   Graficador.X =(int) (x/escalax);
		   Graficador.Y =(int) (y/escalay);
		   
 	     Graficador.escribir_etiqueta();
 	     Graficador.refresh=true;
	   }
	   */
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {

		GraficadorMIP4D.X=e.getX();
		GraficadorMIP4D.Y=e.getY();
		GraficadorMIP4D.moviendose=true;

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
