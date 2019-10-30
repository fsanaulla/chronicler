resolvers += "Era7 maven releases" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com"

libraryDependencies += "com.sun.activation" % "javax.activation" % "1.2.0"

addSbtPlugin("org.xerial.sbt"        % "sbt-sonatype" % "3.8")
addSbtPlugin("com.jsuereth"          % "sbt-pgp"      % "2.0.0")
addSbtPlugin("de.heikoseeberger"     % "sbt-header"   % "5.0.0")
addSbtPlugin("pl.project13.scala"    % "sbt-jmh"      % "0.3.4")
addSbtPlugin("com.github.romanowski" % "hoarder"      % "1.0.2")
