#!/usr/bin/env bash

modules=(
    coreShared coreIO coreManagement
)

for md in "${modules[@]}"
do
   sbt ";project $md; ++$1; fullRelease"
done