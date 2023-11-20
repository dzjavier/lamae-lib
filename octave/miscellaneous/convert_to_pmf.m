function PMF=convert_to_pmf(MODEL,PARAMETERS)
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
  ## converts a model to be valued with PARAMETERS in an approximation
  ## of a probability mass function
  PMF=MODEL(PARAMETERS)./sum(MODEL(PARAMETERS)(:));
endfunction
