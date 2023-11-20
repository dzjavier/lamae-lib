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

m.n_col=25;
m.n_row=25;
m.n_lay=1;
peak_shift=-0.7;
t_s=5;
m.image_formation = @(T_S,NORM)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),...
                                                    m.pixel_size,m.pixel_size,m.delta_z,...
                                                    m.n_col,m.n_row,m.n_lay,NORM,peak_shift);
psf_model=@(THETA_AUX)m.image_formation(THETA_AUX,"none");
psf=psf_model(t_s);
data=zeros(128);
data(32:95,32:95)=data(32:95,32:95)+ones(64);
data(48:79,48:79)=data(48:79,48:79)-ones(32);
max_photon_count=1e5;
data=1e4*data;
data_b=stationary_imaging(data,psf);
data_n=poissrnd(data_b);

MaxIter=100;
P=1; 
t_s_ini=5.2;
t_s_ini_b=t_s_ini;
t_s_ini_n=t_s_ini;
psf_ini=m.image_formation(t_s_ini_b,"none");
psf_ini_b=psf_ini;
psf_ini_n=psf_ini;
J=10;
for j=1:J
  [estimated_data_b estimated_psf_b error_data_b ]=bdeconv3(data_b,psf_ini_b,P,1,MaxIter);
  [estimated_data_n estimated_psf_n error_data_n ]=bdeconv3(data_n,psf_ini_n,P,1,MaxIter);
  error_db=min(error_data_b);
  error_dn=min(error_data_n);
  [psf_param_b ITS THETA_CONV, TIME]=fit_psf(estimated_psf_b*max_photon_count,@(THETA_AUX)(psf_model(THETA_AUX)*max_photon_count), t_s_ini_b,"ml");
  [psf_param_n ITS THETA_CONV, TIME]=fit_psf(estimated_psf_n*max_photon_count,@(THETA_AUX)(psf_model(THETA_AUX)*max_photon_count), t_s_ini_n,"ml");
  t_s_ini_b=psf_param_b 
  t_s_ini_n=psf_param_n 
  psf_ini_b=psf_model(t_s_ini_b);
  psf_ini_n=psf_model(t_s_ini_n);
endfor

graphics_toolkit("gnuplot");
figure(1);
set(gcf,'Color',[.5,.5,.5]) 
subplot(3,3,1)
imshow(data,[])
subplot(3,3,2)
imshow(data_b,[]); 
subplot(3,3,3)
imshow(data_n,[]);
subplot(3,3,4) 
imshow(psf,[]);
subplot(3,3,5)
imshow(estimated_data_b,[]);
subplot(3,3,6)
imshow(estimated_data_n,[]);
subplot(3,3,7)
imshow(psf_ini,[]);
subplot(3,3,8)
imshow(psf_ini_b,[]);
subplot(3,3,9)
imshow(psf_ini_n,[]);
colormap(pink);

figure(2);
subplot(2,2,1:2)
plot(error_data_n,"g"); hold on;
plot(error_data_b,"r"); hold off
subplot(2,2,3)
plot(psf_ini(10,:),"g");hold on;
plot(psf_ini_b(10,:),"r");hold off;
subplot(2,2,4)
plot(psf_ini(10,:),"g");hold on;
plot(psf_ini_n(10,:),"r");hold off;

disp("datos")
disp("\t min \t mean\t max");
disp([min(data(:)) mean(data(:)) max(data(:))]);
disp([min(data_b(:)) mean(data_b(:)) max(data_b(:))]);
disp([min(data_n(:)) mean(data_n(:)) max(data_n(:))]);
disp("datos estimados")
disp([min(estimated_data_b(:)) mean(estimated_data_b(:)) max(estimated_data_b(:))]);
disp([min(estimated_data_n(:)) mean(estimated_data_n(:)) max(estimated_data_n(:))]);
