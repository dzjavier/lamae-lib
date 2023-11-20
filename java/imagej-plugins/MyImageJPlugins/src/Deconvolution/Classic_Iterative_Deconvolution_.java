package Deconvolution;


import edu.emory.mathcs.jtransforms.fft.FloatFFT_3D;
import ij.IJ;
import ij.ImagePlus;

import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.StackConverter;

public class Classic_Iterative_Deconvolution_ implements PlugInFilter{
  
	
	private ImagePlus imp;
	public void run(ImageProcessor ip) {
		//TODO Divide this method's code, it is too long.
	
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
        gd.addChoice("Raw stack ",titles,titles[imageChoice]);
        gd.addChoice("PSF stack ",titles,titles[psfChoice]);
        gd.addNumericField("Tolerance", 0.001, 4);
        gd.addNumericField("Maximum iteration number", 100, 0);
        gd.addNumericField("Wiener regularization paramater K", 0.1, 3);
        gd.addNumericField("Apply Wiener filter each ", 10, 0,3,"iterations");
        gd.addCheckbox("Show iterations results",true);
        
        gd.showDialog();

        if (gd.wasCanceled())
            return;
        
        ImagePlus rawStack = WindowManager.getImage(wList[gd.getNextChoiceIndex()]);
        ImagePlus psfStack = WindowManager.getImage(wList[gd.getNextChoiceIndex()]);
        double tolerance = gd.getNextNumber();
        int maximumIterations = (int) gd.getNextNumber();
        float wienerKParameter = (float) gd.getNextNumber();
        int wienerIter =(int)gd.getNextNumber();
        boolean showResults = gd.getNextBoolean();
        System.out.println(showResults);
        StackConverter rawsc = new StackConverter(rawStack);
		StackConverter psfsc = new StackConverter(psfStack);
		rawsc.convertToGray32();
		psfsc.convertToGray32();
	        
        int nRawPixels = rawStack.getStackSize()*rawStack.getHeight()*rawStack.getWidth();
		
/////////////////// PSF NORMALIZATION /////////////////////////////
		
//		System.out.println("psf min max");
//		System.out.println(minStackValue(psfStack));
//		System.out.println(maxStackValue(psfStack));
		normalize(psfStack);

////////////// END PSF NORMALIZATION ///////////////////////////////

/////////////////// DATA NORMALIZATION /////////////////////////////
		
		
//		System.out.println("data min max");
//		System.out.println(minStackValue(rawStack));
		System.out.println(maxStackValue(rawStack));
		float maxIntensitySignalValue=maxStackValue(rawStack)-minStackValue(rawStack);
		System.out.println(maxIntensitySignalValue);
		normalize(rawStack);
	
////////////// END DATA NORMALIZATION ///////////////////////////////		
		
		
//////////////////// PSF RESIZING AND CENTERING /////////////////////////////
		ImagePlus enlargedPSF = NewImage.createFloatImage("Enlarged PSF", rawStack.getWidth(), 
				rawStack.getHeight(), rawStack.getStackSize(), NewImage.FILL_BLACK);
		stackCentredEnlarger(psfStack, enlargedPSF);        
		//enlargedPSF.show();
////////////////////  END PSF RESIZING AND CENTERING	/////////////////////////////        
		
		float[] PSFArray1; 
		float[] PSFArray2 = new float[nRawPixels*2];
		
		float[] rawArray1; 
		float[] rawArray2 = new float[nRawPixels*2];		
		float[] rawArray3 = new float[nRawPixels*2];
		float[] rawArray4 = new float[nRawPixels*2];
		float[] ImData = new float[nRawPixels];
		
		int counter1 = 0;
		
	    
		for (int k=1;k<=enlargedPSF.getStackSize();k++){
        	PSFArray1 = (float[]) enlargedPSF.getStack().getPixels(k);
        	rawArray1 = (float[]) rawStack.getStack().getPixels(k);
        	System.arraycopy(PSFArray1,0, PSFArray2, counter1, PSFArray1.length);
        	System.arraycopy(rawArray1,0, rawArray2, counter1, PSFArray1.length);
        	counter1=counter1+PSFArray1.length;
        }
		
		//System.arraycopy(rawArray2,0, rawArray3, 0, nRawPixels);
		System.arraycopy(rawArray2,0, ImData, 0, nRawPixels);
		System.arraycopy(rawArray2,0, rawArray4, 0, nRawPixels);
		
		FloatFFT_3D fft3d = new FloatFFT_3D(enlargedPSF.getStackSize(),enlargedPSF.getHeight(),
							enlargedPSF.getWidth());
		fft3d.realForwardFull(PSFArray2);
		fft3d.realForwardFull(rawArray4);
        float pastError=0;
        float presentError=1000;
        float gamma=0;
    	float A = 0.5f;
    	float A_2 = 0.25f;
    	int iter=1;
    	int iter5=3+wienerIter; // because the first three iterations
    	float maxEstimatorValue=0;
        
    	ResultsTable rt = new ResultsTable();
        
        while((Math.abs(presentError-pastError)>tolerance) && (iter<maximumIterations) ){
        //while (iter<50) {        	
        	if (iter<=1) fft3d.realForwardFull(rawArray2);
        	else fft3d.complexForward(rawArray2);
        	
          	counter1=1;
          		 
        	int sliceStride=rawStack.getWidth()*rawStack.getHeight()*2;
        	int rowStride=2*rawStack.getWidth();
        	int reCounter=0;
        	int imCounter=0;
        
        	float reOTF=0;

        	// TODO 3DConvolution and wiener filter may be joined in one control cycle.
///////////////////// 3D CONVOLUTION /////////////////////////        
        	for (int k=0;k<rawStack.getStackSize();k++){
        		for (int i=0;i<rawStack.getHeight();i++){           		
        			for (int j=0;j<rawStack.getWidth();j++){
        				reCounter=k*sliceStride + i*rowStride + 2*j;
        				imCounter=k*sliceStride + i*rowStride + 2*j+1;
        				reOTF=(float)Math.sqrt(Math.pow((double)PSFArray2[reCounter],2));
        				rawArray3[imCounter] = rawArray2[imCounter]*reOTF;
        				rawArray3[reCounter] = rawArray2[reCounter]*reOTF;
        				}
           			}            
        	}
/////////////////// END 3D CONVOLUTION /////////////////////////        
//TODO: Bibliography recommend applying gaussian filter at 5-10 iterations cycles, instead of wiener filter
/////////////////// WIENER FILTER /////////////////////////////
        	if ((iter<4)||(iter==iter5)) {
        	//if (iter<4) {	
        		for (int k=0;k<rawStack.getStackSize();k++){
        			for (int i=0;i<rawStack.getHeight();i++){
        				for (int j=0;j<rawStack.getWidth();j++){
        					reCounter=k*sliceStride + i*rowStride + 2*j;
        					imCounter=k*sliceStride + i*rowStride + 2*j+1;
        					reOTF=(float)Math.sqrt(Math.pow((double)PSFArray2[reCounter],2));
        					rawArray3[imCounter] = (rawArray4[imCounter]-rawArray3[imCounter])/(reOTF*reOTF+wienerKParameter);
        					rawArray3[reCounter] = (rawArray4[reCounter]-rawArray3[reCounter])/(reOTF*reOTF+wienerKParameter);
        					}
        				}
        			}
        		if (iter>3)iter5+=wienerIter;
        		}
////////////////// END WIENER FILTER //////////////////////////        	

        	
       	
        	fft3d.complexInverse(rawArray2, true);
        	fft3d.complexInverse(rawArray3, true);

        	float max=rawArray3[0];
        	float min=rawArray3[0];
        	for (int k=0;k<rawStack.getStackSize();k++){
        		for (int i=0;i<rawStack.getHeight();i++){           		
        			for (int j=1;j<rawStack.getWidth();j++){
        				reCounter=k*sliceStride + i*rowStride + 2*j;
        				if (max<rawArray3[reCounter]){
        					max=rawArray3[reCounter];
           					}
        				if (min>rawArray3[reCounter]){
        					min=rawArray3[reCounter];
           					}
        				}
           			}
        		}
        
///////////////////// ESTIMATOR DETERMINATION ////////////////////
        	int counter=0;
        	float maxError=0;
        	maxEstimatorValue=0;
        	float estimateError=0;
        	float auxEstimateError=0;
        	for (int k=0;k<rawStack.getStackSize();k++){
        		for (int i=0;i<rawStack.getHeight();i++){           		
        			for (int j=0;j<rawStack.getWidth();j++){
        				reCounter=k*sliceStride + i*rowStride + 2*j;
        				imCounter=k*sliceStride + i*rowStride + 2*j+1;
        				//TODO: This requires more analysis. Which is the best relaxation function?
//        				if (rawArray3[reCounter]/max<0.75)gamma=1;
//        				else gamma=0;
        				//gamma=(float) (0.5-0.5*Math.tanh(10*(rawArray3[reCounter]/max-0.8)));
        				//gamma = 1-rawArray3[reCounter]/max;
        				//gamma=(float) (1/Math.sqrt(2*Math.PI)*Math.exp(-25.0*Math.pow(rawArray3[reCounter]/max-A,2))/0.5);
        				gamma=(float)( 1-Math.pow((double)((rawArray3[reCounter]-min)/(max-min)-A), 2)/A_2);
        				if ((iter<4)||(iter==iter5)) {
        					rawArray2[reCounter]=rawArray2[reCounter]+rawArray3[reCounter];
        				}
        				rawArray2[reCounter]=rawArray2[reCounter]+gamma*(ImData[counter]-(rawArray3[reCounter]-min)/(max-min));
        				//rawArray2[reCounter]=rawArray2[reCounter]*(ImData[counter]/(rawArray3[reCounter]/max));//Gold update
        				
        				// positivity
        				if (rawArray2[reCounter]<0){
        					rawArray2[reCounter]=0;
        				}
        				if (maxEstimatorValue<rawArray2[reCounter]){
        					maxEstimatorValue=rawArray2[reCounter];
        				}
        				maxError=Math.max(ImData[counter]-(rawArray3[reCounter]-min)/(max-min),maxError);
        				float aux=Math.abs(ImData[counter]-(rawArray3[reCounter]-min)/(max-min));
        				//if (aux<tolerance)aux=0;
        				estimateError+=aux;
        				auxEstimateError+=ImData[counter];
        				rawArray2[imCounter]=0;
        				//rawArray3[counter]=rawArray2[reCounter];
        				counter++;
        				}
        			}            
        		}
        	estimateError/=auxEstimateError;
        	rt.incrementCounter();
          	rt.addValue("iter",iter);
          	rt.addValue("Maximum voxel error",Math.abs(presentError-pastError));
          	rt.addValue("Estimate error",estimateError);
/////////////////// END ESTIMATOR DETERMINATION ////////////////////
        	pastError=presentError;
        	presentError=maxError;
//        	System.out.print("iter: ");System.out.print(iter);
//        	System.out.print(" preError-pastError: ");System.out.print(Math.abs(presentError-pastError));
//        	System.out.print(" error estimate: ");System.out.println(estimateError);
        	iter++;
        } // end while
      	if (showResults) rt.show("Resultados");      
  		fft3d.complexInverse(PSFArray2, true);
		
		
		ImagePlus newpsf2 = NewImage.createFloatImage("psf nueva 2", rawStack.getWidth(), 
        		rawStack.getHeight(), rawStack.getStackSize(), NewImage.FILL_BLACK);
        ImagePlus conv = NewImage.createFloatImage("Deconvolved stack", rawStack.getWidth(), 
        		rawStack.getHeight(), rawStack.getStackSize(), NewImage.FILL_BLACK);
        
        counter1 = 0;
        int counter2 = 0;
             
        for (int k=1;k<=rawStack.getStackSize();k++){
        	//Arrays.fill(psfArray1, 0, psfArray1.length-1,0.0f);
        	PSFArray1 = (float[]) newpsf2.getStack().getPixels(k);
        	rawArray1 = (float[]) conv.getStack().getPixels(k);
        	for (int j=0;j<PSFArray1.length;j++){
        			PSFArray1[j] = PSFArray2[counter2];
        			rawArray1[j] = maxIntensitySignalValue*rawArray2[counter2]/maxEstimatorValue;
        			counter2 += 2;
       			
        	}
            counter2 = k*(rawStack.getWidth()*rawStack.getHeight())*2;
            //System.out.println(k);
            //newpsf2.getStack().setPixels(psfArray1, k);
        }
        // normalize(conv);
        // normalize(rawStack);
        //conv.setDisplayRange(0, 1);
        
        System.out.println(maxStackValue(conv));
        System.out.println(minStackValue(conv));
        conv.setDisplayRange(0, maxIntensitySignalValue);
        conv.show();
//        newpsf2.setDisplayRange(0, 1);
//        newpsf2.show();
        
        //imp.show();	        
	}
	
	public void stackCentredEnlarger(ImagePlus source, ImagePlus destination){
	
		
		if (destination.getWidth()>=source.getWidth() && destination.getHeight()>=source.getHeight()
														&& destination.getStackSize()>=source.getStackSize())
		{	
	        float[] sourceSliceData;
	        float[] destinationSliceData = new float[destination.getWidth()*destination.getHeight()];
	        int W = destination.getWidth(); int w = source.getWidth();
	        int H = destination.getHeight(); int h = source.getHeight();
	        int D = destination.getStackSize(); int d = source.getStackSize();
	        int posx = (W-w)/2;
	        int posy = (H-h)/2;
	        int posz = (D-d)/2;
	        int counter1=0;
	        int counter2=W*posy+posx;

	        for (int k=1;k<=d;k++){
	            sourceSliceData= (float[]) source.getStack().getPixels(k);
	            destinationSliceData = (float[]) destination.getStack().getPixels(k+posz);
	            for (int j=0;j<d;j++){
	        		System.arraycopy(sourceSliceData,counter1, destinationSliceData, counter2, source.getWidth());
	        		counter1=counter1+source.getWidth();
	        		counter2=counter2+destination.getWidth();
	        	}
	        	counter1=0;
	        	counter2=W*posy+posx;
	        }
	     }
		else {
			System.out.println("stackEnlarger: Error in Destination's new dimensions");
		}
		
       
	}
	public float maxStackValue(ImagePlus source){
		int numberOfElements = imp.getWidth()*imp.getHeight()*imp.getStackSize();
		float[] dataArray = new float[numberOfElements];
		float [] sliceArray;
		int counter=0;
		for (int k=1;k<=imp.getStackSize();k++){
        	sliceArray = (float[]) imp.getStack().getPixels(k);
        	System.arraycopy(sliceArray,0, dataArray, counter, sliceArray.length);
       		counter=counter+imp.getWidth()*imp.getHeight();       		
        }
		
		float maxValue = dataArray[0];
		for (int j=1;j<dataArray.length;j++){
        	maxValue=Math.max(maxValue, dataArray[j]);       		
        
        }
		return maxValue;
	}
	public float minStackValue(ImagePlus source){
		int numberOfElements = imp.getWidth()*imp.getHeight()*imp.getStackSize();
		float[] dataArray = new float[numberOfElements];
		float [] sliceArray;
		int counter=0;
		for (int k=1;k<=imp.getStackSize();k++){
        	sliceArray = (float[]) imp.getStack().getPixels(k);
        	System.arraycopy(sliceArray,0, dataArray, counter, sliceArray.length);
       		counter=counter+imp.getWidth()*imp.getHeight();       		
        }
		
		float minValue = dataArray[0];
		for (int j=1;j<dataArray.length;j++){
        	minValue=Math.min(minValue, dataArray[j]);       		
        
        }
		return minValue;
	}
	public void normalize(ImagePlus imp){
		int numberOfElements = imp.getWidth()*imp.getHeight()*imp.getStackSize();
		float[] dataArray = new float[numberOfElements];
		float [] sliceArray;
		int counter=0;
		for (int k=1;k<=imp.getStackSize();k++){
        	sliceArray = (float[]) imp.getStack().getPixels(k);
        	System.arraycopy(sliceArray,0, dataArray, counter, sliceArray.length);
       		counter=counter+imp.getWidth()*imp.getHeight();       		
        }
		
		float maxValue = dataArray[0];
		float minValue = dataArray[0];
		for (int j=1;j<dataArray.length;j++){
        	maxValue=Math.max(maxValue, dataArray[j]);       		
        	minValue=Math.min(minValue, dataArray[j]);
        }
		float sum = 0;
		for (int j=0;j<dataArray.length;j++){
			dataArray[j]= (dataArray[j]-minValue)/(maxValue-minValue);
			sum+=dataArray[j];
        }
///////////////////// GAIN ////////////////////		
//		for (int j=0;j<dataArray.length;j++){
//			dataArray[j]= dataArray[j]/sum;
//        }
///////////////////// END GAIN ////////////////////////		
		counter=0;
		for (int k=1;k<=imp.getStackSize();k++){
        	sliceArray= (float[]) imp.getStack().getPixels(k);
        	System.arraycopy(dataArray,counter, sliceArray, 0, sliceArray.length);
       		counter=counter+imp.getWidth()*imp.getHeight();       		
        }		
	}
	
	public int setup(String arg, ImagePlus imp) {
		this.imp=imp;
		
		return DOES_8G+DOES_16+DOES_32+STACK_REQUIRED;
	}
}
