function [hml hw1 hw2]= drawArrow(X1,Y1,X2,Y2,L,W)
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
  ## usage: [hml hw1 hw2]= drawArrow(X1,Y1,X2,Y2,L,W)
  alpha=acos((X2-X1)/sqrt((X2-X1)^2+(Y2-Y1)^2));
  salpha=sin(alpha);
  calpha=cos(alpha);
  xt2=-W/2*salpha-L*calpha+X2;
  yt2=W/2*calpha-L*salpha+Y2;
  xt3=-(-W/2*salpha)-L*calpha+X2;
  yt3=-W/2*calpha-L*salpha+Y2;
  hml=line([X1 X2],[Y1 Y2]);
  hw1=line([xt2 X2],[yt2 Y2]);
  hw2=line([xt3 X2],[yt3 Y2]);      
endfunction
