#!/bin/bash

MCVERSION=1.6.1
FORGEVERSION=768
SRCVERSION=1.6.x

# ===== WalkingDead ===================
#MCCLIENTDIR="$HOME/minecraft-$MCVERSION-forge-$FORGEVERSION/mods"
#MCSERVERDIR="$HOME/mc-server-forge-$FORGEVERSION/mods"
#JARDIR="$HOME/projects/walkingdead-src-$SRCVERSION"
#JAR="walkingdead-$MCVERSION-forge-$FORGEVERSION.jar"

#cp -f "$JARDIR/$JAR" $MCCLIENTDIR
#cp -f "$JARDIR/$JAR" $MCSERVERDIR

# ===== Reptiles =======================
#MCCLIENTDIR="$HOME/minecraft-$MCVERSION-forge-$FORGEVERSION/mods"
#MCSERVERDIR="$HOME/mc-server-forge-$FORGEVERSION/mods"
#JARDIR="$HOME/projects/reptiles-src-$SRCVERSION"
#JAR="reptiles-$MCVERSION-forge-$FORGEVERSION.jar"

#cp -f "$JARDIR/$JAR" $MCCLIENTDIR
#cp -f "$JARDIR/$JAR" $MCSERVERDIR

# ===== Parachute =======================
MCCLIENTDIR="$HOME/minecraft/mods"
#MCSERVERDIR="$HOME/mc-server-forge-$FORGEVERSION/mods"
JARDIR="$HOME/projects/parachute-src-$SRCVERSION"
JAR="parachute-$MCVERSION-forge-$FORGEVERSION.jar"

cp -f "$JARDIR/$JAR" $MCCLIENTDIR
#cp -f "$JARDIR/$JAR" $MCSERVERDIR

# ===== MinerHelmet =====================
#MCCLIENTDIR="$HOME/minecraft-$MCVERSION-forge-$FORGEVERSION/coremods"
##MCSERVERDIR="$HOME/mc-server-forge-$FORGEVERSION/coremods"
#JARDIR="$HOME/projects/minerhelmet-src-$SRCVERSION"
#JAR="minerhelmet-$MCVERSION-forge-$FORGEVERSION.jar"

#cp -f "$JARDIR/$JAR" $MCCLIENTDIR
#cp -f "$JARDIR/$JAR" $MCSERVERDIR
