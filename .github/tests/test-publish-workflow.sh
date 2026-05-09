#!/bin/bash

set -e

#############################################
# Defaults
#############################################

BRANCH="dev"
COMMIT_MSG="test: local workflow test"
WORKFLOW_FILE=".github/workflows/publish.yml"
EVENT_FILE=".github/tests/event.json"

#############################################
# Parse Arguments
#############################################

while [[ $# -gt 0 ]]; do
  case "$1" in
    --branch)
      BRANCH="$2"
      shift 2
      ;;
    --commit-msg)
      COMMIT_MSG="$2"
      shift 2
      ;;
    *)
      echo "❌ Unknown argument: $1"
      exit 1
      ;;
  esac
done

#############################################
# Validate workflow exists
#############################################

if [[ ! -f "$WORKFLOW_FILE" ]]; then
  echo "❌ Workflow file not found:"
  echo "   $WORKFLOW_FILE"
  exit 1
fi

#############################################
# Validate push:false for local testing
#############################################

if grep -q "push: true" "$WORKFLOW_FILE"; then
  echo "❌ Validation failed"
  echo
  echo "This is a LOCAL TEST runner."
  echo "Docker image push MUST be disabled."
  echo
  echo "Please change:"
  echo "    push: true"
  echo
  echo "to:"
  echo "    push: false"
  echo
  exit 1
fi

#############################################
# Repo Details
#############################################

REPO_NAME=$(basename -s .git "$(git config --get remote.origin.url)")
HEAD_SHA=$(git rev-parse HEAD)

#############################################
# Generate Event Payload
#############################################

cat > "$EVENT_FILE" <<EOF
{
  "workflow_run": {
    "conclusion": "success",
    "head_sha": "$HEAD_SHA",
    "head_branch": "$BRANCH"
  },
  "repository": {
    "name": "$REPO_NAME"
  }
}
EOF

#############################################
# Display Info
#############################################

echo
echo "========================================="
echo " GitHub Actions Local Workflow Test"
echo "========================================="
echo "Repository   : $REPO_NAME"
echo "Branch       : $BRANCH"
echo "Commit SHA   : $HEAD_SHA"
echo "Commit Msg   : $COMMIT_MSG"
echo "Workflow     : $WORKFLOW_FILE"
echo "Event File   : $EVENT_FILE"
echo "========================================="
echo

#############################################
# Run Workflow
#############################################

act workflow_run \
  -W "$WORKFLOW_FILE" \
  -e "$EVENT_FILE"