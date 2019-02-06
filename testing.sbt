// core testing
addCommandAlias(
  "coreTest",
  ";project coreShared; clean; compile; pt:compile; pt:test; test:compile; test")

// akka testing
addCommandAlias(
  "akkaTest",
  ";project akkaShared; clean; compile; test:compile; test; project akkaManagement; clean; compile; test:compile; test; it:compile; it:test; project akkaIO; clean; compile; test:compile; test; it:compile; it:test")

// url testing
addCommandAlias(
  "ahcTest",
  ";project ahcShared; clean; compile; test:compile; test; project ahcManagement; clean; compile; test:compile; test; it:compile; it:test; project ahcIO; clean; compile; test:compile; test; it:compile; it:test")

// url testing
addCommandAlias(
  "urlTest",
  ";project urlShared; clean; compile; test:compile; test; project urlManagement; clean; compile; test:compile; test; it:compile; it:test; project urlIO; clean; compile; test:compile; test; it:compile; it:test")

addCommandAlias(
  "macrosTest",
  ";project macros;clean;compile;test:compile;test;pt:compile;pt:test")

addCommandAlias("udpTest", ";project udp;clean;compile;it:compile;it:test")
