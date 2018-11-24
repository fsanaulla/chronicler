addCommandAlias(
  "corePublish",
  ";project coreShared; fullRelease; project coreIO; fullRelease; project coreManagement; fullRelease"
)

addCommandAlias(
  "akkaPublish",
  ";project akkaShared; fullRelease; project akkaIO; fullRelease; project akkaManagement; fullRelease"
)

addCommandAlias(
  "ahcPublish",
  ";project ahcShared; fullRelease; project ahcIO; fullRelease; project ahcManagement; fullRelease"
)

addCommandAlias(
  "urlPublish",
  ";project urlShared; fullRelease; project urlIO; fullRelease; project urlManagement; fullRelease"
)

addCommandAlias(
  "macrosPublish",
  ";project macros; fullRelease"
)

addCommandAlias(
  "udpPublish",
  "project udp; fullRelease"
)