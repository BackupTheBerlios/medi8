MODULE = ../../modules/libmltthumb.so

SOURCES = consumer_thumb.c factory.c
OBJECTS = $(SOURCES:.c=.o)

include ../../config.mk

all: $(MODULE)

$(MODULE): $(OBJECTS)
	$(CC) $(CFLAGS) -o $(MODULE) -shared $(OBJECTS) -lmlt
	grep -v libmltthumb.so ../../modules/consumers.dat > /tmp/consumers.dat
	cp /tmp/consumers.dat ../../modules/consumers.dat
	echo "thumb		libmltthumb.so" >> ../../modules/consumers.dat
	rm /tmp/consumers.dat

clean:
	rm -f $(OBJECTS) *~ $(MODULE)


