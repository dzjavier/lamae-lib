function [ESTIMATED_OBJECT PSF_PARAMETERS MSE] = bdeconv2(IMAGE, PSF, THETA_INI,TOL, MAX_ITER) 
  ## usage: [ESTIMATED_OBJECT PSF_PARAMETERS MSE] = bdeconv2(IMAGE, PSF, THETA_INI, TOL, MAX_ITER) 
  ## Blind Janson Van Cittert's algorithm - Nonlinear Deconvolution Method
  ## IMAGE is the acquired image
  ## PSF is the point spread function of the microscope
  ## THETA_INI intial estimates of PSF's parameters
  ## TOL is the minimal error in the estimated object
  ## MAX_ITER is the highest amount of iterations to be run if the specified tolerance value (TOL) has not been reached
  ##
  ## Returns 
  ## ESTIMATED_OBJECT
  ## ERROR is a vector showing the error for the estimated object

  ESTIMATED_OBJECT = zeros(size(IMAGE));
  current_estimated_obj = zeros(size(IMAGE));
  next_estimated_obj = zeros(size(IMAGE));
  estimated_image = zeros(size(IMAGE));
  MSE = 1e6*ones(1, MAX_ITER);
  reached_TOL = false;
  model=@(OBJECT,THETA_AUX)stationary_imaging(OBJECT,PSF(THETA_AUX));
  PSF_PARAMETERS=THETA_INI;

  A = max(IMAGE(:))/2;
  iter = 1;
  REPEAT=5;
  while ((iter <= MAX_ITER) && (reached_TOL!=true))
    if (iter == 1)
      current_estimated_obj = IMAGE;
    else
      current_estimated_obj = next_estimated_obj;
    endif

    estimated_image = stationary_imaging(current_estimated_obj,PSF(PSF_PARAMETERS));

    gamma = 1 - (estimated_image - A).^2 ./ (A^2);
    next_estimated_obj = current_estimated_obj + gamma .* (IMAGE-estimated_image);
    next_estimated_obj(next_estimated_obj< 0) = 0; # Restriccion de positividad
    MSE(iter) = sumsq(IMAGE(:)-estimated_image(:))/prod(size(IMAGE));

    ## PSF parameter estimation
    ## [THETA_EST ITERS THETA_CONV TIME]=fit_psf(DATA,MODEL,THETA_INI,METHOD,MAX_ITER=20,TOL=1e-3,H=0.001)
    next_estimated_obj=imsmooth(next_estimated_obj,"gaussian",0.45);
    if (rem(iter,REPEAT)==0)
      fit_psf_tol=sqrt(crlb_poisson(@(aux)(max(IMAGE(:))*PSF(aux)),PSF_PARAMETERS))	
      [PSF_PARAMETERS ITERS THETA_CONV TIME]=fit_psf(IMAGE,@(THETA_AUX)model(current_estimated_obj,THETA_AUX), PSF_PARAMETERS,"ml",fit_psf_tol);
      THETA_CONV
    endif

    if (MSE(iter)<=TOL)
      reached_TOL = true;
      MSE = MSE(1:iter);
    endif

    iter +=1;

  endwhile

  ESTIMATED_OBJECT = next_estimated_obj;

endfunction
