#!/usr/bin/env bash

sbt ++${TRAVIS_SCALA_VERSION} coreTest macrosTest akkaTest ahcTest urlTest