function D=dfx(F,X,APROX,H=0.001)
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

## usage: D=dfx(F,X,APROX,H=0.001)
## APROX -1, 0 or 1 for backward, central and forward difference
   if (APROX>0)
      D=(F(X+H)-F(X))./H;
    elseif (APROX==0)
      D=(F(X+H)-F(X-H))./(2*H);
    else
      D=(F(X)-F(X-H))./H;
    endif
  endfunction
