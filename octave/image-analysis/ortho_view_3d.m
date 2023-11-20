function H=ortho_view_3d(DATA,Y_PLANE,X_PLANE,Z_PLANE,Y_RES,X_RES,Z_RES)
  ## usage: ortho_view_3d(DATA,Y_PLANE,X_PLANE,Z_PLANE,Y_RES,X_RES,Z_RES)
  ## 
  ## DATA is a threedimensional array 
  ## Y_PLANE, X_PLANE and Z_PLANE are the planes to visualize
  ## Y_RES, X_RES and Z_RES are the resolution to plot scale bars in micrometers
  ## Returns 
  ## Author: Diaz-Zamboni Javier Eduardo
  ## Created: 201

  bar_size=0.15;
  [nx ny nz]=size(DATA);
  h_z=fix(bar_size*nz);
  z_f=nz-fix(0.05*nz);
  z_i=z_f-h_z;
  
  h_y=fix(bar_size*ny);
  y_f=ny-fix(0.05*ny);
  y_i=y_f-h_y;

  h_x=fix(bar_size*nx);
  x_f=nx-fix(0.05*nx);
  x_i=x_f-h_x;
 
  H=figure;
  m=max(DATA(:));
  %% XY plane
  subplot(2,2,1)
%  imag_aux=zeros(nx,ny);
  imag_aux=squeeze(DATA(:,:,Z_PLANE))/m;
  imagesc(imag_aux(:,:),[]);
  axis off;
  colormap(jet);
  colorbar("EastOutside");
  line([fix(0.05*nx) fix(0.05*nx)],[y_i y_f],"color","white","linewidth",3); % vertical calibration bar 
  text(fix(0.06*nx),y_f-h_y/2,strcat(num2str(h_y*Y_RES,2),"um"), "fontsize",9,"color","white");
  line([x_i x_f], [fix(0.05*ny) fix(0.05*ny)], "color","white","linewidth",3); % Horizontal calibration bar 
  text(y_i,fix(0.08*nx),strcat(num2str(h_x*X_RES,2),"um"), "fontsize",9,"color","white");%hold off;
  xlabel("X-Y Plane");
  
  %% y-z plane
  subplot(2,2,2)
%  imag_aux=zeros(ny,nz);
  imag_aux=squeeze(DATA(:,X_PLANE,:))/m;
  imagesc(imag_aux(:,:),[]);
  axis off;
  colormap(jet);
  colorbar("EastOutside");
  line([fix(0.05*nz) fix(0.05*nz)],[y_i y_f],"color","white","linewidth",3); % vertical calibration bar 
  text(fix(0.06*nz),y_f-h_y/2,strcat(num2str(h_y*Y_RES,2),"um"), "fontsize",9,"color","white");
  line([y_i y_f], [fix(0.05*nz) fix(0.05*nz)], "color","white","linewidth",3); % Horizontal calibration bar 
  text(y_i,fix(0.08*nz),strcat(num2str(h_z*Z_RES,2),"um"), "fontsize",9,"color","white");%hold off;
  xlabel("Y-Z Plane");

  
  %% x-z plane
  subplot(2,2,3)
  %% imag_aux=zeros(nx,nz);
  imag_aux=squeeze(DATA(Y_PLANE,:,:))/m;
  imagesc(imag_aux(:,:)',[]);
  axis off;
  colormap(jet);
  colorbar("EastOutside");
  line([fix(0.05*nx) fix(0.05*nx)],[z_i z_f], "color","white","linewidth",3); % vertical calibration bar 
  text(fix(0.06*nx),z_f-h_z/2,strcat(num2str(h_z*Z_RES,2),"um"), "fontsize",9,"color","white");
  line([x_i x_f], [fix(0.05*nz) fix(0.05*nz)], "color","white","linewidth",3); % Horizontal calibration bar 
  text(x_i,fix(0.08*nz),strcat(num2str(h_x*X_RES,2),"um"), "fontsize",9,"color","white");%hold off;
  xlabel("X-Z Plane");

endfunction
