function [IMAGE Z_MAX]=generate_image_stack(F,x_pixel_size,y_pixel_size,z_step,n_row,n_col,n_lay,NORM,PEAK_Z_SHIFT=0)

    ## usage: [IMAGE Z_MAX]=generate_image_stack(F,x_pixel_size,y_pixel_size,z_step,n_row,n_col,n_lay,NORM,PEAK_Z_SHIFT=0)
    z_ini=0;
    F_Z_MAX=@(Z)-1*F(0,0,Z);
    Z_MAX= fminsearch(F_Z_MAX,z_ini);
    F_MAX=F(0,0,Z_MAX);
    SUM_MAX_VALUE=-Inf;
    if ((n_col>=1)&&(n_row>=1)&&(n_lay>=1))
      if (mod(n_col,2)==0)
        X=linspace(0,n_col/2*x_pixel_size,n_col/2);
      else
        X=linspace(0,(n_col+1)/2*x_pixel_size,fix(n_col/2)+2);
      endif
      if (mod(n_row,2)==0)
        Y=linspace(0,n_row/2*y_pixel_size,n_row/2);
      else
        Y=linspace(0,(n_row+1)/2*y_pixel_size,fix(n_row/2)+2);
      endif
      if (mod(n_lay,2)==0)
        Z=linspace(PEAK_Z_SHIFT+Z_MAX-n_lay/2*z_step,PEAK_Z_SHIFT+Z_MAX+n_lay/2*z_step,n_lay);
      else
        Z=linspace(PEAK_Z_SHIFT+Z_MAX-(n_lay+1)/2*z_step,PEAK_Z_SHIFT+Z_MAX+(n_lay+1)/2*z_step,n_lay+2);
        aux_rem=[1 n_lay+2];
        Z(:,aux_rem)=[];
      endif

      IMAGE=zeros(n_row,n_col,n_lay);
      for k=1:n_lay
        for i=fix(n_row/2)+1:n_row
          for j=fix(n_col/2)+1:n_col
            intensity=F(X(j-fix(n_col/2)),Y(i-fix(n_row/2)),Z(k));
            IMAGE(i,j,k)=intensity; ## first quadrant
            ## intensity=F(X(j-fix(n_col/2)),Y(i-fix(n_row/2)),Z(k));
            IMAGE(i,n_col-j+1,k)=intensity; ## second quadrant
            ## intensity=F(X(j-fix(n_col/2)),Y(i-fix(n_row/2)),Z(k));
            IMAGE(n_row-i+1,n_col-j+1,k)=intensity; ## third quadrant
            ## intensity=F(X(j-fix(n_col/2)),Y(i-fix(n_row/2)),Z(k));
            IMAGE(n_row-i+1,j,k)=intensity;## fourth quadrant
          endfor
        endfor
        if ((n_row>1) || (n_col>1))
          if (sum((IMAGE(:,:,k)(:)))>SUM_MAX_VALUE)
            SUM_MAX_VALUE=sum(IMAGE(:,:,k)(:)); 
          endif
        else
          IMAGE(:,:,k)/=F_MAX;
        endif
      endfor
##      if (((n_row>1) || (n_col>1)))
##        IMAGE/=SUM_MAX_VALUE;
##      endif
      switch NORM
        case {"sum" "SUM"}
          IMAGE/=sum(IMAGE(:));
        case {"max" "MAX"}
          IMAGE/=max(IMAGE(:));   
      endswitch    
    endif
  endfunction
