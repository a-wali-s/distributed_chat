#!/bin/bash

for i in {2..50}
do
        USERNAME=`echo peerName$i`
        PORT=$((23909+$i))
        java -jar *.jar $USERNAME $PORT 5 localhost 2001 test 1000 &
        sleep 1
done
