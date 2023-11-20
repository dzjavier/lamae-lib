function [THETA_EST ITER THETA_CONV]=nr_lsqr_fit(DATA,MODEL,THETA_INI,TOL=1e-3,MAX_ITER=20,H=0.001)
  ## usage: [THETA_EST ITER THETA_CONV]=nr_lsqr_fit(DATA,MODEL,THETA_INI,MAX_ITER,TOL=1e-3,H=0.001)
  ## Author: Javier Eduardo Diaz Zamboni <javierdiaz@bioingenieria.edu.ar>
  ## Version: 0.1
  ## Keywords: least square, optimization
  ITER=1;
  THETA_EST=THETA_INI;
  THETA_CONV=zeros(MAX_ITER,1);
  THETA_AUX=inf;
  while((ITER<=MAX_ITER)&&(abs(THETA_EST-THETA_AUX)>TOL))
    THETA_AUX=THETA_EST;
    DIFF_AUX=DATA(:)-MODEL(THETA_AUX)(:);
    DEV1_MODEL=dfx(MODEL,THETA_AUX,1,H);
    DEV1_MODEL_SQR=DEV1_MODEL.^2;
    DEV2_MODEL=df2x2(MODEL,THETA_AUX,1,H);
    NUMERATOR =sum((DIFF_AUX(:).*DEV1_MODEL(:))(:));
    DENOMINATOR = sum(((DIFF_AUX(:).*DEV2_MODEL(:))-(DEV1_MODEL_SQR(:)))(:));
    THETA_EST-=NUMERATOR/DENOMINATOR;
    THETA_CONV(ITER)=THETA_EST;
    ITER+=1;
  endwhile
  if (ITER<=MAX_ITER)
    THETA_CONV(ITER:MAX_ITER)=[];
  endif
endfunction
