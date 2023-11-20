package multiDimVisualization;

import ij.IJ;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.media.opengl.awt.*;

import com.jogamp.opengl.util.Animator;

public class Ventana extends JFrame implements ActionListener, ChangeListener,
		WindowFocusListener
{


	private static final long serialVersionUID = 1L;
	// Componentes de
	// ventana----------------------------------------------------------------------------------------
	private JPanel PanelInf;
	private JPanel PanelSup;
	static GLCanvas Canvas;
	static Animator Animator;
	JPanel PanelCanvas = new JPanel();
	// Control de posicion,tiempo y
	// dz-------------------------------------------------------------------------------
	private static JTextField NAz;
	private static JTextField NEl;
	private static JTextField NDZ;
	private JSlider Velocidad;
	int TimerDelay;
	int TimerActual;
	static Timer Timer;
	// Control de
	// visualizacion--------------------------------------------------------------------------------------
	private JButton ZoomUp;
	private JButton ZoomDown;
	private JComboBox ModoFog;
	private JComboBox ModoInterpolacion;
	private JComboBox VisualizationMode;
	private JComboBox ClassificationMode;
	private JSlider NFogFin;
	private JSlider NDensidadFog;

	public Ventana()
	{
		PanelSup = new JPanel();
		NAz = new JTextField();
		NEl = new JTextField();
		NDZ = new JTextField();
		ZoomUp = new JButton("Z+");
		ZoomDown = new JButton("Z-");

		PanelInf = new JPanel();

		Velocidad = new JSlider(JSlider.HORIZONTAL, 0, 60, 0);
		ModoFog = new JComboBox();
		ModoInterpolacion = new JComboBox();
		VisualizationMode = new JComboBox();
		ClassificationMode = new JComboBox();
		
		NFogFin = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);
		NDensidadFog = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);

		PanelCanvas = new JPanel();
		Canvas = new GLCanvas();
		Canvas.addGLEventListener(new Display());
		Canvas.addKeyListener(new Teclado());
		Mouse MP = new Mouse();
		Canvas.addMouseListener(MP);
		Canvas.addMouseMotionListener(MP);
		Animator = new Animator(Canvas);

		TimerDelay = 0; // detenido
		Timer = new Timer(TimerDelay, this);

	}

	private void InitializeWindow(String nombre, Dimension img)
	{
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		scr.height = (int) (scr.height * 0.9); // to stay out of start up menu

		Dimension pnl = new Dimension(scr.width, scr.height / 8);
		Dimension cnv = new Dimension();

		// width expansion
		cnv.width = scr.width;
		cnv.height = img.height * scr.width / img.width;
		// if its to big, realize height expansion
		if(cnv.height > scr.height - 2 * pnl.height)
		{
			cnv.height = scr.height - 2 * pnl.height;
			cnv.width = img.width * (scr.height - 2 * pnl.height) / img.height;
		}

		// Panel superior
		PanelSup.setLayout(new GridLayout(1, 8));
		PanelSup.setSize(pnl.width, pnl.height);

		NAz.setBorder(BorderFactory.createTitledBorder("Azimutal"));
		NEl.setBorder(BorderFactory.createTitledBorder("Elevation"));
		NDZ.setBorder(BorderFactory.createTitledBorder("Volume Ratio Aspect"));
		NDZ.setText(String.valueOf(D5Visualizer_.Vra.dx) + " : "
				+ String.valueOf(D5Visualizer_.Vra.dy) + " : "
				+ String.valueOf(D5Visualizer_.Vra.dz));
		NAz.addActionListener(this);
		NEl.addActionListener(this);
		ZoomDown.addActionListener(this);
		ZoomUp.addActionListener(this);
		NDZ.addActionListener(this);

		PanelSup.add(NAz);
		PanelSup.add(NEl);
		PanelSup.add(NDZ);
		PanelSup.add(ZoomDown);
		PanelSup.add(ZoomUp);
	
		// Panel Inferior
		PanelInf.setLayout(new GridLayout(1, 5));
		PanelInf.setSize(pnl.width, pnl.height);

		
		ModoFog.setBorder(BorderFactory.createTitledBorder("Fog mode"));
		ModoFog.addItem("No fog");
		ModoFog.addItem("Linear fog");
		ModoFog.addItem("Exponential fog");
		ModoFog.addItem("Square exponential fog");
		ModoFog.setSelectedIndex(Display.FogMode);

		ModoInterpolacion.setBorder(BorderFactory
				.createTitledBorder("Interpolation mode"));
		ModoInterpolacion.addItem("Nearest neighbor interpolation");
		ModoInterpolacion.addItem("Linear interpolation");
		ModoInterpolacion.setSelectedIndex(Display.InterpolationMode);
		
		VisualizationMode.setBorder(BorderFactory
				.createTitledBorder("Visualization mode"));
		VisualizationMode.addItem("MIP");
		VisualizationMode.addItem("DVR");
		
		VisualizationMode.setSelectedIndex(D5Visualizer_.VisualizationMode);
		
		
		ClassificationMode.setBorder(BorderFactory
				.createTitledBorder("Classification"));
		ClassificationMode.addItem("Don't classify");
		ClassificationMode.addItem("Grayscale (original values)");
		ClassificationMode.addItem("RGB (artificial values)");
		ClassificationMode.setSelectedIndex(D5Visualizer_.ClassificationMode);

	/*	if(D5Visualizer_.VisualizationMode==1)
		{
		 ClassificationMode.remove(0);
		}*/
		
		
		
		NDensidadFog.setBorder(BorderFactory
				.createTitledBorder("Fog exponential factor"));
		NDensidadFog.setEnabled(false);

		NFogFin.setBorder(BorderFactory.createTitledBorder("Fog linear end"));

		Velocidad.setMajorTickSpacing(1);
		Velocidad.setBorder(BorderFactory.createTitledBorder("Play : off"));
		if(D5Visualizer_.Nt == 1)
			Velocidad.setEnabled(false);

		PanelInf.add(NFogFin);
		PanelInf.add(NDensidadFog);
		PanelInf.add(ModoFog);
		PanelInf.add(ModoInterpolacion);
		PanelInf.add(VisualizationMode);
		PanelInf.add(ClassificationMode);
		PanelInf.add(Velocidad);

		NFogFin.addChangeListener(this);
		NDensidadFog.addChangeListener(this);
		ModoFog.addActionListener(this);
		ModoInterpolacion.addActionListener(this);
		VisualizationMode.addActionListener(this);
		ClassificationMode.addActionListener(this);
		Velocidad.addChangeListener(this);

		// Margenes del Canvas(se ajustan al espacio entre paneles)
		Canvas.setBounds(0, 0, cnv.width, cnv.height);
		PanelCanvas.add(Canvas);

		this.setTitle(IJ.getImage().getTitle());
		this.setSize(scr);
		// this.setResizable(false);
		this.add("North", PanelSup);
		this.add("Center", PanelCanvas);
		this.add("South", PanelInf);
		this.addWindowFocusListener(this);

		// boton cerrar X
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				D5Visualizer_.endRun();

			}
		});

	}

	// --------------------------------------------------------------------------------------------------------------
	public static void Actualizar(int azimuth, int elevacion)
	{

		NAz.setText(String.valueOf(azimuth));
		NEl.setText(String.valueOf(elevacion));
		Animator.resume();
	}

	// --------------------------------------------------------------------------------------------------------------
	public void CrearVentana(String nombre, Dimension img)
	{
		InitializeWindow(nombre, img);

		this.setVisible(true);
		Canvas.requestFocus();
		Animator.start();

	}

	// --------------------------------------------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent e)
	{

		Animator.resume();

		if(e.getSource() == ZoomDown)
			Display.Zoom -= 0.1;

		if(e.getSource() == ZoomUp)
			Display.Zoom += 0.1;
		
		

		if(e.getSource() == NAz)
			Display.Azimut = Integer.valueOf(NAz.getText());

		if(e.getSource() == NEl)
			Display.Elevation = Integer.valueOf(NEl.getText());

		if(e.getSource() == ModoFog)
		{
			Display.FogChange = true;
			switch (ModoFog.getSelectedIndex())
			{
			case 0:
				Display.FogMode = 0;
				NDensidadFog.setEnabled(false);
				NFogFin.setEnabled(false);
				break;
			case 1:
				Display.FogMode = 1;
				NDensidadFog.setEnabled(false);
				NFogFin.setEnabled(true);
				break;
			case 2:
				Display.FogMode = 2;
				NDensidadFog.setEnabled(true);
				NFogFin.setEnabled(false);
				break;
			case 3:
				Display.FogMode = 3;
				NDensidadFog.setEnabled(true);
				NFogFin.setEnabled(false);
				break;
			}
		}
		if(e.getSource() == ModoInterpolacion)
		{
			Display.TextChange = true;
			switch (ModoInterpolacion.getSelectedIndex())
			{
			case 0:
				Display.InterpolationMode = 0;
				
				break;
			case 1:
				Display.InterpolationMode = 1;
			
				break;
			}
		}
		if(e.getSource() == VisualizationMode)
		{
			switch (VisualizationMode.getSelectedIndex())
			{
			case 0:
				D5Visualizer_.VisualizationMode = 0;
			/*	ClassificationMode.removeAllItems();
				ClassificationMode.addItem("Don't classify");
				ClassificationMode.addItem("Grayscale (original values)");
				ClassificationMode.addItem("RGB (artificial values)");*/
				break;
				
			case 1:
				D5Visualizer_.VisualizationMode = 1;
				if(D5Visualizer_.ClassificationMode == 0)
				 {
				  D5Visualizer_.ClassificationMode = 1;
				  ClassificationMode.setSelectedIndex(1);
				 }
				
				D5Visualizer_.TFuncConfig();
	/*			ClassificationMode.removeAllItems();
				ClassificationMode.addItem("Grayscale (original values)");
				ClassificationMode.addItem("RGB (artificial values)");*/
				break;

			}
			Display.VisualModeChange = true;
		}
		
		if(e.getSource() == ClassificationMode)
		{
			switch (ClassificationMode.getSelectedIndex())
			{
			case 0:
				D5Visualizer_.ClassificationMode = 0;
				if(D5Visualizer_.VisualizationMode==1)
				{
				 IJ.showMessage("DVR needs classification");
				 D5Visualizer_.ClassificationMode = 1;
				}
				break;
			case 1:
				
				D5Visualizer_.ClassificationMode = 1;
				D5Visualizer_.TFuncConfig();
				break;
			case 2:
				D5Visualizer_.ClassificationMode = 2;
				D5Visualizer_.TFuncConfig();
				break;
			}
			Display.TextChange = true;
		}
		
		if(e.getSource() == Timer)
		{
			Display.TActual++;
			Animator.resume();

			if(Display.TActual > D5Visualizer_.Nt)
				Display.TActual = 1;
			Display.TextChange = true;
			if(TimerDelay != 0)
				Timer.start();

		}

	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		Animator.resume();

		if(e.getSource() == NFogFin)
		{
			Display.FogLinearEnd = (int) (((float) (100 + NFogFin.getValue() * 10) / 100.0f) * Display.MaxDim);
			Display.FogChange = true;
		}

		if(e.getSource() == NDensidadFog)
		{
			Display.FogExpDensity = ((float) (NDensidadFog.getValue()) / 1000.0f);
			Display.FogChange = true;
		}

		// timer
		if(Velocidad.getValue() != 0)
			TimerDelay = 1000 / Velocidad.getValue();
		else
			TimerDelay = Velocidad.getValue();

		Velocidad.setBorder(BorderFactory.createTitledBorder(String
				.valueOf(Velocidad.getValue())
				+ " Cuadros por Segundo"));

		if(TimerDelay != 0)
		{
			Timer.setDelay(TimerDelay);
			Timer.start();
		} else
			Timer.stop();
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0)
	{
		if(Animator.isPaused())
			Animator.resume();
		if(Timer.getDelay() != 0)
			Timer.restart();
	}

	@Override
	public void windowLostFocus(WindowEvent arg0)
	{

		if(Animator.isAnimating())
			Animator.pause();
		Timer.stop();
	}

	public static void RenderEnd() // TODO
	{
		Animator.pause();
	}

}
