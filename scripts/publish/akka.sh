#!/usr/bin/env bash

modules=(
    akkaShared akkaIO akkaManagement
)

for md in "${modules[@]}"
do
   sbt ";project $md; ++$1; fullRelease"
done