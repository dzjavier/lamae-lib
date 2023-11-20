clear all;
more off;
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

m.n_col=1;
m.n_row=31;
m.n_lay=45;
peak_shift=0;
t_s=7;
m.image_formation = @(T_S)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),...
                				   m.pixel_size,m.pixel_size,m.delta_z,...
                				   m.n_row,m.n_col,m.n_lay,"none",peak_shift);
S_max=max(sum(sum(m.image_formation(0),1),2));
K=128; #número de secciones ópticas
psf=m.image_formation(t_s);
psf_model=@(THETA_AUX)m.image_formation(THETA_AUX)/S_max;
data=zeros(128,1,K);
## un cuadrado  hueco
data(32:95,1,32:95)=data(32:95,1,32:95)+ones(64,1,64);
data(48:79,1,48:79)=data(48:79,1,48:79)-ones(32,1,32);
## dos lineas en z    
## data(32:95,1,25)=ones(64,1,1);
## data(32:95,1,100)=ones(64,1,1);

## número de fotones
data=2e4*data;

data_b=zeros(size(data));
imagen=zeros(size(data));
sum_psf_vec=zeros(m.n_lay,K);
t_s_vec=linspace(0,15,K);
for k=fix(m.n_lay/2)+1:K-fix(m.n_lay/2)
  tic;
  psf=psf_model(t_s_vec(k));
  sum_psf_vec(:,k)=sum(sum(psf,1),2);
  data_b(:,:,k)=optical_section_imaging(data(:,:,k-fix(m.n_lay/2):k+fix(m.n_lay/2)),psf);
  toc
endfor
data_n=poissrnd(data_b);
k_idx=find(t_s_vec>=5)(1);
imagen=data_b(:,:,k_idx);        
imagen=poissrnd(imagen);       
proc_data=data(:,:,k_idx-fix(m.n_lay/2):k_idx+fix(m.n_lay/2));        
theta_ini=4.5;
disp("ml")
psf_estimation(imagen,proc_data,psf_model,theta_ini,"ml")
disp("midiv")
psf_estimation(imagen,proc_data,psf_model,theta_ini,"midiv")
disp("lsqr")
psf_estimation(imagen,proc_data,psf_model,theta_ini,"lsqr")

assumed_psf=psf_model(theta_ini); 
MAX_ITER=50;
square_error=zeros(MAX_ITER,1);
for i=1:MAX_ITER
dec_data=jvcdeconv(data_n(:,:,k_idx-fix(m.n_lay/2):k_idx+fix(m.n_lay/2)),assumed_psf/sum(assumed_psf(:)),"relaxation","square","max_iter",i,"tol",1e-3);
square_error(i)=sumsq(dec_data(:)-proc_data(:));
endfor
graphics_toolkit("gnuplot")
figure 1;
clf;
subplot(2,2,1);
plot(square_error)
subplot(2,2,1);
imagesc(squeeze(data_n))
subplot(2,2,3);
imagesc(squeeze(proc_data))
subplot(2,2,4);
imagesc(squeeze(dec_data))

disp("ml")
psf_estimation(imagen,dec_data,psf_model,theta_ini,"ml")
disp("midiv")
psf_estimation(imagen,dec_data,psf_model,theta_ini,"midiv")
disp("lsqr")
psf_estimation(imagen,dec_data,psf_model,theta_ini,"lsqr")

figure 2;
clf
subplot(2,2,1)
plot(squeeze(psf));
subplot(2,2,2)
##plot(squeeze(data(62,1,:)),"r");hold on;
plot(squeeze(data_b(62,1,:))); hold off;
subplot(2,2,3)
imagesc(squeeze(data))
subplot(2,2,4)
imagesc(squeeze(data_b))
colormap(parula(256))
