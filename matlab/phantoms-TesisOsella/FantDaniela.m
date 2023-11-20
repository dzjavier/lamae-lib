clear all;
clc;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
NombreArchivoImagen='Fantoma03.tif';
NombreArchivoDatos='Fant03.txt';
NumOb=20;
NumNonOb=20;
Rm=12; % Radio mínimo en pixeles
RM=25; % Radio máximo en pixeles

BG=170; % Nivel de background
DeltaN=20; % Variacion global del ruido

%Valores de intensidad de los objetos
RedLevelS=160;
GreenLevelS=90;
BlueLevelS=80;

%Valores de intensidad de los NO objetos
RedLevelNOS=180;
GreenLevelNOS=123;
BlueLevelNOS=113;
DeltaS=10; % dispersión de la señal y la no señal
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%Resolución de la imagen
Xres=1909;
Yres=1273;

XYOb=(0.8*rand(NumOb,2))+0.1;%generación de posiciones aleatorias

for i=1:NumOb,
    XYOb(i,2)= round(Xres*XYOb(i,2));
    XYOb(i,1)= round(Yres*XYOb(i,1));
end

XYNonOb=(0.8*rand(NumNonOb,2))+0.1;%generación de posiciones aleatorias cuidado: Y X

for i=1:NumNonOb,
    XYNonOb(i,2)= round(Xres*XYNonOb(i,2));
    XYNonOb(i,1)= round(Yres*XYNonOb(i,1));
end

Im=BG*ones(Yres,Xres,3)+(randn(Yres,Xres,3)*0.5*DeltaN);
R=zeros(NumOb,1);
for i=1:NumOb,
    R(i)=round((Rm+(RM-Rm)/2)+(rand-0.5)*((RM-Rm)/2));
    Disco=fspecial('disk',R(i));
    MDeltaS=(randn(2*R(i)+1)*0.5)*DeltaS;
    Disco=Disco/(max(max(Disco)-min(min(Disco))));
    Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),1)= Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),1)-((BG-RedLevelS)*Disco)+MDeltaS;
    Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),2)= Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),2)-((BG-GreenLevelS)*Disco)+MDeltaS;
    Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),3)= Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),3)-((BG-BlueLevelS)*Disco)+MDeltaS;
end
dlmwrite(NombreArchivoDatos,'O','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,'YXR','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,[XYOb R],'-append','delimiter',' ','newline','pc');

R=zeros(NumNonOb,1);
for i=1:NumNonOb,
    R(i)=round((Rm+(RM-Rm)/2)+(rand-0.5)*((RM-Rm)/2));
    Disco=fspecial('disk',R(i));
    MDeltaS=(randn(2*R(i)+1)*0.5)*DeltaS;
    Disco=Disco/(max(max(Disco)-min(min(Disco))));
    Im(XYNonOb(i,1)-R(i):XYNonOb(i,1)+R(i),XYNonOb(i,2)-R(i):XYNonOb(i,2)+R(i),1)= Im(XYNonOb(i,1)-R(i):XYNonOb(i,1)+R(i),XYNonOb(i,2)-R(i):XYNonOb(i,2)+R(i),1)-((BG-RedLevelNOS)*Disco)+MDeltaS;
    Im(XYNonOb(i,1)-R(i):XYNonOb(i,1)+R(i),XYNonOb(i,2)-R(i):XYNonOb(i,2)+R(i),2)= Im(XYNonOb(i,1)-R(i):XYNonOb(i,1)+R(i),XYNonOb(i,2)-R(i):XYNonOb(i,2)+R(i),2)-((BG-GreenLevelNOS)*Disco)+MDeltaS;
    Im(XYNonOb(i,1)-R(i):XYNonOb(i,1)+R(i),XYNonOb(i,2)-R(i):XYNonOb(i,2)+R(i),3)= Im(XYNonOb(i,1)-R(i):XYNonOb(i,1)+R(i),XYNonOb(i,2)-R(i):XYNonOb(i,2)+R(i),3)-((BG-BlueLevelNOS)*Disco)+MDeltaS;
end

dlmwrite(NombreArchivoDatos,' ','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,'NO','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,'YXR','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,[XYNonOb R],'-append','delimiter',' ','newline','pc');

imwrite(uint8(Im),NombreArchivoImagen,'tif');