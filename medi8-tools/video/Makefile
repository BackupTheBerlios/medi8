SUBDIRS = consumer_thumb consumer_info consumer_monitor m8vplay 

all:
	for dir in $(SUBDIRS); do \
	  make -C $$dir all; \
	done;

clean:
	for dir in $(SUBDIRS); do \
	  make -C $$dir clean; \
	done;
