clear all;
clc;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
NombreArchivoImagen='Fantoma01.tif';
NombreArchivoDatos='Fant01.txt';

NumOb=30; % Numero de Objetos 
NumNonOb1=80; % Numero de No objetos Tipo 1
NumNonOb2=50; % Numero de NO objetos Tipo 2

RmOb=12; % Radio mínimo en pixeles de Objetos
RMOb=16; % Radio máximo en pixeles de Objetos

RmNoOb1=8; % Radio mínimo en pixeles de No Objetos Tipo 1
RMNoOb1=30; % Radio máximo en pixeles de No Objetos Tipo 1

RmNoOb2=3; % Radio mínimo en pixeles de No Objetos Tipo 2
RMNoOb2=30; % Radio máximo en pixeles de No Objetos Tipo 2

BG=170; % Nivel de background

%Valores de intensidad de los objetos
RedLevelS=110;
GreenLevelS=86;
BlueLevelS=81;

%Valores de intensidad de los NO objetos Tipo 1
RedLevelNOS=180;
GreenLevelNOS=123;
BlueLevelNOS=113;

%Valores de intensidad de los NO objetos Tipo 2
RedLevelNOS2=48;
GreenLevelNOS2=49;
BlueLevelNOS2=56;


DeltaS=12; % dispersión de la señal
DeltaSNO1=6; % dispersión de la no señal Tipo 1
DeltaSNO2=6; % dispersión de la no señal Tipo 2
DeltaN=6; % Variacion global del ruido
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%Resolución de la imagen
Xres=1909;
Yres=1273;
DimX=0.1426; %micras por pixel
DimY=0.1426; %micras por pixel
AreaPixel=DimX*DimY; % pixeles cuadrados

X=(0.95*rand(NumOb,1))+0.025; %generación de posiciones aleatorias en X de Objetos
Y=(0.25*rand(NumOb,1))+0.25; %generación de posiciones aleatorias en Y de Objetos
XYOb=zeros(NumOb,2);
for i=1:NumOb,
    XYOb(i,2)= round(Xres*X(i));
    XYOb(i,1)= round(Yres*Y(i));
end

X=(0.95*rand(NumNonOb1,1))+0.025;%generación de posiciones aleatorias en X de No Objetos
Y=(0.55*rand(NumNonOb1,1))+0.25;%generación de posiciones aleatorias en Y de No Objetos
XYNonOb1=zeros(NumNonOb1,2);%generación de posiciones aleatorias cuidado: Y X

for i=1:NumNonOb1,
    XYNonOb1(i,2)= round(Xres*X(i));
    XYNonOb1(i,1)= round(Yres*Y(i));
end

X=(0.95*rand(NumNonOb2,1))+0.025;%generación de posiciones aleatorias en X de No Objetos
Y=(0.3*rand(NumNonOb2,1))+0.05;%generación de posiciones aleatorias en Y de No Objetos
XYNonOb2=zeros(NumNonOb2,2);%generación de posiciones aleatorias cuidado: Y X

for i=1:NumNonOb2,
    XYNonOb2(i,2)= round(Xres*X(i));
    XYNonOb2(i,1)= round(Yres*Y(i));
end

Im=BG*ones(Yres,Xres,3)+(randn(Yres,Xres,3)*0.5*DeltaN);
R=zeros(NumOb,1);
for i=1:NumOb,
    R(i)=round((RmOb+(RMOb-RmOb)/2)+(rand-0.5)*((RMOb-RmOb)/2));
    Disco=fspecial('disk',R(i));
    MDeltaS=(randn(2*R(i)+1)*0.5)*DeltaS;
    Disco=Disco/(max(max(Disco)-min(min(Disco))));
    Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),1)= Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),1)-((BG-RedLevelS)*Disco)+MDeltaS;
    Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),2)= Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),2)-((BG-GreenLevelS)*Disco)+MDeltaS;
    Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),3)= Im(XYOb(i,1)-R(i):XYOb(i,1)+R(i),XYOb(i,2)-R(i):XYOb(i,2)+R(i),3)-((BG-BlueLevelS)*Disco)+MDeltaS;
end
Areas=zeros(NumOb,1);
for i=1:NumOb,
    Areas(i)=(pi*R(i)^2)*AreaPixel;
    XYOb(i,1)=XYOb(i,1)*DimY;
    XYOb(i,2)=XYOb(i,2)*DimX;
    R(i)=R(i)*DimX; % o DimY siempre y cuando sea cuadrado
end

dlmwrite(NombreArchivoDatos,'O','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,'XYR Area','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,[XYOb R Areas],'-append','delimiter',' ','newline','pc');

R=zeros(NumNonOb1,1);
for i=1:NumNonOb1,
    R(i)=round((RmNoOb1+(RMNoOb1-RmNoOb1)/2)+(rand-0.5)*((RMNoOb1-RmNoOb1)/2));
    Disco=fspecial('disk',R(i));
    MDeltaS=(randn(2*R(i)+1)*0.5)*DeltaSNO1;
    Disco=Disco/(max(max(Disco)-min(min(Disco))));
    Im(XYNonOb1(i,1)-R(i):XYNonOb1(i,1)+R(i),XYNonOb1(i,2)-R(i):XYNonOb1(i,2)+R(i),1)= Im(XYNonOb1(i,1)-R(i):XYNonOb1(i,1)+R(i),XYNonOb1(i,2)-R(i):XYNonOb1(i,2)+R(i),1)-((BG-RedLevelNOS)*Disco)+MDeltaS;
    Im(XYNonOb1(i,1)-R(i):XYNonOb1(i,1)+R(i),XYNonOb1(i,2)-R(i):XYNonOb1(i,2)+R(i),2)= Im(XYNonOb1(i,1)-R(i):XYNonOb1(i,1)+R(i),XYNonOb1(i,2)-R(i):XYNonOb1(i,2)+R(i),2)-((BG-GreenLevelNOS)*Disco)+MDeltaS;
    Im(XYNonOb1(i,1)-R(i):XYNonOb1(i,1)+R(i),XYNonOb1(i,2)-R(i):XYNonOb1(i,2)+R(i),3)= Im(XYNonOb1(i,1)-R(i):XYNonOb1(i,1)+R(i),XYNonOb1(i,2)-R(i):XYNonOb1(i,2)+R(i),3)-((BG-BlueLevelNOS)*Disco)+MDeltaS;
end
Areas=zeros(NumNonOb1,1);
for i=1:NumNonOb1,
    Areas(i)=(pi*R(i)^2)*AreaPixel;
    XYNonOb1(i,1)=XYNonOb1(i,1)*DimY;
    XYNonOb1(i,2)=XYNonOb1(i,2)*DimX;
    R(i)=R(i)*DimX; % o DimY siempre y cuando sea cuadrado
end

dlmwrite(NombreArchivoDatos,' ','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,'NO1','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,'XYR Areas','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,[XYNonOb1 R Areas],'-append','delimiter',' ','newline','pc');

R=zeros(NumNonOb2,1);
for i=1:NumNonOb2,
    R(i)=round((RmNoOb2+(RMNoOb2-RmNoOb2)/2)+(rand-0.5)*((RMNoOb2-RmNoOb2)/2));
    Disco=fspecial('disk',R(i));
    MDeltaS=(randn(2*R(i)+1)*0.5)*DeltaSNO2;
    Disco=Disco/(max(max(Disco)-min(min(Disco))));
    Im(XYNonOb2(i,1)-R(i):XYNonOb2(i,1)+R(i),XYNonOb2(i,2)-R(i):XYNonOb2(i,2)+R(i),1)= Im(XYNonOb2(i,1)-R(i):XYNonOb2(i,1)+R(i),XYNonOb2(i,2)-R(i):XYNonOb2(i,2)+R(i),1)-((BG-RedLevelNOS2)*Disco)+MDeltaS;
    Im(XYNonOb2(i,1)-R(i):XYNonOb2(i,1)+R(i),XYNonOb2(i,2)-R(i):XYNonOb2(i,2)+R(i),2)= Im(XYNonOb2(i,1)-R(i):XYNonOb2(i,1)+R(i),XYNonOb2(i,2)-R(i):XYNonOb2(i,2)+R(i),2)-((BG-GreenLevelNOS2)*Disco)+MDeltaS;
    Im(XYNonOb2(i,1)-R(i):XYNonOb2(i,1)+R(i),XYNonOb2(i,2)-R(i):XYNonOb2(i,2)+R(i),3)= Im(XYNonOb2(i,1)-R(i):XYNonOb2(i,1)+R(i),XYNonOb2(i,2)-R(i):XYNonOb2(i,2)+R(i),3)-((BG-BlueLevelNOS2)*Disco)+MDeltaS;
end
Areas=zeros(NumNonOb2,1);
for i=1:NumNonOb2,
    Areas(i)=(pi*R(i)^2)*AreaPixel;
    XYNonOb2(i,1)=XYNonOb1(i,1)*DimY;
    XYNonOb2(i,2)=XYNonOb1(i,2)*DimX;
    R(i)=R(i)*DimX; % o DimY siempre y cuando sea cuadrado
end

dlmwrite(NombreArchivoDatos,' ','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,'NO2','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,'XYR Areas','-append','delimiter',' ','newline','pc');
dlmwrite(NombreArchivoDatos,[XYNonOb2 R Areas],'-append','delimiter',' ','newline','pc');

imwrite(uint8(Im),NombreArchivoImagen,'tif');

beep; 
beep;
beep;
beep;