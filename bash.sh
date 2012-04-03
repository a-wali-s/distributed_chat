#!/bin/bash

for i in `seq 2 50`
do
        USERNAME=`echo remoteName$i`
        PORT=$((23909+$i))
        java -jar *.jar $USERNAME $PORT 5 142.58.184.157 2001 test 1000 &
        sleep 1
done
