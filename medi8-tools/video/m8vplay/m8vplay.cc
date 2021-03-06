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

#include "log.hh"
#include "m8vplay.hh"

#include <stdlib.h>
#include <unistd.h>
#include <SDL/SDL.h>

int
main (int argc, char *argv[])
{
	// FIXME do something smarter with log data.
	init_logger (MEDI8_LOG_DEBUG, "/tmp/m8vplay.log");

	if (argc != 3)
	{
	  log_error ("Usage: m8vplay WIDTH HEIGHT\n");
	  exit (1);
	}
	
	log_debug ("Environment variable SDL_WINDOWID=%s", getenv ("SDL_WINDOWID"));
	
	int width = atoi (argv[1]);
	int height = atoi (argv[2]);
    
  log_debug ("width = %d", width);
  log_debug ("height = %d", height);
    	                                                                            
  if (SDL_Init (SDL_INIT_VIDEO) < 0)
    {
      log_error ("Unable to init SDL: %s", SDL_GetError ());
      // FIXME how to fail gracefully?
      // exit (1);
    }
  atexit (SDL_Quit);
                                                                                
  SDL_Surface *screen;
  screen = SDL_SetVideoMode (width, height, 32, SDL_HWSURFACE|SDL_DOUBLEBUF);
  if (screen == NULL)
    {
      log_error ("Unable to set %dx%d video: %s",
               	 width, height, SDL_GetError ());
      // exit (1);
    }
    
  m8vplay player;
	
	player.start();

	player.wait_for_shutdown();
}
