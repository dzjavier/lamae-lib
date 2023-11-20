import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;


public class Autofluorescence_Extraction_ implements PlugInFilter {

	private ImagePlus imp;
	private float deltaT=3;
    public int setup(String arg0, ImagePlus arg1) {
		imp=arg1;
	  GenericDialog gd = new GenericDialog("Autofluorescence Extraction", IJ.getInstance());
      gd.addNumericField("Delta T (sec.):", deltaT, 2);
      gd.showDialog();

      if (gd.wasCanceled()) return 0;
         deltaT = (float)gd.getNextNumber();

		return NO_CHANGES+DOES_16+DOES_32+DOES_8G;
	}
	public void run(ImageProcessor arg0) {
				
		if (IJ.versionLessThan("1.32c"))
			return;
	 	 byte[] Ini,End;
	 	 float[]R;
	 	 ImageProcessor Aux = new FloatProcessor(imp.getWidth(),imp.getHeight());
	 	 R=(float[])Aux.getPixels();
	 	 float aux;
	 	 for (int k=2; k<=imp.getNSlices(); k++){
 	 		 imp.setSlice(k-1);
 	 		 Ini=(byte[])imp.getProcessor().getPixels();
			 imp.setSlice(k);
			 End=(byte[])imp.getProcessor().getPixels();
			for (int i=0;i<imp.getWidth()*imp.getHeight();i++){
			 //R[i]=R[i]+Math.abs(((float)Ini[i]-(float)End[i])/deltaT);
		     aux=(float)Ini[i]-(float)End[i];
		     if (aux>=0)R[i]=R[i]+(aux/deltaT);
			 //R[i]=R[i]+(float)Ini[i];
			 }
			 }
		 ImagePlus impR = new ImagePlus("R",Aux);
    	 impR.show();
	 	 return;
	}
	 
	}		 
		
		 
	  	 
		 	  	 
		 



	

		

  
		
 