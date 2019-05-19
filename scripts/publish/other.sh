#!/usr/bin/env bash

modules=(
    udp macros
)

for md in "${modules[@]}"
do
   sbt ";project $md; ++$1; fullRelease"
done