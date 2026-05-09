#!/bin/bash

set -e

BRANCH="$1"
SHA="$2"

if [[ -z "$BRANCH" ]]; then
  echo "Branch is required"
  exit 1
fi

if [[ -z "$SHA" ]]; then
  echo "SHA is required"
  exit 1
fi

#########################################
# Determine suffix
#########################################

SUFFIX=""

if [[ "$BRANCH" == release/* ]]; then
  SUFFIX="-release"
elif [[ "$BRANCH" == dev ]]; then
  SUFFIX="-dev"
fi

#########################################
# Generate unique tag
#########################################

TIMESTAMP=$(date +'%Y%m%d-%H%M%S')
SHORT_SHA=$(echo "$SHA" | cut -c1-7)

VERSION="${TIMESTAMP}-${SHORT_SHA}${SUFFIX}"

echo "$VERSION"