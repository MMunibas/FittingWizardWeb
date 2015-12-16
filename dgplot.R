


png("dgplot.png",width=2400,height=1200,pointsize=22)

par(cex=1.20,lwd=4,xpd=TRUE,mar=c(5, 5, 2, 18))

plot(csv$delta_G.Exp,
     csv$delta_G.Calc,
     col=csv$Col,
     pch=c(0:20,0:7),
     xlab=expression(paste(Delta,"G Exp (kcal/mol)")),
     ylab=expression(paste(Delta,"G Calc (kcal/mol)")),
     xlim=c(-10,0),
     ylim=c(-10,0)
     )

lines(-10:0,-10:0,lty=2)

legend(x=0.5,y=0.1,
  legend = csv$Compound.Name,
  col=csv$Col,
  pch=c(0:20,0:7)
)

dev.off()
