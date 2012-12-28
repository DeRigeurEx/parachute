#!/bin/bash

echo "== Creating jar =="

KEYPASS=ds1b8zs
STOREPASS=ds1b8zs

MCVER=1.4.6
FMLVER=471

DATE=`date +%Y%m%d`

MCMODDATA="
[\n
{\n
  \"modid\": \"ParachuteMod\",\n
  \"name\": \"Parachute Mod\",\n
  \"description\": \"Adds a Parachute to the game. Jump off cliffs! Base Jumping!! Soar to new heights!\",\n
  \"version\": \"$DATE\",\n
  \"mcversion\": \"$MCVER\",\n
  \"url\": \"http://www.minecraftforum.net/topic/585469-132fmlsplanmp-crackedeggs-mods-reptiles-parachute-updated-09272012/\",\n
  \"updateUrl\": \"\",\n
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

REOBF="reobf/minecraft/"
cd $REOBF

PDIR="$HOME/projects/parachute-src-1.4.x"

rm -f $PDIR/parachute/common/*.class
rm -f $PDIR/parachute/client/*.class

cp -R parachute/ $PDIR
echo -e $MCMODDATA > $PDIR/mcmod.info

echo "> making mod jar file"

cd $PDIR

JAR="parachute-$MCVER-forge-$FMLVER.jar"

echo -e "Main-Class: parachute.common.Parachute\nClass-Path: $JAR\n" > $PDIR/manifest.txt

rm -f $JAR
jar -cfm $JAR manifest.txt parachute/ mcmod.info textures/

echo "> signing $JAR"
jarsigner -storetype pkcs12 -keystore $HOME/.keystore -keypass $KEYPASS -storepass $STOREPASS $JAR cracked

echo " - Mod build complete - `date "+%H:%M:%S"`"

