#!/usr/bin/env python
import numpy as np

class GaussianCube():
    '''
    GaussianCube Class:
    Includes a bunch of methods to manipulate cube data
    '''
    def __init__(self,fname=None):
        if fname != None:
            try:
                self.read_cube(fname)
            except IOError as e:
                print( "File used as input: %s" % fname )
                print( "File error ({0}): {1}".format(e.errno, e.strerror))
                self.terminate_code()
        else:
            self.default_values()
        return None

    def terminate_code(self):
        print( "Code terminating now")
        exit()
        return None

    def default_values(self):
        self.natoms=0
        self.comment1=0
        self.comment2=0
        self.origin=np.array([0,0,0])
        self.NX=0
        self.NY=0
        self.NZ=0
        self.X=0
        self.Y=0
        self.Z=0
        self.atoms=['0']
        self.atomsXYZ=[0,0,0]
        self.data=[0]
        return None

    def read_cube(self,fname):
        """
        Method to read cube file. Just needs the filename
        """

        with open(fname, 'r') as fin:
            self.filename = fname
            self.comment1 = fin.readline() #Save 1st comment
            self.comment2 = fin.readline() #Save 2nd comment
            nOrigin = fin.readline().split() # Number of Atoms and Origin
            self.natoms = int(nOrigin[0]) #Number of Atoms
            self.origin = np.array([float(nOrigin[1]),float(nOrigin[2]),float(nOrigin[3])]) #Position of Origin
            nVoxel = fin.readline().split() #Number of Voxels
            self.NX = int(nVoxel[0])
            self.X = np.array([float(nVoxel[1]),float(nVoxel[2]),float(nVoxel[3])])
            nVoxel = fin.readline().split() #
            self.NY = int(nVoxel[0])
            self.Y = np.array([float(nVoxel[1]),float(nVoxel[2]),float(nVoxel[3])])
            nVoxel = fin.readline().split() #
            self.NZ = int(nVoxel[0])
            self.Z = np.array([float(nVoxel[1]),float(nVoxel[2]),float(nVoxel[3])])
            self.atoms = []
            self.atomsXYZ = []
            for atom in range(self.natoms):
                line= fin.readline().split()
                self.atoms.append(line[0])
                self.atomsXYZ.append(list(map(float,[line[2], line[3], line[4]])))
            self.data = np.zeros((self.NX,self.NY,self.NZ))
            i= int(0)
            for s in fin:
                for v in s.split():
                    self.data[int(i/(self.NY*self.NZ)), int((i/self.NZ)%self.NY), int(i%self.NZ)] = float(v)
                    i+=1
            # if i != self.NX*self.NY*self.NZ: raise NameError, "FSCK!"
        return None

    def write_cube(self,fname,comment='written cube'):
        '''
        Write out a Gaussian Cube file
        '''
        try:
            with open(fname,'w') as fout:
                if len(comment.split('\n')) != 2:
                    print( 'Comment line NEEDS to be two lines!')
                    self.terminate_code()
                fout.write('%s\n' % comment)
                fout.write("%4d %.6f %.6f %.6f\n" % (self.natoms, self.origin[0], self.origin[1], self.origin[2]))
                fout.write("%4d %.6f %.6f %.6f\n" % (self.NX, self.X[0], self.X[1], self.X[2]))
                fout.write("%4d %.6f %.6f %.6f\n" % (self.NY, self.Y[0], self.Y[1], self.Y[2]))
                fout.write("%4d %.6f %.6f %.6f\n" % (self.NZ, self.Z[0], self.Z[1], self.Z[2]))
                for atom,xyz in zip(self.atoms,self.atomsXYZ):
                    fout.write("%s %d %6.3f %6.3f %6.3f\n" % (atom, 0, xyz[0], xyz[1], xyz[2]))
                for ix in range(self.NX):
                   for iy in range(self.NY):
                       for iz in range(self.NZ):
                           fout.write("%.5e " % self.data[ix,iy,iz]),
                           if (iz % 6 == 5): fout.write('\n')
                       fout.write("\n")
        except IOError as e:
            print( "File used as output does not work: %s" % fname)
            print( "File error ({0}): {1}".format(e.errno, e.strerror))
            self.terminate_code()
        return None

    def to_grid_vals(self):
        grid_xyz = np.zeros((self.data.size,3))
        grid_val = np.zeros((self.data.size))
        i = 0
        for ix in range(self.NX):
            for iy in range(self.NY):
                for iz in range(self.NZ):
                    grid_xyz[i,:] = self.origin + ix*self.X + iy*self.Y + iz*self.Z
                    grid_val[i]   = self.data[ix,iy,iz]
                    i += 1
        return grid_xyz, grid_val