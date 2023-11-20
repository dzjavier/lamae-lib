function [ESTIMATED_OBJECT, MSE] = raredeconv(IMAGE, PSF, TOL=1e-3, MAX_ITER=50,RELAXATION_OPTION="square") 
  ESTIMATED_OBJECT = zeros(size(IMAGE));
  current_estimated_obj = zeros(size(IMAGE));
  next_estimated_obj = FIRST_GUESS;
  estimated_image = zeros(size(IMAGE));
  MSE = 1e6*ones(1, MAX_ITER);
  reached_TOL = false;
  iter = 1;
  A = max(FIRST_GUESS(:))/2;

  while ((iter <= MAX_ITER) && (reached_TOL!=true))
    current_estimated_obj = next_estimated_obj;
    estimated_image = optical_section_imaging(current_estimated_obj,PSF);

    switch RELAXATION_OPTION
      case {"square"}
	gamma=1 - (current_estimated_obj - A).^2 ./ (A^2); ## Petter Jansson (1984);
      case {"abs"}
	gamma = 1 - (2/A)*abs(current_estimated_obj - A/2); ## Petter Jansson (1970)
      case {"cittert"}
	gamma = 1; ## Van Cittert 1931
      otherwise 
        error("Invalid value for RELAXATION_OPTION");
    endswitch
      next_estimated_obj = current_estimated_obj + gamma .* (IMAGE2D-estimated_image);
    if (strcmp(RELAXATION_OPTION,"cittert")) ## forcing positivity constraint
      next_estimated_obj(next_estimated_obj< 0) = 0;
    endif
    MSE(iter) = sumsq(IMAGE2D(:)-estimated_image(:))/prod(size(IMAGE2D));
    if (MSE(iter)<=TOL)
      reached_TOL = true;
      MSE = MSE(1:iter);
    endif
    iter += 1;
  endwhile
  ESTIMATED_OBJECT = next_estimated_obj;
endfunction
