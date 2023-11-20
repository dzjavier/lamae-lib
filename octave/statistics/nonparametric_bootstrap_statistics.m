
function [THETA SE CI]=nonparametric_bootstrap_statistics(DATA,STATISTIC,B,ALPHA)
  ## usage: [THETA SE CI]=nonparametric_bootstrap_statistics(DATA,STATISTIC,B,ALPHA)
  narginchk(4,4)

  THETA=STATISTIC(DATA);
  data_dim=size(DATA);
  sample_number=length(DATA);
  theta_boot=zeros(B,1);
  se_aux=zeros(B,1);
  ## sampling with replacement
  for b=1:B
    data_boot=DATA(unidrnd(sample_number,data_dim));
    theta_boot(b)=STATISTIC(data_boot);     
    se_aux(b)=(theta_boot(b)-THETA).^2;
  endfor
  CI=quantile(theta_boot,[ALPHA 1-ALPHA])';
  SE=sqrt(mean(se_aux));

endfunction
