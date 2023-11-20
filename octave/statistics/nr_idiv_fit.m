function [THETA_EST ITER THETA_CONV]=nr_idiv_fit(DATA,MODEL,THETA_INI,TOL=1e-3,MAX_ITER=20,H=0.001)
   ## usage: [THETA_EST ITER THETA_CONV]=nr_idiv_fit(DATA,MODEL,THETA_INI,MAX_ITER=20,TOL=1e-3,H=0.001)
   ## Author: Javier Eduardo Diaz Zamboni <javierdiaz@bioingenieria.edu.ar>
   ## Version: 0.1
   ## Keywords: i-divergence, optimization
   ITER=2;
   THETA_EST=THETA_INI;
   THETA_AUX=inf;
   THETA_CONV=zeros(MAX_ITER,1);
   while((ITER<=MAX_ITER)&&(abs(THETA_EST-THETA_AUX)>TOL))
     THETA_AUX=THETA_EST;
     DEV1_MODEL=dfx(MODEL,THETA_AUX,1,H);
     DEV1_MODEL_SQR=DEV1_MODEL.^2;
     DEV2_MODEL=df2x2(MODEL,THETA_AUX,1,H);
     AUX1=MODEL(THETA_AUX);
     c=min(AUX1(:))/100;
     AUX2=log(AUX1(:))-log(DATA(:)+c); 
     NUMERATOR=sum((DEV1_MODEL(:).*AUX2(:))(:));
     DENOMINATOR=sum(((DEV2_MODEL(:).*AUX2(:)).+(DEV1_MODEL_SQR(:)./AUX1(:)))(:));

     THETA_EST-=NUMERATOR/DENOMINATOR;   

     THETA_CONV(ITER)=THETA_EST;
     ITER+=1;
   endwhile
   if (ITER<=MAX_ITER)
     THETA_CONV(ITER:MAX_ITER)=[];
   endif
 endfunction
