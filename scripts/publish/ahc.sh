#!/usr/bin/env bash

modules=(
    ahcShared ahcIO ahcManagement
)

for md in "${modules[@]}"
do
   sbt ";project $md; ++$1; fullRelease"
done