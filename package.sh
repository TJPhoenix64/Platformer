#!/bin/bash
 
set -e  # Exit on any error
 
echo "🎮 PlatformerGame Build Pipeline"
echo "================================"
echo ""
 
# Step 1: Clean
echo "🧹 Step 1: Cleaning previous builds..."
rm -rf build/ dist/
mkdir -p build/classes build/jar dist
 
# Step 2: Compile
echo ""
echo "⚙️  Step 2: Compiling Java sources..."
javac -d build/classes -sourcepath src/main/java $(find src/main/java -name "*.java")
echo "   ✓ Compilation successful"
 
# Step 3: Copy resources
echo ""
echo "📦 Step 3: Copying resources..."
if [ -d "src/main/resources" ]; then
    cp -r src/main/resources/* build/classes/
    echo "   ✓ Resources copied"
else
    echo "   ⚠ No resources directory found"
fi
 
# Step 4: Create manifest
echo ""
echo "📝 Step 4: Creating manifest..."
cat > build/manifest.txt << EOF
Manifest-Version: 1.0
Main-Class: com.tyler.platformer.PlatformerGame
EOF
echo "   ✓ Manifest created"
 
# Step 5: Create JAR
echo ""
echo "📦 Step 5: Creating JAR..."
jar cfm build/jar/PlatformerGame.jar build/manifest.txt -C build/classes .
echo "   ✓ JAR created: build/jar/PlatformerGame.jar"
 
# Step 6: Test JAR (optional)
echo ""
read -p "🧪 Test JAR before packaging? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "   Testing JAR..."
    java -jar build/jar/PlatformerGame.jar &
    JAR_PID=$!
    echo "   Game launched (PID: $JAR_PID)"
    echo "   Close the game window to continue..."
    wait $JAR_PID
fi
 
# Step 7: Check jpackage
echo ""
echo "🔍 Step 7: Checking jpackage availability..."
if ! command -v jpackage &> /dev/null; then
    echo "   ❌ jpackage not found!"
    echo "   Make sure you have JDK 14+ installed"
    exit 1
fi
echo "   ✓ jpackage found: $(jpackage --version)"
 
# Step 8: Create DMG
echo ""
echo "📀 Step 8: Creating DMG with jpackage..."
 
# Check for icon file
ICON_ARG=""
if [ -f "icon.icns" ]; then
    ICON_ARG="--icon icon.icns"
    echo "   ℹ Using icon: icon.icns"
elif [ -f "src/main/resources/icon.icns" ]; then
    ICON_ARG="--icon src/main/resources/icon.icns"
    echo "   ℹ Using icon: src/main/resources/icon.icns"
else
    echo "   ℹ No icon file found (optional)"
fi
 
jpackage \
  --type dmg \
  --name "PlatformerGame" \
  --app-version "1.0.0" \
  --vendor "Tyler" \
  --description "A Java AWT Platformer Game" \
  --input build/jar \
  --main-jar PlatformerGame.jar \
  --main-class com.tyler.platformer.PlatformerGame \
  --dest dist \
  $ICON_ARG \
  --mac-package-name "PlatformerGame" \
  --mac-package-identifier "com.tyler.platformer" \
  --java-options "-Xmx512m" \
  --java-options "-Dapple.awt.application.name=PlatformerGame"
 
echo ""
echo "================================"
echo "✅ BUILD COMPLETE!"
echo "================================"
echo ""
echo "📦 DMG Location: $(pwd)/dist/PlatformerGame-1.0.0.dmg"
echo "📊 DMG Size: $(du -h dist/PlatformerGame-1.0.0.dmg | cut -f1)"
echo ""
echo "To distribute: Share the DMG file"
echo "To install: Double-click the DMG, drag app to Applications"
echo ""