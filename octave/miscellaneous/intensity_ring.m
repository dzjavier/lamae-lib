function IR=intensity_ring(NE,NI)
  ## usage: ID=intensity_ring(NE,NI) 
  ## NE is the external diameter. It is size of the final square image array.
  ## NI is the internal diameter. 
  ## Returns 
  ## Author: Diaz-Zamboni Javier Eduardo
  ## Created: 2017/04/27
  ## Version: 0.1
  if (NE>NI)
    internal_disk=intensity_disk(NI);
    IR=intensity_disk(NE);
    if (mod(NI,2)==0)
      index_from=fix(NE/2)-fix(NI/2)+1;
    else
      index_from=fix(NE/2)-fix(NI/2);
    endif
    index_to=fix(NE/2)+fix(NI/2);
    IR(index_from:index_to,index_from:index_to)-=internal_disk;
  else
    error("NE must be greater than NI");
  endif
endfunction
