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
m.pixel_size=9;
peak_shift=-1.0;
t_s=5;
m.image_formation = @(T_S,NORM)generate_image_stack(@(XX,YY,ZZ)m.gl_psf_punctual(XX,YY,ZZ,T_S),...
                                                    m.pixel_size,m.pixel_size,m.delta_z,...
                                                    m.n_row,m.n_col,m.n_lay,NORM,peak_shift);
S_max=sum(m.image_formation(0,"none")(:));
psf=m.image_formation(t_s,"none")/S_max;
data=zeros(128);
data(32:95,32:95)=data(32:95,32:95)+ones(64);
data(48:79,48:79)=data(48:79,48:79)-ones(32);
data(100:110,100)=1;
data(100:110,102)=1;
data(10,100:110)=0.3;
data(12,100:110)=0.3;
data=100*data;
data_b=abs(ifft2(fft2(data).*(abs(fft2(psf,128,128)))));
data_n=poissrnd(data_b);
## data_n=data_b+normrnd(100,2,size(data_b));
C=10;
[estimated_data_n]=wdeconv(data_n,psf,"snr",C); 
[estimated_data_b]=wdeconv(data_b,psf,"snr",C); 
graphics_toolkit("gnuplot");
figure(1);
set(gcf,'Color',[.5,.5,.5]) 
subplot(2,3,1)
imshow(data,[])
subplot(2,3,2)
imshow(data_b,[]); 
subplot(2,3,3)
imshow(data_n,[]);
subplot(2,3,4)
imshow(psf,[]);
subplot(2,3,6)
imshow(estimated_data_n,[]);
subplot(2,3,5)
imshow(estimated_data_b,[]);
colormap(parula)
