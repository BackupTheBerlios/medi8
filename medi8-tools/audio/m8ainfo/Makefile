SOURCES = m8ainfo.cc
OBJECTS = $(SOURCES:.cc=.o)

SUFFIXES = .cc .o .hh

include ../../config.mk

all: m8ainfo

%.o: %.cc
	$(CXX) $(CXXFLAGS) -I../../util -c -o $@ $<

m8ainfo: $(OBJECTS)
	g++ -o m8ainfo $(OBJECTS) ../../util/libutil.a -ljack

clean:
	rm -f m8ainfo *.o *~
