// release
addCommandAlias("fullRelease", ";clean;publishSigned;sonatypeRelease")

// core testing
addCommandAlias(
  "coreTest",
  ";project coreShared;clean;compile;pt:compile;pt:test;test:compile;test")

// akka testing
addCommandAlias(
  "akkaTest",
  ";project akkaShared;clean;compile;test:compile;coverage;test;project akkaManagement;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test;project akkaIO;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test")

// url testing
addCommandAlias(
  "ahcTest",
  ";project ahcShared;clean;compile;test:compile;coverage;test;project ahcManagement;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test;project ahcIO;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test")

// url testing
addCommandAlias(
  "urlTest",
  ";project urlShared; clean; compile; test:compile; coverage; test;project urlManagement; clean; compile; test:compile; coverage; test; it:compile; coverage; it:test;project urlIO;clean;compile;test:compile;coverage;test;it:compile;coverage;it:test")

