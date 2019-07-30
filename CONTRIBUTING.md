# Contributing

When contributing to this repository, please first discuss the change you wish to make via issue,
email, or any other method with the owners of this repository before making a change. 

# Navigating around

## Branches summary

Depending on which version (or sometimes module) you want to work on, you should target a specific branch as explained below:

* `master` – publish/deployment branch, target for upcoming `release-v.v.v` branches.
* `release-v.v.v` – maintenance branch of Akka 2.5.x

## Pull Request Process

1. All upcoming changes should be targeted to `release-v.v.v` branch.
2. Ensure any install or build dependencies are removed before the end of the layer when doing a 
   build.
3. Add your changes to release specific changelog file. Located under `/changelog` dir   
3. Update the README.md with details of changes to the interface
5. Merge request should be merged by owner of the repository, please assign pull request to @fsanaulla.