// audio bus
                                                                                                                                                             
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
               
#include <string.h>
#include <math.h>

#include "bus.hh"
#include "log.hh"
#include "audio_source.hh"

// Create a new bus.
bus::bus (const char *name_)
{
	name = strdup (name_);
}

void
bus::add_input (audio_source *source)
{
	input_list.push_back (source);
}

void
bus::add_effect (audio_source *effect)
{
	effect_list.push_back (effect);
}                

void 
bus::locate (jack_nframes_t frame)
{	
	// If there are no effects, simply mix the inputs.  Otherwise, start
	// pulling data through the effects.
	if (effect_list.empty ())
		{
			// Mix all input.
			std::list<audio_source*>::iterator itr;
			for (itr = input_list.begin(); itr != input_list.end(); ++itr)
				(*itr)->locate (frame);
		}
	else
		{
			effect_list.front ()->locate (frame);
		}
}
	
jack_nframes_t
bus::length ()
{
	jack_nframes_t length = 0;
	jack_nframes_t ilength;
	
	std::list<audio_source*>::iterator itr;
	for (itr = input_list.begin(); itr != input_list.end(); ++itr)
	{
		ilength = (*itr)->length ();
		length = length > ilength ? length : ilength;
	}
	
	return length;
}
	                                                                                                                     
jack_nframes_t
bus::process (jack_nframes_t nframes,
              jack_default_audio_sample_t * left,
              jack_default_audio_sample_t * right)
{
	// If there are no effects, simply mix the inputs.  Otherwise, start
	// pulling data through the effects.
	if (effect_list.empty ())
		{
			// Mix all input.
			std::list<audio_source*>::iterator itr;
			for (itr = input_list.begin(); itr != input_list.end(); ++itr)
				(*itr)->process (nframes, left, right);
		}
	else
		{
			effect_list.front ()->process (nframes, left, right);
		}
	return nframes;
}

