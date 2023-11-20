import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.StackConverter;

/**
 * @author Javier E Diaz-Zamboni
 * @version 0.0.1
 */
public class Stack_to_Stack_ implements PlugInFilter {

	private ImagePlus imp;
	@Override
	public void run(ImageProcessor arg0) {
		if (IJ.versionLessThan("1.32c"))
			return;
		 		
		int[] wList = WindowManager.getIDList();
        
 		if (wList == null){
            IJ.noImage();
            return;
        }
        String[] titles = new String[wList.length];
        for (int i = 0; i < wList.length; i++){
            ImagePlus imp = WindowManager.getImage(wList[i]);
            if (imp != null)
                titles[i] = imp.getTitle();
            else
                titles[i] = "";
        }
		String titleImage = Prefs.get("iterativedeconvolve3d.titleImage", titles[0]);
		int imageChoice = 0;
        for (int i = 0; i < wList.length; i++){
			if(titleImage.equals(titles[i])){
				imageChoice = i;
				break;
			}
		}
        GenericDialog gd = new GenericDialog("New Stack from Stack", IJ.getInstance());
        gd.addChoice("Source stack",titles,titles[imageChoice]);
        gd.addNumericField("Average", 3, 4);
        gd.showDialog();

        if (gd.wasCanceled())
            return;

        ImagePlus result = WindowManager.getImage(wList[gd.getNextChoiceIndex()]);
        int nAverage = (int)gd.getNextNumber();
        result.setDimensions(1, imp.getStackSize()/nAverage, 1);
        StackConverter resultsc = new StackConverter(result);
        StackConverter impsc = new StackConverter(imp);
        resultsc.convertToGray32();
        impsc.convertToGray32();
        FloatProcessor fp = new FloatProcessor(imp.getWidth(), imp.getHeight());
        int counter=1;
        for (int k=1; k<imp.getStackSize()-3;++k){
           	result.setSlice(counter);
        	imp.setSlice(k);
        	result.setProcessor(imp.getProcessor());
        	imp.setSlice(k+1);
        	result.getProcessor();
        	counter+=1;
        }
        
        
	}

	@Override
	public int setup(String arg0, ImagePlus arg1) {
		imp=arg1;
		return DOES_8G+DOES_16+DOES_32+STACK_REQUIRED;
	}
// ...


}