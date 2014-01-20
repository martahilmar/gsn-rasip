# GSN - RASIP fer

## 1. Installing GSN

Requirements:

- Java VM
- Apache ANT build tool

These can be installed on ubuntu with:

    sudo apt-get install openjdk-7-jre ant


Clone this repo with:

    git clone https://github.com/janza/gsn-rasip

or download at: https://github.com/janza/gsn-rasip/archive/master.zip

## 2. Configure database

Database can be configured at conf/gsn.xml. There are number of examples that can be used.
For development it's ok to use h2 database which can be enabled by uncommenting first org.h2.Driver entry.
That will use in-memory database that gets destroyed after GSN process is killed.

## 3. How to start GSN

Once GSN is downloaded/cloned, it can be started by running at root gsn directory:

    ant gsn

## 4. Loading your first virtual sensor

To load a virtual sensor into GSN,
you need to load its description file (.xml)
into the root of virtual-sensors directory.
This directory contains a set of samples that can be used.

You can start by loading the MultiFormatTemperatureHandler virtual sensor (virtual-sensors/samples/multiFormatSample.xml).
This virtual sensor generates random values without the need of an actual physical sensor.

## 5. Default admin user

    username: Admin
    password: changeit
