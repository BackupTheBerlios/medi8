SOURCES = log.cc
OBJECTS = $(SOURCES:.cc=.o)

SUFFIXES = .cc .o .hh

include ../config.mk

all: libutil.a

%.o: %.cc
	$(CXX) $(CXXFLAGS) -c -o $@ $<

libutil.a: $(OBJECTS)
	ar cru libutil.a $(OBJECTS)

clean:
	rm -f *.a *.o *~
