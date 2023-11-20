package multiDimVisualization;



import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class Mouse implements MouseListener, MouseMotionListener {

	@Override
	 
	public void mouseDragged(MouseEvent e)
	{
		if(Display.MouseMoving)
			{
			Display.Azimut-=(Display.MouseX-e.getX());
			Display.Elevation-=(Display.MouseY-e.getY());
			
			Ventana.Actualizar(Display.Azimut,Display.Elevation);
			
			Display.MouseX=e.getX();
			Display.MouseY=e.getY();
			}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Display.MouseX=e.getX();
		Display.MouseY=e.getY();
		Display.MouseMoving=true;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
