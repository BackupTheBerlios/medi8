// Engine for playing video.
 
// Copyright (C) 2004 Anthony Green
//
// This file is part of medi8.
//
// Medi8 is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
//
// Medi8 is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with medi8; see the file COPYING.  If not, write to the Free
// Software Foundation, 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.
 
#ifndef M8VPLAY_HH
#define M8VPLAY_HH
 
#include "mlt++/Mlt.h"
 
class m8vplay : public Mlt::Miracle
{
public:
	m8vplay () : Mlt::Miracle ("m8vplay", 5900) { }
};
 
#endif
