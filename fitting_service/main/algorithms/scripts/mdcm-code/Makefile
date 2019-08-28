F90 = gfortran
#F90 = /usr/local/bin/mpif90

FFLAGS_PAR = -fopenmp -O3
LFLAGS_PAR = -fopenmp 
FFLAGS     = -O3
LFLAGS     = 

##########################
# Object Files for build #
##########################

OBJS = \
differential_evolution.o \
main.o \

OBJS_PAR = \
pdifferential_evolution.o \
main.o \

all : cubefit.x pcubefit.x clean

serial : cubefit.x clean
parallel : pcubefit.x clean

cubefit.x : $(OBJS)
	 ${F90}  -o $@ $(LFLAGS) $(OBJS)
	 
pcubefit.x : $(OBJS_PAR)
	 ${F90}  -o $@ $(LFLAGS_PAR) $(OBJS_PAR)

#######################################
# Object dependencies and compilation #
#######################################
differential_evolution.o : src/differential_evolution.f90
	$(F90) -c $(FFLAGS) $(INCLUDES) -o $@ src/differential_evolution.f90
	
pdifferential_evolution.o : src/differential_evolution.f90
	$(F90) -c $(FFLAGS_PAR) $(INCLUDES) -o $@ src/differential_evolution.f90

main.o : src/main.f90 \
differential_evolution.o
	$(F90) -c $(FFLAGS) $(INCLUDES) -o $@ src/main.f90


.PHONY: clean veryclean
clean:
	rm *.o *.mod

veryclean:
	rm *.x *.o *.mod



