SOURCES = m8vplay.cc log.cc
OBJECTS = $(SOURCES:.cc=.o)

SUFFIXES = .cc .o .hh

include ../../config.mk

all: m8vplay

%.o: %.cc
	$(CXX) $(CXXFLAGS) -c -o $@ $<

m8vplay: $(OBJECTS)
	g++ -o m8vplay $(OBJECTS) -lmlt++ -lmlt

clean:
	rm -f m8vplay *.o *~
