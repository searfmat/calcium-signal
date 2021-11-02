#!/bin/bash

# Put the absolute or relative path to the plugins folder of your ImageJ/Fiji installation here.
# If relative, make sure it's relative to the root directory of this project.
imageJ=../fiji-win64/Fiji.app/plugins

mvn clean package
cp target/classes/CalciumSignal_.class plugin
cp src/main/java/CalciumSignal_.java plugin
# Uncomment below line if you needed to recompile any of the imageJ plugins files
cp target/classes/imageJ/plugins/* plugin/imageJ/plugins
cp -r target/classes/celldetection plugin
cd plugin
jar cvfM CalciumSignal_.jar CalciumSignal_.class CalciumSignal_.java imageJ/plugins/*.class celldetection/*.class celldetection/Objects3D/*.class celldetection/Watershed3D/*.class plugins.config
cd ..
cp plugin/CalciumSignal_.jar $imageJ