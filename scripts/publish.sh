#!/usr/bin/env bash

modules=(
    coreShared coreIO coreManagement
    akkaShared akkaIO akkaManagement
    ahcShared ahcIO ahcManagement
    urlShared urlIO urlManagement
    udp macros
)

for md in "${modules[@]}"
do
   sbt ";project $md; ++$1; fullRelease"
done