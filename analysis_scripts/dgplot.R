library(Metrics)

csv=read.csv('dg.csv')

png("dgplot.png",width=1600,height=1200,pointsize=22)

par(lwd=4,xpd=TRUE,mar=c(2.2, 5.2, 0.1,18),
    family = 'Helvetica', font=2, pty='s'
    )

# a few useful statistical estimates for illustating quality of fit
# unit (kcal/mol)^2
mse <- mse(csv$delta_G.Exp,csv$delta_G.Calc)
# unit (kcal/mol)
rmse <- rmse(csv$delta_G.Exp,csv$delta_G.Calc)
#correlation coeficient
cor <- cor(csv$delta_G.Exp,csv$delta_G.Calc)

plot(csv$delta_G.Exp,
     csv$delta_G.Calc,
     col=csv$Col,
     pch=c(0:nrow(csv)),
     xlab=expression(Delta*italic(G)[Exp](kcal/mol)),
     ylab=expression(Delta*italic(G)[Calc](kcal/mol)),
     xlim=c(-10,0),
     ylim=c(-10,0),
     cex=3,
     cex.axis=2,
     cex.lab=2,
     asp=1,
     lwd=6
     )

rmse=round(rmse,2)
cor=round(cor,2)

text(x=-7.25,y=0,cex=1.75,
     substitute(
       paste('RMSE (kcal/mol)=',rms,' ',R^2,'=',corel),
       list(corel=cor,rms=rmse)
     )
)

lines(-10:0,-10:0,lty=2)

par(font=1)

legend("right",inset=-0.45,
  legend = csv$Compound.Name,
  col=csv$Col,
  pch=c(0:nrow(csv)),
  cex=1.5,
  bty='n'
)

dev.off()
