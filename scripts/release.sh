#!/usr/bin/env bash

# set github user info
git config --local user.name ${GITHUB_USERNAME}
git config --local user.email ${GITHUB_EMAIL}

# get tag from version.sbt
version="$(sed -n 's/.*version in ThisBuild := \"\([0-9].[0-9].[0-9]*\)\"/\1/p' version.sbt)" # extracted version
tag="v$version" # git tag

# create tag
git tag ${tag}

# push git tag to remote repo
git push origin ${tag}

# push release
sbt githubRelease
