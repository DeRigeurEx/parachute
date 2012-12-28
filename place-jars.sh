#!/bin/bash

MCVER=1.4.6
FMLVER=471
MCCLIENTDIR="$HOME/minecraft-$MCVER-forge-$FMLVER/mods"
MCSERVERDIR="$HOME/mc-server-forge-$FMLVER/mods"
JARDIR="$HOME/projects/parachute-src-1.4.x"
JAR="parachute-$MCVER-forge-$FMLVER.jar"

cp -f "$JARDIR/$JAR" $MCCLIENTDIR
cp -f "$JARDIR/$JAR" $MCSERVERDIR
