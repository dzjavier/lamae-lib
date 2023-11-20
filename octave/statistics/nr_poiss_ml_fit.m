function [THETA_EST ITER THETA_CONV]=nr_poiss_ml_fit(Q,Q_BAR,THETA_INI,TOL=1e-3,MAX_ITER=20,H=0.001)
  ## usage: [THETA_EST ITER THETA_CONV]=nr_poiss_ml_fit(Q,Q_BAR,THETA_INI,TOL=1e-3,MAX_ITER=20,H=0.001)
  THETA_EST=THETA_INI;
  THETA_AUX=inf;
  ITER=1;
  THETA_CONV=zeros(MAX_ITER,1);
  while ((ITER<=MAX_ITER) && (abs(THETA_AUX-THETA_EST)>TOL))
    THETA_AUX=THETA_EST;
    DQ1=dfx(Q_BAR,THETA_AUX,1,H);
    DQ2=df2x2(Q_BAR,THETA_AUX,1,H);
    Q_Q_BAR_1=(Q(:)./Q_BAR(THETA_AUX)(:)-1);
    Q_Q_BAR_2=Q(:)./(Q_BAR(THETA_AUX).^2)(:);
    NUMERATOR=sum((DQ1(:).*Q_Q_BAR_1(:)));
    DENOMINATOR=sum((DQ2(:).*Q_Q_BAR_1(:))-((DQ1(:).^2).*Q_Q_BAR_2(:)));
    THETA_EST-=NUMERATOR/DENOMINATOR;
    THETA_CONV(ITER)=THETA_EST;
    ITER+=1;
  endwhile
  if (ITER<=MAX_ITER)
    THETA_CONV(ITER:MAX_ITER)=[];
  endif
endfunction
