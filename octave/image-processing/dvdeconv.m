function [ESTIMATED_OBJECT, COMPARISON_VALUE] = dvdeconv(IMAGE, PSF,Z,varargin) 
  ## usage: [ESTIMATED_OBJECT, COMPARISON_VALUE] = dvdeconv(IMAGE, PSF,Z, OPTIONS) 
  ## Depth Variant Nonlinear Deconvolution Method
  ## IMAGE is the acquired image
  ## PSF is a function handle for computing the point spread function
  ## of the microscope.
  ## Z is a vector containing the depths for computing the PSF
  ## OPTIONS 
  ## Returns 
  ## ESTIMATED_OBJECT
  ## COMPARISON_VALUE is the mean scalar value of the image comparison
  ## criteria of each block.
  ## between the image and the estimated image
  ## Author: Javier Eduardo Diaz Zamboni
  ## Created: 2017/05/05
  ## Version: 0.1

  options = struct("exponent",1,"max_iter",50,"tol",1e-3,"window","hanning","window_size",15,"method","ml", "noise_filtering","","iterations_per_filtering",1,"comparison","se");

  ## count arguments
  if ((nargin-3)==1)
    options=test_function_options(options,varargin{1});
  else
    if ((nargin-3)>1)
  options=test_function_options(options,varargin);
    endif
  endif

  switch options.window
    case {"hanning"}
  w=hanning(options.window_size);
    case {"gaussian"}
  w=gausswin(options.window_size);
    otherwise 
  w=hanning(options.window_size);
  endswitch

  w=reshape(w,1,1,options.window_size);     
  COMPARISON_VALUE=0;
  K=size(IMAGE,3);
  T=length(Z);  
  if (T>K)
    error("LENGTH(Z) must be less or equal to SIZE(IMAGE,3)")
  endif
  if (mod(K,T)!=0)
    error(" SIZE(IMAGE,3)/(LENGTH(Z) must be an integer, by now")
  else
    block_size=K/T;
  endif
  warning ("off", "Octave:broadcast");
  ESTIMATED_OBJECT=zeros(size(IMAGE));
  bs_it=0;
  SE=0;
  for t=1:T
    if (t<T)
  aux_data=IMAGE(:,:,bs_it+1:bs_it+block_size+fix(options.window_size/2));
    else
  aux_data=IMAGE(:,:,bs_it+1:bs_it+block_size);
    endif
    ##aux_data(:,:,1:fix(options.window_size/2))=aux_data(:,:,1:fix(options.window_size/2)).*w(1,1,1:fix(options.window_size/2));
    ##aux_data(:,:,end-fix(options.window_size/2):end)=aux_data(:,:,end-fix(options.window_size/2):end).*w(1,1,end-fix(options.window_size/2):end);
    if (ismatrix(PSF))
  psf=PSF(:,:,:,t);
    else
  psf=PSF(Z(t));
    endif
    switch options.method
  case {"ml"}
    [aux_estimated_obj, COM_VAL_AUX]=mldeconv(aux_data,psf,"exponent",options.exponent,"max_iter", options.max_iter,"tol",options.tol,"comparison",options.comparison);
  case {"rl"}
    [aux_estimated_obj aux_estimated_psf COM_VAL_AUX]=rldeconv(aux_data,psf,"exponent",options.exponent/10,"max_iter", options.max_iter,"tol",options.tol,"comparison",options.comparison);

  case {"jvc"}
    [aux_estimated_obj, COM_VAL_AUX]=jvcdeconv(aux_data,psf,"max_iter", options.max_iter,"tol",options.tol,"noise_filtering",options.noise_filtering,"iterations_per_filtering",options.iterations_per_filtering,"comparison",options.comparison);
  case {"gold"}
    [aux_estimated_obj, COM_VAL_AUX]=gdeconv(aux_data,psf,"exponent",options.exponent,"max_iter", options.max_iter,"tol",options.tol,"noise_filtering",options.noise_filtering,"comparison",options.comparison);
  otherwise
    error("Invalid deconvolution method")
    endswitch

    if (bs_it==0)
  ESTIMATED_OBJECT(:,:,bs_it+1:bs_it+block_size+fix(options.window_size/2))=aux_estimated_obj;
    else
  aux_estimated_obj(:,:,1:fix(options.window_size/2))= aux_estimated_obj(:,:,1:fix(options.window_size/2)).*w(1,1,1:fix(options.window_size/2));
  ESTIMATED_OBJECT(:,:,bs_it+1:bs_it+block_size)+=aux_estimated_obj(:,:,1:block_size);
    endif
    if (t<T)
  ESTIMATED_OBJECT(:,:,bs_it+block_size+1:bs_it+block_size+fix(options.window_size/2))=aux_estimated_obj(:,:,block_size+1:block_size+fix(options.window_size/2)).*w(1,1,end-fix(options.window_size/2)+1:end);    
    endif
    bs_it+=block_size;
    COMPARISON_VALUE+=COM_VAL_AUX/T;
  endfor
  warning ("on", "Octave:broadcast");
endfunction
