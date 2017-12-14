#!/usr/bin/env bash
sbt ++$1 clean compile publishSigned sonatypeRelease
