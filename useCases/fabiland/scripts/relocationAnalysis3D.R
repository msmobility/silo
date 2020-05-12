library(readr)
library(dplyr)
library(plot3D)

workingDirectory <- '/Users/dominik/Workspace/git/silo/useCases/fabiland/scenario/scenOutput/'

# scenarioName <- '9r_ae_cap30_2-l_x_smc'
# scenarioName <- '9r_ae_cap30_1-l_ring_smc'
# scenarioName <- '9r_ae_cap30_1-l_nes_smc'

# scenarioName <- '25r_ae_cap30_2-l_x_smc'
# scenarioName <- '25r_ae_cap30_1-l_ring_smc'
# scenarioName <- '25r_ae_cap30_1-l_nes_smc'

scenarioName <- '25r_ae_unr-dev_cap30_2-l_x_smc'

# scenarioName <- '25r_ae_cap30_2-l_x_smc_pt'
# scenarioName <- '25r_ae_cap30_2-l_x_smc_pt_a02'
# scenarioName <- '25r_ae_cap30_2-l_x_smc_pt_a1'
# scenarioName <- '25r_ae_cap30_1-l_ring_smc_pt_a1'
# scenarioName <- '25r_ae_cap30_2-l_x_smc_pt_b08'
# scenarioName <- '25r_ae_rs10_cap30_2-l_x_smc'
# scenarioName <- '25r_ae_rs87_cap30_2-l_x_smc'

# scenarioName <- '25r_ae_cap30_1-l_ring_smc_pt'
# scenarioName <- '25r_ae_cap30_1-l_nes_smc_pt'

# scenarioName <- '1r_ae_cap30_2-l_x_smc'
# scenarioName <- '1r_ae_cap30_1-l_ring_smc'
# scenarioName <- '1r_ae_cap30_1-l_nes_smc'

# scenarioName <- '1r_ae_cap30_2-l_x_smc_dd200'

setwd(paste(workingDirectory,"/",scenarioName, sep=""))
dir.create("graphics")

# zones <- read.csv(paste("../../input/zoneSystem_9-reg.csv", sep=""))
zones <- read.csv(paste("../../input/zoneSystem_25-reg.csv", sep=""))

scalingFactor <- 1.0

startYear <- 0
# endYear <- 4
endYear <- 9

reloc <- read_csv(paste("siloResults/relocation/relocation",startYear,".csv.gz", sep=""))

x = c(1,2,3,4,5)
y = c(1,2,3,4,5)

for(year in startYear:endYear) {
    reloc <- read_csv(paste("siloResults/relocation/relocation",year,".csv.gz", sep=""))
    
    # Classify relocations
    reloc1Car1Work <- reloc %>% filter(reloc$autos == 1 & reloc$workers == 1)
    reloc2Car1Work <- reloc %>% filter(reloc$autos > 1 & reloc$workers == 1)
    reloc0Car1Work <- reloc %>% filter(reloc$autos == 0 & reloc$workers == 1)
    reloc1Car2Work <- reloc %>% filter(reloc$autos == 1 & reloc$workers > 1)
    reloc2Car2Work <- reloc %>% filter(reloc$autos > 1 & reloc$workers > 1)
    reloc0Car2Work <- reloc %>% filter(reloc$autos == 0 & reloc$workers > 1)
    reloc1Car0Work <- reloc %>% filter(reloc$autos == 1 & reloc$workers == 0)
    reloc2Car0Work <- reloc %>% filter(reloc$autos > 1 & reloc$workers == 0)
    reloc0Car0Work <- reloc %>% filter(reloc$autos == 0 & reloc$workers == 0)
    
    #################################################################################
    # Relocations to destination
    reloc1Car1WorkDestCounts <- hist(reloc1Car1Work$newZone, breaks = c(0.5:25.5))$counts
    reloc2Car1WorkDestCounts <- hist(reloc2Car1Work$newZone, breaks = c(0.5:25.5))$counts
    reloc0Car1WorkDestCounts <- hist(reloc0Car1Work$newZone, breaks = c(0.5:25.5))$counts
    reloc1Car2WorkDestCounts <- hist(reloc1Car2Work$newZone, breaks = c(0.5:25.5))$counts
    reloc2Car2WorkDestCounts <- hist(reloc2Car2Work$newZone, breaks = c(0.5:25.5))$counts
    reloc0Car2WorkDestCounts <- hist(reloc0Car2Work$newZone, breaks = c(0.5:25.5))$counts
    reloc1Car0WorkDestCounts <- hist(reloc1Car0Work$newZone, breaks = c(0.5:25.5))$counts
    reloc2Car0WorkDestCounts <- hist(reloc2Car0Work$newZone, breaks = c(0.5:25.5))$counts
    reloc0Car0WorkDestCounts <- hist(reloc0Car0Work$newZone, breaks = c(0.5:25.5))$counts
    
    # Convert Z values into a matrix.
    reloc1Car1WorkDestMatrix = matrix(reloc1Car1WorkDestCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc2Car1WorkDestMatrix = matrix(reloc2Car1WorkDestCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc0Car1WorkDestMatrix = matrix(reloc0Car1WorkDestCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc1Car2WorkDestMatrix = matrix(reloc1Car2WorkDestCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc2Car2WorkDestMatrix = matrix(reloc2Car2WorkDestCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc0Car2WorkDestMatrix = matrix(reloc0Car2WorkDestCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc1Car0WorkDestMatrix = matrix(reloc1Car0WorkDestCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc2Car0WorkDestMatrix = matrix(reloc2Car0WorkDestCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc0Car0WorkDestMatrix = matrix(reloc0Car0WorkDestCounts, nrow=5, ncol=5, byrow=TRUE)
    
    # Plot relocations of current year
    png(paste("graphics/",scenarioName,"_reloc-dest_y",year,".png", sep=""), width = 800, height = 640)
    par(mfrow=c(3,3))
    
    # box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
    cLim = c(0,20*scalingFactor)
    zLim = c(0,20*scalingFactor)
    hist3D(x,y,reloc1Car1WorkDestMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="a) Households with 1 worker and 1 car.")
    hist3D(x,y,reloc2Car1WorkDestMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="b) Households with 1 worker and 2 cars.")
    hist3D(x,y,reloc0Car1WorkDestMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="c) Households with 1 worker and no car.")
    hist3D(x,y,reloc1Car2WorkDestMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="d) Households with 2 workers and 1 car.")
    hist3D(x,y,reloc2Car2WorkDestMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="e) Households with 2 workers and 2 cars.")
    hist3D(x,y,reloc0Car2WorkDestMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="f) Households with 2 workers and no cars.")
    hist3D(x,y,reloc1Car0WorkDestMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="g) Households with no workers and 1 car.")
    hist3D(x,y,reloc2Car0WorkDestMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="h) Households with no workers and 2 cars.")
    hist3D(x,y,reloc0Car0WorkDestMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="i) Households with no workers and no cars.")
    dev.off()
    # End plot current year
    
    #################################################################################
    # Relocations from origin
    reloc1Car1WorkOrigCounts <- hist(reloc1Car1Work$oldZone, breaks = c(0.5:25.5))$counts
    reloc2Car1WorkOrigCounts <- hist(reloc2Car1Work$oldZone, breaks = c(0.5:25.5))$counts
    reloc0Car1WorkOrigCounts <- hist(reloc0Car1Work$oldZone, breaks = c(0.5:25.5))$counts
    reloc1Car2WorkOrigCounts <- hist(reloc1Car2Work$oldZone, breaks = c(0.5:25.5))$counts
    reloc2Car2WorkOrigCounts <- hist(reloc2Car2Work$oldZone, breaks = c(0.5:25.5))$counts
    reloc0Car2WorkOrigCounts <- hist(reloc0Car2Work$oldZone, breaks = c(0.5:25.5))$counts
    reloc1Car0WorkOrigCounts <- hist(reloc1Car0Work$oldZone, breaks = c(0.5:25.5))$counts
    reloc2Car0WorkOrigCounts <- hist(reloc2Car0Work$oldZone, breaks = c(0.5:25.5))$counts
    reloc0Car0WorkOrigCounts <- hist(reloc0Car0Work$oldZone, breaks = c(0.5:25.5))$counts
    
    # Convert Z values into a matrix.
    reloc1Car1WorkOrigMatrix = matrix(reloc1Car1WorkOrigCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc2Car1WorkOrigMatrix = matrix(reloc2Car1WorkOrigCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc0Car1WorkOrigMatrix = matrix(reloc0Car1WorkOrigCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc1Car2WorkOrigMatrix = matrix(reloc1Car2WorkOrigCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc2Car2WorkOrigMatrix = matrix(reloc2Car2WorkOrigCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc0Car2WorkOrigMatrix = matrix(reloc0Car2WorkOrigCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc1Car0WorkOrigMatrix = matrix(reloc1Car0WorkOrigCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc2Car0WorkOrigMatrix = matrix(reloc2Car0WorkOrigCounts, nrow=5, ncol=5, byrow=TRUE)
    reloc0Car0WorkOrigMatrix = matrix(reloc0Car0WorkOrigCounts, nrow=5, ncol=5, byrow=TRUE)
    
    # Plot relocations of current year
    png(paste("graphics/",scenarioName,"_reloc-orig_y",year,".png", sep=""), width = 800, height = 640)
    par(mfrow=c(3,3))
    
    # box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
    cLim = c(0,20*scalingFactor)
    zLim = c(0,20*scalingFactor)
    hist3D(x,y,reloc1Car1WorkOrigMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="a) Households with 1 worker and 1 car.")
    hist3D(x,y,reloc2Car1WorkOrigMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="b) Households with 1 worker and 2 cars.")
    hist3D(x,y,reloc0Car1WorkOrigMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="c) Households with 1 worker and no car.")
    hist3D(x,y,reloc1Car2WorkOrigMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="d) Households with 2 workers and 1 car.")
    hist3D(x,y,reloc2Car2WorkOrigMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="e) Households with 2 workers and 2 cars.")
    hist3D(x,y,reloc0Car2WorkOrigMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="f) Households with 2 workers and no cars.")
    hist3D(x,y,reloc1Car0WorkOrigMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="g) Households with no workers and 1 car.")
    hist3D(x,y,reloc2Car0WorkOrigMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="h) Households with no workers and 2 cars.")
    hist3D(x,y,reloc0Car0WorkOrigMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="i) Households with no workers and no cars.")
    dev.off()
    # End plot current year
    
    #################################################################################
    # Relocation difference
    reloc1Car1WorkDiffMatrix <- reloc1Car1WorkDestMatrix - reloc1Car1WorkOrigMatrix
    reloc2Car1WorkDiffMatrix <- reloc2Car1WorkDestMatrix - reloc2Car1WorkOrigMatrix
    reloc0Car1WorkDiffMatrix <- reloc0Car1WorkDestMatrix - reloc0Car1WorkOrigMatrix
    reloc1Car2WorkDiffMatrix <- reloc1Car2WorkDestMatrix - reloc1Car2WorkOrigMatrix
    reloc2Car2WorkDiffMatrix <- reloc2Car2WorkDestMatrix - reloc2Car2WorkOrigMatrix
    reloc0Car2WorkDiffMatrix <- reloc0Car2WorkDestMatrix - reloc0Car2WorkOrigMatrix
    reloc1Car0WorkDiffMatrix <- reloc1Car0WorkDestMatrix - reloc1Car0WorkOrigMatrix
    reloc2Car0WorkDiffMatrix <- reloc2Car0WorkDestMatrix - reloc2Car0WorkOrigMatrix
    reloc0Car0WorkDiffMatrix <- reloc0Car0WorkDestMatrix - reloc0Car0WorkOrigMatrix
    
    # Plot relocations of current year
    png(paste("graphics/",scenarioName,"_reloc-diff_y",year,".png", sep=""), width = 800, height = 640)
    par(mfrow=c(3,3))
    
    # box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
    cLim = c(-20*scalingFactor,20*scalingFactor)
    zLim = c(0,20*scalingFactor)
    hist3D(x,y,reloc1Car1WorkDiffMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="a) Households with 1 worker and 1 car.")
    hist3D(x,y,reloc2Car1WorkDiffMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="b) Households with 1 worker and 2 cars.")
    hist3D(x,y,reloc0Car1WorkDiffMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="c) Households with 1 worker and no car.")
    hist3D(x,y,reloc1Car2WorkDiffMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="d) Households with 2 workers and 1 car.")
    hist3D(x,y,reloc2Car2WorkDiffMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="e) Households with 2 workers and 2 cars.")
    hist3D(x,y,reloc0Car2WorkDiffMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="f) Households with 2 workers and no cars.")
    hist3D(x,y,reloc1Car0WorkDiffMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="g) Households with no workers and 1 car.")
    hist3D(x,y,reloc2Car0WorkDiffMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="h) Households with no workers and 2 cars.")
    hist3D(x,y,reloc0Car0WorkDiffMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="i) Households with no workers and no cars.")
    dev.off()
    # End plot current year
    
    #################################################################################
    # Merge relocations of all included years
    if (year == startYear) {
        reloc1Car1WorkMerged <- reloc1Car1Work
        reloc2Car1WorkMerged <- reloc2Car1Work
        reloc0Car1WorkMerged <- reloc0Car1Work
        reloc1Car2WorkMerged <- reloc1Car2Work
        reloc2Car2WorkMerged <- reloc2Car2Work
        reloc0Car2WorkMerged <- reloc0Car2Work
        reloc1Car0WorkMerged <- reloc1Car0Work
        reloc2Car0WorkMerged <- reloc2Car0Work
        reloc0Car0WorkMerged <- reloc0Car0Work
    } else {
        reloc1Car1WorkMerged <- merge(reloc1Car1WorkMerged, reloc1Car1Work, all = TRUE)
        reloc2Car1WorkMerged <- merge(reloc2Car1WorkMerged, reloc2Car1Work, all = TRUE)
        reloc0Car1WorkMerged <- merge(reloc0Car1WorkMerged, reloc0Car1Work, all = TRUE)
        reloc1Car2WorkMerged <- merge(reloc1Car2WorkMerged, reloc1Car2Work, all = TRUE)
        reloc2Car2WorkMerged <- merge(reloc2Car2WorkMerged, reloc2Car2Work, all = TRUE)
        reloc0Car2WorkMerged <- merge(reloc0Car2WorkMerged, reloc0Car2Work, all = TRUE)
        reloc1Car0WorkMerged <- merge(reloc1Car0WorkMerged, reloc1Car0Work, all = TRUE)
        reloc2Car0WorkMerged <- merge(reloc2Car0WorkMerged, reloc2Car0Work, all = TRUE)
        reloc0Car0WorkMerged <- merge(reloc0Car0WorkMerged, reloc0Car0Work, all = TRUE)
    }
}

#################################################################################
# Plot relocations in sum of all selected years

#################################################################################
# Relocations from destination
png(paste("graphics/",scenarioName,"_reloc-dest.png", sep=""), width = 800, height = 640)
par(mfrow=c(3,3))

reloc1Car1WorkDestMergedCounts <- hist(reloc1Car1WorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc2Car1WorkDestMergedCounts <- hist(reloc2Car1WorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc0Car1WorkDestMergedCounts <- hist(reloc0Car1WorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc1Car2WorkDestMergedCounts <- hist(reloc1Car2WorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc2Car2WorkDestMergedCounts <- hist(reloc2Car2WorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc0Car2WorkDestMergedCounts <- hist(reloc0Car2WorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc1Car0WorkDestMergedCounts <- hist(reloc1Car0WorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc2Car0WorkDestMergedCounts <- hist(reloc2Car0WorkMerged$newZone, breaks = c(0.5:25.5))$counts
reloc0Car0WorkDestMergedCounts <- hist(reloc0Car0WorkMerged$newZone, breaks = c(0.5:25.5))$counts

# Convert Z values into a matrix.
reloc1Car1WorkDestMergedMatrix = matrix(reloc1Car1WorkDestMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc2Car1WorkDestMergedMatrix = matrix(reloc2Car1WorkDestMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc0Car1WorkDestMergedMatrix = matrix(reloc0Car1WorkDestMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc1Car2WorkDestMergedMatrix = matrix(reloc1Car2WorkDestMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc2Car2WorkDestMergedMatrix = matrix(reloc2Car2WorkDestMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc0Car2WorkDestMergedMatrix = matrix(reloc0Car2WorkDestMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc1Car0WorkDestMergedMatrix = matrix(reloc1Car0WorkDestMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc2Car0WorkDestMergedMatrix = matrix(reloc2Car0WorkDestMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc0Car0WorkDestMergedMatrix = matrix(reloc0Car0WorkDestMergedCounts, nrow=5, ncol=5, byrow=TRUE)

# box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
cLim = c(0,200*scalingFactor)
zLim = c(0,200*scalingFactor)
hist3D(x,y,reloc1Car1WorkDestMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="a) Households with 1 worker and 1 car.")
hist3D(x,y,reloc2Car1WorkDestMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="b) Households with 1 worker and 2 cars.")
hist3D(x,y,reloc0Car1WorkDestMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="c) Households with 1 worker and no car.")
hist3D(x,y,reloc1Car2WorkDestMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="d) Households with 2 workers and 1 car.")
hist3D(x,y,reloc2Car2WorkDestMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="e) Households with 2 workers and 2 cars.")
hist3D(x,y,reloc0Car2WorkDestMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="f) Households with 2 workers and no cars.")
hist3D(x,y,reloc1Car0WorkDestMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="g) Households with no workers and 1 car.")
hist3D(x,y,reloc2Car0WorkDestMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="h) Households with no workers and 2 cars.")
hist3D(x,y,reloc0Car0WorkDestMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="i) Households with no workers and no cars.")
dev.off()

#################################################################################
# Relocations from origin
png(paste("graphics/",scenarioName,"_reloc-orig.png", sep=""), width = 800, height = 640)
par(mfrow=c(3,3))

reloc1Car1WorkOrigMergedCounts <- hist(reloc1Car1WorkMerged$oldZone, breaks = c(0.5:25.5))$counts
reloc2Car1WorkOrigMergedCounts <- hist(reloc2Car1WorkMerged$oldZone, breaks = c(0.5:25.5))$counts
reloc0Car1WorkOrigMergedCounts <- hist(reloc0Car1WorkMerged$oldZone, breaks = c(0.5:25.5))$counts
reloc1Car2WorkOrigMergedCounts <- hist(reloc1Car2WorkMerged$oldZone, breaks = c(0.5:25.5))$counts
reloc2Car2WorkOrigMergedCounts <- hist(reloc2Car2WorkMerged$oldZone, breaks = c(0.5:25.5))$counts
reloc0Car2WorkOrigMergedCounts <- hist(reloc0Car2WorkMerged$oldZone, breaks = c(0.5:25.5))$counts
reloc1Car0WorkOrigMergedCounts <- hist(reloc1Car0WorkMerged$oldZone, breaks = c(0.5:25.5))$counts
reloc2Car0WorkOrigMergedCounts <- hist(reloc2Car0WorkMerged$oldZone, breaks = c(0.5:25.5))$counts
reloc0Car0WorkOrigMergedCounts <- hist(reloc0Car0WorkMerged$oldZone, breaks = c(0.5:25.5))$counts

# Convert Z values into a matrix.
reloc1Car1WorkOrigMergedMatrix = matrix(reloc1Car1WorkOrigMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc2Car1WorkOrigMergedMatrix = matrix(reloc2Car1WorkOrigMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc0Car1WorkOrigMergedMatrix = matrix(reloc0Car1WorkOrigMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc1Car2WorkOrigMergedMatrix = matrix(reloc1Car2WorkOrigMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc2Car2WorkOrigMergedMatrix = matrix(reloc2Car2WorkOrigMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc0Car2WorkOrigMergedMatrix = matrix(reloc0Car2WorkOrigMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc1Car0WorkOrigMergedMatrix = matrix(reloc1Car0WorkOrigMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc2Car0WorkOrigMergedMatrix = matrix(reloc2Car0WorkOrigMergedCounts, nrow=5, ncol=5, byrow=TRUE)
reloc0Car0WorkOrigMergedMatrix = matrix(reloc0Car0WorkOrigMergedCounts, nrow=5, ncol=5, byrow=TRUE)

# box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
cLim = c(0,200*scalingFactor)
zLim = c(0,200*scalingFactor)
hist3D(x,y,reloc1Car1WorkOrigMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="a) Households with 1 worker and 1 car.")
hist3D(x,y,reloc2Car1WorkOrigMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="b) Households with 1 worker and 2 cars.")
hist3D(x,y,reloc0Car1WorkOrigMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="c) Households with 1 worker and no car.")
hist3D(x,y,reloc1Car2WorkOrigMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="d) Households with 2 workers and 1 car.")
hist3D(x,y,reloc2Car2WorkOrigMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="e) Households with 2 workers and 2 cars.")
hist3D(x,y,reloc0Car2WorkOrigMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="f) Households with 2 workers and no cars.")
hist3D(x,y,reloc1Car0WorkOrigMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="g) Households with no workers and 1 car.")
hist3D(x,y,reloc2Car0WorkOrigMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="h) Households with no workers and 2 cars.")
hist3D(x,y,reloc0Car0WorkOrigMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="i) Households with no workers and no cars.")
dev.off()

#################################################################################
# Relocation difference
reloc1Car1WorkDiffMergedMatrix <- reloc1Car1WorkDestMergedMatrix - reloc1Car1WorkOrigMergedMatrix
reloc2Car1WorkDiffMergedMatrix <- reloc2Car1WorkDestMergedMatrix - reloc2Car1WorkOrigMergedMatrix
reloc0Car1WorkDiffMergedMatrix <- reloc0Car1WorkDestMergedMatrix - reloc0Car1WorkOrigMergedMatrix
reloc1Car2WorkDiffMergedMatrix <- reloc1Car2WorkDestMergedMatrix - reloc1Car2WorkOrigMergedMatrix
reloc2Car2WorkDiffMergedMatrix <- reloc2Car2WorkDestMergedMatrix - reloc2Car2WorkOrigMergedMatrix
reloc0Car2WorkDiffMergedMatrix <- reloc0Car2WorkDestMergedMatrix - reloc0Car2WorkOrigMergedMatrix
reloc1Car0WorkDiffMergedMatrix <- reloc1Car0WorkDestMergedMatrix - reloc1Car0WorkOrigMergedMatrix
reloc2Car0WorkDiffMergedMatrix <- reloc2Car0WorkDestMergedMatrix - reloc2Car0WorkOrigMergedMatrix
reloc0Car0WorkDiffMergedMatrix <- reloc0Car0WorkDestMergedMatrix - reloc0Car0WorkOrigMergedMatrix

# Plot relocations of current year
png(paste("graphics/",scenarioName,"_reloc-diff.png", sep=""), width = 800, height = 640)
par(mfrow=c(3,3))

# box types: “b”, “b2”, “f”, “g”, “bl”, “bl2”, “u”, “n”
cLim = c(-100*scalingFactor,100*scalingFactor)
zLim = c(0,100*scalingFactor)
hist3D(x,y,reloc1Car1WorkDiffMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="a) Households with 1 worker and 1 car.")
hist3D(x,y,reloc2Car1WorkDiffMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="b) Households with 1 worker and 2 cars.")
hist3D(x,y,reloc0Car1WorkDiffMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="c) Households with 1 worker and no car.")
hist3D(x,y,reloc1Car2WorkDiffMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="d) Households with 2 workers and 1 car.")
hist3D(x,y,reloc2Car2WorkDiffMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="e) Households with 2 workers and 2 cars.")
hist3D(x,y,reloc0Car2WorkDiffMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="f) Households with 2 workers and no cars.")
hist3D(x,y,reloc1Car0WorkDiffMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="g) Households with no workers and 1 car.")
hist3D(x,y,reloc2Car0WorkDiffMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="h) Households with no workers and 2 cars.")
hist3D(x,y,reloc0Car0WorkDiffMergedMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="i) Households with no workers and no cars.")
dev.off()
# End plot current year

################################################################################
# Plot households and jobs

households = c(300, 10, 10, 10, 10,
               10, 10, 300, 10, 10,
               10, 300, 1020, 10, 300,
               10, 10, 10, 10, 10,
               10, 10, 300, 10, 300)

jobs = c(300, 10, 10, 10, 300,
         10, 10, 10, 10, 10,
         10, 300, 1020, 10, 10,
         10, 10, 300, 10, 10,
         300, 10, 10, 10, 300)

householdsMatrix = matrix(households, nrow=5, ncol=5, byrow=TRUE)
jobsMatrix = matrix(jobs, nrow=5, ncol=5, byrow=TRUE)

png("graphics/householdsJobs.png", width = 800, height = 320)
par(mfrow=c(1,2))
cLim = c(0,1200*scalingFactor)
zLim = c(0,1200*scalingFactor)
hist3D(x,y,householdsMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="a) Households")
hist3D(x,y,jobsMatrix, zlim=zLim, clim=cLim, theta=115, phi=25, axes=FALSE, space=0.7, shade=0.5, bty="n", main ="b) Jobs")
dev.off()