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

#ifndef FFMPEG_FILE_READER_HH
#define FFMPEG_FILE_READER_HH

#include <ffmpeg/avformat.h>
                                                                                                                                                             
#include "audio_source.hh"
                                                                                                                                                             
class ffmpeg_file_reader : public audio_source
{
private:
  static bool ffmpeg_initialized;
  unsigned int total_buffered_packet_count;
                                                                                                                                                             
  AVFormatContext *format_context;
  AVCodecContext *codec_context;
                                                                                                                                                             
  int audio_stream_id;
                                                                                                                                                             
  jack_nframes_t sample_rate;
                                                                                                                                                             
  int audio_buffer_position, audio_buffer_end;
  float audio_buffer[AVCODEC_MAX_AUDIO_FRAME_SIZE * 2];
  
  ReSampleContext *rsc;
                                                                                                                                                             
public:
  ffmpeg_file_reader (const char *filename, jack_nframes_t srate);
  static void *reader_thread (void *);
                                                                                                                                                             
	// Seek to a new position.
	virtual void locate (jack_nframes_t frame);
	
	virtual jack_nframes_t length ();
	
  virtual jack_nframes_t process (jack_nframes_t,
                        					jack_default_audio_sample_t *,
                        					jack_default_audio_sample_t *);
};
                                                                                                                                                             
#endif
