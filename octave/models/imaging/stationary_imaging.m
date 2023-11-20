function  IMAGE = stationary_imaging(OBJECT, PSF)
    ## usage: IMAGE = stationary_imaging(OBJECT, PSF)
    ## OBJECT is the specimen. 
    ## PSF is the point spread function of the microscope
    ## 
    ## Notes: 
    ## - For 1D objects, its non-singleton dimension must match 
    ## with the non-singleton dimension of the PSF (e.g. if 
    ## size(object) is 1xN, then size(PSF) must be 1xN).
    ## - The sum(PSF(:)) should be 1. 

    ## Author: Mauricio Tanus Mafud
    ## Maintainer: Javier Eduardo Diaz Zamboni
    ## Created: 2015/10/15
    ## Revision: 2016/05/31
    ## Version: 0.2
    
    object_dims = size(OBJECT);
    new_object_dims = 2.^nextpow2n(object_dims);
    padded_object = resize(OBJECT,new_object_dims);
    transf_object = fftn(padded_object);
    OTF = fftn(PSF,new_object_dims);
    transf_image = transf_object.*abs(OTF);
    space_image = abs(ifftn(transf_image)); 
    IMAGE = resize(space_image,object_dims);

    IMAGE(IMAGE<0) = 0;

  endfunction
