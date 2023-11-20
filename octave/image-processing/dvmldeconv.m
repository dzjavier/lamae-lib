function [ESTIMATED_OBJECT, MSE] = dvmldeconv(IMAGE, PSF,Z,varargin) 
  ## usage: [ESTIMATED_OBJECT, MSE] = dvmldeconv(IMAGE, PSF, OPTIONS) 
  ## Depth Variant Maximum Likelihood Nonlinear Deconvolution Method
  ## IMAGE is the acquired image
  ## PSF is a function handle for computing the point spread function
  ## of the microscope.
  ## Z is a vector containing the depths for computing the PSF
  ## OPTIONS 
  ## TOL is the minimal error in the estimated object
  ## MAX_ITER is the highest amount of iterations to be run if the specified tolerance value (TOL) has not been reached
  ## P is the exponent of the image fraction P=1 is Lucy-Richardson algorithm
  ## Returns 
  ## ESTIMATED_OBJECT
  ## ERROR is a vector of the sum of the square error for the 
  ## between the image and the estimated image
  ## Author: Javier Eduardo Diaz Zamboni
  ## Created: 2017/05/05
  ## Version: 0.1

  options = struct("exponent",1,"max_iter",50,"tol",1e-3,"window","hanning","window_size",21,"overlap",0.5);

  ## read the acceptable names
  optionNames = fieldnames(options);

  ## count arguments
  if (mod((nargin-3),2)!=0)
    error("dvmldeconv needs propertyName/propertyValue pairs")
  endif

  for pair = reshape(varargin,2,[]) # pair is {propName;propValue}
    inpName = lower(pair{1}); # make case insensitive
    if any(strcmp(inpName,optionNames))
      ## overwrite options. If you want you can test for the right class here
      ## Also, if you find out that there is an option you keep getting wrong,
      ## you can use "if strcmp(inpName,'problemOption'),testMore,end"-statements
      options.(inpName) = pair{2};
    else
      error("%s is not a recognized parameter name",inpName)
    endif
  endfor
  switch options.window
    case {"hanning"}
      w=hanning(options.window_size);
    case {"gaussian"}
      w=gausswin(options.window_size);
    otherwise 
      w=hanning(options.window_size);
  endswitch

  w=reshape(w,1,1,options.window_size);     
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
  ESTIMATED_OBJECT=zeros(size(IMAGE));        
  bs_it=0;
  for t=1:T
    tic;
    if (t<T)
    aux_data=IMAGE(:,:,bs_it+1:bs_it+block_size+fix(options.window_size/2));
    else
    aux_data=IMAGE(:,:,bs_it+1:bs_it+block_size);
    endif
    ##aux_data(:,:,1:fix(options.window_size/2))=aux_data(:,:,1:fix(options.window_size/2)).*w(1,1,1:fix(options.window_size/2));
    ##aux_data(:,:,end-fix(options.window_size/2):end)=aux_data(:,:,end-fix(options.window_size/2):end).*w(1,1,end-fix(options.window_size/2):end);
    [aux_estimated_obj, MSE_AUX]=mldeconv(aux_data,PSF(Z(t)),"exponent",options.exponent,"max_iter", options.max_iter,"tol",options.tol); 
    if (bs_it==0)
      ESTIMATED_OBJECT(:,:,bs_it+1:bs_it+block_size+fix(options.window_size/2))=aux_estimated_obj;
    else
      aux_estimated_obj(:,:,1:fix(options.window_size/2))= aux_estimated_obj(:,:,1:fix(options.window_size/2)).*w(1,1,1:fix(options.window_size/2));
      ESTIMATED_OBJECT(:,:,bs_it+1:bs_it+block_size)+=aux_estimated_obj(:,:,1:block_size);
    endif
     if (t<T)
      ESTIMATED_OBJECT(:,:,bs_it+block_size+1:bs_it+block_size+fix(options.window_size/2))=aux_estimated_obj(:,:,block_size+1:block_size+fix(options.window_size/2)).*w(1,1,end-fix(options.window_size/2)+1:end);    
    endif
    toc
    bs_it+=block_size;
    MSE_AUX;
  endfor
endfunction
