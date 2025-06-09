#!/usr/bin/env bash

set -eu -o pipefail

AWK_SCRIPT='
  {
    # key = major and minor version
    key = $1 "." $2
    # store in map (associative array) with overwrite
    latest[key] = $0
  }
  END {
    # print final map values
    for (k in latest) print latest[k]
  }
'

# Print latest spigot version per minor version
fetch_versions() {
  # index of spigot-api versions (maven repository)
  curl -s https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/spigotmc/spigot-api/maven-metadata.xml |
  # keep version number only
  sed -n 's#.*<version>\(.*\)</version>.*#\1#p' |
  # without experimental versions
  grep -v -- '-experimental' |
  # sort by version
  sort --version-sort |
  # keep last per minor version
  awk -F '[.-]' "$AWK_SCRIPT" |
  # sort by version again (map output is unsorted)
  sort -V
}

# Assign result of function to array
VERSIONS=($(fetch_versions))

# Arrays to track results
SUCCESSFUL=()
FAILED=()

for i in "${!VERSIONS[@]}"; do
	VERSION=${VERSIONS[$i]}
	printf 'Running: mvn clean verify -Dspigot-api.version=%s ... ' $VERSION >&2
	if mvn --quiet --batch-mode clean verify -Dspigot-api.version=$VERSION &> /dev/null; then
		SUCCESSFUL+=($VERSION)
	  printf 'success\n' >&2
	else
		FAILED+=($VERSION)
	  printf 'failed\n' >&2
	fi
done

# Summary
echo  >&2
echo "==================== Summary ====================" >&2
echo  >&2

echo "# Spigot Compatibility Report"
echo
echo "## Compatible versions"
echo
echo "✅ Plugin builds cleanly for the listed Spigot versions"
echo "⚠️ This means the plugin is likely to work with these versions"
echo "🎮️ Not every version was tested in-game"
echo
for VERSION in "${SUCCESSFUL[@]}"; do
  SHORT_VERSION="$(cut -d. -f1,2 <<< "$VERSION")"
  echo "  - $SHORT_VERSION ($VERSION)"
done

echo
echo "*************************************************"
echo
echo "## Incompatible versions"
echo
echo "❌ Plugin will not work with these Spigot versions"
echo
for VERSION in "${FAILED[@]}"; do
  SHORT_VERSION="$(cut -d. -f1,2 <<< "$VERSION")"
  echo "  - $SHORT_VERSION ($VERSION)"
done
