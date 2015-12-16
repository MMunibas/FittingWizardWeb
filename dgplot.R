
csv=read.csv('Fitting-Values.csv')

png("dgplot.png",width=2400,height=1200,pointsize=22)

par(lwd=4,xpd=TRUE,mar=c(5.1, 5.1, 2.1, 25),
    family = 'Helvetica', font=2
    )

plot(csv$delta_G.Exp,
     csv$delta_G.Calc,
     col=csv$Col,
     pch=c(0:20,0:7),
     xlab=expression(paste(Delta,"G Exp (kcal/mol)")),
     ylab=expression(paste(Delta,"G Calc (kcal/mol)")),
     xlim=c(-10,0),
     ylim=c(-10,0),
     # font=2,
     cex=3,
     cex.axis=2,
     cex.lab=2,
     lwd=6,
#      font.axis=2,
#      font.lab=2
     )


lines(-10:0,-10:0,lty=2)

par(font=1)

legend(x=0.5,y=0.9,
  legend = csv$Compound.Name,
  col=csv$Col,
  pch=c(0:20,0:7),
  cex=1.5
)

dev.off()
