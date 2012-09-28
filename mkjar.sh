#!/bin/bash

echo "== Creating jar =="

DATE=`date +%Y%m%d`

MCMODDATA="
[\n
{\n
  \"modid\": \"ParachuteMod\",\n
  \"name\": \"Parachute Mod\",\n
  \"description\": \"Adds a Parachute to the game. Jump off cliffs! Base Jumping!! Soar to new heights!\",\n
  \"version\": \"$DATE\",\n
  \"mcversion\": \"1.3.2\",\n
  \"url\": \"http://www.minecraftforum.net/topic/585469-132modloader-crackedeggs-mods-reptiles-parachute-updated-08272012/\",\n
  \"updateUrl\": \"http://www.minecraftforum.net/topic/585469-132modloader-crackedeggs-mods-reptiles-parachute-updated-08272012/\",\n
  \"authors\": [\n
    \"crackedEgg\"\n
  ],\n
  \"credits\": \"Authored by crackedEgg\",\n
  \"logoFile\": \"/textures/chuteLogo.png\",\n
  \"screenshots\": [],\n
  \"parent\": \"\",\n
  \"dependencies\": []\n
}\n
]"

echo "> copying files"
#MODINFO="../../mcmod.info"

REOBF="reobf/minecraft/"
cd $REOBF

PDIR="$HOME/projects/parachute-src-1.3.2"

rm -f $PDIR/parachute/common/*.class
rm -f $PDIR/parachute/client/*.class

cp -R parachute/ $PDIR
echo -e $MCMODDATA > $PDIR/mcmod.info

echo "> making mod jar file"

cd $PDIR

JAR="parachute-1.3.2-fml-251.jar"
rm -f $JAR

jar -cf $JAR parachute/ mcmod.info textures/
#mv $JAR ../

echo " - Mod build complete"

