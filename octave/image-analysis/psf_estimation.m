function PSF_PARAMETERS = psf_estimation(IMAGE,OBJECT,PSF,THETA_INI,METHOD="ml",TOL=1e-3,MAX_ITER=20) 
    ## IMAGE is the acquired image. Is the sum in the z axis 3D convolution between the psf and object
    ## PSF is a model of the point spread function of the microscope
    ## TOL is the minimal error in the estimated object
    ## MAX_ITER is the highest amount of iterations to be run if the specified tolerance value (TOL) has not been reached
    ## Returns 
    ## ERROR is a vector showing the error for the estimated object
    ## Author: Javier Eduardo Diaz Zamboni
    ## Created: 2017/04/20
    ## Version: 0.1

    ## PSF parameter estimation
    ## [THETA_EST ITERS THETA_CONV TIME]=fit_psf(DATA,MODEL,THETA_INI,METHOD,MAX_ITER=20,TOL=1e-3,H=0.001)
    model=@(THETA)(optical_section_imaging(OBJECT,PSF(THETA)));
    [THETA_EST ITERS THETA_CONV TIME]=fit_psf(IMAGE,model,THETA_INI,METHOD,TOL,MAX_ITER,H=0.001)
    PSF_PARAMETERS=THETA_EST
  endfunction
