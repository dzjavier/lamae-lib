import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.nio.ByteBuffer;

public class JavaRenderer implements GLEventListener, KeyListener {

	byte[] pixeles = (byte[]) Projected_Gallery_View_.imp.getProcessor()
			.getPixels();

	// procesador gl
	GL2 gl;

	boolean subtex = false;
	boolean sin_subtex = true;

	// static IntBuffer textura_puntero;
	int textura = 1;

	int ancho = Projected_Gallery_View_.imp.getWidth();
	int alto = Projected_Gallery_View_.imp.getHeight();

	// subtextura(25%(1/4) del original,para mantenernos dentro de las 2^n)
	int sub_ancho = (int) Projected_Gallery_View_.imp.getWidth() / 4;
	int sub_alto = (int) Projected_Gallery_View_.imp.getHeight() / 4;
	int offsetx = 0;
	int offsety = 0;
	byte[] pixeles_sub = new byte[sub_ancho * sub_alto];

	static float r = 0;
	static float z = 1;

	static float izq = 0;
	static float der = 0;
	static float arr = 0;
	static float aba = 0;

	static float transparencia = 1;

	void hacer_subtex() {
		// dise�o de la subtextura
		Projected_Gallery_View_.imp.getProcessor().invert();

		for (int i = 0; i < sub_ancho; i++)
			for (int j = 0; j < sub_alto; j++)
				pixeles_sub[i * sub_alto + j] = (byte) Projected_Gallery_View_.imp
						.getProcessor().getPixel(offsetx + j, offsety + i);

		Projected_Gallery_View_.imp.getProcessor().invert();

	}

	// ******** INIT *****************//
	public void init(GLAutoDrawable gLDrawable) {
		gl = (GL2) gLDrawable.getGL();

		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// toma la textura con el nombre especificado y aplica los
		// cambios subsiguientes
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textura);

		// setup de las aproximaciones de filtros
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_NEAREST);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_NEAREST);

		// asociamos la textura con los pixeles

		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_LUMINANCE, ancho, alto, 0,
				GL2.GL_LUMINANCE, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(pixeles));

		 gLDrawable.addGLEventListener(this);
	}

	public void display(GLAutoDrawable gLDrawable) {
		// se dibuja la subtex y la textura,este m�todo es un sinsentido
		// ya que justamente las subtexturas existen para no tener que
		// recalcular la textura completa. es solo explicativo
		if (subtex) {
			// PRUEBA
			// se redibuja la textura total para no dejar reminiscencia
			// del paso de la subtextura movil
			gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_LUMINANCE, ancho, alto,
					0, GL2.GL_LUMINANCE, GL2.GL_UNSIGNED_BYTE, ByteBuffer
							.wrap(pixeles));
			if (!sin_subtex) {
				hacer_subtex();
				gl.glTexSubImage2D(GL2.GL_TEXTURE_2D, 0, offsetx, offsety,
						sub_ancho, sub_alto, GL2.GL_LUMINANCE,
						GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(pixeles_sub));

			}
			subtex = false;
		}

		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textura);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glRotatef(r, 0, 1, 0);
		gl.glTranslatef(-ancho / 2, alto / 2, 0);

		// zoom
		gl.glScalef(z, z, 0);

		// translate buscador
		gl.glTranslatef(-izq, 0, 0);
		gl.glTranslatef(der, 0, 0);
		gl.glTranslatef(0, arr, 0);
		gl.glTranslatef(0, -aba, 0);

		gl.glBegin(GL2.GL_QUADS);

		 gl.glColor4f(1.0f, 1.0f, 1.0f, transparencia);
		 gl.glTexCoord2f(0, 0);
		 gl.glVertex3f(0, 0, 0);
		 gl.glTexCoord2f(0, 1);
		 gl.glVertex3f(0, -alto, 0);
		 gl.glTexCoord2f(1, 1);
		 gl.glVertex3f(ancho, -alto, 0);
		 gl.glTexCoord2f(1, 0);
		 gl.glVertex3f(ancho, 0, 0);

		gl.glEnd();

		gl.glDisable(GL2.GL_TEXTURE_2D);

	}

	// LA SIG ES DE EVENTLISTENER
	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width,
			int height) {
		// gl = gLDrawable.getGL();
		// gl.glViewport ( 0, 0, width, height );
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-ancho / 2.0, ancho / 2.0, -alto / 2.0, alto / 2.0,
				-alto / 2.0, alto / 2.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

	}

	public void keyPressed(KeyEvent e) {

		int key = e.getKeyCode();

		switch (key) {
		case KeyEvent.VK_R:
			r += 0.5;
			break;
		case KeyEvent.VK_T:
			r -= 0.5;
			break;
		case KeyEvent.VK_A:
			z += 0.1;
			break;
		case KeyEvent.VK_Z:
			z -= 0.1;
			break;
		case KeyEvent.VK_LEFT:
			izq += 1;
			break;
		case KeyEvent.VK_RIGHT:
			der += 1;
			break;
		case KeyEvent.VK_UP:
			arr += 1;
			break;
		case KeyEvent.VK_DOWN:
			aba += 1;
			break;
		case KeyEvent.VK_ESCAPE:
			Projected_Gallery_View_.bQuit = true;
			break;

		case KeyEvent.VK_X:
			transparencia += 0.01;
			break;
		case KeyEvent.VK_C:
			transparencia -= 0.01;
			break;

		// activamos y/o actualizamos subtextura
		case KeyEvent.VK_O:
			sin_subtex = false;
			subtex = true;
			break;
		case KeyEvent.VK_L:
			offsetx += 4;
			subtex = true;
			break;
		case KeyEvent.VK_J:
			offsetx -= 4;
			subtex = true;
			break;
		case KeyEvent.VK_I:
			offsety -= 4;
			subtex = true;
			break;
		case KeyEvent.VK_K:
			offsety += 4;
			subtex = true;
			break;

		case KeyEvent.VK_P:
			sin_subtex = true;
			subtex = true;
			break;

		default:
			break;
	  

		}

	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

}