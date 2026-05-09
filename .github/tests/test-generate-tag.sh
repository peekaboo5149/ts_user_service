#!/bin/bash

set -e

#########################################
# Defaults
#########################################

BRANCH="dev"
COMMIT_SHA=$(git rev-parse HEAD)

#########################################
# Parse args
#########################################

while [[ $# -gt 0 ]]; do
  case "$1" in
    --branch)
      BRANCH="$2"
      shift 2
      ;;
    --sha)
      COMMIT_SHA="$2"
      shift 2
      ;;
    *)
      echo "Unknown argument: $1"
      exit 1
      ;;
  esac
done

#########################################
# Run tag generator
#########################################

VERSION=$(
  .github/scripts/generate-tag.sh \
  "$BRANCH" \
  "$COMMIT_SHA"
)

#########################################
# Output
#########################################

echo
echo "=================================="
echo " Branch : $BRANCH"
echo " SHA    : $COMMIT_SHA"
echo "=================================="
echo
echo " Generated Docker Tag"
echo "----------------------------------"
echo " $VERSION"
echo