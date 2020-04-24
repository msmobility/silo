library(readr)
library(dplyr)
library(plot3D)

# workingDirectory <- '/Users/dominik/Workspace/git/silo/useCases/fabiland/scripts/'
workingDirectory <- '/Users/dominik/Workspace/git/silo/useCases/fabiland/scenario/scenOutput/'
setwd(workingDirectory)
getwd()

# scenarioName <- 'cap75_2-l_x'
# scenarioName <- 'cap75_2-l_u'
# scenarioName <- 'cap75_1-l_nes_unr-dev'
# scenarioName <- 'cap75_1-l_lower-u'
# scenarioName <- 'cap75_1-l_upper-u'
# scenarioName <- 'cap75_1-l_ring'
# scenarioName <- 'vac300_ae_cap75_1-l_nes_smc'
scenarioName <- 'vac300_1-reg_ae_cap75_1-l_nes_smc'
# scenarioName <- 'vac300_ae_cap75_2-l_x_smc'

setwd(paste(workingDirectory,"/",scenarioName, sep=""))
getwd()
dir.create("graphics")

zones <- read.csv(paste("../../input/zoneSystem.csv", sep=""))

startYear <- 0
endYear <- 9

reloc <- read_csv(paste("siloResults/relocation/relocation",startYear,".csv.gz", sep=""))

reloc1CarWorkMerged <- head(reloc,1)
reloc2CarWorkMerged <- head(reloc,1)
relocNoCarWorkMerged <- head(reloc,1)
reloc1CarNoWorkMerged <- head(reloc,1)
reloc2CarNoWorkMerged <- head(reloc,1)
relocNoCarNoWorkMerged <- head(reloc,1)

x = c(1,2,3,4,5)
# y axis in reverse order
y = c(5,4,3,2,1)

for(year in startYear:endYear) {
    reloc <- read_csv(paste("siloResults/relocation/relocation",year,".csv.gz", sep=""))
    
    reloc1CarWork <- reloc %>% filter(reloc$autos == 1 & reloc$workers > 0)
    reloc2CarWork <- reloc %>% filter(reloc$autos > 1 & reloc$workers > 0)
    relocNoCarWork <- reloc %>% filter(reloc$autos == 0 & reloc$workers > 0)
    reloc1CarNoWork <- reloc %>% filter(reloc$autos == 1 & reloc$workers == 0)
    reloc2CarNoWork <- reloc %>% filter(reloc$autos > 1 & reloc$workers == 0)
    relocNoCarNoWork <- reloc %>% filter(reloc$autos == 0 & reloc$workers == 0)
    
    reloc1CarWorkCounts <- hist(reloc1CarWork$newZone, breaks = c(0.5:25.5))$counts
    reloc2CarWorkCounts <- hist(reloc2CarWork$newZone, breaks = c(0.5:25.5))$counts
    relocNoCarWorkCounts <- hist(relocNoCarWork$newZone, breaks = c(0.5:25.5))$counts
    reloc1CarNoWorkCounts <- hist(reloc1CarNoWork$newZone, breaks = c(0.5:25.5))$counts
    reloc2CarNoWorkCounts <- hist(reloc2CarNoWork$newZone, breaks = c(0.5:25.5))$counts
    relocNoCarNoWorkCounts <- hist(relocNoCarNoWork$newZone, breaks = c(0.5:25.5))$counts
    
    # Convert Z values into a matrix.
    reloc1CarWorkMatrix = matrix(reloc1CarWorkCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc2CarWorkMatrix = matrix(reloc2CarWorkCounts, nrow=5, ncol=5, byrow=TRUE)
    relocNoCarWorkMatrix = matrix(relocNoCarWorkCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc1CarNoWorkMatrix = matrix(reloc1CarNoWorkCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc2CarNoWorkMatrix = matrix(reloc2CarNoWorkCounts, nrow=5, ncol=5, byrow=TRUE)
    relocNoCarNoWorkMatrix = matrix(relocNoCarNoWorkCounts, nrow=5, ncol=5, byrow=TRUE)
    
    # Plot relocations of current year
    png(paste("graphics/",scenarioName,"_reloc_",year,".png", sep=""), width = 800, height = 640)
    par(mfrow=c(2,3))
    
    # box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
    zExtent = c(0,200)
    hist3D(x,y,reloc1CarWorkMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
           ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="a) Workers with one car")
    hist3D(x,y,reloc2CarWorkMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
           ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="b) Workers with at least 2 cars")
    hist3D(x,y,relocNoCarWorkMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
           ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="c) Workers without car")
    hist3D(x,y,reloc1CarNoWorkMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
           ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="d) Non-workers with one ar")
    hist3D(x,y,reloc2CarNoWorkMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
           ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="e) Non-workers with at least two cars")
    hist3D(x,y,relocNoCarNoWorkMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
           ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="f) Non-workers without car")
    
    dev.off()
    # End plot current year
    
    # Merge relocations of all included years
    reloc1CarWorkMerged <- merge(reloc1CarWorkMerged, reloc1CarWork, all = TRUE)
    reloc2CarWorkMerged <- merge(reloc2CarWorkMerged, reloc2CarWork, all = TRUE)
    relocNoCarWorkMerged <- merge(relocNoCarWorkMerged, relocNoCarWork, all = TRUE)
    reloc1CarNoWorkMerged <- merge(reloc1CarNoWorkMerged, reloc1CarNoWork, all = TRUE)
    reloc2CarNoWorkMerged <- merge(reloc2CarNoWorkMerged, reloc2CarNoWork, all = TRUE)
    relocNoCarNoWorkMerged <- merge(relocNoCarNoWorkMerged, relocNoCarNoWork, all = TRUE)
}

# Plot relocations in sum of all selected years

png(paste("graphics/",scenarioName,"_reloc.png", sep=""), width = 800, height = 640)
par(mfrow=c(2,3))

reloc1CarWorkMergedCounts <- hist(reloc1CarWorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc2CarWorkMergedCounts <- hist(reloc2CarWorkMerged$newZone, breaks = c(0.5:25.5))$counts
relocNoCarWorkMergedCounts <- hist(relocNoCarWorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc1CarNoWorkMergedCounts <- hist(reloc1CarNoWorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc2CarNoWorkMergedCounts <- hist(reloc2CarNoWorkMerged$newZone, breaks = c(0.5:25.5))$counts
relocNoCarNoWorkMergedCounts <- hist(relocNoCarNoWorkMerged$newZone, breaks = c(0.5:25.5))$counts

# Convert Z values into a matrix.
reloc1CarWorkMergedMatrix = matrix(reloc1CarWorkMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc2CarWorkMergedMatrix = matrix(reloc2CarWorkMergedCounts, nrow=5, ncol=5, byrow=TRUE)
relocNoCarWorkMergedMatrix = matrix(relocNoCarWorkMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc1CarNoWorkMergedMatrix = matrix(reloc1CarNoWorkMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc2CarNoWorkMergedMatrix = matrix(reloc2CarNoWorkMergedCounts, nrow=5, ncol=5, byrow=TRUE)
relocNoCarNoWorkMergedMatrix = matrix(relocNoCarNoWorkMergedCounts, nrow=5, ncol=5, byrow=TRUE)

# box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
zExtent = c(0,2000)
hist3D(x,y,reloc1CarWorkMergedMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="a) Workers with one car")
hist3D(x,y,reloc2CarWorkMergedMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="b) Workers with at least two cars")
hist3D(x,y,relocNoCarWorkMergedMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="c) Workers without car")
hist3D(x,y,reloc1CarNoWorkMergedMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="d) Non-workers with one car")
hist3D(x,y,reloc2CarNoWorkMergedMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="e) Non-workers with at least two cars")
hist3D(x,y,relocNoCarNoWorkMergedMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="f) Non-workers without car")
dev.off()

################################################################################

# Plot population and jobs
pop = c(250, 0, 0, 0, 250,
    0, 0, 500, 0, 0,
    0, 500, 3000, 500, 0,
    0, 0, 0, 0, 0,
    500, 0, 500, 0, 0)

jobs = c(200, 100, 100, 100, 500,
    100, 100, 400, 100, 100,
    100, 400, 2000, 400, 100,
    100, 100, 400, 0, 0,
    200, 100, 100, 0, 200)

popMatrix = matrix(pop, nrow=5, ncol=5, byrow=TRUE)
jobsMatrix = matrix(jobs, nrow=5, ncol=5, byrow=TRUE)

png("graphics/popJobs.png", width = 800, height = 640)
par(mfrow=c(2,2))
zExtent = c(0,1000)
hist3D(x,y,popMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5,
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="a) Population")
hist3D(x,y,jobsMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5,
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="b) Jobs")
dev.off()

