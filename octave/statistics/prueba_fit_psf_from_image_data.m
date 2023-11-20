addpath(genpath("../../octave"))
graphics_toolkit("gnuplot");
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

m.n_col=1;
m.n_row=1;
m.n_lay=31;
m.pixel_size=9;
peak_shift=0;
t_s=5;
m.image_formation = @(T_S,NORM)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),...
                                                    m.pixel_size,m.pixel_size,m.delta_z,...
                                                    m.n_col,m.n_row,m.n_lay,NORM,peak_shift);
psf=m.image_formation(t_s,"sum");
data=zeros(1,1,128);
data(1,1,10:27)=1;
data(1,1,50:67)=1;
data(1,1,90:107)=1;
data=100*data;
data_b=stationary_imaging(data,psf);
data_n=poissrnd(data_b);
subplot(4,1,1)
plot(100*squeeze(psf));
subplot(4,1,2)
plot(squeeze(data));
subplot(4,1,3)
plot(squeeze(data_b));
subplot(4,1,4)
plot(squeeze(data_n));

data_model=@(T_S)stationary_imaging(data,m.image_formation(T_S,"sum"));
n_samples=30;
t_s_ini=unifrnd(4.75,5.25,10,1);
t_s_estimated=zeros(n_samples,length(t_s_ini));
for n=1:n_samples
  data_n=poissrnd(data_b);
  for t=1:length(t_s_ini)
    [THETA_EST ITERS THETA_CONV TIME]=fit_psf(data_n,data_model,t_s_ini(t),"ML");
    disp(strcat(num2str(t),"\t",num2str(n),"\t",num2str(THETA_EST),"\t", num2str(ITERS)))
    t_s_estimated(n,t)=THETA_EST;
  endfor

endfor
