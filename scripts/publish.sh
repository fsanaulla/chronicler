#!/usr/bin/env bash
sbt release release-version ${RELEASE_VERSION} next-version ${NEXT_RELEASE_VERSION}
