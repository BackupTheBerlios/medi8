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

#ifndef BUS_HH
#define BUS_HH
                         
#include <list>

#include "audio_source.hh"

class bus : public audio_source
{
private:
	// The name of this bus.
	const char *name;

	// A queue of input audio sources.
	std::list<audio_source*> input_list;
  
  // A queue of audio effects.
  std::list<audio_source*> effect_list;
                                                                                                                           
public:
	// Create a new bus.
  bus (const char *name);
  
  // Add an input source to this bus.
  void add_input (audio_source *);

  // Add an effect to this bus.
  void add_effect (audio_source *);                  

	// Seek to a new position.
	virtual void locate (jack_nframes_t frame);
	
	virtual jack_nframes_t length ();
	
  // Pull some audio from this bus.                                                                                                                                       
  virtual jack_nframes_t process (jack_nframes_t,
                        					jack_default_audio_sample_t *,
                        					jack_default_audio_sample_t *);
};
                                                                                                                                                             
#endif
