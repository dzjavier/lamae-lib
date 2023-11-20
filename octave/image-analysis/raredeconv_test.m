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
m.n_row=21;
m.n_lay=45;
peak_shift=0;
t_s=7;
m.peak_formation = @(T_S,PS=0)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),m.pixel_size,m.pixel_size,m.delta_z,1,1,1,"none",PS);
m.image_formation = @(T_S,PS=0)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),...
                			       m.pixel_size,m.pixel_size,m.delta_z,...
                			       m.n_row,m.n_col,m.n_lay,"none",PS);

S_max=max(sum(sum(m.image_formation(0),1),2));
K=128;#número de secciones ópticas
psf=m.image_formation(t_s);
psf_model=@(THETA_AUX,PS=0)m.image_formation(THETA_AUX,PS)/S_max;
data=zeros(128,1,K);
## FANTOMAS
## UN CUADRADO HUECO
## data(32:95,1,32:95)=data(32:95,1,32:95)+ones(64,1,64);
## data(48:79,1,48:79)=data(48:79,1,48:79)-ones(32,1,32);
## DOS LINEAS PARALELAS AL EJE Y    
##data(32:95,1,25)=ones(64,1,1);
##data(32:95,1,100)=ones(64,1,1);
## TRES ESFERAS HUECAS A TRES PROFUNDIDADES DISTINTAS
h_ring=intensity_ring(11,6);
data(23:33,1,43:53)=h_ring;
data(63:73,1,61:71)=h_ring;
data(103:113,1,81:91)=h_ring;
## data(48:79,1,48:79)=data(48:79,1,48:79)-ones(32,1,32);   


## número de fotones
data=2e2*data;

data_b=zeros(size(data));
imagen=zeros(size(data));
## data_b=stationary_imaging(data,psf);
sum_psf_vec=zeros(m.n_lay,K);
t_s_vec=linspace(-7.5,20,K);
for k=fix(m.n_lay/2)+1:K-fix(m.n_lay/2)
  tic;
  psf_aux, peak_shift]=m.peak_formation(t_s_vec(k));
  psf=psf_model(t_s_vec(k),peak_shift);
  sum_psf_vec(:,k)=sum(sum(psf,1),2);
  data_b(:,:,k)=optical_section_imaging(data(:,:,k-fix(m.n_lay/2):k+fix(m.n_lay/2)),psf);
  toc
endfor

k_idx=find(t_s_vec>=5)(1);
imagen=data_b(:,:,k_idx);        
## imagen=poissrnd(imagen);       

graphics_toolkit("gnuplot")
figure 1;
clf;
subplot(3,1,1)
imagesc(t_s_vec,1:128,squeeze(data))
subplot(3,1,2)
imagesc(squeeze(data_b))
subplot(3,1,3)
imagesc(poissrnd(squeeze(data_b)))
colormap(parula(256));

## proc_data=data(:,:,k_idx-fix(m.n_lay/2):k_idx+fix(m.n_lay/2));    
## theta_ini=4.5;
## assumed_psf=psf_model(theta_ini); 
## first_guess=deproject_optical_section(imagen,assumed_psf);
## approx_ob_k=first_guess(:,:,k_idx)-sum(first_guess(:,:,1:k_idx-1),3)-sum(first_guess(:,:,k_idx+1:end),3);

## dec_data=mldeconv(data_b(:,:,k_idx-fix(m.n_lay/2):k_idx+fix(m.n_lay/2)), assumed_psf);
## graphics_toolkit("gnuplot")
## figure 1;
## clf;
## subplot(2,2,1);
## plot(imagen)
## subplot(2,2,2);
## plot(squeeze(sum(first_guess,3)))
## subplot(2,2,3);
## plot(squeeze(approx_ob_k))
## subplot(2,2,4);
## plot(squeeze(proc_data(:,:,k_idx)))
