function SPROD = non_uniform_prod(X1,S1,X2,S2,XI,INTERP="linear")
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
## usage: SPROD = non_uniform_prod(X1,S1,X2,S2,XI)
  if ( ((X1(1)<=XI(1)) && (X1(end)>=XI(end))) && ((X2(1)<=XI(1)) && (X2(end)>=XI(end))) )
  new_S1=interp1(X1,S1,XI,INTERP);
  new_S2=interp1(X2,S2,XI,INTERP);
  SPROD=new_S1.*new_S2;
  endif
endfunction
