#!/usr/bin/env python
#
# Copyright 2013 Tristan Bereau and Christian Kramer
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
#     limitations under the License.
#
################################
# Write R skript to generate the field pictures using ggplot2
#

from sys import *
import subprocess,os,time
import os.path

filename_body = argv[1]

fp = open(filename_body+"_makepics.r","w")
fp.write("library(ggplot2)\n")                            # Diff Fields first
fp.write("xydiffs = read.table('"+filename_body+"_xydiffs.txt',na.strings='********')\n")
fp.write("colnames(xydiffs) = c('X','Y','Diff')\n")
fp.write("postscript('"+filename_body+"_xydiffs.eps')\n")
fp.write("v <- ggplot(xydiffs, aes(Y,X,z=Diff))\n")
fp.write("v + geom_tile(aes(fill=Diff)) + scale_fill_gradientn(colour=c('white','yellow','green','orange','red'),limits=c(0,40)) + stat_contour() + xlab('Z [Bohr]') + ylab('Y [Bohr]') + opts(axis.title.x = theme_text(face = 'bold',size = 14),axis.title.y = theme_text(face = 'bold',size = 14,angle = 90),axis.text.x = theme_text(size = 14,vjust=1),axis.text.y = theme_text(size = 14,hjust = 1),legend.text = theme_text(size = 12),legend.title = theme_text(size = 12, face = 'bold', hjust = 0))\n")
fp.write("dev.off()\n")
fp.write("xzdiffs = read.table('"+filename_body+"_xzdiffs.txt',na.strings='********')\n")
fp.write("colnames(xzdiffs) = c('X','Z','Diff')\n")
fp.write("postscript('"+filename_body+"_xzdiffs.eps')\n")
fp.write("v <- ggplot(xzdiffs, aes(Z,X,z=Diff))\n")
fp.write("v + geom_tile(aes(fill=Diff)) + scale_fill_gradientn(colour=c('white','yellow','green','orange','red'),limits=c(0,40)) + stat_contour() + xlab('Z [Bohr]') + ylab('Y [Bohr]') + opts(axis.title.x = theme_text(face = 'bold',size = 14),axis.title.y = theme_text(face = 'bold',size = 14,angle = 90),axis.text.x = theme_text(size = 14,vjust=1),axis.text.y = theme_text(size = 14,hjust = 1),legend.text = theme_text(size = 12),legend.title = theme_text(size = 12, face = 'bold', hjust = 0))\n")
fp.write("dev.off()\n")
fp.write("yzdiffs = read.table('"+filename_body+"_yzdiffs.txt',na.strings='********')\n")
fp.write("colnames(yzdiffs) = c('Y','Z','Diff')\n")
fp.write("postscript('"+filename_body+"_yzdiffs.eps')\n")
fp.write("v <- ggplot(yzdiffs, aes(Z,Y,z=Diff))\n")
fp.write("v + geom_tile(aes(fill=Diff)) + scale_fill_gradientn(colour=c('white','yellow','green','orange','red'),limits=c(0,40)) + stat_contour() + xlab('Z [Bohr]') + ylab('Y [Bohr]') + opts(axis.title.x = theme_text(face = 'bold',size = 14),axis.title.y = theme_text(face = 'bold',size = 14,angle = 90),axis.text.x = theme_text(size = 14,vjust=1),axis.text.y = theme_text(size = 14,hjust = 1),legend.text = theme_text(size = 12),legend.title = theme_text(size = 12, face = 'bold', hjust = 0))\n")
fp.write("dev.off()\n")

fp.write("xy_gauss = read.table('"+filename_body+"_xy-gauss-en.txt',na.strings='********')\n")        # Potential_fields from Gaussian second
fp.write("colnames(xy_gauss) = c('X','Y','MEP')\n")
fp.write("postscript('"+filename_body+"_xy_gauss.eps')\n")
fp.write("v <- ggplot(xy_gauss, aes(Y,X,z=MEP))\n")
fp.write("v + geom_tile(aes(fill=MEP)) + scale_fill_gradientn(colour=rainbow(10),limits=c(-50,50)) + stat_contour() + xlab('Z [Bohr]') + ylab('Y [Bohr]') + opts(axis.title.x = theme_text(face = 'bold',size = 14),axis.title.y = theme_text(face = 'bold',size = 14,angle = 90),axis.text.x = theme_text(size = 14,vjust=1),axis.text.y = theme_text(size = 14,hjust = 1),legend.text = theme_text(size = 12),legend.title = theme_text(size = 12, face = 'bold', hjust = 0))\n")
fp.write("dev.off()\n")
fp.write("xz_gauss = read.table('"+filename_body+"_xz-gauss-en.txt',na.strings='********')\n")        
fp.write("colnames(xz_gauss) = c('X','Z','MEP')\n")
fp.write("postscript('"+filename_body+"_xz_gauss.eps')\n")
fp.write("v <- ggplot(xz_gauss, aes(Z,X,z=MEP))\n")
fp.write("v + geom_tile(aes(fill=MEP)) + scale_fill_gradientn(colour=rainbow(10),limits=c(-50,50)) + stat_contour() + xlab('Z [Bohr]') + ylab('Y [Bohr]') + opts(axis.title.x = theme_text(face = 'bold',size = 14),axis.title.y = theme_text(face = 'bold',size = 14,angle = 90),axis.text.x = theme_text(size = 14,vjust=1),axis.text.y = theme_text(size = 14,hjust = 1),legend.text = theme_text(size = 12),legend.title = theme_text(size = 12, face = 'bold', hjust = 0))\n")
fp.write("dev.off()\n")
fp.write("yz_gauss = read.table('"+filename_body+"_yz-gauss-en.txt',na.strings='********')\n")        
fp.write("colnames(yz_gauss) = c('Y','Z','MEP')\n")
fp.write("postscript('"+filename_body+"_yz_gauss.eps')\n")
fp.write("v <- ggplot(yz_gauss, aes(Z,Y,z=MEP))\n")
fp.write("v + geom_tile(aes(fill=MEP)) + scale_fill_gradientn(colour=rainbow(10),limits=c(-50,50)) + stat_contour() + xlab('Z [Bohr]') + ylab('Y [Bohr]') + opts(axis.title.x = theme_text(face = 'bold',size = 14),axis.title.y = theme_text(face = 'bold',size = 14,angle = 90),axis.text.x = theme_text(size = 14,vjust=1),axis.text.y = theme_text(size = 14,hjust = 1),legend.text = theme_text(size = 12),legend.title = theme_text(size = 12, face = 'bold', hjust = 0))\n")
fp.write("dev.off()\n")

fp.write("xy_mtp = read.table('"+filename_body+"_xy-mult-en.txt',na.strings='********')\n")            # Potential_fields from Multipoles last
fp.write("colnames(xy_mtp) = c('X','Y','MEP')\n")
fp.write("postscript('"+filename_body+"_xy_mtp.eps')\n")
fp.write("v <- ggplot(xy_mtp, aes(Y,X,z=MEP))\n")
fp.write("v + geom_tile(aes(fill=MEP)) + scale_fill_gradientn(colour=rainbow(10),limits=c(-50,50)) + stat_contour() + xlab('Z [Bohr]') + ylab('Y [Bohr]') + opts(axis.title.x = theme_text(face = 'bold',size = 14),axis.title.y = theme_text(face = 'bold',size = 14,angle = 90),axis.text.x = theme_text(size = 14,vjust=1),axis.text.y = theme_text(size = 14,hjust = 1),legend.text = theme_text(size = 12),legend.title = theme_text(size = 12, face = 'bold', hjust = 0))\n")
fp.write("dev.off()\n")
fp.write("xz_mtp = read.table('"+filename_body+"_xz-mult-en.txt',na.strings='********')\n")    
fp.write("colnames(xz_mtp) = c('X','Z','MEP')\n")
fp.write("postscript('"+filename_body+"_xz_mtp.eps')\n")
fp.write("v <- ggplot(xz_mtp, aes(Z,X,z=MEP))\n")
fp.write("v + geom_tile(aes(fill=MEP)) + scale_fill_gradientn(colour=rainbow(10),limits=c(-50,50)) + stat_contour() + xlab('Z [Bohr]') + ylab('Y [Bohr]') + opts(axis.title.x = theme_text(face = 'bold',size = 14),axis.title.y = theme_text(face = 'bold',size = 14,angle = 90),axis.text.x = theme_text(size = 14,vjust=1),axis.text.y = theme_text(size = 14,hjust = 1),legend.text = theme_text(size = 12),legend.title = theme_text(size = 12, face = 'bold', hjust = 0))\n")
fp.write("dev.off()\n")
fp.write("yz_mtp = read.table('"+filename_body+"_yz-mult-en.txt',na.strings='********')\n")    
fp.write("colnames(yz_mtp) = c('Y','Z','MEP')\n")
fp.write("postscript('"+filename_body+"_yz_mtp.eps')\n")
fp.write("v <- ggplot(yz_mtp, aes(Z,Y,z=MEP))\n")
fp.write("v + geom_tile(aes(fill=MEP)) + scale_fill_gradientn(colour=rainbow(10),limits=c(-50,50)) + stat_contour() + xlab('Z [Bohr]') + ylab('Y [Bohr]') + opts(axis.title.x = theme_text(face = 'bold',size = 14),axis.title.y = theme_text(face = 'bold',size = 14,angle = 90),axis.text.x = theme_text(size = 14,vjust=1),axis.text.y = theme_text(size = 14,hjust = 1),legend.text = theme_text(size = 12),legend.title = theme_text(size = 12, face = 'bold', hjust = 0))\n")
fp.write("dev.off()\n")

fp.write("q()\n")

fp.close()

print
print " Run 'R CMD BATCH "+filename_body+"_makepics.r' to generate the pictures"
print

exit(0)
