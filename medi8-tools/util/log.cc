// Log file support.

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
#include <assert.h>
#include <stdio.h>
#include <stdarg.h>

log_level_t log_level = MEDI8_LOG_NONE;

static FILE *log_file = 0;

void 
init_logger (log_level_t level, const char *filename)
{
#ifndef NDEBUG
  log_level = level;
  log_file = fopen (filename, "w");
#endif
}

static const char *level_names[] = { "DEBUG  : ",
																		 "WARNING: ",
																		 "ERROR  : " };

int
log (log_level_t level, const char *fmt, ...)
{
  assert (level < MEDI8_LOG_NONE);
  va_list ap;
  va_start (ap, fmt);
  fputs (level_names[level], log_file);
  int ret = vfprintf (log_file, fmt, ap);
  fputs ("\n", log_file);
  fflush (log_file);
  va_end(ap);
  return ret;
}
