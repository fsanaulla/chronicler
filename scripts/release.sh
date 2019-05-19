#!/usr/bin/env bash

# set github user info
git config --local user.email "travis@travis-ci.org"
git config --local user.name "Travis CI"
git remote add origin https://${GITHUB_TOKEN}@github.com/fsanaulla/chronicler.git

# get tag from version.sbt
version="$(sed -n 's/.*version in ThisBuild := \"\([0-9].[0-9].[0-9]*\)\"/\1/p' version.sbt)" # extracted version
tag="v$version" # git tag

echo $tag

# create tag
git tag ${tag}

# push git tag to remote repo
git push origin ${tag}

# push release
sbt githubRelease