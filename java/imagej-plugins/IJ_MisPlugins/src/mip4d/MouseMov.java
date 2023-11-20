package mip4d;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


public class MouseMov implements MouseMotionListener {

	@Override
	 
	public void mouseDragged(MouseEvent e)
	{
		
		


		
		if(GraficadorMIP4D.moviendose)
			{
			GraficadorMIP4D.azimut-=(GraficadorMIP4D.X-e.getX());
			GraficadorMIP4D.elevacion-=(GraficadorMIP4D.Y-e.getY());
			
			GraficadorMIP4D.X=e.getX();
			GraficadorMIP4D.Y=e.getY();
			}
		
		

		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

}
