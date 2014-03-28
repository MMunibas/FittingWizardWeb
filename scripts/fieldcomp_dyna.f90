!
! Copyright 2013 Tristan Bereau and Christian Kramer
!
! Licensed under the Apache License, Version 2.0 (the "License");
! you may not use this file except in compliance with the License.
! You may obtain a copy of the License at
!
!     http://www.apache.org/licenses/LICENSE-2.0
!
! Unless required by applicable law or agreed to in writing, software
! distributed under the License is distributed on an "AS IS" BASIS,
! WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
! See the License for the specific language governing permissions and
!     limitations under the License.
!
!--------------------------------------------------------------
!
! Fieldcomp compares the MEPs calculated by Gaussian
! with the field calculated by a Multipole approximation from GDMA.
!
! Units: All units are read in in atomic units (especially bohr). The only
!        Exception are the coordinates read in from the .pun file, which 
!        are in Angstrom. The Multipoles from the .pun again however are in
!        atomic units. There should be no unit conversion error made here.
!        (Has been checked extensively)
!
! 
! Improve readability of the code

!collection of variables in modules

!character strings
module characters

  implicit none
  
  character(len=250) :: cubefile, vdwfile, punfile, basename
  character(len=78) :: line1, line2
  character(len=20) :: wrd, rnk

end module characters

!allocatables
module allocatables

  implicit none
  
  character(len=250),allocatable,dimension(:) :: Arg

  integer,allocatable,dimension(:) :: ele_type  
  integer,allocatable,dimension(:) :: irank, jrank

  real,allocatable, dimension(:,:,:) :: en, totener, diff
  
  real*8,allocatable,dimension(:) :: xr, yr, zr
  real*8,allocatable,dimension(:) :: xs, ys, zs
  real*8,allocatable,dimension(:) :: x1, y1, z1 
  real*8,allocatable,dimension(:) :: qu, qu1z, qu1y, qu1x
  real*8,allocatable,dimension(:) :: qu20, qu21c, qu21s, qu22c, qu22s
  real*8,allocatable,dimension(:) :: qu30, qu31c, qu31s, qu32c, qu32s
  real*8,allocatable,dimension(:) :: qu33c, qu33s, vdw
  
  logical, allocatable, dimension(:,:,:) :: excl, sigma_range, near_vdw


end module allocatables

!integers
module integers

  implicit none

  integer :: nArgs, Error, io_error
  integer :: n0, n1, n2, n3, pts(3)
  integer :: diffcnt, i, j, k, natoms
  integer :: diffcnt_sigma, diffcnt_nvdw, diffcnt_farout

end module integers

!reals
module reals

  implicit none
  
  real :: xstart, ystart, zstart, step_x, step_y, step_z, o, p, q, shell_i, shell_o 
  real :: diffsum_sigma, diffsum_nvdw, diffsum_farout, diffperc_sigma, diffperc_nvdw, diffperc_farout, diffsum_sigma_sq

end module reals

!real*8s
module real8s

  implicit none
  
  real*8 :: xc, yc, zc, x, y, z, r, a2b, b2a, chrg
  real*8 :: trax, tray, traz
  real*8 :: que, qu1ze, qu1ye, qu1xe, qu20e, qu21ce, qu21se, qu22ce, qu22se
  real*8 :: qu30e, qu31ce, qu31se, qu32ce, qu32se, qu33ce, qu33se
  real*8 :: diffsum
  real*8 :: diffperc

end module real8s

!logicals
module logicals

  implicit none

  logical :: no_pics, sigma_only, cubeout


end module logicals

program fieldcomp
  
use characters
use allocatables
use integers
use reals
use real8s
use logicals

implicit none

integer::iargc

!Conversion parameters form Angstrom to Bohr and vice versa

a2b = 1.889726d0
b2a = 0.52917720859d0

! Factors defining the shells for anaylsis of MEP deviation

shell_i = 1.66
shell_o = 2.2

! Read input from commandline

no_pics = .true.
sigma_only = .false.
cubeout = .false.

nArgs = iargc()
if (nArgs == 0) stop 'Usage: ./fieldcomp -cube [file] -vdw [file] -pun [file]&
& [-pics] [-sigma_only] [-si] [-so] [-cubeout] [-h]'
allocate (Arg(nArgs),stat = Error)
if(Error > 0) stop 'Error preparing input-reading'
do i=1,nArgs
  call getarg(i,Arg(i))
enddo
do i=1,nArgs
  if(Arg(i) .eq. '-h') then
    stop 'Usage: ./fieldcomp -cube [file] -vdw [file] -pun [file] [-pics] [-sigma_only] [-si] [-so] [-cubeout] [-h]'
  elseif(Arg(i) .eq. '-cube') then
    cubefile = Arg(i+1)
  elseif(Arg(i) .eq. '-vdw') then
    vdwfile = Arg(i+1)
  elseif(Arg(i) .eq. '-pun') then
    punfile = Arg(i+1)
  elseif(Arg(i) .eq. '-pics') then
    no_pics = .false.
  elseif(Arg(i) .eq. '-sigma_only') then
    sigma_only = .true.
  elseif(Arg(i) .eq. '-cubeout') then
    cubeout = .true.
  elseif(Arg(i) .eq. '-si') then
    wrd = Arg(i+1)
    read(wrd,*) shell_i
  elseif(Arg(i) .eq. '-so') then
    wrd = Arg(i+1)
    read(wrd,*) shell_o
  endif
enddo  

!define basename using cubfile

basename = cubefile(1:index(cubefile,'.')-1)//'_'

!check inner and outer shell

if (shell_i >= shell_o) stop 'Inner shell has to be smaller than outer shell. Check "-si" and "-so" settings'

! Read .cube file
! Cubegen output is in bohr

open(unit=23,file=cubefile)
read(23,'(A)') line1
read(23,'(A)') line2
read(23,*) natoms, xstart, ystart, zstart
read(23,*) pts(1), step_x, o, p
read(23,*) pts(2), o, step_y, p
read(23,*) pts(3), o, p, step_z

! Allocate all needed variables to natoms

allocate(ele_type(natoms),stat=Error)
if(Error > 0) stop 'Error allocating ele_type'

allocate(irank(natoms),stat=Error)
if(Error > 0) stop 'Error allocating irank'
allocate(jrank(natoms),stat=Error)
if(Error > 0) stop 'Error allocating jrank'

allocate(xr(natoms),stat=Error)
if(Error > 0) stop 'Error allocating xr'
allocate(yr(natoms),stat=Error)
if(Error > 0) stop 'Error allocating yr'
allocate(zr(natoms),stat=Error)
if(Error > 0) stop 'Error allocating zr'

allocate(xs(natoms),stat=Error)
if(Error > 0) stop 'Error allocating xs'
allocate(ys(natoms),stat=Error)
if(Error > 0) stop 'Error allocating ys'
allocate(zs(natoms),stat=Error)
if(Error > 0) stop 'Error allocating zs'

allocate(x1(natoms),stat=Error)
if(Error > 0) stop 'Error allocating x1'
allocate(y1(natoms),stat=Error)
if(Error > 0) stop 'Error allocating y1'
allocate(z1(natoms),stat=Error)
if(Error > 0) stop 'Error allocating z1'

allocate(qu(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu'
allocate(qu1x(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu1x'
allocate(qu1y(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu1y'
allocate(qu1z(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu1z'

allocate(qu20(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu20'
allocate(qu21c(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu21c'
allocate(qu21s(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu21s'
allocate(qu22c(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu22c'
allocate(qu22s(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu22s'

allocate(qu30(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu30'
allocate(qu31c(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu31c'
allocate(qu31s(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu31s'
allocate(qu32c(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu32c'
allocate(qu32s(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu32s'
allocate(qu33c(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu33c'
allocate(qu33s(natoms),stat=Error)
if(Error > 0) stop 'Error allocating qu33s'

allocate(vdw(natoms),stat=Error)
if(Error > 0) stop 'Error allocating vdw'


! Done allocating variables to natoms


do n1=1, natoms
   read(23,*) ele_type(n1), chrg, x1(n1), y1(n1), z1(n1)
enddo

allocate(excl(pts(3),pts(2),pts(1)),stat=Error)
if(Error > 0) stop 'Error allocating excl'
allocate(sigma_range(pts(3),pts(2),pts(1)),stat=Error)
if(Error > 0) stop 'Error allocating sigma_range'
allocate(near_vdw(pts(3),pts(2),pts(1)),stat=Error)
if(Error > 0) stop 'Error allocating sigma_range'
allocate(en(pts(3),pts(2),pts(1)),stat=Error)
if(Error > 0) stop 'Error allocating en'
allocate(totener(pts(3),pts(2),pts(1)),stat=Error)
if(Error > 0) stop 'Error allocating totener'
allocate(diff(pts(3),pts(2),pts(1)),stat=Error)
if(Error > 0) stop 'Error allocating diff'


! Read ESP calculated by cubegen

do n1=1,pts(1)
  do n2=1,pts(2)
    do i =6,pts(3)-1, 6
      read(23,*) (en(k,n2,n1), k=i-5,i)
    enddo
    read(23,*) (en(k,n2,n1), k=i-5,pts(3))
  enddo
enddo
close(23)

if (sigma_only .eqv. .false.) write(*,*) 'ESP read.'

! Read .vdw file

open(unit=30,file=vdwfile)
do n1=1,natoms
   read(30,*) vdw(n1)
   read(30,*) jrank(n1)
enddo
close(30)

! Read .pun file and transfer angstrom units to bohr

open(unit=28,file=punfile)
read(28,*) wrd
read(28,*) wrd
read(28,*) wrd
do n1=1, natoms
  read(28,*) wrd, xs(n1), ys(n1), zs(n1), rnk, irank(n1)
  xs(n1) = xs(n1) * a2b
  ys(n1) = ys(n1) * a2b
  zs(n1) = zs(n1) * a2b
  if (irank(n1).ne.jrank(n1)) then
    print *, "Check rank in vdw and dma file !"
    stop
  endif
  read(28,*) qu(n1)
  if ( irank(n1) .ne. 0 ) then
    read(28,*) qu1z(n1), qu1x(n1), qu1y(n1)
    if ( irank(n1) .ne. 1 ) then
      read(28,*) qu20(n1), qu21c(n1), qu21s(n1), qu22c(n1), qu22s(n1)
      if ( irank(n1) .ne. 2 ) then
        read(28,*) qu30(n1), qu31c(n1), qu31s(n1), qu32c(n1), qu32s(n1)
        read(28,*) qu33c(n1), qu33s(n1)
      endif
    endif
  endif
enddo
close(28)

! exclude point if within vdw radius of any atom and mark if close to vdw or within sigma range
! Cycle if calculation is demanded for sigma range only

excl = .false.
near_vdw = .false.
sigma_range = .false.

x = xstart-step_x
do n1=1, pts(1)
  x = x + step_x
  y = ystart-step_y
  do n2=1, pts(2)
    y = y + step_y
    z = zstart-step_z
    do n3=1, pts(3)
      z = z + step_z
      do n0=1, natoms
        o = vdw(n0)**2
        p = (shell_i*vdw(n0))**2
        q = (shell_o*vdw(n0))**2
        r = (xs(n0)-x)**2+(ys(n0)-y)**2+(zs(n0)-z)**2
        if ( r .LT. o ) then
          excl(n3,n2,n1) = .true.
          cycle
        elseif((r .GT. o) .and. (r .LT. p)) then
          near_vdw(n3,n2,n1) = .true.
          if (sigma_only .eqv. .true. ) then
            excl(n3,n2,n1) = .true.
            cycle
          endif
        elseif((r .GT. p) .and. (r .LT.q)) then
          sigma_range(n3,n2,n1) = .true.
        endif
      enddo
      if ((sigma_only .eqv. .true.) .and. (sigma_range(n3,n2,n1) .eqv. .false.)) excl(n3,n2,n1) = .true.
    enddo
  enddo
enddo

! step through all grid points, calculate potentials from Multipoles

totener(:,:,:) = 0
do n0=1, natoms
  x = xstart-step_x
  do n1=1, pts(1)
    x = x + step_x
    y = ystart-step_y
    do n2=1, pts(2)
      y = y + step_y
      z = zstart-step_z
      do n3=1, pts(3)
        z = z + step_z
        if (excl(n3,n2,n1) .eqv. .true.) cycle
        r = sqrt((xs(n0)-x)**2+(ys(n0)-y)**2+(zs(n0)-z)**2)
        trax = -(xs(n0)-x)/r
        tray = -(ys(n0)-y)/r
        traz = -(zs(n0)-z)/r

! qu(n0) is the charge on atom n0. The Potential due to this charge is calculated as (qu(n0))/(r)

        que = (qu(n0))/(r)

! Contribution according to the monopole

        if ( irank(n0) .eq. 0 ) then
          totener(n3,n2,n1) = totener(n3,n2,n1)+que
        else

! qu1[x,y,z]e are the components of the dipole vector. The potential due to the dipole is calculated as qu1[x,y,z](n0)/(r**2)*-delta[x,y,z]/r
! (r**2) comes from the interaction between a dipole and a monopole
! the other terms (-delta[x,y,z]/r) are there because the directionality of the dipole has to be taken account of, weighted by the contribution 
! of the single terms to the unit vector. (-delta[x,y,z]/r) scales to the unit vector.

          qu1ze = qu1z(n0)/(r**2)*traz
          qu1ye = qu1y(n0)/(r**2)*tray
          qu1xe = qu1x(n0)/(r**2)*trax
        if ( irank(n0) .eq. 1 ) then
          totener(n3,n2,n1) = totener(n3,n2,n1)+que+qu1ze+qu1xe+qu1ye
        else

! This is the contribution according to the quadrupole

          qu20e  = qu20(n0) /(r**3)*0.5*(3*(traz**2)-1)
          qu21ce = qu21c(n0)/(r**3)*(3**(0.5))*(trax*traz)
          qu21se = qu21s(n0)/(r**3)*(3**(0.5))*(tray*traz)
          qu22ce = qu22c(n0)/(r**3)*(0.5*(3**(0.5))*(trax**2-tray**2))
          qu22se = qu22s(n0)/(r**3)*(3**(0.5))*(trax*tray)
        
        if ( irank(n0) .eq. 2 ) then
          totener(n3,n2,n1) = totener(n3,n2,n1)+que+qu1ze+qu1xe+qu1ye&
&                            +qu20e+qu21ce+qu21se+qu22ce+qu22se
        else
     
! This is the contribution according to the octupole
      
          qu30e  = qu30(n0) /(r**4)*(5*traz**3-3*traz)
          qu31ce = qu31c(n0)/(r**4)*0.25*2.449409*trax*(traz**2-1)
          qu31se = qu31s(n0)/(r**4)*0.25*2.449409*tray*(traz**2-1)
          qu32ce = qu32c(n0)/(r**4)*0.5*3.872983*traz*(trax**2-tray**2)
          qu32se = qu32s(n0)/(r**4)*3.872983*trax*tray*traz
          qu33ce=qu33c(n0)/(r**4)*0.25*3.162278*trax*(trax**2-3*tray**2)
          qu33se=qu33s(n0)/(r**4)*0.25*3.162278*tray*(3*trax**2-tray**2)
          totener(n3,n2,n1)=totener(n3,n2,n1)+que+qu1ze+qu1xe+qu1ye&
&                           +qu20e+qu21ce+qu21se+qu22ce+qu22se+qu30e&
&                           +qu31ce+qu31se+qu32ce+qu32se+qu33ce+qu33se
        endif
        endif
        endif
      enddo
    enddo
  enddo
enddo

! Analysis of the differences

diffcnt = 0
diffsum = 0
diffperc = 0
diffcnt_sigma = 0
diffsum_sigma = 0
diffperc_sigma = 0
diffsum_sigma_sq = 0
diffcnt_nvdw = 0
diffsum_nvdw = 0
diffperc_nvdw = 0
diffcnt_farout = 0
diffsum_farout = 0
diffperc_farout = 0



do n1=1, pts(1)
  do n2=1, pts(2)
    do n3=1, pts(3)
      if ( excl(n3,n2,n1) .eqv. .true. ) then
        diff(n3,n2,n1) = 0
       
      elseif (near_vdw(n3,n2,n1) .eqv. .true. ) then
       diffcnt_nvdw = diffcnt_nvdw + 1
       diffcnt = diffcnt + 1
       diff(n3,n2,n1) = abs(totener(n3,n2,n1)-en(n3,n2,n1))
       diffsum_nvdw = diffsum_nvdw + diff(n3,n2,n1)
       diffsum = diffsum + diff(n3,n2,n1)
       diffperc_nvdw =  diffperc_nvdw + diff(n3,n2,n1)/(abs(en(n3,n2,n1)))
       diffperc = diffperc +  diff(n3,n2,n1)/(abs(en(n3,n2,n1)))

      elseif (sigma_range(n3,n2,n1) .eqv. .true.) then
       diffcnt_sigma = diffcnt_sigma + 1
       diffcnt = diffcnt + 1
       diff(n3,n2,n1) = abs(totener(n3,n2,n1)-en(n3,n2,n1))
       diffsum_sigma = diffsum_sigma + diff(n3,n2,n1) 
       diffsum_sigma_sq = diffsum_sigma_sq + diff(n3,n2,n1)**2
       diffsum = diffsum + diff(n3,n2,n1)
       diffperc_sigma = diffperc_sigma + diff(n3,n2,n1)/(abs(en(n3,n2,n1)))
       diffperc = diffperc +  diff(n3,n2,n1)/(abs(en(n3,n2,n1)))

      else
       diffcnt_farout = diffcnt_farout + 1
       diffcnt = diffcnt + 1
       diff(n3,n2,n1) = abs(totener(n3,n2,n1)-en(n3,n2,n1))
       diffsum_farout = diffsum_farout + diff(n3,n2,n1)
       diffsum = diffsum + diff(n3,n2,n1)
       diffperc_farout = diffperc_farout + diff(n3,n2,n1)/(abs(en(n3,n2,n1)))
       diffperc = diffperc +  diff(n3,n2,n1)/(abs(en(n3,n2,n1)))
      endif
    enddo
  enddo
enddo

if (sigma_only .eqv. .true.) then
  write(*,*)diffsum_sigma_sq/diffcnt_sigma
else
  write(*,*) 'Analysis of total space'      
  write(*,*) 'sum of differences: ', diffsum*2625.5, ' kJ/mol'
  write(*,*) 'difference average: ', diffsum*2625.5/diffcnt, ' kJ/mol'
  write(*,*) 'difference percentage: ',(diffperc/diffcnt)*100, ' %'
  write(*,*)
  write(*,'(A,F4.2,A)') 'Analysis of space between vdW-Surface and ',shell_i,' * vdW-Surface'	   
  write(*,*) 'sum of differences: ', diffsum_nvdw*2625.5, ' kJ/mol'
  write(*,*) 'difference average: ', diffsum_nvdw*2625.5/diffcnt_nvdw, ' kJ/mol'
  write(*,*) 'difference percentage: ',(diffperc_nvdw/diffcnt_nvdw)*100, ' %'
  write(*,*)
  write(*,'(A,F4.2,A,F4.2,A)') 'Analysis of space between ',shell_i,' * vdW-Surface - ',shell_o,' * vdw-Surface'
  write(*,*) 'sum of differences: ', diffsum_sigma*2625.5, ' kJ/mol'
  write(*,*) 'difference average: ', diffsum_sigma*2625.5/diffcnt_sigma, ' kJ/mol'
  write(*,*) 'difference percentage: ',(diffperc_sigma/diffcnt_sigma)*100, ' %'
  write(*,*)
  write(*,'(A,F4.2,A)') 'Analysis of space outside ',shell_o,' * vdW-Surface'	   
  write(*,*) 'sum of differences: ', diffsum_farout*2625.5, ' kJ/mol'
  write(*,*) 'difference average: ', diffsum_farout*2625.5/diffcnt_farout, ' kJ/mol'
  write(*,*) 'difference percentage: ',(diffperc_farout/diffcnt_farout)*100, ' %'
endif

! write outputfiles if not neglected

if ((no_pics .eqv. .false.) .and. (sigma_only .eqv. .false.)) then

  call rscripts('xy-diffs   ',pts(3),pts(1),pts(2),xstart,ystart,step_x,step_y,diff,1)
  call rscripts('xz-diffs   ',pts(2),pts(1),pts(3),xstart,zstart,step_x,step_z,diff,2)
  call rscripts('yz-diffs   ',pts(1),pts(2),pts(3),ystart,zstart,step_y,step_z,diff,3)

  call rscripts('xy-gauss-en',pts(3),pts(1),pts(2),xstart,ystart,step_x,step_y,en,1)
  call rscripts('xz-gauss-en',pts(2),pts(1),pts(3),xstart,zstart,step_x,step_z,en,2)
  call rscripts('yz-gauss-en',pts(1),pts(2),pts(3),ystart,zstart,step_y,step_z,en,3)

  call rscripts('xy-mult-en ',pts(3),pts(1),pts(2),xstart,ystart,step_x,step_y,totener,1)
  call rscripts('xz-mult-en ',pts(2),pts(1),pts(3),xstart,zstart,step_x,step_z,totener,2)
  call rscripts('yz-mult-en ',pts(1),pts(2),pts(3),ystart,zstart,step_y,step_z,totener,3)

endif

!write cubefiles if requested

if (cubeout .eqv. .true.) then

  call cubes(en,'gausscube','Electrostatic potential from Total SCF Density                        ')
  call cubes(totener,'mtpcube  ','Electrostatic potential from Atomic Multipoles                        ')
  call cubes(en-totener,'diffcube ','Difference between ab-initio and MTP Electrostatic Potential          ')

endif

end program fieldcomp




!subroutine to write scripts for R


subroutine rscripts(rname,pt1,pt2,pt3,coorx,coory,stepx,stepy,matscript,coords)


use characters, only: basename
use integers, only: pts

implicit none

real,intent(in):: matscript(pts(3),pts(2),pts(1))
integer::k,i,j
integer,intent(in)::pt1,pt2,pt3,coords
real,intent(in)::coorx,coory,stepx,stepy
real::coorxb,cooryb
character(len=11),intent(in)::rname


open(unit=25,file=trim(basename)//trim(rname)//'.txt',action='write',status='replace')
k = pt1/2
if (coords==1) then
  coorxb=coorx
  do i=1,pt2
    cooryb=coory
    do j=1,pt3
      write (25,'(F8.3,3X,F8.3,3X,F8.3)')coorxb,cooryb,matscript(k,j,i)*2625.5
      cooryb = cooryb + stepy
    enddo
    coorxb = coorxb + stepx
  enddo
elseif (coords==2) then
  coorxb=coorx
  do i=1,pt2
    cooryb=coory
    do j=1,pt3
      write (25,'(F8.3,3X,F8.3,3X,F8.3)')coorxb,cooryb,matscript(j,k,i)*2625.5
      cooryb = cooryb + stepy
    enddo
    coorxb = coorxb + stepx
  enddo
elseif (coords==3) then
  coorxb=coorx
  do i=1,pt2
    cooryb=coory
    do j=1,pt3
      write (25,'(F8.3,3X,F8.3,3X,F8.3)')coorxb,cooryb,matscript(j,i,k)*2625.5
      cooryb = cooryb + stepy
    enddo
    coorxb = coorxb + stepx
  enddo
endif
close(unit=25)


end subroutine rscripts




!subroutine to write cubefiles


subroutine cubes(matcube,cubename,description)

use characters, only: basename,line1
use allocatables, only: ele_type,excl,near_vdw,sigma_range,x1,y1,z1,irank,jrank
use integers, only: natoms, pts
use reals, only: xstart,ystart,zstart,step_x,step_y,step_z

implicit none

character(len=9),intent(in) :: cubename
character(len=70),intent(in) :: description
integer :: n1,n2,n3

real,intent(in) :: matcube(pts(3),pts(2),pts(1))

! Write File with Gaussian ESPs with point in sigma range only
open(unit=22,file=trim(basename)//trim(cubename)//'.cube',action='write',status='replace')
! Write header
write(22,*)line1
write(22,*)description
write(22,'(I5, F12.6, F12.6, F12.6)') natoms, xstart, ystart, zstart
write(22,'(I5, F12.6,A)')     pts(1),step_x,'    0.000000    0.000000'
write(22,'(I5, A, F12.6, A)') pts(2),'    0.000000',step_y,'	0.000000'
write(22,'(I5, A, F12.6)')    pts(3),'    0.000000    0.000000',step_z
do n1=1, natoms
  write(22,'(I5, F12.6, F12.6, F12.6, F12.6)') ele_type(n1), real(ele_type(n1)), x1(n1), y1(n1), z1(n1)
enddo
! Write numbers
do n1=1, pts(1)
  do n2=1, pts(2)
    do n3=1, pts(3)
      if ((excl(n3,n2,n1) .eqv. .false.) .and. &
&         (near_vdw(n3,n2,n1) .eqv. .false.) .and. &
&         (sigma_range(n3,n2,n1) .eqv. .true.)) then
        write(22,'(X,E12.5)',advance='no') matcube(n3,n2,n1)
      else
        write(22,'(A)',advance='no') '  0.00000E+00'
      endif
      if (modulo(n3,6) == 0 .and. n3<pts(3)) then 
        write(22,*)
      endif
    enddo
    write(22,*)
  enddo
enddo
close(unit=22)

end subroutine cubes
