SOURCES = m8aplay.cc ffmpeg_file_reader.cc bus.cc playlist.cc
OBJECTS = $(SOURCES:.cc=.o)

SUFFIXES = .cc .o .hh

include ../../config.mk

all: m8aplay

%.o: %.cc
	$(CXX) $(CXXFLAGS) -I../../util -c -o $@ $<

m8aplay: $(OBJECTS)
	g++ -o m8aplay $(OBJECTS) ../../util/libutil.a -ljack -llo -lavcodec -lavformat

clean:
	rm -f m8aplay *.o *~
