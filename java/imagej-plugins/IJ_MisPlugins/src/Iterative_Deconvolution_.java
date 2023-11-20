
//import edu.emory.mathcs.jtransforms.fft.FloatFFT_3D;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor; 
public class Iterative_Deconvolution_ implements PlugInFilter{
  
	public void run(ImageProcessor ip) {
		// TODO Auto-generated method stub
		//array = imp.get
		//FloatFFT_3D fft3d = new FloatFFT_3D(imp.getStackSize(),
		//						imp.getHeight(), imp.getWidth());		
	}

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G+DOES_16;
	}
}
