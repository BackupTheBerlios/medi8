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

#ifndef LOG_HH
#define LOG_HH

#ifdef NDEBUG
#define log_debug(...)
#else

typedef enum {
  MEDI8_LOG_DEBUG = 0,
  MEDI8_LOG_ERROR = 1,
  MEDI8_LOG_NONE = 2
} log_level_t;

extern log_level_t log_level;

#define log_debug(...) \
  ((log_level <= LOG_DEBUG) ? \
   log(MEDI8_LOG_DEBUG, ## __VA_ARGS__) : 0)

#define log_error(...) \
  log(MEDI8_LOG_ERROR, ## __VA_ARGS__)

int
log (log_level_t level, const char *fmt, ...)
  __attribute__((format (printf, 2, 3)));

#endif

void init_logger (log_level_t, const char *);

#endif
