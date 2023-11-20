import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.FileWriter;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.gui.YesNoCancelDialog;
import ij.io.SaveDialog;
import ij.plugin.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.util.*;
import ij.text.*;
import ij.*;


public class ROI_to_data implements PlugInFilter  {

	ImagePlus imp;
	int Kend;
	int Kini;
	@Override
	public void run(ImageProcessor arg0) {
		
		Rectangle r = imp.getProcessor().getRoi();
		
		int[] data = new int[(Kend-Kini+1)*r.height*r.width];
		int counter = 0;
        for (int k=Kini;k<=Kend;k++){
			imp.setSlice(k);
			for (int j=r.x;j<r.x+r.width;j++)
				for (int i=r.y;i<r.y+r.height;i++){
					data[counter] = imp.getProcessor().getPixel(j,i);
					counter++;
			 }
         }
		SaveDialog sd = new SaveDialog("Save data...","data",".txt");
		String directory = sd.getDirectory();
		String filename = sd.getFileName();
		if (filename==null) return;
		IJ.showStatus("Saving: "+directory+filename);
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(directory+filename));
			for (int j=0;j<data.length;j++){
				bw.write(new Integer(data[j]).toString());
				//if (j!=data.length-1)bw.write(",");
				bw.newLine();				
			}
			
			bw.close();
		}catch (Exception e) {
			IJ.error("ROI to data",e.getMessage());
		}
			
	}

	@Override
	public int setup(String arg0, ImagePlus arg1) {
		Roi roi = arg1.getRoi();
		int flags=NO_CHANGES+DOES_16+DOES_8G+ROI_REQUIRED;
		if (roi==null) {
			return flags;	
			}
		if (roi.getType()>=Roi.LINE) {
			IJ.showMessage("ROI to data", "This command does not work with line selections.");
			return DONE;	
			}
		imp=arg1;
		Kend = imp.getNSlices();
		Kini=1;
		
	    GenericDialog gd = new GenericDialog("ROI to data setup");
		gd.addCheckbox("Require stack", false);
		gd.showDialog();
		if (gd.wasCanceled()) return DONE;
		boolean stackrequired = gd.getNextBoolean();
	    if (stackrequired){
			  Kend=imp.getNSlices();
			  flags=flags+STACK_REQUIRED;
		       }
		   else{
			Kend=imp.getSlice();
			Kini=imp.getSlice();
		  }
		return flags;
	}

			
}


