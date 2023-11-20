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

m.n_col=51;
m.n_row=51;
m.n_lay=1;
m.pixel_size=13;
peak_shift=-1.0;
t_s=5;
m.image_formation = @(T_S,NORM)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),...
                                                    m.pixel_size,m.pixel_size,m.delta_z,...
                                                    m.n_row,m.n_col,m.n_lay,"none",peak_shift);
S_0=sum(m.image_formation(0)(:));
psf=m.image_formation(t_s,"none")/S_0;
data=zeros(128);
data(32:95,32:95)=data(32:95,32:95)+ones(64);
data(48:79,48:79)=data(48:79,48:79)-ones(32);
data(100:110,100)=1;
data(100:110,102)=1;
data(10,100:110)=0.3;
data(12,100:110)=0.3;
photon_number=1e2;
data=photon_number*data;
data_b=abs(ifft2(fft2(data).*(abs(fft2(psf,128,128)))));
data_n=poissrnd(data_b);
t_s_ini=4;
psf_ini=m.image_formation(t_s_ini,"none")/S_0;
MaxIter=100;
mse_bo=zeros(MaxIter,1);
mse_no=zeros(MaxIter,1);
ssim_bo=zeros(MaxIter,1);
ssim_no=zeros(MaxIter,1);
ssim_bi=zeros(MaxIter,1);
ssim_ni=zeros(MaxIter,1);
for i=1:MaxIter
  [estimated_data_b estimated_psf_b error_data_b]=rldeconv(data_b,psf_ini,"max_iter",i); 
  [estimated_data_n estimated_psf_n error_data_n]=rldeconv(data_n,psf_ini,"max_iter",i);
  mse_bo(i)=sumsq(data(:)-estimated_data_b(:));
  mse_no(i)=sumsq(data(:)-estimated_data_n(:));
  ssim_bo(i)=mean(ssim(data,estimated_data_b)(:));
  ssim_no(i)=mean(ssim(data,estimated_data_n)(:));
  ssim_bi(i)=mean(ssim(data_b,stationary_imaging(estimated_data_b,estimated_psf_b))(:));
  ssim_ni(i)=mean(ssim(data_n,stationary_imaging(estimated_data_n,estimated_psf_n))(:));
endfor
 format short e;
  disp("Data\t Min\t Max\t Sum")
  disp(strcat("Or\t",num2str(min(data(:))),"\t",num2str(max(data(:))),"\t",num2str(sum(data(:)))))
  disp(strcat("blur\t",num2str(min(data_b(:))),"\t",num2str(max(data_b(:))),"\t",num2str(sum(data_b(:)))))
  disp(strcat("noisy\t",num2str(min(data_n(:))),"\t",num2str(max(data_n(:))),"\t",num2str(sum(data_n(:)))))
  disp(strcat("rl\t",num2str(min(estimated_data_b(:))),"\t",num2str(max(estimated_data_b(:))),"\t",num2str(sum(estimated_data_b(:)))))
  disp(strcat("rl\t", num2str(min(estimated_data_n(:))),"\t",num2str(max(estimated_data_n(:))),"\t",num2str(sum(estimated_data_n(:)))))

  graphics_toolkit("gnuplot");
  figure(1);
  set(gcf,'Color',[.5,.5,.5]) 
  subplot(2,4,1);  imshow(psf,[])
  subplot(2,4,2);  imshow(data,[0 photon_number]); 
  subplot(2,4,3);  imshow(data_b,[0 photon_number]);
  subplot(2,4,4);  imshow(data_n,[0 photon_number]);
  subplot(2,4,5);  imshow(estimated_psf_b,[])
  subplot(2,4,6);  imshow(estimated_psf_n,[])
  subplot(2,4,7);  imshow(estimated_data_b,[0 photon_number]); 
  subplot(2,4,8);  imshow(estimated_data_n,[0 photon_number]); 
  colormap(parula(256))

  figure(2);
  clf;
  subplot(2,2,1)
  plot(error_data_b,"r");
  subplot(2,2,2)
  plot(error_data_n,"r"); 
  subplot(2,2,3)
  plot(mse_bo,"r"); 
  subplot(2,2,4)
  plot(mse_no,"r"); 
  figure(3);
  clf;
  subplot(2,2,1)
  plot(ssim_bo,"r"); 
  subplot(2,2,2)
  plot(ssim_no,"r"); 
  subplot(2,2,3)
  plot(ssim_bi,"r"); 
  subplot(2,2,4)
  plot(ssim_ni,"r");
