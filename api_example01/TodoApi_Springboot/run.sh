#!/bin/sh
# Build and run the TodoApi Spring Boot application.
#
# Required environment variables (set these before running, or export them in your shell):
#   MYSQL_URL3      - MySQL hostname or IP (e.g. localhost)
#   MYSQL_USERNAME  - Database username
#   MYSQL_PASSWORD  - Database password
#
# Usage:
#   ./run.sh            # build + run
#   ./run.sh run        # same as above
#   ./run.sh build      # compile and package only (creates target/todoapi-*.jar)
#   ./run.sh jar        # run the packaged JAR directly (skip Maven at runtime)

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

check_env() {
  missing=0
  for var in MYSQL_URL3 MYSQL_USERNAME MYSQL_PASSWORD; do
    if [ -z "$(eval echo \$$var)" ]; then
      echo "ERROR: environment variable $var is not set." >&2
      missing=1
    fi
  done
  [ "$missing" -eq 0 ] || exit 1
}

MODE="${1:-run}"

case "$MODE" in
  build)
    echo "==> Building ..."
    ./mvnw clean package -DskipTests
    echo "==> Build complete: $(ls target/todoapi-*.jar 2>/dev/null || echo 'see target/')"
    ;;
  jar)
    check_env
    JAR=$(ls "$SCRIPT_DIR/target/todoapi-"*.jar 2>/dev/null | grep -v sources | head -1)
    if [ -z "$JAR" ]; then
      echo "No JAR found. Run './run.sh build' first." >&2
      exit 1
    fi
    echo "==> Starting from JAR: $JAR"
    java -jar "$JAR"
    ;;
  run|*)
    check_env
    echo "==> Building and starting TodoApi ..."
    ./mvnw spring-boot:run
    ;;
esac
