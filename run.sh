#!/bin/bash

# Clean
rm -rf build/
mkdir -p build/classes

# Compile
echo "⚙️  Compiling..."
javac -d build/classes -sourcepath src/main/java $(find src/main/java -name "*.java")

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

# Copy resources
echo "📦 Copying resources..."
cp -r src/main/resources/* build/classes/

# Run
echo "🎮 Running game..."
cd build/classes
java com.tyler.platformer.PlatformerGame