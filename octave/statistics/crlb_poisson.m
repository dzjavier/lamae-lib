function CRLB=crlb_poisson(MODEL,THETA)
      
      ## usage: CRB=crlb_poisson(MODEL,THETA)
      ## Author: Javier Eduardo Diaz Zamboni <javierdiaz@bioingenieria.edu.ar>
      ## Version: 0.1
      ## Keywords: Cramer Rao Lower Bound (variance) for poisson distributed data 
      deriv_sq=dfx(MODEL,THETA,0,1e-10).^2;
      CRLB=1/sum(deriv_sq(:)./MODEL(THETA)(:));
  endfunction
