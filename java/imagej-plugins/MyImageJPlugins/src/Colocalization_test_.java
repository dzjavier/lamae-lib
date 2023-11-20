import ij.*;
import ij.gui.*;
import ij.plugin.*;
import ij.process.*;
import ij.measure.ResultsTable;


public class  Colocalization_test_ implements PlugIn{

	
	public void run(String arg){
        
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
		String titlePSF = Prefs.get("iterativedeconvolve3d.titlePSF", titles[0]);
		int psfChoice = 0;
        for (int i = 0; i < wList.length; i++){
			if(titlePSF.equals(titles[i])){
				psfChoice = i;
				break;
			}
		}
			
		GenericDialog gd = new GenericDialog("Deconvlove 3D", IJ.getInstance());
        gd.addChoice("Canal Verde ",titles,titles[imageChoice]);
        gd.addChoice("Canal Rojo ",titles,titles[psfChoice]);
        gd.showDialog();

        if (gd.wasCanceled())
            return;
        
        ImageProcessor ChGreen = WindowManager.getImage(wList[gd.getNextChoiceIndex()]).getProcessor();
        ImageProcessor ChRed = WindowManager.getImage(wList[gd.getNextChoiceIndex()]).getProcessor();
        int w1=ChGreen.getWidth();
        int w2=ChRed.getWidth();
        int h1=ChGreen.getHeight();
        int h2=ChRed.getHeight();
        if (w1==w2 && h1==h2){
        	
        	ImagePlus Merge = NewImage.createRGBImage("Merge", w1, h1, 1, NewImage.FILL_BLACK);
        	Merge.getProcessor().convertToRGB();
        	int sNumerator =0;
        	int sRed=0;
        	int sGreen=0;
        	for (int i=0;i<h1;i++)
        		for (int j=0;j<w1;j++){
        			int cGreen = ChGreen.getPixel(j, i) & 0x0000FF;
        			int cRed = ChRed.getPixel(j, i) & 0x0000FF;
        			int c = ((cRed<<16))+((cGreen<<8));
        			Merge.getProcessor().putPixel(j, i, c);
        			
        			sNumerator=sNumerator+cGreen*cRed;
        			sRed = sRed+(cRed*cRed);
        			sGreen = sGreen+(cGreen*cGreen);
        		}
        	 float k1 = (float) (sNumerator) / (float)sRed;
        	 float k2 = (float) (sNumerator) / (float)sGreen;
        	 float RR=k1*k2;
        	
        	ResultsTable rt = ResultsTable.getResultsTable();
    		rt.reset();
    		rt.incrementCounter();
    		rt.addValue("k1", k1);
    		rt.addValue("k2", k2);
    		rt.addValue("Coeficiente solapamiento R", RR);
    		rt.show("Resultados de la prueba de colocalizaci�n");
    		Merge.show();
    		}
    		
    	}
              
     }
		
 