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

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <lo/lo.h>
#include <jack/jack.h>

static jack_client_t *jack_client;
static jack_port_t *jack_left_output_port;
static jack_port_t *jack_right_output_port;

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
                      
/**
 * The process callback is called by jack at the appropriate times.
 */
static int
process (jack_nframes_t nframes, void *arg)
{     
#if 0	                                                                                                                                  
  if (player->playing)
    {
      jack_default_audio_sample_t *left_out = (jack_default_audio_sample_t *)
        jack_port_get_buffer (player->jack_left_output_port, nframes);
                                                                                                                                        
      jack_default_audio_sample_t *right_out = (jack_default_audio_sample_t *)
        jack_port_get_buffer (player->jack_left_output_port, nframes);
                                                                                                                                        
      // Fill the buffers.
    }
#endif
                                                                                                                                   
  return 0;
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
                                                                                                                                        
  if (jack_set_process_callback (jack_client, process, NULL /* THIS */) != 0)
    log_error ("can't set callback");
                                                                                                                                        
  jack_error_callback = error_callback;
                                                                                                                                        
  jack_left_output_port =
    jack_port_register (jack_client, "output",
                        JACK_DEFAULT_AUDIO_TYPE, JackPortIsOutput, 0);
  jack_right_output_port =
    jack_port_register (jack_client, "output",
                        JACK_DEFAULT_AUDIO_TYPE, JackPortIsOutput, 0);
                                                                                                                                        
  log_debug ("engine sample rate: %u", jack_get_sample_rate (jack_client));
                                                                                                                                        
  jack_set_buffer_size (jack_client, 2048);
                                                                                                                                        
  jack_activate (jack_client);
                                                                                                                                        
  const char **ports;
  if ((ports = jack_get_ports (jack_client, NULL, NULL,
                               JackPortIsPhysical|JackPortIsInput)) == NULL)
    log_error ("Cannot find any physical playback ports");
                                                                                                                                        
  if (jack_connect (jack_client,
                    jack_port_name (jack_left_output_port), ports[0]))
    log_error ("Cannot connect left output ports");
  if (jack_connect (jack_client,
                    jack_port_name (jack_right_output_port), ports[1]))
    log_error ("Cannot connect right output ports");
                                                                                                                                        
  free (ports);
	
	// Tell medi8 that we've started up OK.
	puts ("OK");
	fflush (stdout);
	
	while (1)
	{
		sleep (100);
	}
}

int
generic_handler (const char *path, const char *types, lo_arg **argv,
                 int argc, void *data, void *user_data)
{
  int i;
                                                                                
  log_debug ("path: <%s>\n", path);
  for (i=0; i<argc; i++)
  {
    log_debug ("\targ %d '%c' ", i, types[i]);
    lo_arg_pp ((lo_type) types[i], argv[i]);
  }
              
  // Returning 1 means that we didn't handle the message, and
  // the server should try other methods.                                                                            
  return 1;
}
                                                                                
int
volume_handler (const char *path, const char *types, lo_arg **argv, 
                int argc, void *data, void *user_data)
{
  log_debug ("setting volume to %d\n", argv[0]->i);
  return 0;
}
