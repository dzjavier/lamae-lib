function [THETA_EST ITERS THETA_CONV TIME]=fit_psf(DATA,MODEL,THETA_INI,METHOD,TOL=1e-3,MAX_ITER=20,H=0.001)
  
  ## usage: [THETA_EST ITERS THETA_CONV TIME]=fit_psf(DATA,MODEL,THETA_INI,METHOD,TOL=1e-3,MAX_ITER=20,H=0.001)
  ## MODEL is a function handle 
  ## METHOD could be "midiv" "ml" or "lsqr" string
  ## DATA must be correctly normalized for each METHOD, and MODEL must be normalized in the same way
  # Author: Javier Eduardo Diaz Zamboni <javierdiaz@bioingenieria.edu.ar>
  # This a generic function that allows to select a method to fit a PSF.
  # Version: 0.1
  # Keywords: i-divergence, maximum likelihood, least square optimization
  switch METHOD
    case {"midiv" "MIDIV"}
      tic;
      [THETA_EST ITERS THETA_CONV]=nr_idiv_fit(DATA,MODEL,THETA_INI,TOL,MAX_ITER,H);
      TIME=toc;
    case {"ml" "ML"}
      tic;
      [THETA_EST ITERS THETA_CONV]=nr_poiss_ml_fit(DATA,MODEL,THETA_INI,TOL,MAX_ITER,H);
      TIME=toc;           
    case {"lsqr" "LSQR"}
      tic;
      [THETA_EST ITERS THETA_CONV]=nr_lsqr_fit(DATA,MODEL,THETA_INI,TOL,MAX_ITER,H);
      TIME=toc;
  endswitch
endfunction
