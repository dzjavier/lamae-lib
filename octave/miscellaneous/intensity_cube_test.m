img=intensity_cube;
graphics_toolkit("gnuplot");
imagesc(squeeze(img(:,:,64)));
print("-dpng","cubo.png");
