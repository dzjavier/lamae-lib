function ID=intensity_disk(N)
  ## usage: ID=intensity_disk(N) 
  ## N size of the image
  ## Returns 
  ## Author: Diaz-Zamboni Javier Eduardo
  ## Created: 2017/04/27
  ## Version: 0.1
  if (N>0)
    ID=zeros(N);
    X=linspace(-0.5,0.5,N);
    for i=1:N
      for j=1:N
        if (sqrt(X(i)^2+X(j)^2)<=0.5)
          ID(i,j)=1; 
        endif
      endfor
    endfor
  else
    ID=-1;
  endif
endfunction
