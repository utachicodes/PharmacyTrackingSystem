#!/bin/bash
# start.sh — builds and launches the Pharmacy Tracking System

set -e

# ── Find Maven ────────────────────────────────────────────────────────────────
MVN=""
for candidate in \
    "mvn" \
    "/Applications/Apache NetBeans.app/Contents/Resources/netbeans/java/maven/bin/mvn" \
    "/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn" \
    "/usr/local/bin/mvn" \
    "/opt/homebrew/bin/mvn"; do
    if command -v "$candidate" &>/dev/null 2>&1 || [ -x "$candidate" ]; then
        MVN="$candidate"
        break
    fi
done

if [ -z "$MVN" ]; then
    echo "ERROR: Maven not found. Install it with: brew install maven"
    exit 1
fi

echo "Using Maven: $MVN"

# ── Check MySQL is running ────────────────────────────────────────────────────
if ! mysqladmin ping --silent 2>/dev/null; then
    echo "MySQL is not running. Starting it..."
    brew services start mysql 2>/dev/null || mysql.server start 2>/dev/null || {
        echo "ERROR: Could not start MySQL. Please start it manually and re-run this script."
        exit 1
    }
    sleep 2
fi

echo "MySQL is running."

# ── Build & run ───────────────────────────────────────────────────────────────
cd "$(dirname "$0")"

echo "Compiling..."
"$MVN" compile -q

echo "Launching PharmTrack..."
"$MVN" exec:java -q -Dexec.mainClass="pharmacyinventorymanagement.PharmacyInventoryManagement"
