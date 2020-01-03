library(readr)
library(dplyr)

#sets working directory to script location, only works in rstudio
#setwd(dirname(rstudioapi::getActiveDocumentContext()$path))
getwd()

# read relocation for specific year
year <- 0
scenarioName <- 'base'
# scenarioName <- 'unrestricted-dev'
# scenarioName <- 'base_less-pop'

zones <- read.csv("../scenarios/",scenarioName,"/input/zoneSystem.csv", sep=",")

relocation <- read_csv(paste("../scenarios/",scenarioName,"/scenOutput/",scenarioName,"/siloResults/relocation/relocation",year,".csv", sep=""))

# number of relocations to zone 13
nrow(relocation[relocation$newZone == 13,])

# filter relocations by workers and number of cars
relocationWithcarAndWorkers <- relocation %>% filter(relocation$autos > 0 & relocation$workers > 0)
relocationWithoutcarAndWorkers <- relocation %>% filter(relocation$autos ==0 & relocation$workers > 0)
relocationWithcarNoWorkers <- relocation %>% filter(relocation$autos > 0 & relocation$workers == 0)
relocationWithoutcarNoWorkers <- relocation %>% filter(relocation$autos == 0 & relocation$workers == 0)

# filter by license (not very smart right now as everyone is assigned a license once hes a worer in current population creation)
relocationDriversLicense <- relocation %>% filter(relocation$licenses < relocation$workers)
relocationDriversLicense2 <- relocation %>% filter(relocation$licenses == relocation$workers & relocation$workers > 0)

# 3 rows, 2 columns of plots
par(mfrow=c(3,2))

# plot relative frequencies of target zones of relocation
abc <- relocationWithcarAndWorkers$newZone
hist(abc, breaks = c(0.5:25.5), freq = FALSE, xaxt="n", ylim=c(0,0.8), xlab="Zone id", ylab = "Share of Relocations", main ="Distribution of Relocations by Zone - Workers with car")
xtick<-seq(1, 25, by=1)
text(x=xtick,  par("usr")[3], labels = xtick, srt = 0, pos = 1, xpd = TRUE)

hist(relocationWithoutcarAndWorkers$newZone, breaks = c(0.5:25.5), freq = FALSE,xaxt="n", ylim=c(0,0.8), xlab="Zone id", ylab = "Share of Relocations", main ="Distribution of Relocations by Zone - Workers without car")
xtick<-seq(1, 25, by=1)
text(x=xtick,  par("usr")[3], labels = xtick, srt = 0, pos = 1, xpd = TRUE)

hist(relocationWithcarNoWorkers$newZone, breaks = c(0.5:25.5), freq = FALSE,xaxt="n", ylim=c(0,0.8), xlab="Zone id", ylab = "Share of Relocations", main ="Distribution of Relocations by Zone - Non-workers with car")
xtick<-seq(1, 25, by=1)
text(x=xtick,  par("usr")[3], labels = xtick, srt = 0, pos = 1, xpd = TRUE)

hist(relocationWithoutcarNoWorkers$newZone, breaks = c(0.5:25.5), freq = FALSE,xaxt="n",ylim=c(0,0.8), xlab="Zone id", ylab = "Share of Relocations", main ="Distribution of Relocations by Zone - Non-workers without car")
xtick<-seq(1, 25, by=1)
text(x=xtick,  par("usr")[3], labels = xtick, srt = 0, pos = 1, xpd = TRUE)



# 3 rows, 2 columns of plots
par(mfrow=c(3,2))

# plot absolute numbers of target zones of relocation
hist(relocationWithcarAndWorkers$newZone, breaks = c(0.5:25.5), xaxt="n", ylim=c(0,100), xlab="Zone id", ylab = "Relocations", main ="Distribution of Relocations by Zone - Workers with car")
xtick<-seq(1, 25, by=1)
text(x=xtick,  par("usr")[3], labels = xtick, srt = 0, pos = 1, xpd = TRUE)

hist(relocationWithoutcarAndWorkers$newZone, breaks = c(0.5:25.5), xaxt="n", ylim=c(0,100), xlab="Zone id", ylab = "Relocations", main ="Distribution of Relocations by Zone - Workers without car")
xtick<-seq(1, 25, by=1)
text(x=xtick,  par("usr")[3], labels = xtick, srt = 0, pos = 1, xpd = TRUE)

hist(relocationWithcarNoWorkers$newZone, breaks = c(0.5:25.5), xaxt="n", ylim=c(0,100), xlab="Zone id", ylab = "Relocations", main ="Distribution of Relocations by Zone - Non-workers with car")
xtick<-seq(1, 25, by=1)
text(x=xtick,  par("usr")[3], labels = xtick, srt = 0, pos = 1, xpd = TRUE)

hist(relocationWithoutcarNoWorkers$newZone, breaks = c(0.5:25.5), xaxt="n",ylim=c(0,100), xlab="Zone id", ylab = "Relocations", main ="Distribution of Relocations by Zone - Non-workers without car")
xtick<-seq(1, 25, by=1)
text(x=xtick,  par("usr")[3], labels = xtick, srt = 0, pos = 1, xpd = TRUE)

# 3D plot
library(plot3D)
par(mfrow=c(2,2))

x = c(1,2,3,4,5)
# y axis in reverse order
y = c(5,4,3,2,1)

relocationWithcarAndWorkersCounts <- hist(relocationWithcarAndWorkers$newZone, breaks = c(0.5:25.5))$counts
relocationWithoutcarAndWorkersCounts <- hist(relocationWithoutcarAndWorkers$newZone, breaks = c(0.5:25.5))$counts
relocationWithcarNoWorkersCounts <- hist(relocationWithcarNoWorkers$newZone, breaks = c(0.5:25.5))$counts
relocationWithoutcarNoWorkersCounts <- hist(relocationWithoutcarNoWorkers$newZone, breaks = c(0.5:25.5))$counts

# Convert Z values into a matrix.
relocationWithcarAndWorkersMatrix = matrix(relocationWithcarAndWorkersCounts, nrow=5, ncol=5, byrow=TRUE)
relocationWithoutcarAndWorkersMatrix = matrix(relocationWithoutcarAndWorkersCounts, nrow=5, ncol=5, byrow=TRUE)
relocationWithcarNoWorkersMatrix = matrix(relocationWithcarNoWorkersCounts, nrow=5, ncol=5, byrow=TRUE)
relocationWithoutcarNoWorkersMatrix = matrix(relocationWithoutcarNoWorkersCounts, nrow=5, ncol=5, byrow=TRUE)

# Following line is to check of a particular zone sits at the corect position in the plot
# z[1] <- 100

# box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
hist3D(x,y,relocationWithcarAndWorkersMatrix, zlim=c(0,100), clim=c(0,100), theta=20, phi=20, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.6, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="Distribution of Relocations by Zone - Workers with car")
hist3D(x,y,relocationWithoutcarAndWorkersMatrix, zlim=c(0,100), clim=c(0,100), theta=20, phi=20, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.6, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="Distribution of Relocations by Zone - Workers without car")
hist3D(x,y,relocationWithcarNoWorkersMatrix, zlim=c(0,100), clim=c(0,100), theta=20, phi=20, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.6, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="Distribution of Relocations by Zone - Non-workers with car")
hist3D(x,y,relocationWithoutcarNoWorkersMatrix, zlim=c(0,100), clim=c(0,100), theta=20, phi=20, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.6, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="Distribution of Relocations by Zone - Non-workers without car")


######################################################################

library(ggplot2)
library(gridExtra)

relocation$class <- ifelse(relocation$autos > 0 & relocation$workers> 0 , "Workers with cars", ifelse(relocation$workers>0 & relocation$autos == 0, "Workers without cars", "Non-Workers"))

# Histogram on a Continuous (Numeric) Variable
g1 <- ggplot(relocation, aes(relocation$newZone)) + scale_fill_brewer(palette = "Spectral")


g1 <- g1 + geom_histogram(aes(fill=class),
bins=25,
col="black",
size=.1) +  # change binwidth
    labs(title="Histogram relocation destinations. Grouped by household characteristics",
    subtitle="") + xlab('Zone id') + ylab('Number of relocations') + scale_x_continuous(breaks=seq(1,25,by=1))
g1

g2 <- ggplot(relocation, aes(relocation$newZone))
g2 <- g2 + geom_density(aes(fill=factor(class)), alpha=0.5) + theme(text = element_text(size=10))+
    labs(title="",
    subtitle="",
    x="Zone id",
    y = "Density",
    fill="Household type")  + scale_x_continuous(breaks=seq(1,25,by=1))
g2


######################################################################

#read dwellings for specific year
year <- 10
dd <- read_csv(paste("../scenarios/base/scenOutput/",scenarioName,"/microData/dd_",year,".csv", sep =""))

# merge dwellings file with relocations to get exact coordinates of target dwellings
joined <- relocation %>% inner_join(dd, by = c("newDd"="id"))
# calculate distance to the coordinate system origin
joined$distance <- sqrt(joined$coordX * joined$coordX + joined$coordY * joined$coordY)
# filter to central zone and to households with at least 1 worker
joined <- joined %>% filter(joined$newZone == 13 & joined$workers > 0)

# filter by car ownership
joinedNoCar <- joined %>% filter(joined$autos == 0)
joinedWithCar <- joined %>% filter(joined$autos > 0)

#mean distances to center, i.e. transit stop and car access point
mean(joinedNoCar$distance)
mean(joinedWithCar$distance)

