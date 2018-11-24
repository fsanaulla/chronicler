credentials in ThisBuild +=
  Credentials(
          "Sonatype Nexus Repository Manager",
          "oss.sonatype.org",
          sys.env.getOrElse("SONATYPE_USER", "user"),
          sys.env.getOrElse("SONATYPE_PASS", "password")
  )
