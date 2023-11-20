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
m.n_lay=51;
peak_shift=0;
t_s=0;

m.peak_formation = @(T_S,PS=0)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),m.pixel_size,m.pixel_size,m.delta_z,1,1,1,"none",PS);

m.image_formation = @(T_S,PS=0)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),...
                    			   m.pixel_size,m.pixel_size,m.delta_z,...
                    			   m.n_row,m.n_col,m.n_lay,"none",PS);


S_max=sum(m.image_formation(0)(:));
K=128;#número de secciones ópticas
psf_model=@(THETA_AUX,PS=0)m.image_formation(THETA_AUX,PS)/S_max;
data=zeros(128,1,K);

## TRES ESFERAS HUECAS A TRES PROFUNDIDADES DISTINTAS
h_ring=intensity_ring(15,8);
data(21:35,1,41:55)=h_ring*0.3;
data(61:75,1,59:73)=h_ring*0.75;
data(101:115,1,79:93)=h_ring;

## número de fotones
data=1e3*data;
data_b=zeros(size(data));
imagen=zeros(size(data));
## data_b=stationary_imaging(data,psf);
sum_psf_vec=zeros(m.n_lay,K);
t_s_vec=linspace(-7.5,20,K);
for k=fix(m.n_lay/2)+1:K-fix(m.n_lay/2)
  tic;
  [psf_aux, peak_shift]=m.peak_formation(t_s_vec(k));
  psf=psf_model(t_s_vec(k),peak_shift);
  sum_psf_vec(:,k)=sum(sum(psf,1),2);
  data_b(:,:,k)=optical_section_imaging(data(:,:,k-fix(m.n_lay/2):k+fix(m.n_lay/2)),psf);
  toc
endfor

k_idx_0=find(t_s_vec>=0)(1);
k_idx_3=find(t_s_vec>=3)(1);
k_idx_7=find(t_s_vec>=7)(1);
k_idx_11=find(t_s_vec>=11)(1);
data_n=poissrnd(data_b);

graphics_toolkit("gnuplot")
figure 1;
clf;
subplot(4,3,1)
imagesc(t_s_vec,1:128,squeeze(data));hold on;
line([t_s_vec(k_idx_0) t_s_vec(k_idx_0)], [1 128],"color","white");hold off
subplot(4,3,2)
imagesc(squeeze(data_b))
subplot(4,3,3)
imagesc(poissrnd(squeeze(data_b)))
psf=psf_model(11); 

t_s_test=[t_s_vec(k_idx_0) t_s_vec(k_idx_3) t_s_vec(k_idx_7) t_s_vec(k_idx_11)];
max_iter=200;  
ws=19; # window_size
## gold
gdeconv_b_image =dvdeconv(data_b,@(T)psf_model(T),t_s_test,"max_iter",max_iter,"window_size",ws,"method","gold");
gdeconv_n_image =dvdeconv(data_n,@(T)psf_model(T),t_s_test,"max_iter",max_iter,"window_size",ws,"method","gold");
subplot(4,3,5)
imagesc(squeeze(gdeconv_b_image))
subplot(4,3,6)
imagesc(squeeze(gdeconv_n_image))

## ML
mldeconv_b_image =dvdeconv(data_b,@(T)psf_model(T),t_s_test,"max_iter",max_iter,"window_size",ws,"method","ml");
mldeconv_n_image =dvdeconv(data_n,@(T)psf_model(T),t_s_test,"max_iter",max_iter,"window_size",ws,"method","ml");
subplot(4,3,8)
imagesc(squeeze(mldeconv_b_image))
subplot(4,3,9)
imagesc(squeeze(mldeconv_n_image))
colormap(parula(256));

## JVC
jvcdeconv_b_image =dvdeconv(data_b,@(T)psf_model(T),t_s_test,"max_iter",max_iter,"window_size",ws,"method","jvc");
jvcdeconv_n_image =dvdeconv(data_n,@(T)psf_model(T),t_s_test,"max_iter",max_iter,"window_size",ws,"method","jvc");

subplot(4,3,11)
imagesc(squeeze(jvcdeconv_b_image))
subplot(4,3,12)
imagesc(squeeze(jvcdeconv_n_image))
colormap(parula(256));

figure 2;
clf;
subplot(3,1,1)
plot(squeeze(data(29,1,:)),"g");hold on
plot(squeeze(data_b(29,1,:)),"b");
plot(squeeze(data_n(29,1,:)),"r");
plot(squeeze(gdeconv_b_image(29,1,:)),"y");
plot(squeeze(gdeconv_n_image(29,1,:)),"k");hold off;

subplot(3,1,2)
plot(squeeze(data(68,1,:)),"g");hold on
plot(squeeze(data_b(68,1,:)),"b");
plot(squeeze(data_n(68,1,:)),"r");
plot(squeeze(mldeconv_b_image(68,1,:)),"y");
plot(squeeze(mldeconv_n_image(68,1,:)),"k");hold off;

subplot(3,1,3)
plot(squeeze(data(108,1,:)),"g");hold on
plot(squeeze(data_b(108,1,:)),"b");
plot(squeeze(data_n(108,1,:)),"r");
plot(squeeze(jvcdeconv_b_image(108,1,:)),"y");
plot(squeeze(jvcdeconv_n_image(108,1,:)),"k");hold off;

disp("datos\t min\t max\t sum")
disp([min(data_b(:)) max(data_b(:)) sum(data_b(:))])
data_n=poissrnd(data_b);       
disp([min(data_n(:)) max(data_n(:)) sum(data_n(:))])

disp("Gold")
disp([min( gdeconv_b_image(:)) max( gdeconv_b_image(:)) sum( gdeconv_b_image(:))])
disp([min( gdeconv_n_image(:)) max( gdeconv_n_image(:)) sum( gdeconv_n_image(:))])
disp("ML")
disp([min( mldeconv_b_image(:)) max( mldeconv_b_image(:)) sum( mldeconv_b_image(:))])
disp([min( mldeconv_n_image(:)) max( mldeconv_n_image(:)) sum( mldeconv_n_image(:))])
disp("JVC")
disp([min( jvcdeconv_b_image(:)) max( jvcdeconv_b_image(:)) sum( jvcdeconv_b_image(:))])
disp([min( jvcdeconv_n_image(:)) max( jvcdeconv_n_image(:)) sum( jvcdeconv_n_image(:))])
