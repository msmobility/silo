[![Build Status](https://travis-ci.org/msmobility/silo.svg?branch=master)](https://travis-ci.org/msmobility/silo)

# siloCode
SILO Model Java Code



Load into eclipse

A possibility is

(1) check out git as normal (e.g. on command line)

(2) in eclipse: import --> Maven --> existing maven projects --> browse to location of local git repository --> import

(3) missing hdf5 library: in eclipse:
    right click on "silo"
    --> Build Path
    --> Configure Build Path
    --> Source
    --> silo/src/main/java
    --> Native library location
    --> Edit ...
    --> add "silo/lib/macosx64" or whatever you need for your operating system

-----

For a full step-by-step guide of the overall setup, pelase see "SILO-MATSim_Installation.docx" in this directory

