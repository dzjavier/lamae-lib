function [ESTIMATED_OBJECT, COMPARISON_VALUE] = grestore(IMAGE, FORWARD_MODEL,varargin) 
  ## usage: [ESTIMATED_OBJECT, COMPARISON_VALUE] = grestore(IMAGE, FORWARD_MODEL,OPTIONS) 
  ## Nonlinear Restoration Method
  ## IMAGE is the acquired image
  ## FORWARD_MODEL
  ## TOL is the minimal error in the estimated object
  ## MAX_ITER is the highest amount of iterations to be run if the specified tolerance value (TOL) has not been reached
  ##
  ## Returns 
  ## ESTIMATED_OBJECT
  ## SE is a vector containing the square error for the difference between IMAGE and the estimated image
  ## Maintainer: Javier Eduardo Diaz Zamboni
  ## Version: 0.1
  ## Author: Javier Eduardo Diaz Zamboni
  ## Created: 2017/02/21
  options = struct("exponent",1,...
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
  warning ("off", "Octave:broadcast");

  im_size=size(IMAGE);
  IMAGE_aux=IMAGE;
  if (~strcmp(options.noise_filtering,""))
    switch options.noise_filtering
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
        IMAGE=abs(ifft2(fft2(IMAGE).*abs(fft2(w,im_size(1),im_size(2)))));
  otherwise
    error("'%s' is an invalid value for NOISE_FILTERING",options.noise_filtering)
    endswitch
  endif

  ESTIMATED_OBJECT = zeros(size(IMAGE));
  current_estimated_obj = zeros(size(IMAGE));
  next_estimated_obj = IMAGE_aux;
  estimated_image = zeros(size(IMAGE));
  SE = 1e6*ones(1, options.max_iter);
  reached_TOL = false;
  iter = 1;
  while ((iter <= options.max_iter) && (reached_TOL!=true))
    current_estimated_obj = next_estimated_obj;
    estimated_image = FORWARD_MODEL(current_estimated_obj);

    next_estimated_obj = current_estimated_obj.*(IMAGE./estimated_image).^options.exponent;

    ## filtering

    if (~(strcmp(options.noise_filtering,""))&&(mod(iter,options.iterations_per_filtering)==0))
  switch options.noise_filtering
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
    iter += 1;
  endwhile
  warning ("on", "Octave:broadcast");
  ESTIMATED_OBJECT = next_estimated_obj;
endfunction
