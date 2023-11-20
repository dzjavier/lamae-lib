function C=intensity_cube(size_a=128,size_c=64,intensity=255)
  ##  Copyright (C)  2015 Javier Eduardo Diaz Zamboni
  ##
  ##  This program is free software: you can redistribute it and/or modify
  ##  it under the terms of the GNU General Public License as published by
  ##  the Free Software Foundation, either version 3 of the License, or
  ##  (at your option) any later version.
  ##
  ##  This program is distributed in the hope that it will be useful,
  ##  but WITHOUT ANY WARRANTY; without even the implied warranty of
  ##  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ##  GNU General Public License for more details.
  ##
  ##  You should have received a copy of the GNU General Public License
  ##  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  
  ## usage: C=intensity_cube(SIZE_A,SIZE_C,INTENSITY)
  ##
  ## Returns a three-dimensional array of size SIZE_A*SIZE_A*SIZE_A
  ## with a centered cube of size SIZE_C*SIZE_C*SIZE_C with uniform INTENSITY.
  ## Example:
  ##  cub=intensity_cube(10,4,20);
  ##  cub(:,:,1)
  ##  cub(:,:,5)
  ##
  ## Author: Diaz-Zamboni Javier Eduardo
  ## Created: 2012/08/22
  ## Version: 0.1
  ##
  C=0;
  if ((size_a<0) && (size_c<0) && (intensity<0))
    usage ("all arguments must be positive");
  else
    if (size_a<size_c)
      usage(" 'SIZE_C' argument must be less or equal than SIZE_A");
    endif
    C=zeros(size_a,size_a,size_a);
    aux=intensity*ones(size_c,size_c,size_c);
    beg_aux=fix((size_a-size_c)/2)+1;
    end_aux=beg_aux+size_c-1;
    C(beg_aux:end_aux,beg_aux:end_aux,beg_aux:end_aux)=aux;
  endif

endfunction
