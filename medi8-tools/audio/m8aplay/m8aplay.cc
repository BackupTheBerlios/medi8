// Engine for playing audio.

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

#include "bus.hh"
#include "ffmpeg_file_reader.hh"
#include "playlist.hh"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <lo/lo.h>
#include <jack/jack.h>
#include <jack/transport.h>

static jack_client_t *jack_client;
static jack_port_t *jack_left_output_port;
static jack_port_t *jack_right_output_port;

static bus *master_bus;

// Forward declarations of OSC message handlers.
static int
generic_handler (const char *path, const char *types, lo_arg **argv, 
                 int argc, void *data, void *user_data);

static int
volume_handler (const char *path, const char *types, lo_arg **argv, 
                int argc, void *data, void *user_data);
                
// The OSC error handler.
static void
error (int num, const char *msg, const char *path)
{
	log_error ("liblo server error %d in path %s: %s", num, path, msg);
}
      
      
static int playing = 0;    
            
/**
 * The process callback is called by jack at the appropriate times.
 */
static int
process (jack_nframes_t nframes, void *arg)
{                                                                                                                                 
  if (master_bus)
    {
    	jack_position_t pos;
    	
      jack_default_audio_sample_t *left_out = (jack_default_audio_sample_t *)
        jack_port_get_buffer (jack_left_output_port, nframes);
                                                                                                                                        
      jack_default_audio_sample_t *right_out = (jack_default_audio_sample_t *)
        jack_port_get_buffer (jack_right_output_port, nframes);

			// Clear the buffers before we start.
			bzero (left_out, nframes * 4);
			bzero (right_out, nframes * 4);
			
      if (jack_transport_query (jack_client, &pos) == JackTransportRolling)
      { 
        // Pull the data from the master bus.
		    master_bus->process (nframes, left_out, right_out);
      }
    }
                                                                 
  return 0;
}

static int
sync_callback (jack_transport_state_t state, jack_position_t *pos, void *arg)
{
	log_debug ("sync_callback");
	if (state != JackTransportStopped)
		playing = 1;
		
 	if (master_bus)
 		master_bus->locate (pos->frame);
	
	return 1;
}

static void
error_callback (const char *s)
{
  log_error ("%s", s);
}
                                                                                                                                        
int
main (int argc, char *argv[])
{
	// FIXME do something smarter with log data.
	init_logger (MEDI8_LOG_DEBUG, "/tmp/m8aplay.log");
	
	// Create the master bus.
	master_bus = new bus ("Master Bus");

	// Start a new server on port 5333
	// FIXME read the port from the cmd line.
  lo_server_thread st = lo_server_thread_new ("5333", error);
                                                                                
  // add method that will match any path and args.
  lo_server_thread_add_method (st, NULL, NULL, generic_handler, NULL);
                                                                                
  // add the volume handler.
  lo_server_thread_add_method (st, "/medi8/audio/volume", 
           	                   "i", volume_handler, NULL);
           	                   
  // Start the OSC server thread.
  lo_server_thread_start (st);

  if ((jack_client = jack_client_new ("medi8")) == 0)
    log_error ("can't create jack client");
                                                                                                                                        
  if (jack_set_process_callback (jack_client, process, NULL) != 0)
    log_error ("can't set process callback");
    
  if (jack_set_sync_callback (jack_client, sync_callback, NULL) != 0)
  	log_error ("can't set sync callback");
                                                                                                                                        
  jack_error_callback = error_callback;
                                                                                                                                        
  jack_left_output_port =
    jack_port_register (jack_client, "output_left",
                        JACK_DEFAULT_AUDIO_TYPE, JackPortIsOutput, 0);
  jack_right_output_port =
    jack_port_register (jack_client, "output_right",
                        JACK_DEFAULT_AUDIO_TYPE, JackPortIsOutput, 0);
                                                                                                                                        
  log_debug ("engine sample rate: %u", jack_get_sample_rate (jack_client));
                                                                                                                                        
  if (jack_activate (jack_client))
  	log_error ("cannot activate client");

	// Tell medi8 that we've started up OK.
	puts ("OK");
	fflush (stdout);
	
#if 1
	// FIXME --------------------------------------------------------------
	// FIXME --------------------------------------------------------------
	// FIXME --------------------------------------------------------------
	playlist pl;
	pl.add (new ffmpeg_file_reader ("/home/green/runtime-workbench-workspace/Demo/monk.mpg",
						jack_get_sample_rate (jack_client)));
	pl.add (new ffmpeg_file_reader ("/home/green/runtime-workbench-workspace/Demo/monk.mpg",
						jack_get_sample_rate (jack_client)));						
	master_bus->add_input (&pl);
	// FIXME --------------------------------------------------------------
	// FIXME --------------------------------------------------------------
	// FIXME --------------------------------------------------------------
#endif

	while (1)
	{
		sleep (100);
	}
}

#define LEFT_PORT "/medi8/audio/port/left/"
#define RIGHT_PORT "/medi8/audio/port/right/"

static int
set_left_port (const char *pname)
{
	log_debug ("setting left port to %s", pname);
	jack_port_disconnect (jack_client, jack_left_output_port);
	jack_connect (jack_client,
								jack_port_name (jack_left_output_port), pname);
	return 0;
}

static int
set_right_port (const char *pname)
{
	log_debug ("setting right port to %s", pname);
	jack_port_disconnect (jack_client, jack_right_output_port);
	jack_connect (jack_client,
								jack_port_name (jack_right_output_port), pname);
	return 0;
}

int
generic_handler (const char *path, const char *types, lo_arg **argv,
                 int argc, void *data, void *user_data)
{
	/* Handle various messages.  */
	if (strncmp (path, LEFT_PORT, 
		           strlen (LEFT_PORT)) == 0)
		set_left_port (&path[strlen (LEFT_PORT)]);
	else if (strncmp (path, RIGHT_PORT, 
		           strlen (RIGHT_PORT)) == 0)
		set_right_port (&path[strlen (RIGHT_PORT)]);
	else                                          	                                                                            
    log_warning ("unrecognized OSC message: <%s>", path);
   
  // Returning 1 means that we didn't handle the message, and
  // the server should try other methods.                                                                            
  return 1;
}
                                                                    
int
volume_handler (const char *path, const char *types, lo_arg **argv, 
                int argc, void *data, void *user_data)
{
  log_debug ("setting volume to %d", argv[0]->i);
  return 0;
}
