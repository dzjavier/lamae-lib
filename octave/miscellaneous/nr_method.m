function [X iteration x_error] = nr_method(F,FPRIME,X0,TOL=10e-3,MAX_ITER=20)
    iteration=1;
    x_error=inf;
    XOLD=X0;
    while ((iteration<MAX_ITER)&&(x_error>TOL))
      X=XOLD-F(XOLD)/FPRIME(XOLD);
      x_error=abs(X-XOLD);
      XOLD=X;
      iteration+=1;
    endwhile
endfunction
