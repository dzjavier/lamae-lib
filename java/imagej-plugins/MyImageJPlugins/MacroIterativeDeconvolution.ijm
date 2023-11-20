///////////////////////////////////////////40x///////////////////////////////////////////////////
run("Image Sequence...", "open=/media/10B6C5E7B6C5CE02/Javier/ImagenesDePrueba/PSFs/Emp/PSF40/psf40004.tif number=32 starting=6 increment=1 scale=100 file=[] or=[] sort");
run("Image Sequence...", "open=/media/10B6C5E7B6C5CE02/Javier/ImagenesDePrueba/Samples/Patrones/Patron40X/4umCortadas64x64/psf40004.tif number=32 starting=7 increment=1 scale=100 file=[] or=[] sort");
run("Classic Iterative Deconvolution ", "raw=4umCortadas64x64 psf=PSF40");
///////////////////////////////////////////40x///////////////////////////////////////////////////

////////////////////////////////100x///////////////////////////////////////
//run("Image Sequence...", "open=/media/10B6C5E7B6C5CE02/Javier/ImagenesDePrueba/PSFs/Emp/psf100/psf100000.tif number=32 starting=1 increment=1 scale=100 file=[] or=[] sort");
//run("Image Sequence...", "open=/media/10B6C5E7B6C5CE02/Javier/ImagenesDePrueba/Samples/Patrones/Patron100x/4umCortadas64x64/psf100000.tif number=32 starting=1 increment=1 scale=100 file=[] or=[] sort");
//run("Classic Iterative Deconvolution ", "raw=4umCortadas64x64 psf=psf100");
////////////////////////////////100x///////////////////////////////////////
