function [ESTIMATED_OBJECT] = llsdeconv(IMAGE, PSF,NOISE_CONSTANT=0.1) 
  ## usage: [ESTIMATED_OBJECT] = wdeconv(IMAGE, PSF,NOISE_CONSTANT=0.1) 
  ## Linear Deconvolution Method
  ## IMAGE is the acquired image
  ## PSF is the point spread function
  ## Returns 
  ## ESTIMATED_OBJECT
  ## Author: Javier Eduardo Diaz Zamboni
  ## Created: 2003/09/23
  ## Version: 0.2
  ## Maintainer: Javier Eduardo Diaz Zamboni
  ## Revision: 2017/02/17

  N = length(IMAGE);
  n = length(PSF);
  NN=N*N;
  KNN=K*NN;

  A = zeros(N);
  A1 = zeros(N);
  A(1:n,1:n) = imread(strcat(psfdir,num2str(psfnum),'.tif'));
  aux = 0;
  for i = psfnum + 1 : psfnum + (K - 1)
    A1(1:n,1:n) = imread(strcat(psfdir,num2str(psfnum + DZ*aux),'.tif'));
    A  = cat(2,A,A1);
    aux=aux+1;
  end
  sss =  zeros(KNN,1);
  sss = Circulant(A, N, K); % Funcion que devuelve un vector columna obtenido de una matriz en bloque circular con la informacion 3D.


  clear A A1 i; 

  Mu = fft(sss); % Aplicacion de dos FFT para determinar los autovalores Mu de sss. Estos resultados estan en la diagonal de Tsss. 

  clear sss;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Determinacion de los autovalores de sss para obtener los autovalores de
% la pseudoinversa de sss.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  MuOr = zeros(KNN,1);

  aux = sort(Mu);

  for i=1:KNN
    MuOr(i)= aux(KNN+(1-i));
  end

  clear aux i;

  AVal = MuOr/max(abs(Mu));

  clear Mu;

  MuOptimo = 0.0001; % Seleccion de los autovalores optimos de sss para la determinacion del estimador.


  for lh = 0:fix(NI/K)

    t1 = cputime; % Toma tiempo inicial. es para evaluar la velocidad de la ejecucion completa  de la implementacion del algoritmo.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% El siguientes ciclo While organiza el conjunto de imagenes como un vector columna
% donde se toman los renglones se los trasponen y se los va apilando uno
% debajo del otro, del primero al ultimo y desde la primer imagen a la
% ultima.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    chi = zeros(KNN,1);
    c=1;
    LL=0;
    while LL <= K-1
      Im = imread(strcat(DirIn,num2str(NumIn+LL),'.tif'));
      for i=1:N
        chi(c:c+N-1)=Im(:,i)';
        c = c + N;
      end
      LL = LL+1;
    end
    SumChi = sum(chi);
    clear LL Im c;

    Machi=max(chi);
    chiLim = [ min(chi) max(chi)];
				%chi=chi/Machi;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Ultimas expreciones. La segunda es la formula del estimador.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    chik = (fft(chi)); % Exprecion del vector de la pila de imagenes en el cnjunto ortogonal de autovectores dado por la matriz de transformacio de Fourier.

    aux= zeros(KNN,1);

    for i=1:KNN
      if abs(AVal(i))^2 >= MuOptimo 
        aux(i) = ((chik(i))/abs(AVal(i))^2); % Formula del estimador.
      else
        aux(i)=0;
      end
    end

    clear chi chik;

    Taok = abs(ifft(aux));
    TaokM = max(Taok);
				% Taok = Taok/Machi;
    Taok = Taok/TaokM;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% El siguientes ciclo While organiza reoganiza el resultado Taok como una
% pila de imagenes. es el proceso inveros al descrito en el ciclo anterior.
% Las imagenes obtenidas luego son visualizadas.s
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%E = sum(abs(chi-Taok))/SumChi;

    IR = zeros(N);
    c = 1;
    LL = 0;
    while LL <= K-1
      for i=1:N
        IR(i,:) = 255*Taok(c:c+N-1)';
        c = c + N;
      end
      imwrite(uint8(IR'),strcat(DirOut,num2str(NumOut + LL),'.tif'),'tif');
      LL = LL + 1;
    end

    %disp(strcat('Error (%): ',num2str(100*E)));   

    clear E LL c aux IR Taok chiLim Mu SumChi Machi;

    NumIn = NumIn + K;     
    NumOut = NumOut + K;     

    cputime - t1

    beep; beep; beep;

  end;
endfunction
