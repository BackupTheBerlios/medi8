all: .repository-stamp
	(cd video/m8vplay && make all)

clean:
	@rm -rf modules
	@rm -f .repository-stamp
	@find ./ -name \*~ | xargs rm -f
	(cd video/m8vplay && make clean)

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

