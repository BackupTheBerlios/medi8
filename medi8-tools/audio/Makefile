SUBDIRS = m8aplay m8ainfo

all:
	for dir in $(SUBDIRS); do \
	  make -C $$dir all; \
	done;

clean:
	for dir in $(SUBDIRS); do \
	  make -C $$dir clean; \
	done;
