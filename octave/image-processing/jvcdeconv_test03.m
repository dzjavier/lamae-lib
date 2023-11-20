addpath(genpath("../../octave"))
m.Lambda = 0.560;  
m.NA = 1.35;
m.M = 100; 
m.z_d = [160e3 160e3]; 
m.n_oil = [1.515 1.515]; 
m.t_oil=100;
m.n_g=[1 1];
m.t_g=[1 1];
m.n_s = 1.33; 
m.delta_z=0.18;
m.tol=1e-5;
m.gl_psf_punctual=@(X,Y,Z,T_S)gl_psf(X,Y,Z,m.Lambda,m.NA,m.M,m.z_d,m.n_oil,m.t_oil,m.n_g,m.t_g,m.n_s,T_S,m.tol);
m.pixel_size=9;  

m.gl_psf_pixelated=@(X,Y,Z,T_S)intensity_over_pixel_area(@(XX,YY)gl_psf_punctual(XX,YY,Z,T_S),X,Y,m.pixel_size,m.pixel_size,m.tol);
m.n_col=1;
m.n_row=1;
m.n_lay=45;

m.image_formation = @(T_S,NORM)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),...
                                                    m.pixel_size,m.pixel_size,m.delta_z,...
                                                    m.n_col,m.n_row,m.n_lay,NORM);

m.image_formation_pix = @(T_S,NORM)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_pixelated(XX,YY,ZZ,T_S),...
                                                    m.pixel_size,m.pixel_size,m.delta_z,...
                                                    m.n_col,m.n_row,m.n_lay,NORM);

  m.n_col=71;
  m.n_row=71;
  m.n_lay=1;
  m.pixel_size=4.5;
  peak_shift=0;
  t_s=5;
  m.image_formation = @(T_S,NORM,PEAK_SHIFT)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),...
                                                                 m.pixel_size,m.pixel_size,m.delta_z,...
                                                                 m.n_row,m.n_col,m.n_lay,"none",peak_shift);
  S_0=sum(m.image_formation(0,0)(:));
  psf=m.image_formation(t_s,peak_shift)/S_0;
  data=zeros(128);
  data(32:95,32:95)=data(32:95,32:95)+ones(64);
  data(48:79,48:79)=data(48:79,48:79)-ones(32);
  data(100:110,100)=1;
  data(100:110,102)=1;
  data(10,100:110)=0.3;
  data(12,100:110)=0.3;
  data=1000*data;
  data_b=stationary_imaging(data,psf);
  data_n=poissrnd(data_b);
  ## pixelar
  func=inline("sum(x(:))");
  data_np=blockproc(data_n,[2 2],func);
  data_bp=blockproc(data_b,[2 2],func);
  ## despixelar
  dfunc=inline("x(:).*ones(2)/4");
  data_np_d=blockproc(data_np,[1 1],dfunc);
  data_bp_d=blockproc(data_bp,[1 1],dfunc);

  ## jvc deconvolution   function [ESTIMATED_OBJECT, ERROR] = jvcdeconv(IMAGE, PSF, OPTIONS) 
  MaxIter=20;
  [estimated_data_n,error_data_n]=jvcdeconv(data_n,psf,"relaxation","square","max_iter",MaxIter,"tol",1e-3);
  [estimated_data_b,error_data_b]=jvcdeconv(data_b,psf,"relaxation","square","max_iter",MaxIter,"tol",1e-3); 

  [estimated_data_np_d,error_data_n]=jvcdeconv(data_np_d,psf,"relaxation","square","max_iter",MaxIter,"tol",1e-3);
  [estimated_data_bp_d,error_data_b]=jvcdeconv(data_bp_d,psf,"relaxation","square","max_iter",MaxIter,"tol",1e-3); 

  graphics_toolkit("gnuplot");
  figure(1);
  clf;
  set(gcf,"Color",[.5,.5,.5]) 
  subplot(3,4,1);  imshow(data,[]);
  subplot(3,4,2);  imshow(psf,[]);
  subplot(3,4,5);  imshow(data_b,[]); 
  subplot(3,4,6);  imshow(data_n,[]);
  subplot(3,4,7);  imshow(data_bp_d,[]);
  subplot(3,4,8);  imshow(data_np_d,[]);
  subplot(3,4,9);  imshow(estimated_data_b,[]);
  subplot(3,4,10); imshow(estimated_data_n,[]);
  subplot(3,4,11); imshow(estimated_data_bp_d,[]); 
  subplot(3,4,12); imshow(estimated_data_np_d,[]);

  colormap(parula(256))
  figure(2);
  clf;
  plot(error_data_n,"g"); hold on;
  plot(error_data_b,"r"); hold off
