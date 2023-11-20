function AT=vst(DATA)
  
  ## usage: vst(DATA)
  ## Variance Stabilization Transformation. Anscombe Transformation
  AT=2*sqrt(DATA+3/8); 
endfunction
