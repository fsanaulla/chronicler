#!/usr/bin/env bash

modules=( coreShared coreIO coreManagement akkaShared akkaIO akkaManagement )

for md in "${modules[@]}"
do
   sbt "project $md" "+ fullRelease"
done
#sbt ++${TRAVIS_SCALA_VERSION} corePublish
#sbt ++${TRAVIS_SCALA_VERSION} akkaPublish
#sbt ++${TRAVIS_SCALA_VERSION} ahcPublish
#sbt ++${TRAVIS_SCALA_VERSION} urlPublish
#sbt ++${TRAVIS_SCALA_VERSION} macrosPublish
#sbt ++${TRAVIS_SCALA_VERSION} udpPublish