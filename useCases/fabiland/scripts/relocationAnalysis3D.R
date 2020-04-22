library(readr)
library(dplyr)
library(plot3D)

getwd()

# scenarioName <- 'cap75_2-l_x'
# scenarioName <- 'cap75_2-l_u'
# scenarioName <- 'cap75_1-l_nes'
# scenarioName <- 'cap75_1-l_lower-u'
scenarioName <- 'cap75_1-l_upper-u'
# scenarioName <- 'cap75_1-l_ring'

zones <- read.csv(paste("../scenario/input/zoneSystem.csv", sep=""))

startYear <- 0
endYear <- 9

reloc <- read_csv(paste("../scenario/scenOutput/",scenarioName,"/siloResults/relocation/relocation",startYear,".csv.gz", sep=""))

relocCarWorkMerged <- head(reloc,1)
relocNoCarWorkMerged <- head(reloc,1)
relocCarNoWorkMerged <- head(reloc,1)
relocNoCarNoWorkMerged <- head(reloc,1)


x = c(1,2,3,4,5)
# y axis in reverse order
y = c(5,4,3,2,1)

for(year in startYear:endYear) {
    reloc <- read_csv(paste("../scenario/scenOutput/",scenarioName,"/siloResults/relocation/relocation",year,".csv.gz", sep=""))
    
    relocCarWork <- reloc %>% filter(reloc$autos > 0 & reloc$workers > 0)
    relocNoCarWork <- reloc %>% filter(reloc$autos == 0 & reloc$workers > 0)
    relocCarNoWork <- reloc %>% filter(reloc$autos > 0 & reloc$workers == 0)
    relocNoCarNoWork <- reloc %>% filter(reloc$autos == 0 & reloc$workers == 0)
    
    relocCarWorkCounts <- hist(relocCarWork$newZone, breaks = c(0.5:25.5))$counts
    relocNoCarWorkCounts <- hist(relocNoCarWork$newZone, breaks = c(0.5:25.5))$counts
    relocCarNoWorkCounts <- hist(relocCarNoWork$newZone, breaks = c(0.5:25.5))$counts
    relocNoCarNoWorkCounts <- hist(relocNoCarNoWork$newZone, breaks = c(0.5:25.5))$counts
    
    # Convert Z values into a matrix.
    relocCarWorkMatrix = matrix(relocCarWorkCounts, nrow=5, ncol=5, byrow=TRUE)
    relocNoCarWorkMatrix = matrix(relocNoCarWorkCounts, nrow=5, ncol=5, byrow=TRUE)
    relocCarNoWorkMatrix = matrix(relocCarNoWorkCounts, nrow=5, ncol=5, byrow=TRUE)
    relocNoCarNoWorkMatrix = matrix(relocNoCarNoWorkCounts, nrow=5, ncol=5, byrow=TRUE)
    
    # Plot relocations of current year
    png(paste(scenarioName,"_reloc_",year,".png", sep=""),
        width = 800, height = 640)
    par(mfrow=c(2,2))
    
    # box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
    zExtent = c(0,200)
    hist3D(x,y,relocCarWorkMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
           ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="a) Workers with car")
    hist3D(x,y,relocNoCarWorkMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
           ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="b) Workers without car")
    hist3D(x,y,relocCarNoWorkMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
           ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="c) Non-workers with car")
    hist3D(x,y,relocNoCarNoWorkMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
           ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="d) Non-workers without car")
    
    dev.off()
    # End plot current year
    
    # Merge relocations of all included years
    relocCarWorkMerged <- merge(relocCarWorkMerged, relocCarWork, all = TRUE)
    relocNoCarWorkMerged <- merge(relocNoCarWorkMerged, relocNoCarWork, all = TRUE)
    relocCarNoWorkMerged <- merge(relocCarNoWorkMerged, relocCarNoWork, all = TRUE)
    relocNoCarNoWorkMerged <- merge(relocNoCarNoWorkMerged, relocNoCarNoWork, all = TRUE)
}

# Plot relocations in sum of all selected years
png(paste(scenarioName,"_reloc.png", sep=""),
    width = 800, height = 640)
par(mfrow=c(2,2))

relocCarWorkMergedCounts <- hist(relocCarWorkMerged$newZone, breaks = c(0.5:25.5))$counts
relocNoCarWorkMergedCounts <- hist(relocNoCarWorkMerged$newZone, breaks = c(0.5:25.5))$counts
relocCarNoWorkMergedCounts <- hist(relocCarNoWorkMerged$newZone, breaks = c(0.5:25.5))$counts
relocNoCarNoWorkMergedCounts <- hist(relocNoCarNoWorkMerged$newZone, breaks = c(0.5:25.5))$counts

# Convert Z values into a matrix.
relocCarWorkMergedMatrix = matrix(relocCarWorkMergedCounts, nrow=5, ncol=5, byrow=TRUE)
relocNoCarWorkMergedMatrix = matrix(relocNoCarWorkMergedCounts, nrow=5, ncol=5, byrow=TRUE)
relocCarNoWorkMergedMatrix = matrix(relocCarNoWorkMergedCounts, nrow=5, ncol=5, byrow=TRUE)
relocNoCarNoWorkMergedMatrix = matrix(relocNoCarNoWorkMergedCounts, nrow=5, ncol=5, byrow=TRUE)

# box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
zExtent = c(0,2000)
hist3D(x,y,relocCarWorkMergedMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="a) Workers with car")
hist3D(x,y,relocNoCarWorkMergedMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="b) Workers without car")
hist3D(x,y,relocCarNoWorkMergedMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="c) Non-workers with car")
hist3D(x,y,relocNoCarNoWorkMergedMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5, 
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="d) Non-workers without car")
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

png("popJobs.png",
    width = 800, height = 640)
par(mfrow=c(2,2))
zExtent = c(0,1000)
hist3D(x,y,popMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5,
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="a) Population")
hist3D(x,y,jobsMatrix, zlim=zExtent, clim=zExtent, theta=25, phi=15, axes=FALSE,label=FALSE, nticks=5,
       ticktype="detailed", space=0.7, lighting=TRUE, light="diffuse", shade=0.5, bty = "n", main ="b) Jobs")
dev.off()
