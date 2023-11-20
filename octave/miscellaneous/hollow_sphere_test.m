NE=128;
  img=hollow_sphere(NE,fix(NE/2));
  graphics_toolkit("gnuplot");
  subplot(2,2,1)
  imagesc(squeeze(img(:,:,fix(NE/2))));
  subplot(2,2,2)
  imagesc(squeeze(img(fix(NE/2),:,:)));
  subplot(2,2,3)
  imagesc(squeeze(img(:,fix(NE/2),:)));
#  print("-dpng","cubo.png");
