// Thumbnail generator.
 
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

#include "consumer_thumb.h"

#include <framework/mlt.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct consumer_thumb_s *consumer_thumb;

struct consumer_thumb_s
{
	struct mlt_consumer_s parent;
	mlt_properties properties;
	int running;
	int width;
	int height;
};

static int consumer_start (mlt_consumer parent);
static int consumer_is_stopped (mlt_consumer parent);

/* This function is called by the factory object.  */
mlt_consumer 
consumer_thumb_init (char *arg)
{
  // Create the consumer object
  consumer_thumb this = calloc (sizeof (struct consumer_thumb_s), 1);
  
  // If no malloc'd and consumer init ok
  if ( this != NULL && mlt_consumer_init( &this->parent, this ) == 0 )
    {
      // Get the parent consumer object
      mlt_consumer parent = &this->parent;
      
      // Attach a colour space converter
      mlt_filter filter = mlt_factory_filter ("avcolour_space", NULL);
      mlt_properties_set_int (mlt_filter_properties (filter), 
      												"forced", mlt_image_yuv422);
      mlt_service_attach (mlt_consumer_service (&this->parent), filter);
      mlt_filter_close (filter);
      
      // get a handle on properties
      mlt_service service = mlt_consumer_service (parent);
      this->properties = mlt_service_properties (service);

      if (arg == NULL 
      		|| sscanf (arg, "%dx%d", &this->width, &this->height) != 2)
			{
	  		this->width = mlt_properties_get_int (this->properties, "width");
	  		this->height = mlt_properties_get_int (this->properties, "height");
			}
      
      // Allow thread to be started/stopped
      parent->start = consumer_start;
      parent->is_stopped = consumer_is_stopped;
      
      // Return the consumer produced
      return parent;
    }
  
  // malloc or consumer init failed
  free( this );
  
  // Indicate failure
  return NULL;
}

// TODO: Use user specified extents
#define WIDTH (320/4)
#define HEIGHT (240/4)

static int
consumer_start (mlt_consumer this)
{
  mlt_frame frame = NULL;
  uint8_t *image;
  mlt_image_format vfmt = mlt_image_rgb24;
  int width = WIDTH, height = HEIGHT;
  
  // Get the producer service
  mlt_service service = mlt_service_get_producer (mlt_consumer_service (this));
  
  // Seek to frame 666.
  // TODO: Seek to a frame defined by the user.
  mlt_producer_seek ((mlt_producer) service, 666);
  
  // Get a frame from the attached producer
  mlt_properties_set (mlt_consumer_properties (this), "aspect_ratio", "1.0");
  frame = mlt_consumer_get_frame (this);
  
  mlt_properties_set (mlt_frame_properties (frame), "distort", "true");
  mlt_frame_get_image (frame, 
					       			 &image,
		       						 &vfmt,
		       						 &width, &height, 
		       						 0);
  
  // TODO: write to a file specified by the user
  FILE *thumb = fopen ("/tmp/thumb.ppm", "wb");
  fprintf (thumb, "P6\n%d %d\n255\n", width, height);
  fwrite (image, width * height * 3, 1, thumb);
  fclose (thumb);
  
  // Close the frame, and stop processing.
  mlt_frame_close (frame);
  mlt_consumer_stop( this );
  
  return 0;
}

static int
consumer_is_stopped (mlt_consumer this)
{
  return 1;
}
