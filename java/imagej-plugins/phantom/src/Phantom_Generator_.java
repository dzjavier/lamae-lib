import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;

public class Phantom_Generator_ implements PlugIn{
	
	/* (non-Javadoc)
	 * @see ij.plugin.PlugIn#run(java.lang.String)
	 */
	@Override
	public void run(String arg0){

	 GenericDialog gd = new GenericDialog("Phantom generator",
			 IJ.getInstance());
	 gd.addNumericField("X dimension:", 256, 0);
	 gd.addNumericField("Y dimension:", 256, 0);
         gd.addNumericField("Number of Slices:", 1, 0);
	 gd.addNumericField("Number of frames:", 1, 0);
	 gd.addNumericField("Number of channels:", 1, 0);
	 gd.addCheckbox("Poisson intensity", true);
	 gd.showDialog();
	 int X=0;
	 int Y=0;
	 int Z=0;
	 int slices=0;
	 int frames =0;
	 System.out.print(frames);
	 int channels=0;
	 ImagePlus phantom; 
       
	 if (!gd.wasCanceled()){
		X=(int)gd.getNextNumber();
		Y=(int)gd.getNextNumber();
		
		slices=(int)gd.getNextNumber();
		frames=(int)gd.getNextNumber();
		channels=(int)gd.getNextNumber();
		if (gd.getNextBoolean()){
			phantom = NewImage.createShortImage("Phantom", X,Y, 
					 slices,NewImage.FILL_BLACK);
			int value=100000;
			for(int k=slices/4;k<3*slices/4;++k){
			  phantom.setSlice(k);	
				for(int i=Y/4;i<3*Y/4;++i)
					for(int j=X/4;j<3*X/4;++j)
						phantom.getProcessor().putPixel(j, i,value );
			}
			phantom.show();
		}
	 }
	 
//	 = NewImage.createFloatImage("Phantom", X,Y, 
//			 slices, NewImage.FILL_BLACK);
//	 
		
	}

}
