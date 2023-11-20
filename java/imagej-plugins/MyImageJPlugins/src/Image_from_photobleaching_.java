import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;

import flanagan.analysis.Regression;

public class Image_from_photobleaching_ implements PlugInFilter {

	private ImagePlus imp;
	private double deltaT=3;
    public int setup(String arg0, ImagePlus arg1) {
		imp=arg1;
	  GenericDialog gd = new GenericDialog("Image from photobleaching setup", IJ.getInstance());
      gd.addNumericField("Delta T (sec.):", deltaT, 2);
      gd.showDialog();

      if (gd.wasCanceled()) return 0;
         deltaT = gd.getNextNumber();

		return NO_CHANGES+DOES_16+DOES_32+DOES_8G;
	}
	public void run(ImageProcessor arg0) {
				
		if (IJ.versionLessThan("1.32c"))
			return;
 	 	 ImageProcessor Coef = new FloatProcessor(imp.getWidth(),imp.getHeight());
 	 	 ImageProcessor R = new FloatProcessor(imp.getWidth(),imp.getHeight());
 	 	 //impCoef.setDimensions(1, 1, 1);
 	 	 //R.setDimensions(1, 1, 1);
 	 	 //impCoef.setresize(imp.getWidth(), imp.getHeight());
 	 	 //R.getProcessor().resize(imp.getWidth(), imp.getHeight());
	  	 double[] Ydata = new double[imp.getNSlices()];
		 double[] Xdata = new double[imp.getNSlices()];
		 Regression reg = new Regression(Xdata, Ydata);	
		 for (int j=0; j<=imp.getWidth()-1;j++){
			 for (int i=0; i<=imp.getHeight()-1;i++){
				 for (int k=1; k<=imp.getNSlices(); k++){
					 imp.setSlice(k);
					 Xdata[k-1]=(k-1)*deltaT;
					 Ydata[k-1]=(double)imp.getProcessor().getPixel(j,i);
					 }
				 reg.enterData(Xdata, Ydata);
				 reg.exponentialSimple();
				 Coef.putPixelValue(j, i, reg.getCoeff()[0]);
				 R.putPixelValue(j, i, reg.getAdjustedR());
				}
		 }
		 ImagePlus impCoef = new ImagePlus("Coeficientes",Coef);
		 ImagePlus impR = new ImagePlus("R",R);
		 impCoef.show();
	 	 impR.show();
	 	 return;
	}
	 
	}		 
		
		 
	  	 
		 	  	 
		 



	

		

  
		
 