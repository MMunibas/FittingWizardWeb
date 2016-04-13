library(Metrics)

csv=read.csv('dg.csv')

png("dgplot.png",width=1600,height=1200,pointsize=22)

par(lwd=4,xpd=TRUE,mar=c(5.2, 5.2, 2.1, 22),
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
     pch=c(0:20,0:7),
#      xlab=expression(paste(Delta,"G[Exp] (kcal/mol)")),
#      ylab=expression(paste(Delta,"G[Calc] (kcal/mol)")),
     xlab=expression(Delta*italic(G)[Exp](kcal/mol)),
     ylab=expression(Delta*italic(G)[Calc](kcal/mol)),
     xlim=c(-10,0),
     ylim=c(-10,0),
     # font=2,
     cex=3,
     cex.axis=2,
     cex.lab=2,
     asp=1,
     lwd=6
#      font.axis=2,
#      font.lab=2
     )

rmse=round(rmse,4)
cor=round(cor,4)

mytext = paste("RMSE (kcal/mol) = ",rmse,
               "\n",
               expression('R^2')," = ",cor,
               sep="")
text(x=-7.5,y=-0.5,mytext,cex=1.5)

lines(-10:0,-10:0,lty=2)

par(font=1)

legend(x=0.5,y=-0.5,
  legend = csv$Compound.Name,
  col=csv$Col,
  pch=c(0:20,0:7),
  cex=1.5,
  bty='n'
)

dev.off()
