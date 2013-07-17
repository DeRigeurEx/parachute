#!/bin/bash

./recompile.sh
./reobfuscate_srg.sh
./mkjar.sh
./place-jars.sh

