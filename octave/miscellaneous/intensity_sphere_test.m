N=64;
img=intensity_sphere(N);
graphics_toolkit("gnuplot");
subplot(2,2,1)
imagesc(squeeze(img(:,:,fix(N/2))));
subplot(2,2,2)
imagesc(squeeze(img(fix(N/2),:,:)));
subplot(2,2,3)
imagesc(squeeze(img(:,fix(N/2),:)));
