function NEWV = nextpow2n(V)
  ## usage: NEWV = nextpow2n(V)
  ## This function is vectorized version of nextpow2n.
  if (isvector(V))
    L=length(V);
    NEWV=zeros(size(V));
    for j=1:L
      NEWV(j)=nextpow2(V(j));
    endfor
  endif
endfunction
