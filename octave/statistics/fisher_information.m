function FI=fisher_information(PROB,THETA)
  
  ## usage: FI=fisher_information(PROB,THETA)
  ## Author: Javier Eduardo Diaz Zamboni <javierdiaz@bioingenieria.edu.ar>
  ## Version: 0.2
  ## Keywords: Fisher information
  log_prob_deriv2=@(theta)df2x2(@(theta2)log(PROB(theta2)),theta,0);
  FI=-1.0*sum(log_prob_deriv2(THETA)(:).*PROB(THETA)(:));
endfunction
