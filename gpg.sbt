ThisBuild / credentials += Credentials(
  "GnuPG Key ID",
  "gpg",
  "671029B321DD6582831014D1C70607956920685B", // key identifier
  "ignored" // this field is ignored; passwords are supplied by pinentry
)
