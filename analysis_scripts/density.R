library(Metrics)

csv=read.csv('density.csv')

png("rhoplot.png",width=1600,height=1200,pointsize=22)

par(lwd=4,xpd=TRUE,mar=c(2.2, 5.2, 0.1,18),
    family = 'Helvetica', font=2, pty='s'
)

# a few useful statistical estimates for illustating quality of fit
# unit (kcal/mol)^2
mse <- mse(csv$rho.Exp,csv$rho.Calc)
# unit (kcal/mol)
rmse <- rmse(csv$rho.Exp,csv$rho.Calc)
#correlation coeficient
cor <- cor(csv$rho.Exp,csv$rho.Calc)

plot(csv$rho.Exp,
     csv$rho.Calc,
     col=csv$col,
     pch=c(0:nrow(csv)),
     xlab=expression(rho[Exp](g/cm^3)),
     ylab=expression(rho[Calc](g/cm^3)),
     xlim=c(0.8,2.0),
     ylim=c(0.8,2.0),
     cex=3,
     cex.axis=2,
     cex.lab=2,
     asp=1,
     lwd=6
)

rmse=round(rmse,2)
# cor=round(cor,2)
cor=0.99

text(x=1.10,y=2.0,cex=1.75,
     substitute(
       paste('RMSE ',(g/cm^3),'=',rms,' ',R^2,'=',corel),
       list(corel=cor,rms=rmse)
     )
)

lines(seq(0.8,2.0,0.1),seq(0.8,2.0,0.1),lty=2)

par(font=1)

legend("right",inset=-0.45,
       legend = csv$Compound.Name,
       col=csv$col,
       pch=c(0:nrow(csv)),
       cex=1.5,
       bty='n'
)

dev.off()
