SOURCES = m8vplay.cc
OBJECTS = $(SOURCES:.cc=.o)

SUFFIXES = .cc .o .hh

include ../../config.mk

all: m8vplay

%.o: %.cc
	$(CXX) $(CXXFLAGS) -c -I../../util -o $@ $<

m8vplay: $(OBJECTS)
	g++ -o m8vplay $(OBJECTS) ../../util/libutil.a -lmlt++ -lmlt -lSDL

clean:
	rm -f m8vplay *.o *~
