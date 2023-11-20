function [INTENSITY REL_ERR] = gl_psf(X,Y,Z,Lambda,NA,M,z_d,n_oil,t_oil,n_g,t_g,n_s,t_s,TOL)
    
    ## usage: INTENSITY = gl_psf(X,Y,Z,Lambda,NA,M,z_d,n_oil,t_oil,n_g,t_g,n_s,t_s,TOL)
    ## The INTENSITY resulting is not intensity normalized.
    ## IMAGE PARAMETERS
    ## X, Y and Z are the coordinates in the image space
    ##
    ## PHYSICAL PARAMETERS
    ## Lambda > 0 is the wavelength [um]
    ## NA > 0 is the numerical aperture
    ## M > 0 total magnification
    ## z_d > 0 is the tube length [um] (design)
    ## n_oil > 0 and n_oil > NA is the refraction index of the immersion oil (design)
    ## n_s > 0 and n_s > NA is the refraction index of the specimen
    ## t_s >= 0 is the depth where the object lies [um] measured from the coverslip
    ## DZ > 0 is the inter plane distance [um]
    ##  
    ## NUMERICAL PARAMETERS
    ## TOL is numerical tolerance for computing the Born and Wolf integral.
    ##
    ## Returns 
    ## Author: Diaz-Zamboni Javier Eduardo
    ## Created: 2014/10/23
    ## Version: 0.1
    ## parameters control
           
    INTENSITY=-1;
    REL_ERR=-1;
    if ((Lambda>0) && (NA>0) && (M>0)  && (n_s>0) && (TOL>0))
##    if ((Lambda>0) && (NA>0) && (M>0)  && (n_s>0) && (t_s>=0) && (TOL>0))
      K=2*pi./Lambda; ##  K: wavelength number 
      if (n_s<NA)
        rho_aux=n_s/NA;
      else 
        rho_aux=1;
      endif    
      a_aux = z_d(1)*NA/sqrt(M^2-NA^2);

      W = @(rho)(K.*gl_opd(rho,NA,M,n_oil,n_s,t_s,Z,t_oil,n_g,t_g,z_d));
      
      integrand = @(rho)(besselj(0,(K.*a_aux.*sqrt((X.^2).+(Y.^2)))/z_d(2).*rho).*exp(I*W(rho)).*rho);

      try
        INTENSITY = abs(1/z_d(2).*quadgk(integrand,0,rho_aux,"RelTol",TOL)).^2;
      catch
        disp("Gibson Lanni model, trying quadv integration");
        INTENSITY = abs(1/z_d(2).*quadv(integrand,0,rho_aux,TOL)).^2;     
      end_try_catch

    else
      usage("check parameter values");
    endif  

  endfunction
