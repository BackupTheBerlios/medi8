// Thumbnail generator factory.
 
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

#include "consumer_thumb.h"

void *
mlt_create_producer (char *id, void *arg)
{
	return NULL;
}

void *
mlt_create_filter (char *id, void *arg)
{
	return NULL;
}

void *
mlt_create_transition (char *id, void *arg)
{
	return NULL;
}

void *
mlt_create_consumer (char *id, void *arg)
{
	if (!strcmp (id, "thumb"))
		return consumer_thumb_init (arg);
	return NULL;
}
