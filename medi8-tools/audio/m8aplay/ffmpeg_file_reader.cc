// ffmpeg file reader

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

#include <assert.h>
#include <stdlib.h>

#include <ffmpeg/avcodec.h>
#include "ffmpeg_file_reader.hh"
#include "log.hh"

bool ffmpeg_file_reader::ffmpeg_initialized = false;

ffmpeg_file_reader::ffmpeg_file_reader (const char *filename,
																				jack_nframes_t srate)
{
  sample_rate = srate;

  if (ffmpeg_initialized == false)
    {
      log_debug ("initializing ffmpeg library");
      av_register_all ();
      ffmpeg_initialized = true;
    }

  // FIXME: error handling
  if (av_open_input_file (&format_context, filename, NULL, 0, NULL) != 0)
  {
  	log_error ("ffmpeg couldn't open file %s", filename);
    assert (0);
  }

  // FIXME: error handling
  if (av_find_stream_info (format_context) < 0)
  {
  	log_error ("ffmpeg couldn't find stream info for %s", filename);
    assert (0);
  }

  audio_buffer_position = 0;
  audio_buffer_end = 0;

  for (int i = 0; i < format_context->nb_streams; i++)
    {
      AVCodec *codec = NULL;

      codec_context = &format_context->streams[i]->codec;
      if (codec_context->codec_type == CODEC_TYPE_AUDIO)
			{
	  		audio_stream_id = i;
	  		log_debug ("ffmpeg_file_reader: setting audio_stream_id to %d", i);
	  		codec = avcodec_find_decoder (codec_context->codec_id);
	  		if (codec)
	    		avcodec_open (codec_context, codec);
	  		else
	  		{
	  			log_error ("ffmpeg_file_reader: no codec for %s", filename);
	    		assert (0);
	  		}

	  		total_buffered_packet_count = 0;
			}
    }
    
    rsc = audio_resample_init (2, 2, srate, codec_context->sample_rate);
	  log_debug ("initialized rsc with srate = %d", srate);
}

// Seek to a new position.
void 
ffmpeg_file_reader::locate (jack_nframes_t frame)
{
	log_debug ("about to seek");
	if (av_seek_frame (format_context, audio_stream_id, frame, 0) < 0)
		log_error ("could not seek to position %u", (unsigned) frame);
}

jack_nframes_t
ffmpeg_file_reader::length ()
{
	return format_context->duration;
}

jack_nframes_t
ffmpeg_file_reader::process (jack_nframes_t nframes, 
			     									 jack_default_audio_sample_t *left_buffer,
			     									 jack_default_audio_sample_t *right_buffer)
{
	jack_nframes_t nframe_request = nframes;
  int valid_buffer_frames = (audio_buffer_end - audio_buffer_position) / 2;

  while (true)
    {
      // Handle the case where our audio buffer holds enough data already.
      // We always exit the loop here.
      if ((unsigned) valid_buffer_frames >= (unsigned) nframes)
			{
	  		for (unsigned int i = 0; i < nframes; i++)
	    	{
	      	left_buffer[i] += audio_buffer[audio_buffer_position++];
	      	right_buffer[i] += audio_buffer[audio_buffer_position++];
	    	}
	  		return nframe_request;
			}
      // We don't currently have enough in our buffer.  Write out
      // everything we currently have.
      for (int i = 0; i < valid_buffer_frames; i++)
			{
	  		left_buffer[i] += audio_buffer[audio_buffer_position++];
	  		right_buffer[i] += audio_buffer[audio_buffer_position++];
			}
     	// Adjust what we're looking for.
      nframes -= valid_buffer_frames;
     	left_buffer = &left_buffer[valid_buffer_frames];
     	right_buffer = &right_buffer[valid_buffer_frames];
     	// Refill our buffer.
     	AVPacket *packet = (AVPacket *) malloc (sizeof (AVPacket));
      int read_audio = 0;
      while (!read_audio)
      {
     		if (av_read_frame (format_context, packet) >= 0)
				{
	  			if (packet->stream_index == audio_stream_id)
	   			{
	   				read_audio = 1;
	   				
     				short samples[AVCODEC_MAX_AUDIO_FRAME_SIZE / 2];
     				short resamples[AVCODEC_MAX_AUDIO_FRAME_SIZE];
     				int data_size;
     				avcodec_decode_audio (codec_context, samples, &data_size, 
			   													packet->data, packet->size);
			   													   													   													
         		int c = audio_resample (rsc, resamples, samples, data_size / 4);

     				audio_buffer_position = 0;
     				audio_buffer_end = c * 2; // / sizeof (short);

     				// short to float conversion
     				for (int i = audio_buffer_end - 1; i >= 0; i--)
     					audio_buffer[i] = (float) (resamples[i] / (1.0 * (float)0x8000));
 	    			valid_buffer_frames = audio_buffer_end / 2;
	   			}
  	   		av_free_packet (packet);
	   		}	
				else
				{
					// We've hit the end of the file.  Return the number
					// of frames read so far.
					return (nframe_request - nframes);
				}
      }
		}
}
