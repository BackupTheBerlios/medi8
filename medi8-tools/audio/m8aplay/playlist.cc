// playlist
                                                                                                                                                             
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

#include "playlist.hh"
#include "log.hh"
#include <assert.h>

class list_item
{
	public:
		audio_source *source;
		jack_nframes_t length;
};

void
playlist::add (audio_source *source)
{
	list_item *item = new list_item ();
	item->source = source;
	item->length = source->length();
	plist.push_back (item);
	
	// Did we just push the first item?  If so,
	// set up the play list iterator.
	if (plist.size() == 1)
		play_itr = plist.begin();
}

jack_nframes_t
playlist::length ()
{
	jack_nframes_t length = 0;

  std::list<list_item*>::iterator itr;
	for (itr = plist.begin(); itr != plist.end(); ++itr)
		length += (*itr)->length;
		
	return length;
}

void 
playlist::locate (jack_nframes_t frame)
{
	for (play_itr = plist.begin(); play_itr != plist.end(); ++play_itr)
	{
		jack_nframes_t length = (*play_itr)->length; 
		if (frame > length)
			frame -= length;
		else
		{
			((*play_itr)->source)->locate (frame);
			return;
		}
	}
	log_error ("playlist::locate: attempting to seek beyond end");
	assert (0);
}
	                                                                                                                     
jack_nframes_t
playlist::process (jack_nframes_t nframes,
              		 jack_default_audio_sample_t * left,
              		 jack_default_audio_sample_t * right)
{
	jack_nframes_t count;
	
	count = (*play_itr)->source->process (nframes, left, right);
	
	// FIXME handle end.
	while (count < nframes)
		{
			++play_itr;
			nframes -= count;
			left = &left[count];
			right = &right[count];
			count = (*play_itr)->source->process (nframes, left, right);
		}
		
	return nframes;
}
