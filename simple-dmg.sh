#!/bin/bash
 
# Simple DMG builder - no frills version
 
# Build JAR first
echo "Building JAR..."
rm -rf build/
mkdir -p build/classes build/jar
 
javac -d build/classes -sourcepath src/main/java $(find src/main/java -name "*.java")
cp -r src/main/resources/* build/classes/
 
cat > build/manifest.txt << EOF
Manifest-Version: 1.0
Main-Class: com.tyler.platformer.PlatformerGame
EOF
 
jar cfm build/jar/PlatformerGame.jar build/manifest.txt -C build/classes .
 
# Create DMG
echo "Creating DMG..."
mkdir -p dist
 
jpackage \
  --type dmg \
  --name "PlatformerGame" \
  --app-version "1.0" \
  --input build/jar \
  --main-jar PlatformerGame.jar \
  --dest dist \
  --java-options "-Dapple.awt.application.name=PlatformerGame" \
  --java-options "-Dapple.laf.useScreenMenuBar=false" \
  --java-options "-Djava.awt.headless=false"
 
echo "Done! Check dist/ folder for your DMG"