function [ESTIMATED_OBJECT ESTIMATED_PSF MSE_OBJ ] = bdeconv3(IMAGE, PSF_INI, P,TOL, MAX_ITER) 
    ## usage: [ESTIMATED_OBJECT ESTIMATED_PSF MSE_OBJ] = bdeconv3(IMAGE, PSF_INI, P,TOL, MAX_ITER) 
    ESTIMATED_OBJECT = zeros(size(IMAGE));
    ESTIMATED_PSF = zeros(size(PSF_INI));
    current_estimated_obj = zeros(size(IMAGE));
    next_estimated_obj = zeros(size(IMAGE));
    current_estimated_psf=zeros(size(PSF_INI));
    next_estimated_psf=zeros(size(PSF_INI));
    estimated_image = zeros(size(IMAGE));
    reached_TOL = false;
    psf_dims=size(PSF_INI);
    iter = 1;
    lambda=0;
    while ((iter <= MAX_ITER) && (reached_TOL!=true))
      if (iter == 1)
        current_estimated_obj = IMAGE;
        current_estimated_psf = resize(PSF_INI,size(IMAGE));
        current_estimated_psf/=sum(current_estimated_psf(:));
        next_estimated_psf=current_estimated_psf;
      else
        current_estimated_obj = next_estimated_obj;
        current_estimated_psf= next_estimated_psf;
        current_estimated_obj(current_estimated_obj<0)=0;
        current_estimated_psf(current_estimated_psf<0)=0;
      endif

      estimated_image=stationary_imaging(current_estimated_obj,current_estimated_psf);

      image_frac=IMAGE./estimated_image;
      if (lambda!=0)
        maximum_entropy_obj=lambda*current_estimated_obj.*log(current_estimated_obj);
        maximum_entropy_obj(isnan(maximum_entropy_obj))=0;
      endif
      next_estimated_obj=current_estimated_obj.*(abs(stationary_imaging(image_frac,current_estimated_psf)).^P);

      next_estimated_psf=current_estimated_psf.*(abs(stationary_imaging(image_frac,current_estimated_obj)).^P);

      next_estimated_psf/=sum(next_estimated_psf(:));

      MSE_OBJ(iter) = sumsq(IMAGE(:)-estimated_image(:))/prod(size(IMAGE)); 
##      MSE_OBJ(iter) = poiss_log_likelihood(IMAGE(:),estimated_image(:)); 
      if (iter>1)
        if ((MSE_OBJ(iter)<MSE_OBJ(iter-1)))
          reached_TOL = true;
          MSE_OBJ = MSE_OBJ(1:iter-1);
          iter
        endif
      endif
      iter +=1;

    endwhile
    
    ESTIMATED_OBJECT = current_estimated_obj;
    ESTIMATED_PSF = resize(current_estimated_psf,psf_dims);
  endfunction
