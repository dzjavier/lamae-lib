function OPD=gl_opd(rho,NA,M,n_oil,n_s,t_s,DZ,t_oil,n_g,t_g,z_d)
    
    ## usage: OPD = gl_opd(rho,NA,M,n_oil,n_s,t_s,DZ,t_oil,n_g,t_g)
    ##
    ## PHYSICAL PARAMETERS
    ## rho normalized radius (0<=rho<=1) in the back focal plane
    ## NA > 0 is the numerical aperture
    ## M > 0 is the total magnification of the microscope
    ## s_pixel > 0 is the size of the pixel [um] at the camera
    ## z_d > 0 is the tube length [um] (design)
    ## z_dn > 0 is the tube length [um] (real)
    ## n_oil > 0 and n_oil > NA is the refraction index of the immersion oil (design)
    ## n_oiln > 0 and n_oiln > NA is the refraction index of the immersion oil (real)
    ## n_s > 0 and n_s > NA is the refraction index of the specimen
    ## n_g > 0 and n_g > NA is the refraction index of the coverslip (design)
    ## n_gn > 0 and n_gn > NA is the refracction index of the coverslip (real)
    ## t_s > 0 is the depth where the object lies [um].
    ## t_g > 0 is the coverslip thickness [um] (design).
    ## t_gn > 0 coverslip thickness [um] (real)
    ## t_oil >0 is the working distance [um] (design). 
    ## DZ amount of defocus [um]. 
    ## Returns 
    ## Author: Diaz-Zamboni Javier Eduardo
    ## Created: 2014/10/16 
    ## Version: 0.1
    ##  parameters control
    OPD=-1;
    ##  if ((Lambda>0) && (NA>0) && (M>0) && (z_d(1)>0) && (z_d(1)n>0) && (n_oil > 0) && (n_oiln>0) && (n_s>0) && (n_g>0) && (t_s>=0) && (t_g>0) && (t_gn>0))
    ## if ((n_oil>NA)&&(n_oiln>NA)&&(n_s>NA)&&(n_g>NA))
    OPD=0;
    sqrt_aux=sqrt(1-(NA*rho/n_oil(2)).^2);
    if (DZ!=0)
      OPD+=n_oil(2)*DZ*sqrt_aux;
    endif
    if (z_d(1)!=z_d(2))
      a=z_d(1)*NA/sqrt(M^2-NA^2);
      OPD+=(z_d(1)-z_d(2))*a^2*n_oil(2)^2/(z_d(1)*z_d(2)*NA^2)*sqrt_aux+a^2*rho.^2*(z_d(1)-z_d(2))/(2*n_oil(2)*z_d(1)*z_d(2));
    endif  
##    if (t_s!=0)
      OPD+=n_s*t_s*(sqrt(1-(NA*rho/n_s).^2)-(n_oil(2)/n_s)^2*sqrt_aux);
##    endif
    if (t_g(1)!=t_g(2)||(n_g(1)!=n_g(2)))
      OPD+=n_g(2)*t_g(2)*(sqrt(1-(NA*rho/n_g(2)).^2)-(n_oil(2)/n_g(2))^2*sqrt_aux)-(n_g(1)*t_g(1)*(sqrt(1-(NA*rho/n_g(1)).^2)-(n_oil(2)/n_g(1))^2*sqrt_aux));
    endif 
    if (n_oil(1)!=n_oil(2))
      OPD-=n_oil(2)*t_oil*(sqrt(1-(NA*rho/n_oil(2)).^2)-(n_oil(2)/n_oil(1))^2*sqrt_aux);
    endif
    ## else 
    ##  usage("some refraction index is less than the numerical aperture");
    ##  endif
    ##else 
    ##  usage("check parameter values");
    ## endif  
  endfunction
