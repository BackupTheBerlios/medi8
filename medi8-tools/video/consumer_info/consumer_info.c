// Info generator.
 
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

#include "consumer_info.h"

#include <framework/mlt.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct consumer_info_s *consumer_info;

struct consumer_info_s
{
	struct mlt_consumer_s parent;
	mlt_properties properties;
};

static int consumer_start (mlt_consumer parent);
static int consumer_is_stopped (mlt_consumer parent);

/* This function is called by the factory object.  */
mlt_consumer 
consumer_info_init (char *arg)
{
  // Create the consumer object
  consumer_info this = calloc (sizeof (struct consumer_info_s), 1);
  
  // If no malloc'd and consumer init ok
  if ( this != NULL && mlt_consumer_init (&this->parent, this) == 0)
    {
      // Get the parent consumer object
      mlt_consumer parent = &this->parent;
      
      // get a handle on properties
      mlt_service service = mlt_consumer_service (parent);
      this->properties = mlt_service_properties (service);

      // Allow thread to be started/stopped
      parent->start = consumer_start;
      parent->is_stopped = consumer_is_stopped;
      
      // Return the consumer produced
      return parent;
    }
  
  // malloc or consumer init failed
  free (this);
  
  // Indicate failure
  return NULL;
}

static int
consumer_start (mlt_consumer this)
{
  // Get the producer service
  mlt_service service = mlt_service_get_producer (mlt_consumer_service (this));
  
 	// Print out the info we care about in java properties format.
 	printf ("length = %u\n", mlt_producer_get_playtime ((mlt_producer) service));
 	printf ("fps = %g\n", mlt_producer_get_fps ((mlt_producer) service));
  
  // Stop processing.
  mlt_consumer_stop( this );
  
  return 0;
}

static int
consumer_is_stopped (mlt_consumer this)
{
  return 1;
}
