all: .repository-stamp
	make -C util all
	make -C audio all
	make -C video all

clean:
	@rm -rf modules
	@rm -f .repository-stamp *~
	make -C util clean
	make -C audio clean
	make -C video clean

# MLT doesn't support multiple repositories currently.
# This project simply copies the contents of the installed repository
# to our local workspace.
# We will install our own modules into this workspace.
# All MLT users should depend on this project and set the MLT_REPOSITORY 
# environement variable to point to this modules repository.

.repository-stamp:
	@if test -d /usr/share/mlt/modules; then \
	  cp -r /usr/share/mlt/modules . ; \
	  touch .repository-stamp ; \
	else \
	  echo "You do not have MLT installed on your machine." ; \
	fi

