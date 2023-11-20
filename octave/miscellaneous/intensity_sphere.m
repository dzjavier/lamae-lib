function IS=intensity_sphere(N)
  ## usage: ID=intensity_sphere(N) 
  ## N size of the image
  ## Returns 
  ## Author: Diaz-Zamboni Javier Eduardo
  ## Created: 2017/04/27
  ## Version: 0.1
  if (N>0)
    IS=zeros(N,N,N);
    X=linspace(-0.5,0.5,N);
    for k=1:N
      for i=1:N
        for j=1:N
          if (sqrt(X(i)^2+X(j)^2+X(k)^2)<=0.5)
            IS(i,j,k)=1; 
          endif
         endfor
      endfor
    endfor
  else
    IS=-1;
  endif
endfunction
