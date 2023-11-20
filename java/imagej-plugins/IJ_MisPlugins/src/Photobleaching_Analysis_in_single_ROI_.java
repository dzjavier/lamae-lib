import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;


import ij.measure.ResultsTable;
import flanagan.analysis.Regression;

public class Photobleaching_Analysis_in_single_ROI_ implements PlugInFilter {

	private ImagePlus imp;
	private double deltaT;
    public int setup(String arg0, ImagePlus arg1) {
		imp=arg1;
	  GenericDialog gd = new GenericDialog("Photobleaching analysis setup", IJ.getInstance());
      gd.addNumericField("Delta T (sec.):", 1.0, 2);
      gd.showDialog();

      if (gd.wasCanceled()) return 0;
      
      deltaT = gd.getNextNumber();
	return NO_CHANGES+DOES_16+DOES_32+DOES_8G+ROI_REQUIRED;
	}
	public void run(ImageProcessor arg0) {
				
		if (IJ.versionLessThan("1.32c"))
			return;
 		
	
		Rectangle r = imp.getProcessor().getRoi();
		double[] Ydata = new double[imp.getNSlices()];
		double[] Xdata = new double [imp.getNSlices()];
	  	for (int k=1; k<=imp.getNSlices(); k++){
    		//rt.incrementCounter();
    		int counter = 1;
    		imp.setSlice(k);
    		double suma=0;
    		for (int j=r.x; j<=(r.x+r.width-1);j++)
    			for (int i=r.y; i<=(r.y+r.height-1);i++){
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
		 //reg.exponentialMultiplePlot(2);
		 reg.addConstraint(1, -1, -0.005);// para que salga ordenado, pero me parece que hace cagada
		 reg.exponentialMultiple(2);
		 double rBiExp=reg.getAdjustedR();
		 double coefBiExp[]=new double[4];
		 
		 coefBiExp=reg.getCoeff();
		 ResultsTable rt = ResultsTable.getResultsTable();
	  	 rt.incrementCounter();
		 rt.setPrecision(5);
		 rt.addValue("Mono Exp.", rMonoExp);
		 rt.addValue("A1 ME", coefMonoExp[1]);rt.addValue("B1 ME", coefMonoExp[0]);
		 rt.addValue("Bi Exp.", rBiExp);
		 rt.addValue("A1 BiE", coefBiExp[1]);rt.addValue("B1 BiE", coefBiExp[0]);
		 rt.addValue("A2 BiE", coefBiExp[3]);rt.addValue("B2 BiE", coefBiExp[2]);
		 ResultsTable.getResultsTable().show("Results");
		 
		
       }
	}



	

		

  
		
 