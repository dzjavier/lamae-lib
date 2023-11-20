function  IMAGE3D = deproject_optical_section(IMAGE2D, PSF)
    image3d_dims = [size(IMAGE2D)(1) size(IMAGE2D)(2) size(PSF)(3)];
    psf_dims = size(PSF);
    PSF/=sum(PSF(:));
    new_object_dims = [2.^nextpow2n(image3d_dims(1)) 2.^nextpow2n(image3d_dims(2)) image3d_dims(3)];
    padded_object = resize(IMAGE2D,[new_object_dims(1) new_object_dims(2)]);
    transf_object = fft2(padded_object);
    OTF = fft2(PSF,[new_object_dims(1) new_object_dims(2)]);
    transf_image = transf_object.*abs(OTF);
    IMAGE3D = abs(ifft2(transf_image)); 
    IMAGE3D = resize(IMAGE3D,image3d_dims(1),image3d_dims(2),image3d_dims(3));
  endfunction
