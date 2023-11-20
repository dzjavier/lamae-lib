import ij.*;
import ij.process.*; //import ij.gui.*;
//import java.awt.*;
import ij.plugin.filter.*;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import javax.media.opengl.awt.GLCanvas;


//Por simplicidad,voy a eliminar el manejo de Hilos, despues
//veo

public class Projected_Gallery_View_ implements PlugInFilter {
	static boolean bQuit = false; // cierra
	// la ventana.
	static ImagePlus imp;

	// Setup.................
	// Plugin para imagen 8bits(gris 256)
	public int setup(String arg, ImagePlus imp_nuevo) {
		imp = imp_nuevo;
		return DOES_8G;
	}

	// REEMPLAZARIA EL MAIN
	public void run(ImageProcessor ip) {

		// Toma el nombre de la imagen
		String Nombre = imp.getTitle();

		// lo pone en la ventana
		Frame frame = new Frame(Nombre);

		// ponemos tama�o segun la imagen

		frame.setSize(imp.getWidth(), imp.getHeight());

		GLCanvas canvas = new GLCanvas();

		canvas.addGLEventListener(new JavaRenderer());
		frame.add(canvas);

		/*
		 * Para cerrar con la (x) la ventana Si implementara WindowAdapter como
		 * interfase,deberia definir todos los metodos,al implementarla aca,
		 * solo defino los que me interesan ;)
		 */
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				bQuit = true;
			}
		});

		frame.setVisible(true);
		canvas.requestFocus();

		// IJ.showMessage("Empieza loop");

		// loop de dibujo
		while (!bQuit) {
			canvas.display();

		}
		frame.dispose();
		bQuit = false; // x si se quere usar de nuevo el plug

		// IJ.showMessage("termina loop");

	}

}