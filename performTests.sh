#!/bin/bash
set -e

# some cleanup
./gradlew --stop
git clean -xdf

# performs tests
for LIBS in 10 50 100
do
   for CLASSES in 100 300 1000
   do
      for KIND in 'android' 'jvm'
      do
        COMMAND="./gradlew -PlibsCount=$LIBS -PclassesCount=$CLASSES -Pkind=$KIND assembleDebug"

        # warmup runs
        eval $COMMAND
        eval $COMMAND

        COMMAND="$COMMAND --rerun-tasks --scan"

        # actual run
        eval $COMMAND

        echo "| number of libs | number of classes per lib | kind | command |"
        echo "| $LIBS | $CLASSES | $KIND | $COMMAND |"
        read -p "Take note"
      done
   done
done
