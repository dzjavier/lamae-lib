function log_fact=log_factorial(n)
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
  ## usage: log_fact=log_factorial(n)
  N=max(n(:));
  if (N==0)
  log_fact=log(1);
  else
  log_fact=zeros(size(n));
  data=1:N;
  for j=1:length(n(:))
    log_fact(j)=sum(log(data(1:n(j))));
  endfor
  endif
endfunction
