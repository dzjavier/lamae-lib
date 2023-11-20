#!/bin/sh
NC=`nproc`
NCORES=$NC
filename=$1
NPROC=0
MAX_NPROC=$NC
NUM=0
function queue {
    QUEUE="$QUEUE $1"
    NUM=$(($NUM+1))
}

function regeneratequeue {
    OLDREQUEUE=$QUEUE
    QUEUE=""
    NUM=0
    for PID in $OLDREQUEUE
    do
        if [ -d /proc/$PID  ] ; then
            QUEUE="$QUEUE $PID"
            NUM=$(($NUM+1))
        else
            echo "PID: $PID finished"
        fi
    done
}

function checkqueue {
    OLDCHQUEUE=$QUEUE
    for PID in $OLDCHQUEUE
    do
        if [ ! -d /proc/$PID ] ; then
            regeneratequeue # at least one PID has finished
            break
        fi
    done
}

while read line; do 
## be nice and reduce priority
    eval nice $line &
    PID=$!
    echo "Running $line in PID: $PID"
    queue $PID
    while [ $NUM -ge $MAX_NPROC ]; do
        checkqueue
        sleep 1
    done
done < $filename
while [ $NUM -ne 0 ];do
    checkqueue
    sleep 1
done
echo "The process of the file $filename has finished"
