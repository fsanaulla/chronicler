#!/usr/bin/env bash

sbt ++${TRAVIS_SCALA_VERSION} corePublish
sbt ++${TRAVIS_SCALA_VERSION} akkaPublish
sbt ++${TRAVIS_SCALA_VERSION} ahcPublish
sbt ++${TRAVIS_SCALA_VERSION} urlPublish
sbt ++${TRAVIS_SCALA_VERSION} macrosPublish
sbt ++${TRAVIS_SCALA_VERSION} udpPublish