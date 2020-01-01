library(readr)
library(dplyr)

#sets working directory to script location, only works in rstudio
#setwd(dirname(rstudioapi::getActiveDocumentContext()$path))
getwd()

# read zone system file
zones <- read.csv("C:/Users/Nico/tum/fabilut/svn/fabilut/muc/sm-ssa50/input/zoneSystem.csv", sep=",")
zonesMuc <- zones %>% filter(zones$Name == "München-Landeshauptstadt")

zonesAug <- zones %>% filter(zones$Landkreis_ID == 9761)
zonesRos <- zones %>% filter(zones$Landkreis_ID == 9187)
zonesLands <- zones %>% filter(zones$Landkreis_ID == 9261)
zonesIng <- zones %>% filter(zones$Landkreis_ID == 9161)

# read relocation for specific year
year <- 2029

hhbase <- read_csv("C:/Users/Nico/tum/fabilut/svn/fabilut/muc/sm-b50/scenOutput/sm-b50/microData/hh_2030.csv")
ddbase <- read_csv("C:/Users/Nico/tum/fabilut/svn/fabilut/muc/sm-b50/scenOutput/sm-b50/microData/dd_2030.csv")
ppbase <- read_csv("C:/Users/Nico/tum/fabilut/svn/fabilut/muc/sm-b50/scenOutput/sm-b50/microData/pp_2030.csv")

hhscen <- read_csv("C:/Users/Nico/tum/fabilut/svn/fabilut/muc/sm-ssa50/scenOutput/sm-ssla50/microData/hh_2030.csv")
ddscen <- read_csv("C:/Users/Nico/tum/fabilut/svn/fabilut/muc/sm-ssa50/scenOutput/sm-ssla50/microData/dd_2030.csv")
ppscen <- read_csv("C:/Users/Nico/tum/fabilut/svn/fabilut/muc/sm-ssa50/scenOutput/sm-ssla50/microData/pp_2030.csv")

hhddbase <- hhbase %>% inner_join(ddbase, by=c("id"="hhID"))
hhddbase$zoneclass <- ifelse(hhddbase$zone %in% zonesMuc$Zone, 'Munich',ifelse(hhddbase$zone %in% zonesAug$Zone,'Augsburg', ifelse(hhddbase$zone %in% zonesIng$Zone, 'Ingolstadt', ifelse( hhddbase$zone %in% zonesLands$Zone, 'Landshut', ifelse(hhddbase$zone %in% zonesRos$Zone, 'Rosenheim', 'Rural')))))

hhddscen <- hhscen %>% inner_join(ddscen, by=c("id"="hhID"))
hhddscen$zoneclass <- ifelse(hhddscen$zone %in% zonesMuc$Zone, 'Munich',ifelse(hhddscen$zone %in% zonesAug$Zone,'Augsburg', ifelse(hhddscen$zone %in% zonesIng$Zone, 'Ingolstadt', ifelse( hhddscen$zone %in% zonesLands$Zone, 'Landshut', ifelse(hhddscen$zone %in% zonesRos$Zone, 'Rosenheim', 'Rural')))))


oinkbase <- ppbase  %>% filter(workplace> 0)
oinkbase <- oinkbase %>% group_by(hhid) %>% summarise(n())

oinkscen <- ppscen  %>% filter(workplace> 0)
oinkscen <- oinkscen %>% group_by(hhid) %>% summarise(n())

hhJoinbase <- hhbase %>% left_join(oinkbase, by=(c("id"="hhid")))
hhJoinbase$`n()`<- replace_na(hhJoinbase$`n()`, 0)

hhJoinscen <- hhscen %>% left_join(oinkscen, by=(c("id"="hhid")))
hhJoinscen$`n()`<- replace_na(hhJoinscen$`n()`, 0)



hhddbase <- hhddbase %>% inner_join(hhJoinbase, by = (c("id"="id")))

hhddbase <- hhddbase %>%
rename(
workers ='n()'
)

hhddscen <- hhddscen %>% inner_join(hhJoinscen, by = (c("id"="id")))

hhddscen <- hhddscen %>%
rename(
workers ='n()'
)


frequWorkersCarsbase <- hhddbase %>%  filter(hhddbase$autos.x > 0 & hhddbase$workers > 0) %>% group_by(zoneclass) %>% summarise(n())
frequWorkersTransitbase <- hhddbase %>%  filter(hhddbase$autos.x == 0 & hhddbase$workers > 0) %>% group_by(zoneclass) %>% summarise(n())


frequWorkersCarsscen <- hhddscen %>%  filter(hhddscen$autos.x > 0 & hhddscen$workers > 0) %>% group_by(zoneclass) %>% summarise(n())
frequWorkersTransitscen <- hhddscen %>%  filter(hhddscen$autos.x == 0 & hhddscen$workers > 0) %>% group_by(zoneclass) %>% summarise(n())

library(gridExtra)


pie1base <- ggplot(frequWorkersCarsbase, aes(x = "", y=frequWorkersCarsbase$`n()`, fill = factor(zoneclass))) +
    geom_bar(width = 1, stat = "identity") +
    theme(axis.line = element_blank(),
    plot.title = element_text(hjust=0.5)) +
    labs(fill="class",
    x=NULL,
    y=NULL,
    title="Workers with cars",
    caption="")

pie1base <- pie1base + coord_polar(theta = "y", start=0)
pie1base

pie2base <- ggplot(frequWorkersTransitbase, aes(x = "", y=frequWorkersTransitbase$`n()`, fill = factor(zoneclass))) +
    geom_bar(width = 1, stat = "identity") +
    theme(axis.line = element_blank(),
    plot.title = element_text(hjust=0.5)) +
    labs(fill="class",
    x=NULL,
    y=NULL,
    title="Workers without cars",
    caption="")

pie2base <-pie2base + coord_polar(theta = "y", start=0)
pie2base



pie1scen <- ggplot(frequWorkersCarsscen, aes(x = "", y=frequWorkersCarsscen$`n()`, fill = factor(zoneclass))) +
    geom_bar(width = 1, stat = "identity") +
    theme(axis.line = element_blank(),
    plot.title = element_text(hjust=0.5)) +
    labs(fill="class",
    x=NULL,
    y=NULL,
    title="Workers with cars",
    caption="")

pie1scen <- pie1scen + coord_polar(theta = "y", start=0)
pie1scen

pie2scen <- ggplot(frequWorkersTransitscen, aes(x = "", y=frequWorkersTransitscen$`n()`, fill = factor(zoneclass))) +
    geom_bar(width = 1, stat = "identity") +
    theme(axis.line = element_blank(),
    plot.title = element_text(hjust=0.5)) +
    labs(fill="class",
    x=NULL,
    y=NULL,
    title="Workers without cars",
    caption="")

pie2scen <-pie2scen + coord_polar(theta = "y", start=0)
pie2scen

grid.arrange(pie1base,pie2base, nrow = 1)
