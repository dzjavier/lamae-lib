function [REJECT CHI0]=chisquare_test_for_the_variance(DATA,SIGMA0,CONFIDENCE,ALTERNATIVE)
  ## usage: [REJECT CHI0]=chisquare_test_for_the_variance(DATA,SIGMA0,CONFIDENCE,ALTERNATIVE)
  ## version: 0.1
  narginchk(4,4);
  degree_of_freedom=length(DATA)-1;
  CHI0=degree_of_freedom*(std(DATA).^2)/SIGMA0^2;
  switch ALTERNATIVE
    case {"!="}
      if ((CHI0<chi2inv(CONFIDENCE/2,degree_of_freedom))||(CHI0>chi2inv(1-CONFIDENCE/2,degree_of_freedom)))
        REJECT=true;
      else REJECT=false;
      endif
    case {"<"}
      if (CHI0<chi2inv(CONFIDENCE,degree_of_freedom))
        REJECT=true;
      else REJECT=false;
      endif
    case {">"}
      if (CHI0>chi2inv(1-CONFIDENCE,degree_of_freedom))
        REJECT=true;
      else REJECT=false;
      endif
  endswitch
endfunction
