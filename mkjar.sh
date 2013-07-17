#!/bin/bash

echo "== Creating jars =="

KEYPASS=ds1b8zs
STOREPASS=ds1b8zs

MCVERSION=1.6.1
FORGEVERSION=768
SRCVERSION=1.6.x

DATE=`date +%Y%m%d`

PWD=`pwd`

REOBF="$PWD/reobf/minecraft/"

# ======= WalkingDead ==================================

#MCMODDATA="
#[\n
#{\n
#  \"modid\": \"WalkingDeadMod\",\n
#  \"name\": \"WalkingDead Mod\",\n
#  \"description\": \"Walking Dead mod adds zombies in the day time! Walkers they are called.\",\n
#  \"version\": \"$DATE\",\n
#  \"mcversion\": \"$MCVERSION\",\n
#  \"url\": \"\",\n
#  \"updateUrl\": \"\",\n
#  \"authors\": [\"crackedEgg\"],\n
#  \"credits\": \"Authored by crackedEgg\",\n
#  \"logoFile\": \"/logo/deadLogo.png\",\n
#  \"screenshots\": [],\n
#  \"parent\": \"\",\n
#  \"dependencies\": []\n
#}\n
#]"


#echo "> copying files"

#cd $REOBF

#RDIR="$HOME/projects/walkingdead-src-$SRCVERSION"

#rm -f $RDIR/walkingdead/common/*.class
#rm -f $RDIR/walkingdead/client/*.class

#cp -R walkingdead/ $RDIR
#echo -e $MCMODDATA > $RDIR/mcmod.info

#echo "> making mod jar file"

#cd $RDIR

#JAR="walkingdead-$MCVERSION-forge-$FORGEVERSION.jar"

#echo -e "Main-Class: walkingdead.common.WalkingDead\nClass-Path: $JAR\n" > $RDIR/manifest.txt

#rm -f $JAR
#jar -cfm $JAR manifest.txt walkingdead/ mcmod.info skins/ logo/ README

#echo "> signing $JAR"
#jarsigner -storetype pkcs12 -keystore $HOME/.keystore -keypass $KEYPASS -storepass $STOREPASS $JAR cracked 

#echo " - $JAR build complete - `date "+%H:%M:%S"`"

# ======== Reptiles ========================================

#cd $PWD

#MCMODDATA="
#[\n
#{\n
#  \"modid\": \"ReptileMod\",\n
#  \"name\": \"Reptile Mod\",\n
#  \"description\": \"Reptile mod adds monitor lizards, turtles, iguanas, chameleons, and crocodiles. Komodo Dragons! Man-eating Crocodiles! And cute little turtles.\",\n
#  \"version\": \"$DATE\",\n
#  \"mcversion\": \"$MCVERSION\",\n
#  \"url\": \"\",\n
#  \"updateUrl\": \"\",\n
#  \"authors\": [\n\"crackedEgg\"\n],\n
#  \"credits\": \"Authored by crackedEgg\",\n
#  \"logoFile\": \"/logo/reptileLogo.png\",\n
#  \"screenshots\": [],\n
#  \"parent\": \"\",\n
#  \"dependencies\": []\n
#}\n
#]"

#echo "> copying files"

#cd $REOBF

#RDIR="$HOME/projects/reptiles-src-$SRCVERSION"

#rm -f $RDIR/reptiles/common/*.class
#rm -f $RDIR/reptiles/client/*.class

#cp -R reptiles/ $RDIR
#echo -e $MCMODDATA > $RDIR/mcmod.info

#echo "> making mod jar file"

#cd $RDIR

#JAR="reptiles-$MCVERSION-forge-$FORGEVERSION.jar"

#echo -e "Main-Class: reptiles.common.Reptiles\nClass-Path: $JAR\n" > $RDIR/manifest.txt

#rm -f $JAR
#jar -cfm $JAR manifest.txt reptiles/ mcmod.info mob/ sound/ logo/

#echo "> signing $JAR"
#jarsigner -storetype pkcs12 -keystore $HOME/.keystore -keypass $KEYPASS -storepass $STOREPASS $JAR cracked

#echo " - $JAR build complete - `date "+%H:%M:%S"`"

# ======= Parachute ===========================================
cd $PWD

MCMODDATA="
[\n
{\n
  \"modid\": \"ParachuteMod\",\n
  \"name\": \"Parachute Mod\",\n
  \"description\": \"Adds a Parachute to the game. Jump off cliffs! Base Jumping!! Soar to new heights!\",\n
  \"version\": \"$DATE\",\n
  \"mcversion\": \"$MCVERSION\",\n
  \"url\": \"\",\n
  \"updateUrl\": \"\",\n
  \"authors\": [\n
    \"crackedEgg\"\n
  ],\n
  \"credits\": \"Authored by crackedEgg\",\n
  \"logoFile\": \"pack.png\",\n
  \"screenshots\": [],\n
  \"parent\": \"\",\n
  \"dependencies\": []\n
}\n
]"

echo "> copying files"

cd $REOBF

PDIR="$HOME/projects/parachute-src-$SRCVERSION"

rm -f $PDIR/parachute/common/*.class
rm -f $PDIR/parachute/client/*.class

cp -R parachute/ $PDIR
echo -e $MCMODDATA > $PDIR/mcmod.info

echo "> making mod jar file"

cd $PDIR

JAR="parachute-$MCVERSION-forge-$FORGEVERSION.jar"

echo -e "Main-Class: parachute.common.Parachute\nClass-Path: $JAR\n" > $PDIR/manifest.txt

rm -f $JAR
jar -cfm $JAR manifest.txt parachute/ mcmod.info pack.png pack.mcmeta assets/ 

echo "> signing $JAR"
jarsigner -storetype pkcs12 -keystore $HOME/.keystore -keypass $KEYPASS -storepass $STOREPASS $JAR cracked

echo " - $JAR build complete - `date "+%H:%M:%S"`"

# ====== minerhelmet ==================================================

#MCMODDATA="
#[\n
#{\n
#  \"modid\": \"MinerHelmetMod\",\n
#  \"name\": \"MinerHelmet Mod\",\n
#  \"description\": \"Miner helmet mod provides a miners helmet with a switchable light.\",\n
#  \"version\": \"$DATE\",\n
#  \"mcversion\": \"$MCVERSION\",\n
#  \"url\": \"\",\n
#  \"updateUrl\": \"\",\n
#  \"authors\": [\"crackedEgg\"],\n
#  \"credits\": \"Authored by crackedEgg\",\n
#  \"logoFile\": \"/logo/helmet.png\",\n
#  \"screenshots\": [],\n
#  \"parent\": \"\",\n
#  \"dependencies\": []\n
#}\n
#]"


#echo "> copying files"

#cd $REOBF

#RDIR="$HOME/projects/minerhelmet-src-$SRCVERSION"

#rm -f $RDIR/minerhelmet/common/*.class
#rm -f $RDIR/minerhelmet/client/*.class

#cp -R minerhelmet/ $RDIR
#echo -e $MCMODDATA > $RDIR/mcmod.info

#echo "> making mod jar file"

#cd $RDIR

#JAR="minerhelmet-$MCVERSION-forge-$FORGEVERSION.jar"

#echo -e "FMLCorePlugin: minerhelmet.common.MinerHelmetLoadingPlugin\n" > $RDIR/manifest.txt
##echo -e "Main-Class: minerhelmet.client.MinerHelmet\nClass-Path: $JAR\n" > $RDIR/manifest.txt

#rm -f $JAR
#jar -cfm $JAR manifest.txt minerhelmet/ mcmod.info mods/ logo/ README

#echo "> signing $JAR"
#jarsigner -storetype pkcs12 -keystore $HOME/.keystore -keypass $KEYPASS -storepass $STOREPASS $JAR cracked 

#echo " - $JAR build complete - `date "+%H:%M:%S"`"

