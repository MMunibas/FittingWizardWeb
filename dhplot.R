library(Metrics)

csv=read.csv('dh.csv')

png("dhplot.png",width=1700,height=1200,pointsize=22)

par(lwd=4,xpd=TRUE,mar=c(2.2, 5.2, 0.1,22),
    family = 'Helvetica', font=2, pty='s'
)

# a few useful statistical estimates for illustating quality of fit
# unit (kcal/mol)^2
mse <- mse(csv$delta_H.Exp,csv$delta_H.Calc)
# unit (kcal/mol)
rmse <- rmse(csv$delta_H.Exp,csv$delta_H.Calc)
#correlation coeficient
cor <- cor(csv$delta_H.Exp,csv$delta_H.Calc)

plot(csv$delta_H.Exp,
     csv$delta_H.Calc,
     col=csv$col,
     pch=c(0:nrow(csv)),
     xlab=expression(Delta*italic(H)[Exp](kcal/mol)),
     ylab=expression(Delta*italic(H)[Calc](kcal/mol)),
     xlim=c(8,16),
     ylim=c(8,16),
     cex=3,
     cex.axis=2,
     cex.lab=2,
     asp=1,
     lwd=6
)

rmse=round(rmse,4)
cor=round(cor,4)

text(x=10.5,y=15.75,cex=1.5,
     substitute(
       paste('RMSE (kcal/mol)=',rms,' ',R^2,'=',corel),
       list(corel=cor,rms=rmse)
     )
)

lines(8:16,8:16,lty=2)

par(font=1)

legend("right",inset=-0.58,
  legend = csv$Compound.Name,
  col=csv$col,
  pch=c(0:nrow(csv)),
  cex=1.5,
  bty='n'
)

dev.off()
