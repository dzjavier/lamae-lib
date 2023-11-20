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

## jvc deconvolution   function [ESTIMATED_OBJECT, ERROR] = jvcdeconv(IMAGE, PSF, TOL, MAX_ITER, RELAXATION_OPTION) 
MaxIter=100;
mse_bo_vs_rest_sqr=zeros(MaxIter,1);
mse_bo_vs_rest_abs=zeros(MaxIter,1);
mse_bo_vs_rest_cittert=zeros(MaxIter,1);
mse_no_vs_rest_sqr=zeros(MaxIter,1);
mse_no_vs_rest_abs=zeros(MaxIter,1);
mse_no_vs_rest_cittert=zeros(MaxIter,1);
ns="gaussian"; ## filtering
for i=1:MaxIter
  [estimated_data_n_sqr error_data_n_sqr]=jvcdeconv(data_n,psf,"noise_filtering",ns,"relaxation","square","max_iter",i,"tol",1e-3);
  [estimated_data_b_sqr error_data_b_sqr]=jvcdeconv(data_b,psf,"noise_filtering",ns,"relaxation","square","max_iter",i,"tol",1e-3); 
  [estimated_data_n_abs error_data_n_abs]=jvcdeconv(data_n,psf,"noise_filtering",ns,"relaxation","abs","max_iter",i,"tol",1e-3);
  [estimated_data_b_abs error_data_b_abs]=jvcdeconv(data_b,psf,"noise_filtering",ns,"relaxation","abs","max_iter",i,"tol",1e-3);
  [estimated_data_n_cittert error_data_n_cittert]=jvcdeconv(data_n,psf,"noise_filtering",ns,"relaxation","cittert","max_iter",i,"tol",1e-3); 
  [estimated_data_b_cittert error_data_b_cittert]= jvcdeconv(data_b,psf,"noise_filtering",ns,"relaxation","cittert","max_iter",i,"tol",1e-3);
  mse_bo_vs_rest_sqr(i)=sumsq(data(:)-estimated_data_b_sqr(:));
  mse_bo_vs_rest_abs(i)=sumsq(data(:)-estimated_data_b_abs(:));
  mse_bo_vs_rest_cittert(i)=sumsq(data(:)-estimated_data_b_cittert(:));
  mse_no_vs_rest_sqr(i)=sumsq(data(:)-estimated_data_n_sqr(:));
  mse_no_vs_rest_abs(i)=sumsq(data(:)-estimated_data_n_abs(:));
  mse_no_vs_rest_cittert(i)=sumsq(data(:)-estimated_data_n_cittert(:));  
endfor
  disp("Data\t Min\t Max\t Sum")
  disp(strcat("Or\t",num2str(min(data(:))),"\t",num2str(max(data(:))),"\t",num2str(sum(data(:)))))
  disp(strcat("blur\t",num2str(min(data_b(:))),"\t",num2str(max(data_b(:))),"\t",num2str(sum(data_b(:)))))
  disp(strcat("noisy\t",num2str(min(data_n(:))),"\t",num2str(max(data_n(:))),"\t",num2str(sum(data_n(:)))))
  disp(strcat("sqr\t",num2str(min(estimated_data_b_sqr(:))),"\t",num2str(max(estimated_data_b_sqr(:))),"\t",num2str(sum(estimated_data_b_sqr(:)))))
  disp(strcat("abs\t", num2str(min(estimated_data_b_abs(:))),"\t",num2str(max(estimated_data_b_abs(:))),"\t",num2str(sum(estimated_data_b_abs(:)))))
  disp(strcat("citt\t", num2str(min(estimated_data_b_cittert(:))),"\t",num2str(max(estimated_data_b_cittert(:))),"\t",num2str(sum(estimated_data_b_cittert(:)))))

  disp(strcat("sqr\t", num2str(min(estimated_data_n_sqr(:))),"\t",num2str(max(estimated_data_n_sqr(:))),"\t",num2str(sum(estimated_data_n_sqr(:)))))
  disp(strcat("abs\t", num2str(min(estimated_data_n_abs(:))),"\t",num2str(max(estimated_data_n_abs(:))),"\t",num2str(sum(estimated_data_n_abs(:)))))
  disp(strcat("citt\t", num2str(min(estimated_data_n_cittert(:))),"\t",num2str(max(estimated_data_n_cittert(:))),"\t",num2str(sum(estimated_data_n_cittert(:)))))

  graphics_toolkit("gnuplot");
  figure(1);
  set(gcf,'Color',[.5,.5,.5]) 
  subplot(4,4,1);  imshow(psf,[])
  subplot(4,4,2);  imshow(data,[0 photon_number]); 
  subplot(4,4,3);  imshow(data_b,[0 photon_number]);
  subplot(4,4,4);  imshow(data_n,[0 photon_number]);
  subplot(4,4,7);  imshow(estimated_data_b_sqr,[0 photon_number]); title("square")
  subplot(4,4,8);  imshow(estimated_data_n_sqr,[0 photon_number]); title("square")
  subplot(4,4,11);  imshow(estimated_data_b_abs,[0 photon_number]); title("abs")
  subplot(4,4,12);  imshow(estimated_data_n_abs,[0 photon_number]); title("abs")
  subplot(4,4,15);  imshow(estimated_data_b_cittert,[0 photon_number]); title("cittert")
  subplot(4,4,16);  imshow(estimated_data_n_cittert,[0 photon_number]); title("cittert")
  colormap(parula(256))

  figure(2);
  clf;
  subplot(2,2,1)
  plot(error_data_b_sqr,"r"); hold on;
  plot(error_data_b_abs,"g");
  plot(error_data_b_cittert,"b");hold off;
  subplot(2,2,2)
  plot(error_data_n_sqr,"r"); hold on;
  plot(error_data_n_abs,"g");
  plot(error_data_n_cittert,"b");hold off;
  subplot(2,2,3)
  plot(mse_bo_vs_rest_sqr,"r"); hold on;
  plot(mse_bo_vs_rest_abs,"g");
  plot(mse_bo_vs_rest_cittert,"b");hold off;
  subplot(2,2,4)
  plot(mse_no_vs_rest_sqr,"r"); hold on;
  plot(mse_no_vs_rest_abs,"g");
  plot(mse_no_vs_rest_cittert,"b");hold off;
