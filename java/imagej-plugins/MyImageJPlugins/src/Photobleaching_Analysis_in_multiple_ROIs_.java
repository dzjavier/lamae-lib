import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;
import java.util.Date;
import java.util.Random;


import ij.measure.ResultsTable;
import flanagan.analysis.Regression;


public class Photobleaching_Analysis_in_multiple_ROIs_ implements PlugInFilter {

	private ImagePlus imp;
	private double deltaT=3;
    private int numberOfSamples=30;
    private int sampleDim=5;
    public int setup(String arg0, ImagePlus arg1) {
		imp=arg1;
	  GenericDialog gd = new GenericDialog("Photobleaching analysis setup", IJ.getInstance());
      gd.addNumericField("Delta T (sec.):", 1.0, 2);
      gd.addNumericField("Number of samples:", 30, 0);
      gd.addNumericField("Sample dimensions:", 5, 0);
      gd.showDialog();

      if (gd.wasCanceled()) return 0;
      
       deltaT = gd.getNextNumber();
       numberOfSamples = (int) gd.getNextNumber();
       sampleDim = (int) gd.getNextNumber();

		return NO_CHANGES+DOES_16+DOES_32+DOES_8G+DOES_STACKS+ROI_REQUIRED;
	}
	public void run(ImageProcessor arg0) {
				
		if (IJ.versionLessThan("1.32c"))
			return;
 				 ResultsTable rt = new ResultsTable();
		 // normalización
		
	  	 Rectangle r = imp.getProcessor().getRoi();
	  	 double rMeanMonoExp=0;
	  	 double rMeanBiExp=0;
	  	 double A1MeanMonoExp=0;
	  	 double B1MeanMonoExp=0;
	  	 double A1MeanBiExp=0;
	  	 double B1MeanBiExp=0;
	  	 double A2MeanBiExp=0;
	  	 double B2MeanBiExp=0;
		//rt.incrementCounter();
		//rt.addValue(1, r.x); rt.addValue(2, r.y);rt.addValue(3, r.width);rt.addValue(4, r.height);
		 if ((r.width>sampleDim) && (r.height>sampleDim)) {
		  Rectangle raux = new Rectangle(sampleDim,sampleDim);
		  double[] Ydata = new double[imp.getNSlices()];
		  double[] Xdata = new double [imp.getNSlices()];
		  Random rand=new Random();
		  rand.setSeed(new Date().getTime());
		  for (int l=1; l<=numberOfSamples;l++){
			 long centreX=(rand.nextInt(r.width-6)+(int)r.getCenterX()-(r.width/2)+3);
			 long centreY=(rand.nextInt(r.height-6)+(int)r.getCenterY()-(r.height/2)+3);
			 raux.x=(int)centreX-2;
			 raux.y=(int)centreY-2;
			 imp.setRoi(new Roi(raux), true);
			 //imp.setRoi(new Roi(r), true);
		   for (int k=1; k<=imp.getNSlices(); k++){
    		//rt.incrementCounter();
    		int counter = 1;
    		imp.setSlice(k);
    		double suma=0;
    		for (int j=raux.x; j<=(raux.x+raux.width-1);j++)
    			for (int i=raux.y; i<=(raux.y+raux.height-1);i++){
    				counter+=1;
    				suma+=(double)imp.getProcessor().getPixel(j, i);
    			}
    		Xdata[k-1]=(k-1)*deltaT;
    		Ydata[k-1]=suma/(double)counter;
    		
		   }
		 Regression reg = new Regression(Xdata, Ydata);
		 //reg.plotXY();
		 //reg.exponentialSimplePlot();
		 reg.exponentialSimple();
		 double rMonoExp=reg.getAdjustedR();
		 double coefMonoExp[]=new double[2];
	  	 coefMonoExp=reg.getCoeff();
		 rMeanMonoExp+=(rMonoExp/numberOfSamples);
	  	 A1MeanMonoExp+=(coefMonoExp[1]/numberOfSamples);
	  	 B1MeanMonoExp+=(coefMonoExp[0]/numberOfSamples);
	  	 
		 //reg.exponentialMultiplePlot(2);
		 reg.addConstraint(1, -1, -0.005);// para que salga ordenado, pero me parece que hace cagada
		 reg.exponentialMultiple(2);
		 double rBiExp=reg.getAdjustedR();
		 double coefBiExp[]=new double[4];
		 
		 coefBiExp=reg.getCoeff();
		 rMeanBiExp+=(rBiExp/numberOfSamples);
	  	 A1MeanBiExp+=(coefBiExp[1]/numberOfSamples);
	  	 B1MeanBiExp+=(coefBiExp[0]/numberOfSamples);
	  	 A2MeanBiExp+=(coefBiExp[3]/numberOfSamples);
	  	 B2MeanBiExp+=(coefBiExp[2]/numberOfSamples);
		 
	  	 rt.incrementCounter();
		 rt.setPrecision(5);
		 rt.addValue("Mono Exp.", rMonoExp);
		 rt.addValue("A1 ME", coefMonoExp[1]);rt.addValue("B1 ME", coefMonoExp[0]);
		 rt.addValue("Bi Exp.", rBiExp);
		 rt.addValue("A1 BiE", coefBiExp[1]);rt.addValue("B1 BiE", coefBiExp[0]);
		 rt.addValue("A2 BiE", coefBiExp[3]);rt.addValue("B2 BiE", coefBiExp[2]);
		 } // for l
		 rt.incrementCounter();
		 rt.addValue("Mono Exp.", rMeanMonoExp);
		 rt.addValue("A1 ME", A1MeanMonoExp);rt.addValue("B1 ME", B1MeanMonoExp);
		 rt.addValue("Bi Exp.", rMeanBiExp);
		 rt.addValue("A1 BiE", A1MeanBiExp);rt.addValue("B1 BiE", B1MeanBiExp);
		 rt.addValue("A2 BiE", A2MeanBiExp);rt.addValue("B2 BiE", B2MeanBiExp);
		 
		 rt.show("Resultados");
		 
		} // if roi
       }
	}



	

		

  
		
 