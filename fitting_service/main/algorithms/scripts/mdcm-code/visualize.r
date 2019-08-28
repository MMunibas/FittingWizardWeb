#!/usr/bin/env Rscript

#for conveniently drawing labels
line2user <- function(line, side) {
  lh <- par('cin')[2] * par('cex') * par('lheight')
  x_off <- diff(grconvertX(0:1, 'inches', 'user'))
  y_off <- diff(grconvertY(0:1, 'inches', 'user'))
  switch(side,
         `1` = par('usr')[3] - line * y_off * lh,
         `2` = par('usr')[1] - line * x_off * lh,
         `3` = par('usr')[4] + line * y_off * lh,
         `4` = par('usr')[2] + line * x_off * lh,
         stop("side must be 1, 2, 3, or 4", call.=FALSE))
}


#define color
numlev = 100 #number of colour levels
ndim = 2 #spatial dimensions for now
coulombcolor <- colorRampPalette(c("red","yellow","green","cyan","blue"),space = "rgb")(numlev)
errcolor <- colorRampPalette(c("red","white","blue"),space = "rgb")(numlev)

#read data
xyslice <- data.matrix(read.csv("slices/sliceXY.csv",header = FALSE))
xzslice <- data.matrix(read.csv("slices/sliceXZ.csv",header = FALSE))
yzslice <- data.matrix(read.csv("slices/sliceYZ.csv",header = FALSE))
truexyslice <- data.matrix(read.csv("slices/truesliceXY.csv",header = FALSE))
truexzslice <- data.matrix(read.csv("slices/truesliceXZ.csv",header = FALSE))
trueyzslice <- data.matrix(read.csv("slices/truesliceYZ.csv",header = FALSE))
fullxyslice <- data.matrix(read.csv("slices/fullsliceXY.csv",header = FALSE))
fullxzslice <- data.matrix(read.csv("slices/fullsliceXZ.csv",header = FALSE))
fullyzslice <- data.matrix(read.csv("slices/fullsliceYZ.csv",header = FALSE))
truefullxyslice <- data.matrix(read.csv("slices/truefullsliceXY.csv",header = FALSE))
truefullxzslice <- data.matrix(read.csv("slices/truefullsliceXZ.csv",header = FALSE))
truefullyzslice <- data.matrix(read.csv("slices/truefullsliceYZ.csv",header = FALSE))

#compute the error slices
xyerr    = xyslice-truexyslice
xyrelerr = xyerr/truexyslice
xzerr    = xzslice-truexzslice
xzrelerr = xzerr/truexzslice
yzerr    = yzslice-trueyzslice
yzrelerr = yzerr/trueyzslice
fullxyerr    = fullxyslice-truefullxyslice
fullxyrelerr = fullxyerr/truefullxyslice
fullxzerr    = fullxzslice-truefullxzslice
fullxzrelerr = fullxzerr/truefullxzslice
fullyzerr    = fullyzslice-truefullyzslice
fullyzrelerr = fullyzerr/truefullyzslice


#custom max and min function, needed or else NA reports -Infty
my.max <- function(x) ifelse( !all(is.na(x)), max(x, na.rm=T), NA)
my.min <- function(x) ifelse( !all(is.na(x)), min(x, na.rm=T), NA)
#for plotting the fitted and the real slices on the same scale, 0 = green (else you cannot compare)
#old method
#xycol     = max(my.max(abs(xyslice)),my.max(abs(truexyslice)))
#xzcol     = max(my.max(abs(xzslice)),my.max(abs(truexzslice)))
#yzcol     = max(my.max(abs(yzslice)),my.max(abs(trueyzslice)))
#new method
xycol     = my.max(abs(truexyslice))
xzcol     = my.max(abs(truexzslice))
yzcol     = my.max(abs(trueyzslice))
xycol     = c(-xycol,    xycol)
xzcol     = c(-xzcol,    xzcol)
yzcol     = c(-yzcol,    yzcol)

#clamp slices to min and max values of the color ranges (else it gets drawn in white)
xyslice        [xyslice         < xycol[1]] <- xycol[1]
xyslice        [xyslice         > xycol[2]] <- xycol[2]
fullxyslice    [fullxyslice     < xycol[1]] <- xycol[1]
fullxyslice    [fullxyslice     > xycol[2]] <- xycol[2]
truefullxyslice[truefullxyslice < xycol[1]] <- xycol[1]
truefullxyslice[truefullxyslice > xycol[2]] <- xycol[2]
xzslice        [xzslice         < xzcol[1]] <- xzcol[1]
xzslice        [xzslice         > xzcol[2]] <- xzcol[2]
fullxzslice    [fullxzslice     < xzcol[1]] <- xzcol[1]
fullxzslice    [fullxzslice     > xzcol[2]] <- xzcol[2]
truefullxzslice[truefullxzslice < xzcol[1]] <- xzcol[1]
truefullxzslice[truefullxzslice > xzcol[2]] <- xzcol[2]
yzslice        [yzslice         < yzcol[1]] <- yzcol[1]
yzslice        [yzslice         > yzcol[2]] <- yzcol[2]
fullyzslice    [fullyzslice     < yzcol[1]] <- yzcol[1]
fullyzslice    [fullyzslice     > yzcol[2]] <- yzcol[2]
truefullyzslice[truefullyzslice < yzcol[1]] <- yzcol[1]
truefullyzslice[truefullyzslice > yzcol[2]] <- yzcol[2]

# clamp the relative error slices (relative error ranges from -100 % to 100 %)
    xyrelerr[    xyrelerr < -1] <- -1
    xyrelerr[    xyrelerr >  1] <-  1
    xzrelerr[    xzrelerr < -1] <- -1
    xzrelerr[    xzrelerr >  1] <-  1
    yzrelerr[    yzrelerr < -1] <- -1
    yzrelerr[    yzrelerr >  1] <-  1
fullxyrelerr[fullxyrelerr < -1] <- -1
fullxyrelerr[fullxyrelerr >  1] <-  1
fullxzrelerr[fullxzrelerr < -1] <- -1
fullxzrelerr[fullxzrelerr >  1] <-  1
fullyzrelerr[fullyzrelerr < -1] <- -1
fullyzrelerr[fullyzrelerr >  1] <-  1

# clamp the absolute error slices
    xyerr[    xyerr < xycol[1]] <- xycol[1] 
    xyerr[    xyerr > xycol[2]] <- xycol[2] 
    xzerr[    xzerr < xzcol[1]] <- xzcol[1] 
    xzerr[    xzerr > xzcol[2]] <- xzcol[2] 
    yzerr[    yzerr < yzcol[1]] <- yzcol[1] 
    yzerr[    yzerr > yzcol[2]] <- yzcol[2] 
fullxyerr[fullxyerr < xycol[1]] <- xycol[1]
fullxyerr[fullxyerr > xycol[2]] <- xycol[2]
fullxzerr[fullxzerr < xzcol[1]] <- xzcol[1]
fullxzerr[fullxzerr > xzcol[2]] <- xzcol[2]
fullyzerr[fullyzerr < yzcol[1]] <- yzcol[1]
fullyzerr[fullyzerr > yzcol[2]] <- yzcol[2]

#plots without border
png(filename = "comparison.png",
      width = 1200, height = 1800, units = "px", pointsize = 12,
      bg = "white",  res = NA)
op <- par(mfrow=c(6,4),mar=c(3,4,3,3))

#plot xyslice
image(truexyslice,zlim=xycol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
mtext("True ESP", font=2, cex=2, side = 3, line = 0, outer = FALSE)
mtext("XY plane", font=2, cex=2, side = 2, line = 1,outer = FALSE)
image(xyslice,zlim=xycol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
mtext("Fitted ESP", font=2, cex=2, side = 3, line = 0, outer = FALSE)
image(xyrelerr,zlim=c(-1,1),xlab='',ylab='',axes=FALSE,col=errcolor)
mtext("Relative Error", font=2, cex=2, side = 3, line = 0, outer = FALSE)
image(xyerr,zlim=xycol,xlab='',ylab='',axes=FALSE,col=errcolor)
mtext("Absolute Error", font=2, cex=2, side = 3, line = 0, outer = FALSE)

#plot fullxyslice
image(truefullxyslice,zlim=xycol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
mtext("XY plane - full", font=2, cex=2, side = 2, line = 1,outer = FALSE)
image(fullxyslice,zlim=xycol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
image(fullxyrelerr,zlim=c(-1,1),xlab='',ylab='',axes=FALSE,col=errcolor)
image(fullxyerr,zlim=xycol,xlab='',ylab='',axes=FALSE,col=errcolor)

#plot xzslice
image(truexzslice,zlim=xzcol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
mtext("XZ plane", font=2, cex=2, side = 2, line = 1,outer = FALSE)
image(xzslice,zlim=xzcol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
image(xzrelerr,zlim=c(-1,1),xlab='',ylab='',axes=FALSE,col=errcolor)
image(xzerr,zlim=xzcol,xlab='',ylab='',axes=FALSE,col=errcolor)

#plot fullxzslice
image(truefullxzslice,zlim=xzcol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
mtext("XZ plane - full", font=2, cex=2, side = 2, line = 1,outer = FALSE)
image(fullxzslice,zlim=xzcol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
image(fullxzrelerr,zlim=c(-1,1),xlab='',ylab='',axes=FALSE,col=errcolor)
image(fullxzerr,zlim=xzcol,xlab='',ylab='',axes=FALSE,col=errcolor)

#plot yzslice
image(trueyzslice,zlim=yzcol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
mtext("YZ plane", font=2, cex=2, side = 2, line = 1,outer = FALSE)
image(yzslice,zlim=yzcol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
image(yzrelerr,zlim=c(-1,1),xlab='',ylab='',axes=FALSE,col=errcolor)
image(yzerr,zlim=yzcol,xlab='',ylab='',axes=FALSE,col=errcolor)

#plot fullyzslice
image(truefullyzslice,zlim=yzcol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
mtext("XZ plane - full", font=2, cex=2, side = 2, line = 1,outer = FALSE)
image(fullyzslice,zlim=yzcol,xlab='',ylab='',axes=FALSE,col=coulombcolor)
image(fullyzrelerr,zlim=c(-1,1),xlab='',ylab='',axes=FALSE,col=errcolor)
image(fullyzerr,zlim=yzcol,xlab='',ylab='',axes=FALSE,col=errcolor)


