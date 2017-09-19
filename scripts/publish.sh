#!/usr/bin/env bash
sbt "pgp-cmd recv-key ${PGP_KEY} hkp://pool.sks-keyservers.net"
sbt release release-version ${RELEASE_VERSION} next-version ${NEXT_RELEASE_VERSION}
