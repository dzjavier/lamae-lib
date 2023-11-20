function  IMAGE = optical_section_imaging(OBJECT, PSF)
    ## usage: IMAGE = optical_section_imaging(OBJECT, PSF)
    ## OBJECT is the specimen. 
    ## PSF is the point spread function of the microscope. 
    ## 
    ## Notes: 
    ## 
    ## Author: Javier Eduardo Diaz Zamboni
    ## Created: 2017/04/24
    ## Version: 0.1

    object_dims = size(OBJECT);
    psf_dims = size(PSF);
    new_object_dims = [2.^nextpow2n(object_dims(1)) 2.^nextpow2n(object_dims(2)) object_dims(3)];
    padded_object = resize(OBJECT,new_object_dims);
    transf_object = fft2(padded_object);

    OTF = fft2(PSF,new_object_dims(1),new_object_dims(2));

    transf_image = transf_object.*abs(OTF);
    space_image = sum(abs(ifft2(transf_image)),3); 
    IMAGE = resize(space_image,object_dims(1),object_dims(2));
    IMAGE(IMAGE<0) = 0;
  endfunction
