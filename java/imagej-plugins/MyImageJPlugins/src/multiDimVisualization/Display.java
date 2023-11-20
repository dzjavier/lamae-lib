package multiDimVisualization;

import ij.IJ;

import javax.media.opengl.GL2;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLAutoDrawable;

import java.nio.FloatBuffer;

/**
 * Grafica Direct Volume Rendering de im�genes multidimensionales
 * 
 * @author Leandro Bugnon
 * @version 18/03/2010
 */

public class Display implements GLEventListener
{

	// Atributos******************************************************************************************************

	// Variables de entorno
	// ------------------------------------------------------------------------------------------
	static GL2 gl;
	/** Reset all the transformations on 3D volume */
	static boolean Reset = false;
	/** Mouse moving */
	static boolean MouseMoving = false;
	/** Flag that indicate a change in the model */
	static boolean TextChange = false;
	/** Flag that indicate a change in the fog */
	static boolean FogChange = false;
	static boolean SizeChange = false;
	static boolean VisualModeChange = false;
	// Image Atributes

	static float GHeight, GWidth, GDepth;
	// Variables de
	// transformacion-----------------------------------------------------------------------------------
	static float Zoom;
	static float MaxDim; // dimension de la diagonal maxima
	static float MaxPix; // numero necesario de pixeles para representar la
							// diagonal maxima completa
	static int Azimut;
	static int Elevation;

	static boolean ModelMoving = true; // false let move the clipping planes
	/*
	 * //clip-planes public static PlaneCoord[] ClipPlanes; static float[]
	 * CoordGeomPlanos; // 4 vertices
	 */

	// mouse
	static int MouseX;
	static int MouseY;
	// --------------------------------------------------------------------------------------------------------------

	// Variables de
	// fog----------------------------------------------------------------------------------------------
	static int FogMode; // modo_fog 0=sin fog 1=linear 2=exp 3=exp2
	static float FogExpDensity; // solo para exp y exp2
	static int FogLinearEnd;
	// --------------------------------------------------------------------------------------------------------------

	// Variables de texturas, capas y
	// frames-------------------------------------------------------------------------
	static int InterpolationMode; // 0-valor vecino 1-interpolacion lineal
	static int TActual = 1;

	static void TransformReset(GL2 gl)
	{
		Azimut = 0;
		Elevation = 0;
		Zoom = 1;
		FogLinearEnd = (int) (MaxDim);

		// actualiza display
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		gl.glOrtho(-MaxDim / 2.0f, MaxDim / 2.0f, -MaxDim / 2.0f,
				MaxDim / 2.0f, -MaxDim, 0);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		Reset = false;
		if(Ventana.Animator.isPaused())
			Ventana.Animator.resume();
	}

	//TODO falta implementar que cargue una textura reducida para un vistazo rapido, luego mejorar 
	
	private void TextureLoad(GL2 gl)
	{
		int InternalFormat = 0;
		int ImageFormat = 0;

		float[] image = new float[0];

		switch (D5Visualizer_.ClassificationMode)
		{
		case 0:
			InternalFormat = GL2.GL_COMPRESSED_LUMINANCE;
			ImageFormat = GL2.GL_LUMINANCE;
			image = new float[(int) (D5Visualizer_.IWidth
					* D5Visualizer_.IHeight * D5Visualizer_.Nz)];
			break;
		case 1:
			InternalFormat = GL2.GL_COMPRESSED_LUMINANCE_ALPHA;
			ImageFormat = GL2.GL_LUMINANCE_ALPHA;
			image = new float[(int) (2 * D5Visualizer_.IWidth
					* D5Visualizer_.IHeight * D5Visualizer_.Nz)]; 
			
			break;

		case 2:
			InternalFormat = GL2.GL_COMPRESSED_RGBA;
			ImageFormat = GL2.GL_RGBA;
			image = new float[(int) (4 * D5Visualizer_.IWidth
					* D5Visualizer_.IHeight * D5Visualizer_.Nz)];

			break;
		}

		Archiver.LoadTexture(image);

		gl.glBindTexture(GL2.GL_TEXTURE_3D, 1);

		int modo = 0;
		switch (InterpolationMode)
		{
		case 0:
			modo = GL2.GL_NEAREST;
			break;
		case 1:
			modo = GL2.GL_LINEAR;
			break;
		}

		gl.glTexParameterf(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MAG_FILTER, modo);
		gl.glTexParameterf(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MIN_FILTER, modo);
		gl.glTexParameterf(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S,
				GL2.GL_CLAMP);
		gl.glTexParameterf(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T,
				GL2.GL_CLAMP);
		gl.glTexParameterf(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R,
				GL2.GL_CLAMP);

		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE,
				GL2.GL_REPLACE);

		gl.glTexImage3D(GL2.GL_TEXTURE_3D, 0, InternalFormat,
				D5Visualizer_.IWidth, D5Visualizer_.IHeight, D5Visualizer_.Nz,
				0, ImageFormat, GL2.GL_FLOAT, FloatBuffer.wrap(image));

		gl.glEnable(GL2.GL_TEXTURE_3D);

		TextChange = false;
	}

	// --------------------------------------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------------------------------------
	private void FogUpdate(GL2 gl)
	{
		switch (FogMode)
		{
		// sin fog
		case 0:
			gl.glDisable(GL2.GL_FOG);
			break;
		// fog lineal
		case 1:
			gl.glEnable(GL2.GL_FOG);
			gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
			gl.glFogf(GL2.GL_FOG_START, 0);
			gl.glFogf(GL2.GL_FOG_END, FogLinearEnd);
			break;
		// fog exp
		case 2:
			gl.glEnable(GL2.GL_FOG);
			gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP);
			gl.glFogf(GL2.GL_FOG_DENSITY, FogExpDensity);
			break;
		// fog exp2
		case 3:
			gl.glEnable(GL2.GL_FOG);
			gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP);
			gl.glFogf(GL2.GL_FOG_DENSITY, FogExpDensity);
			break;

		}
		FogChange = false;
	}

	// --------------------------------------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------------------------------------
	public void init(GLAutoDrawable gLDrawable)
	{
		gl = gLDrawable.getGL().getGL2();

		// OpenGL version verification (>= 1.2)
		String version = gl.glGetString(GL2.GL_VERSION);
		if(version.charAt(0) < 1
				|| (version.charAt(0) == 1 && version.charAt(2) < 2))
		{
			version += "\n Old OpenGL version.";
			IJ.showMessage(version);
			System.exit(0);
		}

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		GeometricCalc.DimensionUpdate();

		gl.glShadeModel(GL2.GL_FLAT); // Reduce innecesary shade process

		gl.glEnable(GL2.GL_BLEND);
		gl.glHint(GL2.GL_FOG_HINT, GL2.GL_NICEST);

		switch (D5Visualizer_.VisualizationMode)
		{
		case 0:
			gl.glBlendEquation(GL2.GL_MAX);
			gl.glDisable(GL2.GL_ALPHA_TEST);
			break;

		case 1:
			gl.glBlendEquation(GL2.GL_FUNC_ADD);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); // cada
																			// elemento
																			// nuevo
			gl.glAlphaFunc(GL2.GL_GREATER, 0.005f); // TODO REVISAR
			gl.glEnable(GL2.GL_ALPHA_TEST);
			break;
		}

		// TransformReset(gl);
		FogUpdate(gl);

		TextureLoad(gl);
	}

	// --------------------------------------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------------------------------------
	public void VisualModeUpdate(GL2 gl)
	{
		switch (D5Visualizer_.VisualizationMode)
		{
		case 0:
			gl.glBlendEquation(GL2.GL_MAX);
			gl.glDisable(GL2.GL_ALPHA_TEST);
			break;

		case 1:
			gl.glBlendEquation(GL2.GL_FUNC_ADD);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); // cada
																			// elemento
																			// nuevo
			gl.glAlphaFunc(GL2.GL_GREATER, 0.005f); // TODO REVISAR
			gl.glEnable(GL2.GL_ALPHA_TEST);
			break;
		}
		VisualModeChange = false;
	}

	public void display(GLAutoDrawable gLDrawable)
	{
		gl = gLDrawable.getGL().getGL2();

		if(Azimut > 360)
			Azimut -= 360;
		if(Azimut < 0)
			Azimut += 360;
		if(Elevation > 360)
			Elevation -= 360;
		if(Elevation < 0)
			Elevation += 360;

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

		if(Reset)
			TransformReset(gl);
		if(SizeChange)
		{
			GeometricCalc.DimensionUpdate();
			FogLinearEnd = (int) (MaxDim);
			FogChange = true;
			TransformReset(gl);
		}
		if(TextChange)
			TextureLoad(gl);
		if(FogChange)
			FogUpdate(gl);
		if(VisualModeChange)
			VisualModeUpdate(gl);

		gl.glPushMatrix();

		gl.glScalef(Zoom, -Zoom, 1);
		gl.glTranslatef(0, 0.0f, MaxDim / 2.0f);

		// if(ModelMoving)
		// {
		gl.glRotatef(Azimut, 0, 1, 0);
		gl.glRotatef(Elevation, 1, 0, 0);
		gl.glTranslatef(-GWidth / 2.0f, -GHeight / 2.0f, -GDepth / 2.0f);

		// }
		/*
		 * else //clip-plane rotation { gl.glPopMatrix();
		 * 
		 * 
		 * gl.glRotatef(Azimut, 0, 1, 0); gl.glRotatef(Elevation, 1, 0, 0);
		 * gl.glTranslatef(0,0.0f,Zoom); //elevaci�n
		 * gl.glTranslatef(-DVR_.Width/
		 * 2.0f,-DVR_.Height/2.0f,-DVR_.Nz*DVR_.dz/2.0f);
		 * 
		 * 
		 * gl.glDisable(GL2.GL_TEXTURE_3D);
		 * 
		 * gl.glColor4f(1, 1, 1,1); gl.glVertex3f(0, 0, 20);
		 * gl.glVertex3f(DVR_.Width, 0, 20); gl.glVertex3f(DVR_.Width,
		 * DVR_.Height, 20); gl.glVertex3f(0, DVR_.Height, 20);
		 * 
		 * gl.glEnable(GL2.GL_TEXTURE_3D);
		 * 
		 * gl.glPushMatrix(); }
		 */

		// se toma la direccion con menor diferencia,haciendo producto interno
		// entre
		// vector posicion y las opciones
		/*
		 * 0-zp 1-xp 2-zn 3-xn 4-yp 5-yn
		 */
		gl.glBindTexture(GL2.GL_TEXTURE_3D, 1);

		int orientation = GeometricCalc.OptimizeOrientation(Elevation, Azimut);
		switch (orientation)
		{
		case 0: // zp
			for (float i = 1; i >= 0; i -= 1 / GDepth)
			{
				gl.glBegin(GL2.GL_QUADS);

				gl.glTexCoord3f(0, 0, i);
				gl.glVertex3f(0, 0, i * GDepth);
				gl.glTexCoord3f(1, 0, i);
				gl.glVertex3f(GWidth, 0, i * GDepth);
				gl.glTexCoord3f(1, 1, i);
				gl.glVertex3f(GWidth, GHeight, i * GDepth);
				gl.glTexCoord3f(0, 1, i);
				gl.glVertex3f(0, GHeight, i * GDepth);

				gl.glEnd();
			}
			break;

		case 1: // xp
			for (float i = 0; i <= 1; i += 1 / GWidth)
			{
				gl.glBegin(GL2.GL_QUADS);

				gl.glTexCoord3f(i, 0, 0);
				gl.glVertex3f(i * GWidth, 1, 0);
				gl.glTexCoord3f(i, 0, 1);
				gl.glVertex3f(i * GWidth, 1, GDepth);
				gl.glTexCoord3f(i, 1, 1);
				gl.glVertex3f(i * GWidth, GHeight, GDepth);
				gl.glTexCoord3f(i, 1, 0);
				gl.glVertex3f(i * GWidth, GHeight, 0);

				gl.glEnd();
			}
			break;

		case 2: // zn
			for (float i = 0; i <= 1; i += 1 / GDepth)
			{
				gl.glBegin(GL2.GL_QUADS);

				gl.glTexCoord3f(0, 0, i);
				gl.glVertex3f(0, 0, i * GDepth);
				gl.glTexCoord3f(1, 0, i);
				gl.glVertex3f(GWidth, 0, i * GDepth);
				gl.glTexCoord3f(1, 1, i);
				gl.glVertex3f(GWidth, GHeight, i * GDepth);
				gl.glTexCoord3f(0, 1, i);
				gl.glVertex3f(0, GHeight, i * GDepth);

				gl.glEnd();
			}
			break;

		case 3: // xn
			for (float i = 1; i >= 0; i -= 1 / GWidth)
			{
				gl.glBegin(GL2.GL_QUADS);

				gl.glTexCoord3f(i, 0, 0);
				gl.glVertex3f(i * GWidth, 1, 0);
				gl.glTexCoord3f(i, 0, 1);
				gl.glVertex3f(i * GWidth, 1, GDepth);
				gl.glTexCoord3f(i, 1, 1);
				gl.glVertex3f(i * GWidth, GHeight, GDepth);
				gl.glTexCoord3f(i, 1, 0);
				gl.glVertex3f(i * GWidth, GHeight, 0);

				gl.glEnd();
			}
			break;

		case 4: // yp
			for (float i = 1; i >= 0; i -= 1 / GHeight)
			{
				gl.glBegin(GL2.GL_QUADS);

				gl.glTexCoord3f(0, i, 0);
				gl.glVertex3f(0, i * GHeight, 0);
				gl.glTexCoord3f(0, i, 1);
				gl.glVertex3f(0, i * GHeight, GDepth);
				gl.glTexCoord3f(1, i, 1);
				gl.glVertex3f(GWidth, i * GHeight, GDepth);
				gl.glTexCoord3f(1, i, 0);
				gl.glVertex3f(GWidth, i * GHeight, 0);

				gl.glEnd();
			}
			break;

		case 5: // yn
			for (float i = 0; i <= 1; i += 1 / GHeight)
			{
				gl.glBegin(GL2.GL_QUADS);

				gl.glTexCoord3f(0, i, 0);
				gl.glVertex3f(0, i * GHeight, 0);
				gl.glTexCoord3f(0, i, 1);
				gl.glVertex3f(0, i * GHeight, GDepth);
				gl.glTexCoord3f(1, i, 1);
				gl.glVertex3f(GWidth, i * GHeight, GDepth);
				gl.glTexCoord3f(1, i, 0);
				gl.glVertex3f(GWidth, i * GHeight, 0);

				gl.glEnd();
			}
			break;
		}

		gl.glPopMatrix();
		// DrawAxes(gl);

		Ventana.RenderEnd();
	}

	// --------------------------------------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------------------------------------
	/*
	 * private void DrawAxes(GL2 gl) { // gl.glDisable(GL2.GL_BLEND); //
	 * gl.glDisable(GL2.GL_TEXTURE_3D); gl.glDisable(GL2.GL_FOG);
	 * gl.glEnable(GL2.GL_LINE_STIPPLE);
	 * 
	 * gl.glColor3f(1.0f,1.0f,0.0f); gl.glLineStipple(1, (short) 0x2F);
	 * 
	 * gl.glBegin(GL2.GL_LINES);
	 * 
	 * //x gl.glVertex3f(0,GHeight,0); gl.glVertex3f(GWidth,GHeight,0); //y
	 * gl.glVertex3f(0,0,0); gl.glVertex3f(0,GHeight,0); //z
	 * gl.glVertex3f(0,GHeight,0); gl.glVertex3f(0,GHeight,GDepth);
	 * 
	 * gl.glEnd();
	 * 
	 * gl.glDisable(GL2.GL_LINE_STIPPLE); gl.glEnable(GL2.GL_TEXTURE_3D); //
	 * gl.glEnable(GL2.GL_BLEND); if(FogMode!=0) gl.glEnable(GL2.GL_FOG); }
	 */
	// --------------------------------------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------------------------------------
	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged,
			boolean deviceChanged)
	{// TODO

	}

	// --------------------------------------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------------------------------------
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width,
			int height)
	{

		gl = gLDrawable.getGL().getGL2();

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		GeometricCalc.DimensionUpdate();
		gl.glOrtho(-MaxDim / 2.0f, MaxDim / 2.0f, -MaxDim / 2.0f,
				MaxDim / 2.0f, -MaxDim, 0);

	}

	// --------------------------------------------------------------------------------------------------------------

	@Override
	public void dispose(GLAutoDrawable arg0)
	{

	}

	public static void Actualize()
	{

		TextChange = true;
	}

	// --------------------------------------------------------------------------------------------------------------

}