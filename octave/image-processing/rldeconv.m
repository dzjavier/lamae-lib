function [ESTIMATED_OBJECT ESTIMATED_PSF COMPARISON_VALUE] = rldeconv(IMAGE, PSF, varargin) 
  ## usage: [ESTIMATED_OBJECT ESTIMATED_PSF COMPARISON_VALUE] = rleconv(IMAGE, PSF, OPTIONS) 
  ## Blind Deconvolution by Maximum Likelihood
  ## IMAGE is the acquired image
  ## PSF is an estimate of point spread function of the microscope
  ## TOL is the minimal error in the estimated object
  ## MAX_ITER is the highest amount of iterations to be run if the specified tolerance value (TOL) has not been reached
  ## Returns 
  ## ESTIMATED_OBJECT
  ## ESTIMATED_PSF
  ## Author: Javier Eduardo Diaz Zamboni
  ## Created: 2016/06/01
  ## Version: 0.1

  options = struct("exponent",0.1,...
	           "max_iter",50,...
		   "tol",1e-3,...
    		   "noise_filtering","gaussian",...
    		   "iterations_per_filtering",1,...
    		   "filter_size",3,...
		   "comparison","se",...
		   "gaussian_spread",0.4,...
		   "wiener_snr",100);
  if ((nargin-2)==1)
    options=test_function_options(options,varargin{1});
  else
    if ((nargin-2)>1)
  options=test_function_options(options,varargin);
    endif
  endif
  switch options.comparison
    case {"se"}
    case {"idiv"}
    otherwise
  error("%s is not a valid comparison method",options.comparison)
  endswitch


  psf_size=size(PSF);
  im_size=size(IMAGE);
  ESTIMATED_OBJECT = zeros(im_size);
  current_estimated_obj = zeros(im_size);
  next_estimated_obj = zeros(im_size);
  estimated_image = zeros(im_size);
  reached_TOL = false;
  sum_psf=sum(PSF(:));
  IMAGE_aux=IMAGE;
  if (~strcmp(options.noise_filtering,""))
    switch options.noise_filtering
  case {"wiener"}
    IMAGE_aux=wdeconv((IMAGE),PSF/sum(PSF(:)),"snr",options.wiener_snr);
  case {"gaussian"}
        if (im_size(1)>=options.filter_size)
          rows=options.filter_size;
        else
          rows=im_size(1);
        endif
        if (im_size(2)>=options.filter_size)
          cols=options.filter_size;
        else
          cols=im_size(2);
        endif
        w=fspecial("gaussian",[rows cols],options.gaussian_spread);
        IMAGE_aux=abs(ifft2(fft2(IMAGE).*abs(fft2(w,im_size(1),im_size(2)))));
  otherwise
    error("'%s' is an invalid value for NOISE_FILTERING",options.noise_filtering)
    endswitch
  endif

  iter = 1;

  while ((iter <= options.max_iter) && (reached_TOL!=true))
    if (iter == 1)
  current_estimated_obj = IMAGE_aux;
  current_estimated_psf= PSF;
  current_estimated_psf=resize(current_estimated_psf,im_size);
    else
  current_estimated_obj = next_estimated_obj;
  current_estimated_psf = next_estimated_psf;
    endif

    estimated_image=stationary_imaging(current_estimated_obj, current_estimated_psf);


    image_frac=IMAGE./estimated_image;

    ## object estimation 
    next_estimated_obj=current_estimated_obj.*(abs(stationary_imaging(image_frac,flipdim(current_estimated_psf,3))).^options.exponent);

    ## psf estimation    
    next_estimated_psf=current_estimated_psf.*(abs(stationary_imaging(image_frac,current_estimated_obj)).^options.exponent);
    next_estimated_psf/=(sum(next_estimated_psf(:))*sum_psf);
    ## filtering

    if (~(strcmp(options.noise_filtering,""))&&(mod(iter,options.iterations_per_filtering)==0))
    switch options.noise_filtering
    case {"wiener"}
          next_estimate_obj=wdeconv(next_estimated_obj,PSF/sum(PSF(:)),"snr",options.wiener_snr);
    case {"gaussian"}
          next_estimated_obj=abs(ifft2(fft2(next_estimated_obj).*abs(fft2(w,im_size(1),im_size(2)))));
    endswitch
    endif

    switch options.comparison
  case {"se"}
    COMPARISON_VALUE = sumsq(IMAGE(:)-estimated_image(:));
  case {"idiv"}
    COMPARISON_VALUE = i_divergence(IMAGE(:),estimated_image(:));
  otherwise
    error("%s is not a valid comparison method",options.comparison)
    endswitch

    iter +=1;

  endwhile

  ESTIMATED_OBJECT =next_estimated_obj;
  ESTIMATED_PSF= resize(next_estimated_psf,psf_size);
endfunction
