! Copyright (c) 2015 Shun Sakuraba
! 
! This software is provided 'as-is', without any express or implied
! warranty. In no event will the authors be held liable for any damages
! arising from the use of this software.
! 
! Permission is granted to anyone to use this software for any purpose,
! including commercial applications, and to alter it and redistribute it
! freely, subject to the following restrictions:
! 
! 1. The origin of this software must not be misrepresented; you must not
!    claim that you wrote the original software. If you use this software
!    in a product, an acknowledgement in the product documentation would be
!    appreciated but is not required.
! 2. Altered source versions must be plainly marked as such, and must not be
!    misrepresented as being the original software.
! 3. This notice may not be removed or altered from any source distribution.
!
! 
! Vigna's xorshift1024* pseudorandom generator.
! (Sebastiano Vigna. An experimental exploration of Marsaglia's xorshift generators, scrambled. CoRR, abs/1402.6246, 2014.)
! xorshift1024* is a pseudorandom generator with a reasonable speed and a good state space size. This is a standard choice of the generator.

module xorshift1024ast
  implicit none
  
  ! random number state
  public :: xorshift1024ast_state
  ! state initialization functions
  public :: state_init_full, state_init, state_init_self
  ! global state initialization functions
  public :: rand_init, rand_init_self

  ! Draw integer from range, or uniform random number
  public :: draw_integer, draw_uniform
  ! Draw integer from range, or uniform random number, from global rng state
  public :: rand_integer, rand_uniform

  type xorshift1024ast_state
     integer(8) :: s(0:15)
     integer :: ptr
  end type xorshift1024ast_state

  type(xorshift1024ast_state) :: global_state
  data global_state%s &
       /   451509857683038208_8, &
         -7168371501937287167_8, &
          2115663879300805632_8, &
         -5198843501874649088_8, &
          3831841687921073152_8, &
          2414704009071242240_8, &
          5592173855717519360_8, &
         -2195396877202491392_8, &
          1894995878171655168_8, &
          6100627220025457664_8, &
         -7761650469603411968_8, &
         -5013013128728581120_8, &
          6926607125233290240_8, &
          8583948487292914688_8, &
          4062296450332998656_8, &
         -5113646682930042880_8 /
  data global_state%ptr /0/
contains

  subroutine state_init_full(state, s)
    implicit none
    type(xorshift1024ast_state), intent(out) :: state
    integer(8), intent(in) :: s(16)
    logical :: have_nonzero
    integer :: i

    have_nonzero = .false.
    do i = 1, 16
       if(s(i) /= 0) then
          have_nonzero = .true.
          exit
       end if
    end do
    if(have_nonzero) then
       state%s(0:15) = s(1:16)
    else
       ! initialize with some non-zero values
       do i = 0, 15
          state%s(i) = i * i * i * i * i + i
       end do
    endif
    state%ptr = 0
  end subroutine state_init_full

  integer(8) function draw_integer8(state)
    implicit none
    type(xorshift1024ast_state), intent(inout) :: state
    
    integer(8) :: s0, s1, r
    integer(8), parameter :: spreader = 1181783497276652981_8
    
    ! get state variables
    s0 = state%s(state%ptr)
    state%ptr = iand(state%ptr + 1, 15)
    s1 = state%s(state%ptr)

    ! xorshift
    s1 = ieor(s1, ishft(s1, 31))
    s1 = ieor(s1, ishft(s1, -11))
    s0 = ieor(s0, ishft(s1, -30))

    r = ieor(s0, s1)
    state%s(state%ptr) = r

    draw_integer8 = r * spreader
  end function draw_integer8
  
  ! Following are utility functions
  
  subroutine state_init(state, seed)
    implicit none
    type(xorshift1024ast_state), intent(out) :: state
    integer, intent(in) :: seed
    integer(8) :: tmp, x0
    integer(8) :: s(16)
    integer(8), parameter :: spreader = 2685821657736338717_8
    integer :: i

    ! do something similar to xorshift64*
    tmp = int(seed, 8)
    tmp = tmp * spreader
    x0 = tmp
    do i = 1, 16
       x0 = ieor(x0, ishft(x0, -12))
       x0 = ieor(x0, ishft(x0, 25))
       x0 = ieor(x0, ishft(x0, -27))
       s(i) = x0 * spreader
    end do

    call state_init_full(state, s)
  end subroutine state_init

  subroutine rand_init(seed)
    implicit none
    integer, intent(in) :: seed
    call state_init(global_state, seed)
  end subroutine rand_init

  subroutine state_init_self(state)
    implicit none
    type(xorshift1024ast_state), intent(out) :: state
    integer :: current_time(8)
    call date_and_time(VALUES=current_time)
    if (current_time(4) == -huge(0)) then
       stop "xorshift1024ast:state_init_self: too exotic system! (UNIX time not available)"
    endif
    call state_init(state, current_time(4))
  end subroutine state_init_self

  subroutine rand_init_self()
    call state_init_self(global_state)
  end subroutine rand_init_self
  
  ! rmax must be positive
  integer function draw_integer(state, rmax)
    implicit none
    type(xorshift1024ast_state), intent(inout) :: state
    integer, intent(in) :: rmax
    integer(8) :: rmax8
    integer(8), parameter :: rmask = 9223372036854775807_8 ! (1 << 63) - 1
    integer(8) :: rnd, qmax, q

    rmax8 = int(rmax, 8)
    ! real maximum is (rmask + 1) / rmax8, but it is impossible to do in 64-bit arithmetic.
    ! Instead we evaluate conservatively. Even if the compiler uses integer(8) as a default integer, 
    ! rmax is maxed at ((1 << 63) - 1), thus qmax >= 1 is ensured.
    qmax = rmask / rmax8
    do
       ! mask to convert to positive integer
       rnd = iand(draw_integer8(state), rmask)
       ! Now both are positive, divide to check whether it is ok to go
       q = rnd / rmax8
       if(q < qmax) then
          draw_integer = int(mod(rnd, rmax8), kind=kind(rmax))
          exit
       endif
       ! otherwise repeat the last step to ensure equidistribution
    end do
  end function draw_integer

  integer(8) function rand_integer(rmax)
    implicit none
    integer, intent(in) :: rmax
    rand_integer = draw_integer(global_state, rmax)
  end function rand_integer

  real(8) function draw_uniform(state)
    implicit none
    type(xorshift1024ast_state), intent(inout) :: state
    integer(8) :: rnd 

    ! 1.0 / (1 << 53)
    real(8), parameter :: multiplier = 1.0d0 / 9007199254740992d0

    rnd = draw_integer8(state)
    
    ! 53-bit, divided by 2^53
    draw_uniform = real(ishft(rnd, -11), kind=8) * multiplier
  end function draw_uniform

  real(8) function rand_uniform()
    implicit none
    rand_uniform = draw_uniform(global_state)
  end function rand_uniform

end module xorshift1024ast


!///////////////////////////////////////////////////////////////////////////////
!
!      differential_evolution.f90
!      Created: 29 April 2016 at 16:53
!
!///////////////////////////////////////////////////////////////////////////////
!TODO: deallocate everything at the end
module differential_evolution
!$ use omp_lib
use xorshift1024ast
implicit none
private
public :: DE_init, DE_exit, DE_optimize, DE_simplex, rp, DErand1, DErand2, DEbest1, DEbest2, DEtargettobest1, DErand2dir

! real precision
integer, parameter :: rp = kind(0d0) 

enum, bind(c) ! Mutation strategy
    enumerator :: DErand1, DErand2, DEbest1, DEbest2, DEtargettobest1, DErand2dir
end enum

! algorithm parameters
integer,  save :: mutationStrategy = DErand1 ! mutation strategy
integer,  save :: maxGens          = 1000      ! maximum generations
integer,  save :: maxChilds        = 5         ! maximum children
integer,  save :: dim              = 0         ! dimensionality
integer,  save :: popSize          = 0         ! population size        >=4,  at least [2*dim,3*dim]
real(rp), save :: diffWeight       = 0.99_rp   ! standard differential weight [0,2], usually 1 
real(rp), save :: crossProb        = 0.50_rp   ! crossover probability [0,1], usually  [0.25,0.75]
real(rp), save :: diversity        = 0.55_rp   ! diversity parameter, starts at 0.55 
logical,  save :: forceRange       = .false.   ! forces solutions to stay inside search range
logical,  save :: verbose          = .false.   ! print? yes/no
integer,  save :: Nprint           = 1         ! print every Nprint steps (only if verbose)

! variables needed
real(rp),    dimension(:,:), allocatable, save :: pop, popnew             ! population (dim,popSize)
real(rp),    dimension(:),   allocatable, save :: fpop, fpopnew           ! stores function value (popSize)
real(rp),    dimension(:),   allocatable, save :: constrpop, constrpopnew  ! stores sum of constraint violations (popSize)
logical,     dimension(:),   allocatable, save :: fsblepop, fsblepopnew   ! stores feasibility (popSize)
real(rp),    dimension(:,:), allocatable, save :: range                   ! defines search space (2,dim)

! random number generators
type(xorshift1024ast_state), dimension(:), allocatable, save :: rng   

!OpenMP stuff
integer, save :: numThreads = 1, myID = 0
!$omp threadprivate(myID)

contains
subroutine DE_init(set_range, set_popSize, set_crossProb, set_diffWeight, set_maxGens, &
                   set_mutationStrategy, set_maxChilds, set_forceRange, set_verbose, set_Nprint)
    implicit none
    real(rp), dimension(:,:), intent(in) :: set_range
    integer,  intent(in), optional       :: set_popSize, set_maxGens, set_maxChilds, set_mutationStrategy
    real(rp), intent(in), optional       :: set_crossProb, set_diffWeight
    logical,  intent(in), optional       :: set_forceRange
    logical,  intent(in), optional       :: set_verbose
    integer,  intent(in), optional       :: set_Nprint
    integer :: i
    
    if(size(set_range, dim=1) /= 2) &
        call DE_error("DE_init",&
             "set_range must be of dimensions (2,dim)!")
             
    !read dimensionality
    dim = size(set_range, dim=2)
        
    !read optional arguments
    if(present(set_mutationStrategy)) then
        if(set_mutationStrategy == DErand1         .or. &
           set_mutationStrategy == DErand2         .or. &
           set_mutationStrategy == DEbest1         .or. &
           set_mutationStrategy == DEbest2         .or. &
           set_mutationStrategy == DEtargettobest1 .or. &
           set_mutationStrategy == DErand2dir      ) then
            mutationStrategy = set_mutationStrategy
        else
            call DE_warning("DE_init",&
                 "set_mutationStrategy must be one of these: DErand1, DErand2, DEbest1, DEbest2, DEtargettobest1, DErand2dir."//&
                 "Setting to default (DErand1)")
        end if 
    end if
    
    if(present(set_popSize)) then
        if(set_popSize >= 4) then 
            popSize = set_popSize
        else
            call DE_warning("DE_init",&
                 "set_popSize < 4, setting popSize to 4")
            popSize = 4
        end if
    else
        popSize = 5*dim
    end if
    
    if(present(set_crossProb)) then
        if(.not.(set_crossProb < 0._rp .or. set_crossProb > 1._rp)) then
            crossProb = set_crossProb
        else
            call DE_warning("DE_init",&
                 "set_crossProb is not in [0,1], setting crossProb  to default (0.5)")
        end if 
    end if
    
    if(present(set_diffWeight)) then
        if(.not.(set_diffWeight < 0._rp .or. set_diffWeight > 2._rp)) then
            diffWeight = set_diffWeight
        else
            call DE_warning("DE_init",&
                 "set_diffWeight is not in [0,2], setting diffWeight  to default (1.0)")
        end if 
    end if
    
    if(present(set_maxGens)) then
        if(set_maxGens > 0) then
            maxGens = set_maxGens
        else
            maxGens = 1
            call DE_warning("DE_init",&
                 "set_maxGens must be larger than 0!")
        end if 
    end if
    
    if(present(set_maxChilds)) then
        if(set_maxChilds > 0) then
            maxChilds = set_maxChilds
        else
            maxChilds = 1
            call DE_warning("DE_init",&
                 "set_maxChilds must be larger than 0!")
        end if 
    end if
        
    if(present(set_forceRange)) then
        forceRange = set_forceRange
    end if

    if(present(set_Nprint)) then
        if(set_Nprint > 0) then
            Nprint = set_Nprint
        else
            call DE_warning("DE_init",&
                 "set_Nprint must be larger than 0, using default Nprint = 1")
        end if
        verbose = .true.
    end if
    
    if(present(set_verbose)) then
        verbose = set_verbose
    end if
    
  ! initialize OpenMP
    !$ numThreads = omp_get_max_threads() 
    !$ if(verbose) write(*,'(A,I0,A)') "Running DE in parallel with ",numThreads," thread(s)!"
    !$ if(verbose) write(*,*)
    
    
  ! allocate memory for variables
    if(.not.allocated(pop))          allocate(pop(dim,popSize))
    if(.not.allocated(popnew))       allocate(popnew(dim,popSize))

    if(.not.allocated(fpop))         allocate(fpop(popSize))
    if(.not.allocated(fpopnew))      allocate(fpopnew(popSize))

    if(.not.allocated(fsblepop))     allocate(fsblepop(popSize))
    if(.not.allocated(fsblepopnew))  allocate(fsblepopnew(popSize))

    if(.not.allocated(constrpop))    allocate(constrpop(popSize))
    if(.not.allocated(constrpopnew)) allocate(constrpopnew(popSize))

    
    if(.not.allocated(range))     allocate(range(2,dim))
    if(.not.allocated(rng))       allocate(rng(0:numThreads-1))
    
    range = set_range
end subroutine DE_init

subroutine DE_exit()
    if(allocated(pop))          deallocate(pop)
    if(allocated(popnew))       deallocate(popnew)
    if(allocated(fpop))         deallocate(fpop)
    if(allocated(fpopnew))      deallocate(fpopnew)
    if(allocated(fsblepop))     deallocate(fsblepop)
    if(allocated(fsblepopnew))  deallocate(fsblepopnew)
    if(allocated(constrpop))    deallocate(constrpop)
    if(allocated(constrpopnew)) deallocate(constrpopnew)
    if(allocated(range))        deallocate(range)
    if(allocated(rng))          deallocate(rng)
end subroutine DE_exit

subroutine DE_optimize(func,feasible,sumconstr,x,guess,init_pop)
    implicit none
    real(rp), dimension(:), intent(out) :: x
    real(rp), dimension(size(x, dim=1)), intent(in), optional :: guess
    interface
        real*8 function func(y)
            !real*8, dimension(:), intent(in) :: y
            real*8, dimension(:) :: y
        end function func
    end interface 
    interface
        logical function feasible(y)
            real*8, dimension(:) :: y
        end function feasible
    end interface
    interface
        real*8 function sumconstr(y)
            real*8, dimension(:) :: y
        end function sumconstr
    end interface  
    interface
        subroutine init_pop(pop)
            real*8, dimension(:,:), intent(out) :: pop 
        end subroutine init_pop
    end interface 
    optional :: init_pop
    
    real(rp), dimension(dim,4) :: m  !randomly selected individuals
    real(rp), dimension(4)     :: fm !and their function values
    real(rp), dimension(size(x,dim=1)) :: child, mutant, v
    real(rp), parameter :: eps = 1.e-12_rp
    real(rp) :: ranr, f, constr, popSizer, fbestlast
    integer :: j, gen, p, d, c, rani, bestindx, dum, mutVar, bestlast

    logical :: converged, fsble, booldummy
    
    converged = .false.
    popSizer = real(popSize,rp) ! popSize as real
    constrpop = 0._rp
    gen = 0    
    
    !$omp parallel num_threads(numThreads)    
    !$myID = omp_get_thread_num()
    !$omp end parallel
    
    ! initialize random number generator
    call system_clock(count=dum)
    do p = 0,numThreads-1
        call state_init(rng(p), dum +p)
    end do
    
    ! initialize population
    if(present(init_pop)) then
        call init_pop(pop)
        if(present(guess)) then
            pop(:,1) = guess
        end if
        !$omp parallel do default(shared) private(p) num_threads(numThreads)
        do p = 1,popSize
            fpop(p) = func(pop(:,p))
            fsblepop(p) = feasible(pop(:,p))
            if(.not.fsblepop(p)) constrpop(p) = sumconstr(pop(:,p))
        end do
        !$omp end parallel do
    else if(present(guess)) then !if a guess is used, the population is initialized around it
        pop(:,1) = guess
        fpop(1)  = func(pop(:,1))
        fsblepop(1) = feasible(pop(:,1))
        if(.not.fsblepop(1)) constrpop(1) = sumconstr(pop(:,1))
        !$omp parallel do default(shared) private(p,d) num_threads(numThreads) 
        do p = 2,popSize
            do d = 1,dim
                pop(d,p) = 1._rp + (2._rp*draw_uniform(rng(myID))-1._rp) * real(p,rp)/real(popSize,rp)
                if(abs(pop(d,1)) > epsilon(0._rp)) then
                    pop(d,p) = pop(d,1)*pop(d,p)
                else
                    pop(d,p) = range(1,d) + pop(d,p)*(range(2,d)-range(1,d))
                end if
            end do
            fpop(p) = func(pop(:,p))
            fsblepop(p) = feasible(pop(:,p))
            if(.not.fsblepop(p)) constrpop(p) = sumconstr(pop(:,p))
        end do  
        !$omp end parallel do 
    else
        !$omp parallel do default(shared) private(p,d) num_threads(numThreads)   
        do p = 1,popSize
            !print*, "Iteration", i, " is executed by", myID
            do d = 1,dim
                pop(d,p) = range(1,d) + draw_uniform(rng(myID))*(range(2,d)-range(1,d))
            end do
            fpop(p) = func(pop(:,p))
            fsblepop(p) = feasible(pop(:,p))
            if(.not.fsblepop(p)) constrpop(p) = sumconstr(pop(:,p))
        end do
        !$omp end parallel do 
    end if
    
    
    ! initialize the best solution
    bestindx = find_best()
    bestlast = bestindx
    fbestlast = fpop(bestindx)
    
    !initialize the "new population"
    popnew       = pop
    fpopnew      = fpop
    fsblepopnew  = fsblepop
    constrpopnew = constrpop
    
    !here it is decided how many individuals need to be drawn in reservoir sampling, based on selected strategy 
    if      (mutationStrategy == DEtargettobest1 .or. mutationStrategy == DEbest1) then
        mutVar = 2
    else if (mutationStrategy == DErand1 .or. mutationStrategy == DErand2dir) then
        mutVar = 3 
    else
        mutVar = 4
    end if
    
    
    do while(gen <= maxGens .and. .not.converged)
        
        ! print best solution
        if(verbose.and.modulo(gen,Nprint) == 0) then
            write(*,'(A4,I6,A1,I0,5X,A9,ES14.7,A,L)') "gen ", gen, "/", maxGens,&
                     " elitist ", fpop(bestindx), " feasible? ", fsblepop(bestindx)
        end if
        
        ! decrease diversity parameter => focus search more on feasible region
        if(gen < maxGens/3) then
            diversity = diversity - 3*(0.55_rp-0.025_rp)/real(maxGens,rp)
        else
            diversity = 0.025_rp ! minimum diversity
        end if
  
        ! loop over the generation                     
        !$omp parallel do default(shared) private(mutant,child,booldummy,dum,m,p,d,c,rani,ranr,f,fsble,constr), &
        !$omp& num_threads(numThreads) 
        do p = 1,popSize
            do c = 1,maxChilds 
                ! draw dum random individuals from the population using reservoir sampling
                m(:,1:mutVar) = pop(:,1:mutVar)
                fm(1:mutVar)  = fpop(1:mutVar)
                ! ensures that the 4 random individuals are really different from p by replacing them if necessary
                if(p >= 1 .and. p <= mutVar) then
                    m(:,p) = pop(:,mutVar+1) 
                    fm(p)  = fpop(mutVar+1)
                end if 
                do d = mutVar+1,popSize
                    if(d == p) cycle !skip (the random individual must not be the candidate itself)
                    rani = ceiling(draw_uniform(rng(myID))*d)
                    if(rani <= mutVar) then
                        m(:,rani) = pop(:,d)
                        fm(rani)  = fpop(d)
                    end if
                end do
                                         
                !do crossover (not used, its always better to mutate everything for charges)
                rani = ceiling(draw_uniform(rng(myID))*dim) !randomly chosen integer guaranteed to mutate
                do d = 1,dim
                    ranr = draw_uniform(rng(myID)) !draw random number
                    if((ranr < crossProb .or. d == rani)) then
                        ! DE/rand/1
                        if(mutationStrategy == DErand1) then
                            child(:) = m(:,1) + diffWeight*(m(:,2)-m(:,3))  
                        ! DE/rand/2 
                        else if(mutationStrategy == DErand2) then       
                            child(:) = m(:,1) + diffWeight*(m(:,1)-m(:,2) + m(:,3)-m(:,4))   
                        ! DE/best/1 
                        else if(mutationStrategy == DEbest1) then      
                            child(:) = pop(:,bestindx) + diffWeight*(m(:,1)-m(:,2)) 
                        !DE/best/2
                        else if(mutationStrategy == DEbest2) then   
                            child(:) = pop(:,bestindx) + diffWeight*(m(:,1) + m(:,2) - m(:,3) - m(:,4))
                        !DE/target-to-best/1 
                        else if(mutationStrategy == DEtargettobest1) then   
                            child(:) = pop(:,p) + diffWeight*(pop(:,bestindx) - pop(:,p)) + diffWeight*(m(:,1) - m(:,2)) 
                        ! DE/rand/2/dir 
                        else if(mutationStrategy == DErand2dir) then    
                            !put the solution with the lowest function value in m1
                            if      (fm(2) < fm(1) .and. fm(2) <= fm(3)) then
                                m(:,4) = m(:,1)
                                m(:,1) = m(:,2)
                                m(:,2) = m(:,4)
                            else if (fm(3) < fm(1) .and. fm(3) <= fm(2)) then
                                m(:,4) = m(:,1)
                                m(:,1) = m(:,3)
                                m(:,3) = m(:,4)
                            end if 
                            child(:) = m(:,1) + diffWeight/2._rp * (m(:,1) - m(:,2) - m(:,3))  
                        else
                            call DE_error("DE_optimize","Invalid mutationStrategy!")
                        end if
                    else
                        child(d) = pop(d,p) ! use old genes
                    end if
                    
                    !force values to stay in bounds
                    if(forceRange) then
                        if( child(d) < range(1,d) .or.  child(d) > range(2,d) ) & !is outside range?
                            child(d) = range(1,d) + draw_uniform(rng(myID))*(range(2,d)-range(1,d))
                    end if
                end do   
                
                ! store child if better than current mutant
                if(c > 1) then
                    call compare_solutions(func,feasible,sumconstr,mutant,f,fsble,constr,child)     
                else
                    mutant      = child
                    f           = func(mutant)
                    fsble       = feasible(mutant)
                    constr      = sumconstr(mutant)
                end if            
            end do  
            
            !decide whether to accept or reject mutant based on diversity switch
            if(draw_uniform(rng(myID)) <= diversity) then
                ! accept mutant if it is a better solution (no matter if feasible or not)
                if((p /= bestindx).and.(f < fpop(p))) then                    
                    popnew(:,p)     = mutant
                    fpopnew(p)      = f
                    fsblepopnew(p)  = fsble
                    constrpopnew(p) = constr
                end if
            else
                ! accept mutant only if it is better based on selection criteria
                call compare_solutions(func,feasible,sumconstr,popnew(:,p),fpopnew(p),fsblepopnew(p),constrpopnew(p),mutant,f)     
            end if     
        end do
        !$omp end parallel do  
        
        !load new solutions
        pop       = popnew
        fpop      = fpopnew
        fsblepop  = fsblepopnew
        constrpop = constrpopnew
        
        ! increment generation count
        gen = gen+1 
        
        !find current best solution
        bestindx = find_best()
            
        ! check for convergence via cost function diversity check
        if(sum(sqrt((fpop-fpop(bestindx))**2))/popSizer < eps) then
            converged = .true.
            if(verbose) write(*,'(A,I0,A)') "population converged towards a single solution after ", gen," generations."
        end if
    end do
    ! store the best solution as return value
    bestindx = find_best()
    x = pop(:,bestindx)
    if(verbose.and..not.feasible(x)) write(*, '(A)') "WARNING: No feasible solution was found!"
    write(*, '(A)') "Starting local simplex optimization:"
    write(*,'(A28,ES21.10)') "Solution before refinement: ", fpop(bestindx)
    if(feasible(x)) call DE_simplex(func,feasible,x)
    write(*,'(A28,ES21.10)') "Solution after  refinement: ", func(x)
    return
end subroutine DE_optimize

! finds the better of two solutions x1 and x2, puts better in x1
subroutine compare_solutions(func,feasible,sumconstr,x1,f1,fsble1,constr1,x2,func_x2,feasible_x2,sumconstr_x2)
    implicit none
    interface
        real*8 function func(y)
            real*8, dimension(:) :: y
        end function func
    end interface 
    interface
        logical function feasible(y)
            real*8, dimension(:) :: y
        end function feasible
    end interface 
    interface
        real*8 function sumconstr(y)
            real*8, dimension(:) :: y
        end function sumconstr
    end interface 
    real(rp), dimension(:), intent(out) :: x1 !x1 is the "old" solution
    real(rp), dimension(:), intent(in)  :: x2 !x2 is the new solution
    real(rp), intent(in), optional :: func_x2, sumconstr_x2 !can save function evaluation
    logical, intent(in), optional :: feasible_x2
    real(rp), intent(out) :: constr1, f1
    real(rp) :: constr2, f2
    logical, intent(out) :: fsble1
    logical :: fsble2
    
    if(present(feasible_x2)) then
        fsble2 = feasible_x2
    else
        fsble2 = feasible(x2)
    end if
    
    if(fsble1.and.fsble2) then !both are feasible
        if(present(func_x2)) then
            f2 = func_x2
        else
            f2 = func(x2)
        end if
        
        if(f1 < f2) then
            return ! x1 is the better solution already, we can return
        else
            !print*, "x2 wins"
            !swap x2 into x1
            x1     = x2
            f1     = f2
            fsble1 = fsble2
            return
        end if
    else if (.not.fsble1 .and. .not.fsble2) then !both are infeasible
        if(present(sumconstr_x2)) then
            constr2 = sumconstr_x2
        else
            constr2 = sumconstr(x2)
        end if
        if(constr1 <= constr2) then
            return ! x1 is the better solution already
        else
            !swap x2 into x1
            if(present(func_x2)) then
                f1 = func_x2
            else
                f1 = func(x2)
            end if
            x1      = x2
            fsble1  = fsble2
            constr1 = constr2 
            return
        end if
    else !one is feasible, one is not
        if(fsble1) then
            return ! x1 is the feasible solution already
        else
            x1      = x2
            if(present(func_x2)) then
                f1 = func_x2
            else
                f1 = func(x1)
            end if
            fsble1  = fsble2
            constr1 = 0._rp ! feasible, so this must be 0
            return
        end if
    end if
end subroutine compare_solutions

! finds the best solution from the population
integer function find_best()
    implicit none
    integer :: i
    integer :: best
    best = 1 !initialize to 1
    do i = 2,popSize
        if(fsblepop(best).and.fsblepop(i)) then !both are feasible
            if(fpop(i) < fpop(best)) best = i
        else if ((.not.fsblepop(best)).and.(.not.fsblepop(i))) then !both are infeasible
            if(constrpop(i) < constrpop(best)) best = i
        else ! one is feasible, one is not
            if(fsblepop(i)) best = i
        end if 
    end do
    find_best = best  
end function find_best



subroutine DE_simplex(func,feasible,sol) !local optimization to refine solution
    implicit none
    real(rp), dimension(:), intent(inout) :: sol !solution
    interface
        real*8 function func(y)
            real*8, dimension(:) :: y
        end function func
    end interface 
    interface
        logical function feasible(y)
            real*8, dimension(:) :: y
        end function feasible
    end interface 
    real(rp), dimension(size(sol,dim=1),size(sol,dim=1)+1) :: x ! simplex vertices
    real(rp), dimension(size(sol,dim=1)+1) :: f ! simplex function values
    real(rp), dimension(size(sol,dim=1)) :: x0, xr, xe, xc ! centroid, reflection, expanded, contracted
    real(rp) :: fr, fe, fc, conv

    logical,  dimension(size(sol,dim=1)+1) :: mask ! for finding second worst solution
    real(rp), parameter :: eps = 1d-12 ! convergence criterion
    real(rp), parameter :: unfeasible_penalty = 1e2_rp ! penalty for unfeasible solutions
    integer :: i, Ndim
    real(rp) :: Ndimr
    integer :: step, worst, secondworst, best
    integer, parameter :: maxsteps = 10000 !to prevent infinite loop 
    
    Ndim = size(sol,dim=1)
    Ndimr = real(Ndim,rp)
    
    !generate initial simplex
    x(:,1) = sol(:)
    do i = 2,Ndim+1
        !if(abs(sol(i)) < eps) call random_number(sol(i))
        call random_number(x(:,i))
        x(:,i) = 1._rp + 0.1_rp*(2._rp*x(:,i)-1._rp)
        x(:,i) = sol(:)*x(:,i)
        !x(i-1,i) = 1.5_rp*x(i-1,i)
    end do
    
    !initialize function values
    do i = 1,Ndim+1
        f(i) = func(x(:,i))
        if(.not.feasible(x(:,i))) f(i) = f(i) + unfeasible_penalty
    end do
    
    !optimization loop
    do step = 1,maxsteps
            
        !0) order -> i dont do this, i just three indices, that's easier
        worst = maxloc(f,dim=1)
        best  = minloc(f,dim=1)
        mask = .true.
        mask(worst) = .false.
        secondworst = maxloc(f(:), mask=mask(:), dim=1)
        
        !1) check for convergence
        !function value criterion
        if(abs(f(best)-f(worst)) < eps) then 
            exit
        end if
        !simplex volume criterion
        conv = 0._rp
        do i = 1,Ndim+1
            conv = conv + sum(abs(x(:,best) - x(:,i)))/Ndimr
        end do
        if(conv/Ndimr < eps) exit
                
        !2) calculate the centroid of all but the worst solution
        x0 = 0._rp
        do i = 1,Ndim+1
            if(i == worst) cycle
            x0 = x0 + x(:,i)
        end do
        x0 = x0/Ndimr
        
        !3) reflection
        xr = x0 + (x0 - x(:,worst))
        fr = func(xr); if(.not.feasible(xr)) fr = fr + unfeasible_penalty;
        if((fr < f(secondworst)).and.(.not.(fr < f(best)))) then
            x(:,worst) = xr
            f(worst)   = fr
            cycle
        end if
        
        !4) expansion
        if(fr < f(best)) then
            xe = x0 + 2*(xr-x0)
            fe = func(xe); if(.not.feasible(xe)) fe = fe  + unfeasible_penalty;
            if(fe < fr) then
                x(:,worst) = xe
                f(worst)   = fe  
            else
                x(:,worst) = xr
                f(worst)   = fr                
            end if
            cycle
        end if
        
        !5) contraction
        xc = x0 + 0.5_rp*(x(:,worst) - x0)
        fc = func(xc); if(.not.feasible(xc)) fc = fc + unfeasible_penalty;
        if(fc < f(worst)) then
            x(:,worst) = xc
            f(worst)   = fc
            cycle               
        end if
        
        !6) shrink
        do i = 1,Ndim+1
            if(i == best) cycle
            x(:,i) = x(:,best) + 0.5_rp*(x(:,i)-x(:,best))
            f(i) = func(x(:,i)); if(.not.feasible(x(:,i))) f(i) = f(i) + unfeasible_penalty;
        end do   
    end do
    
    if(step == maxsteps .and. verbose) write(*,'(A)') "Simplex exceeded maximum number of steps!"    
    best  = minloc(f,dim=1)
    sol = x(:,best)
    
    return
end subroutine DE_simplex


subroutine DE_error(routine,message)
    implicit none
    character(len=*), intent(in) :: routine
    character(len=*), intent(in) :: message
    write(0,*) routine,": ERROR: ",message
    stop 
end subroutine DE_error

subroutine DE_warning(routine,message)
    implicit none
    character(len=*), intent(in) :: routine
    character(len=*), intent(in) :: message
    write(0,*) routine,": WARNING: ",message 
end subroutine DE_warning

end module differential_evolution
