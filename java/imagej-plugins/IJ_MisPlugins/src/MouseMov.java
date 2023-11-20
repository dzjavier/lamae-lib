import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


public class MouseMov implements MouseMotionListener {

	@Override
	 
	public void mouseDragged(MouseEvent e)
	{
		
		


		
		if(Graficador.moviendose)
			{
			Graficador.azimut-=(Graficador.X-e.getX());
			Graficador.elevacion-=(Graficador.Y-e.getY());
			
			Graficador.X=e.getX();
			Graficador.Y=e.getY();
			}
		
		

		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

}
