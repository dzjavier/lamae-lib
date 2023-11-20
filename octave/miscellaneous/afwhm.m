function [X_inf X_sup fw] = afwhm(DATA,X)
  
  ## usage: [X_inf X_sup fw] = afwhm(DATA,X)
  HM = max(DATA)/2;
  data_aux = DATA-HM;
  inds=find(data_aux>=0);
  ind_inf_2=inds(1);
  ind_sup_1=inds(length(inds));
  if (ind_inf_2>1) 
    ind_inf_1=ind_inf_2-1;
    data_inf_1=DATA(ind_inf_1);
    data_inf_2=DATA(ind_inf_2);
    X_inf=interp1([data_inf_1 data_inf_2],[X(ind_inf_1) X(ind_inf_2)],HM,"linear");
  else
    X_inf=X(ind_inf_2);
  endif
  if (ind_sup_1<length(X)) 
    ind_sup_2=ind_sup_1+1;
    data_sup_1=DATA(ind_sup_1);
    data_sup_2=DATA(ind_sup_2);
    X_sup=interp1([data_sup_1 data_sup_2],[X(ind_sup_1) X(ind_sup_2)],HM,"linear");
  else
    X_sup=X(ind_sup_2);
  endif
  fw=abs(X_sup-X_inf);
endfunction
