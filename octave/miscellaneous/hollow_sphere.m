function HS=hollow_sphere(NE,NI)
  ## usage: hollow_sphere(NE,NI) 
  ## NE is the external diameter. It is the final size of the 3D array.
  ## NI is de internal diameter.
  ## Returns 
  ## Author: Diaz-Zamboni Javier Eduardo
  ## Created: 2017/04/27
  ## Version: 0.1
  if (NE>NI)
    internal_sphere=intensity_sphere(NI);
    HS=intensity_sphere(NE);
    index_from=fix(NE/2)-fix(NI/2)+1;
    index_to=fix(NE/2)+fix(NI/2);
    HS(index_from:index_to,index_from:index_to,index_from:index_to)-=internal_sphere;
  else
    HS=-1;
  endif
endfunction
