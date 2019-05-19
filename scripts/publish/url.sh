#!/usr/bin/env bash

modules=(
    urlShared urlIO urlManagement
)

for md in "${modules[@]}"
do
   sbt ";project $md; ++$1; fullRelease"
done