function [ESTIMATED_OBJECT, COMPARISON_VALUE] = jvcdeconv(IMAGE, PSF,varargin) 
  ## usage: [ESTIMATED_OBJECT, SE] = jvcdeconv(IMAGE, PSF, OPTIONS)
  ## Nonlinear Deconvolution Method
  ## IMAGE is the acquired image
  ## PSF is the point spread function 
  ## TOL is the minimal error accepted between actual data and in the estimated data
  ## MAX_ITER is the highest amount of iterations to be run if the specified tolerance value (TOL) has not been reached
  ##
  ## Returns 
  ## ESTIMATED_OBJECT
  ## SE is a vector containing the square error for the difference between IMAGE and the estimated image
  ## Maintainer: Javier Eduardo Diaz Zamboni
  ## Version: 0.3

  ## Revision: 2017/12/15
  ## History revision: Refactor. No noise filtering option when empty.

  ## Revision: 2017/05/19
  ## History revision: Adding idiv comparison method

  ## Revision: 2017/05/15
  ## History revision: Adding varargin parameter control

  ## Revision: 2017/02/20
  ## History revision: Adding square, abs and cittert relaxation functions.

  ## Revision: 2016/12/21
  ## History revision: Adding return MSE vector.

  ## Author: Mauricio Tanus Mafud
  ## Created: 2015/09/23

  options=struct("exponent",1,...
                 "relaxation","square",...
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

  im_size=size(IMAGE);
  ESTIMATED_OBJECT = zeros(im_size);
  current_estimated_obj = zeros(im_size);
  estimated_image = zeros(im_size);
  reached_TOL = false;
  IMAGE_aux = IMAGE;
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
  next_estimated_obj = IMAGE_aux;
  iter = 1;

  while ((iter <= options.max_iter) && (reached_TOL!=true))
    current_estimated_obj = next_estimated_obj;
    estimated_image = stationary_imaging(current_estimated_obj,PSF);
    switch options.relaxation
  case {"square"}
    A = max(IMAGE(:))/2; 
    gamma=1 - (current_estimated_obj - A).^2 ./ (A^2); ## Petter Jansson (1984);
  case {"abs"}
        A = max(IMAGE(:));
    gamma = 1 - (2/A)*abs(current_estimated_obj - A/2); ## Petter Jansson (1970)
  case {"cittert"}
    gamma = 1; ## Van Cittert 1931
  otherwise 
        error("Invalid value for RELAXATION");
    endswitch
    next_estimated_obj = current_estimated_obj + gamma .* (IMAGE-estimated_image).^options.exponent;
    next_estimated_obj(next_estimated_obj< 0) = 0;# forcing positivity constraint

    ## filtering

    if (~(strcmp(options.noise_filtering,""))&&(mod(iter,options.iterations_per_filtering)==0))
  switch options.noise_filtering
    case {"wiener"}
          next_estimate_obj=wdeconv(next_estimated_obj,PSF/sum(PSF(:)),"snr",options.wiener_snr);
    case {"gaussian"}
          next_estimated_obj=abs(ifft2(fft2(next_estimated_obj).*abs(fft2(w,im_size(1),im_size(2)))));
  endswitch
    endif
    iter += 1;
  endwhile
  switch options.comparison
    case {"se"}
      COMPARISON_VALUE = sumsq(IMAGE(:)-estimated_image(:));
    case {"idiv"}
      COMPARISON_VALUE = i_divergence(IMAGE(:),estimated_image(:));
    otherwise
      error("%s is not a valid comparison method",options.comparison)
  endswitch
  ESTIMATED_OBJECT = next_estimated_obj;
endfunction
