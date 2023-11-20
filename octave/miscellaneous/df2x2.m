function D=df2x2(F,X,APROX,H=0.001)
  ##  Copyright (C)  2016 Javier Eduardo Diaz Zamboni
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
  ## usage: D=df2x2(F,X,APROX,H=0.001)
  if (APROX>0)
    D=(F(X+2*H)-2*F(X+H)+F(X))/H.^2;
  elseif (APROX==0)
    D=(F(X+H)-2*F(X)+F(X-H))/H.^2;
  else
    D=(F(X)-2*F(X-H)+F(X-2*H))/H.^2;
  endif
endfunction
