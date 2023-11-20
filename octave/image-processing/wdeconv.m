function [ESTIMATED_OBJECT] = wdeconv(IMAGE, PSF, varargin) 
  ## usage: [ESTIMATED_OBJECT, MSE] = wdeconv(IMAGE, PSF,SNR=10) 
  ## Linear Deconvolution Method
  ## IMAGE is the acquired image
  ## PSF is the point spread function
  ## SNR is the signal noise ratio assuming gaussian independent noise
  ## Returns 
  ## ESTIMATED_OBJECT
  ## Author: Javier Eduardo Diaz Zamboni
  ## Created: 2003/09/23
  ## Version: 0.2
  ## Maintainer: Javier Eduardo Diaz Zamboni
  ## Revision: 2017/02/17
  options=struct("snr",10);

  ## count arguments 
  if ((nargin-2)==1)
    options=test_function_options(options,varargin{1});
  else
    if ((nargin-2)>1)
     options=test_function_options(options,varargin);
    endif
  endif

  ESTIMATED_OBJECT = IMAGE;
  Co = zeros(size(IMAGE));
  Ho = fftn(PSF,size(IMAGE));
  Go = fftn(IMAGE);
  Co = conj(Ho);
  Fo =  Go .* abs(Co ./ (abs(Ho).^2 + 1./options.snr));
  ESTIMATED_OBJECT = abs(ifftn(Fo));

endfunction
