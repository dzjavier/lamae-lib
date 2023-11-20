import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;

public class Autofluorescence_Extraction_ implements PlugInFilter {

	private ImagePlus imp;
	private double deltaT=3;
    public int setup(String arg0, ImagePlus arg1) {
		imp=arg1;
	  GenericDialog gd = new GenericDialog("Autofluorescence Extraction", IJ.getInstance());
      gd.addNumericField("Delta T (sec.):", deltaT, 2);
      gd.showDialog();

      if (gd.wasCanceled()) return 0;
         deltaT = gd.getNextNumber();

		return DOES_STACKS+NO_CHANGES+DOES_16+DOES_32+DOES_8G;
	}
	public void run(ImageProcessor arg0) {
				
		if (IJ.versionLessThan("1.32c"))
			return;
	 	 ImageProcessor R = new FloatProcessor(imp.getWidth(),imp.getHeight());
 	 	 float pixIni, pixEnd, aux;
 	 	 for (int j=0; j<=imp.getWidth()-1;j++){
			 for (int i=0; i<=imp.getHeight()-1;i++){
				 for (int k=2; k<=imp.getNSlices(); k++){
					 imp.setSlice(k-1);
					 pixIni=(float)imp.getProcessor().getPixel(j,i);
					 imp.setSlice(k);
					 pixEnd=(float)imp.getProcessor().getPixel(j,i);
					 aux=R.getPixelValue(j, i)+(pixIni-pixEnd);
					 R.putPixelValue(j, i, aux);
					 }
				 
				}
		 }
		 ImagePlus impR = new ImagePlus("R",R);

	 	 impR.show();
	 	 return;
	}
	 
	}		 
		
		 
	  	 
		 	  	 
		 



	

		

  
		
 