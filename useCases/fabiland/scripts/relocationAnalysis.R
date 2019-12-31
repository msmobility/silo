library(readr)
library(dplyr)

#sets working directory to script location, only works in rstudio
setwd(dirname(rstudioapi::getActiveDocumentContext()$path))
getwd()

# read zone system file
zones <- read.csv("../input/base/input/zoneSystem.csv", sep=",")

# read relocation for specific year
year <- 0
relocation <- read_csv(paste("../input/base/scenOutput/base/siloResults/relocation/relocation",year,".csv", sep=""))

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
hist(relocationWithcarAndWorkers$newZone, breaks = 25, freq = FALSE)
hist(relocationWithoutcarAndWorkers$newZone, breaks = 25, freq = FALSE)
hist(relocationWithcarNoWorkers$newZone, breaks = 25, freq = FALSE)
hist(relocationWithoutcarNoWorkers$newZone, breaks = 25, freq = FALSE)

hist(relocationDriversLicense$newZone, breaks = 25, freq = FALSE)
hist(relocationDriversLicense2$newZone, breaks = 25, freq = FALSE)

######################################################################

#read dwellings for specific year
year <- 10
dd <- read_csv(paste("../input/base/scenOutput/base/microData/dd_",year,".csv", sep =""))

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
