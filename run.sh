#!/usr/bin/env bash
# Build and run DoorDasH GUI. Requires Java 11+ and JavaFX SDK at $JAVAFX_LIB
# (defaults to /usr/share/openjfx/lib on Debian/Ubuntu via the `openjfx` package).

set -e

JAVAFX_LIB="${JAVAFX_LIB:-/usr/share/openjfx/lib}"
if [ ! -d "$JAVAFX_LIB" ]; then
  echo "JavaFX libraries not found at $JAVAFX_LIB"
  echo "Set JAVAFX_LIB to your JavaFX SDK 'lib' directory."
  exit 1
fi

mkdir -p bin

find src -name "*.java" -print0 | xargs -0 javac \
  --module-path "$JAVAFX_LIB" \
  --add-modules javafx.controls,javafx.fxml \
  -d bin

# Copy stylesheet so the classpath can find it.
mkdir -p bin/game/gui
cp src/game/gui/styles.css bin/game/gui/styles.css

java \
  --module-path "$JAVAFX_LIB" \
  --add-modules javafx.controls,javafx.fxml \
  -cp bin \
  game.gui.Main "$@"
