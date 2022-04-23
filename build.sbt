ThisBuild / tlBaseVersion := "2.9"
ThisBuild / version := "2.8.2-kotori"

val scalaCheckVersion = "1.16.0"

val disciplineVersion = "1.5.1"

val disciplineMunitVersion = "2.0.0-M2"

val munitVersion = "1.0.0-M5"

val kindProjectorVersion = "0.13.2"

val PrimaryJava = JavaSpec.temurin("8")
val LTSJava = JavaSpec.temurin("17")
val GraalVM11 = JavaSpec.graalvm("11")

ThisBuild / githubWorkflowJavaVersions := Seq(PrimaryJava, LTSJava, GraalVM11)

val Scala212 = "2.12.16"
val Scala213 = "2.13.8"
val Scala3 = "3.1.2"

ThisBuild / crossScalaVersions := Seq(Scala212, Scala213, Scala3)
ThisBuild / scalaVersion := Scala213

ThisBuild / tlFatalWarnings := false
ThisBuild / tlFatalWarningsInCi := false

ThisBuild / githubWorkflowAddedJobs ++= Seq(
  WorkflowJob(
    "scalafix",
    "Scalafix",
    githubWorkflowJobSetup.value.toList ::: List(
      WorkflowStep.Run(List("cd scalafix", "sbt test"), name = Some("Scalafix tests"))
    ),
    javas = List(PrimaryJava),
    scalas = List((ThisBuild / scalaVersion).value)
  )
)

lazy val macroSettings = Seq(
  libraryDependencies ++= {
    if (tlIsScala3.value)
      Nil
    else
      Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided)
  }
)

lazy val cats1BincompatSettings = Seq(
  tlMimaPreviousVersions ++= {
    if (scalaVersion.value.startsWith("2.12")) Set("1.0.1", "1.1.0", "1.2.0", "1.3.1", "1.4.0", "1.5.0", "1.6.1")
    else Set.empty
  }
)

ThisBuild / tlVersionIntroduced := Map("3" -> "2.6.1")

lazy val commonJvmSettings = Seq(
  Test / fork := true,
  Test / javaOptions := Seq("-Xmx3G"),
  doctestGenTests := { if (tlIsScala3.value) Nil else doctestGenTests.value }
) ++ sharedPublishSettings

lazy val sharedPublishSettings = Seq(
  publishMavenStyle := true,
  Test / publishArtifact := false,
  pomIncludeRepository := Function.const(false),
  publishTo := {
    Some("mods".at("https://pkgs.dev.azure.com/Kotori316/minecraft/_packaging/mods/maven/v1"))
  },
  credentials += Credentials(Path.userHome / ".sbt" / "sbt.properties")
)

lazy val commonJsSettings = Seq(
  doctestGenTests := Seq.empty,
  tlVersionIntroduced ++= List("2.12", "2.13").map(_ -> "2.1.0").toMap
)

lazy val commonNativeSettings = Seq(
  doctestGenTests := Seq.empty,
  tlVersionIntroduced ++= List("2.12", "2.13").map(_ -> "2.4.0").toMap + ("3" -> "2.8.0")
)

lazy val disciplineDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "discipline-core" % disciplineVersion
  )
)

lazy val testingDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.scalameta" %%% "munit" % munitVersion % Test,
    "org.typelevel" %%% "discipline-munit" % disciplineMunitVersion % Test
  )
)

lazy val root = tlCrossRootProject
  .aggregate(
    kernel,
    kernelLaws,
    algebra,
    algebraLaws,
    core,
    laws,
    free,
    testkit,
    tests,
    alleycatsCore,
    alleycatsLaws,
    alleycatsTests,
    unidocs,
    bench,
    binCompatTest
  )

lazy val kernel = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("kernel"))
  .settings(moduleName := "cats-kernel", name := "Cats kernel")
  .settings(testingDependencies)
  .settings(Compile / sourceGenerators += (Compile / sourceManaged).map(KernelBoiler.gen).taskValue)
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings, cats1BincompatSettings)
  .nativeSettings(commonNativeSettings)

lazy val kernelLaws = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("kernel-laws"))
  .dependsOn(kernel)
  .settings(moduleName := "cats-kernel-laws", name := "Cats kernel laws")
  .settings(disciplineDependencies)
  .settings(testingDependencies)
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings)
  .nativeSettings(commonNativeSettings)

lazy val algebraSettings = Seq[Setting[_]](
  tlMimaPreviousVersions += "2.2.3",
  tlVersionIntroduced := List("2.12", "2.13", "3").map(_ -> "2.7.0").toMap
)

lazy val algebraNativeSettings = Seq[Setting[_]](
  tlMimaPreviousVersions ~= (_ - "2.2.3"),
  tlVersionIntroduced += ("3" -> "2.8.0")
)

lazy val algebra = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("algebra-core"))
  .dependsOn(kernel)
  .settings(moduleName := "algebra", name := "Cats algebra")
  .settings(Compile / sourceGenerators += (Compile / sourceManaged).map(AlgebraBoilerplate.gen).taskValue)
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings)
  .nativeSettings(commonNativeSettings)
  .settings(
    algebraSettings,
    libraryDependencies += "org.scalacheck" %%% "scalacheck" % scalaCheckVersion % Test,
    testingDependencies
  )
  .nativeSettings(algebraNativeSettings)

lazy val algebraLaws = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("algebra-laws"))
  .dependsOn(kernelLaws, algebra)
  .settings(moduleName := "algebra-laws", name := "Cats algebra laws")
  .settings(disciplineDependencies)
  .settings(testingDependencies)
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings)
  .nativeSettings(commonNativeSettings)
  .settings(algebraSettings)
  .nativeSettings(algebraNativeSettings)

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .dependsOn(kernel)
  .settings(moduleName := "cats-core", name := "Cats core")
  .settings(macroSettings)
  .settings(Compile / sourceGenerators += (Compile / sourceManaged).map(Boilerplate.gen).taskValue)
  .settings(
    libraryDependencies += "org.scalacheck" %%% "scalacheck" % scalaCheckVersion % Test,
    Compile / doc / scalacOptions ~= { _.filterNot(_.startsWith("-W")) } // weird bug
  )
  .settings(testingDependencies)
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings, cats1BincompatSettings)
  .nativeSettings(commonNativeSettings)

lazy val laws = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .dependsOn(kernel, core, kernelLaws)
  .settings(moduleName := "cats-laws", name := "Cats laws")
  .settings(disciplineDependencies)
  .settings(testingDependencies)
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings)
  .nativeSettings(commonNativeSettings)

lazy val free = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core, tests % "test-internal -> test")
  .settings(moduleName := "cats-free", name := "Cats Free")
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings, cats1BincompatSettings)
  .nativeSettings(commonNativeSettings)

lazy val tests = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .dependsOn(testkit % Test)
  .enablePlugins(NoPublishPlugin)
  .settings(moduleName := "cats-tests")
  .settings(testingDependencies)
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings)
  .nativeSettings(commonNativeSettings)

lazy val testkit = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core, laws)
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoKeys := Seq[BuildInfoKey](scalaVersion), buildInfoPackage := "cats.tests")
  .settings(moduleName := "cats-testkit")
  .settings(disciplineDependencies)
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings)
  .nativeSettings(commonNativeSettings)

lazy val alleycatsCore = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("alleycats-core"))
  .dependsOn(core)
  .settings(moduleName := "alleycats-core", name := "Alleycats core")
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings)
  .nativeSettings(commonNativeSettings)

lazy val alleycatsLaws = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("alleycats-laws"))
  .dependsOn(alleycatsCore, laws)
  .settings(moduleName := "alleycats-laws", name := "Alleycats laws")
  .settings(disciplineDependencies)
  .settings(testingDependencies)
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings)
  .nativeSettings(commonNativeSettings)

lazy val alleycatsTests = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("alleycats-tests"))
  .dependsOn(alleycatsLaws, tests % "test-internal -> test")
  .enablePlugins(NoPublishPlugin)
  .settings(moduleName := "alleycats-tests")
  .jsSettings(commonJsSettings)
  .jvmSettings(commonJvmSettings)
  .nativeSettings(commonNativeSettings)

lazy val unidocs = project
  .enablePlugins(TypelevelUnidocPlugin)
  .settings(
    name := "cats-docs",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(kernel.jvm,
                                                             core.jvm,
                                                             free.jvm,
                                                             algebra.jvm,
                                                             alleycatsCore.jvm
    ),
    scalacOptions ~= { _.filterNot(_.startsWith("-W")) }, // weird nsc bug
    ScalaUnidoc / unidoc / scalacOptions ++= Seq("-groups", "-diagrams")
  )

// bench is currently JVM-only

lazy val bench = project
  .dependsOn(core.jvm, free.jvm, laws.jvm)
  .settings(moduleName := "cats-bench")
  .settings(commonJvmSettings)
  .settings(
    libraryDependencies ++= {
      if (scalaVersion.value.startsWith("2.12"))
        Seq(
          "org.scalaz" %% "scalaz-core" % "7.3.6",
          "org.spire-math" %% "chain" % "0.3.0",
          "co.fs2" %% "fs2-core" % "0.10.7"
        )
      else Nil
    },
    evictionErrorLevel := Level.Warn
  )
  .enablePlugins(NoPublishPlugin, JmhPlugin)

lazy val binCompatTest = project
  .enablePlugins(NoPublishPlugin)
  .settings(
    useCoursier := false, // workaround so we can use an old version in compile
    libraryDependencies += {
      val oldV = if (tlIsScala3.value) "2.6.1" else "2.0.0"
      "org.typelevel" %%% "cats-core" % oldV % Provided
    }
  )
  .settings(testingDependencies)
  .dependsOn(core.jvm % Test)

// cats-js is JS-only
lazy val js = project
  .dependsOn(core.js, tests.js % "test-internal -> test")
  .settings(moduleName := "cats-js")
  .settings(catsSettings)
  .settings(commonJsSettings)
  .enablePlugins(ScalaJSPlugin)

// cats-native is Native-only
lazy val native = project
  .dependsOn(core.native, tests.native % "test-internal -> test")
  .settings(moduleName := "cats-native")
  .settings(catsSettings)
  .settings(commonNativeSettings)
  .enablePlugins(ScalaNativePlugin)

// cats-jvm is JVM-only
lazy val jvm = project
  .dependsOn(core.jvm, tests.jvm % "test-internal -> test")
  .settings(moduleName := "cats-jvm")
  .settings(catsSettings)
  .settings(commonJvmSettings)

lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/typelevel/cats")),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(ScmInfo(url("https://github.com/typelevel/cats"), "scm:git:git@github.com:typelevel/cats.git")),
  autoAPIMappings := true,
  apiURL := Some(url("http://typelevel.org/cats/api/")),
  pomExtra :=
    <developers>
      <developer>
        <id>ceedubs</id>
        <name>Cody Allen</name>
        <url>https://github.com/ceedubs/</url>
      </developer>
      <developer>
        <id>rossabaker</id>
        <name>Ross Baker</name>
        <url>https://github.com/rossabaker/</url>
      </developer>
      <developer>
        <id>johnynek</id>
        <name>P. Oscar Boykin</name>
        <url>https://github.com/johnynek/</url>
      </developer>
      <developer>
        <id>travisbrown</id>
        <name>Travis Brown</name>
        <url>https://github.com/travisbrown/</url>
      </developer>
      <developer>
        <id>adelbertc</id>
        <name>Adelbert Chang</name>
        <url>https://github.com/adelbertc/</url>
      </developer>
      <developer>
        <id>peterneyens</id>
        <name>Peter Neyens</name>
        <url>https://github.com/peterneyens/</url>
      </developer>
      <developer>
        <id>tpolecat</id>
        <name>Rob Norris</name>
        <url>https://github.com/tpolecat/</url>
      </developer>
      <developer>
        <id>non</id>
        <name>Erik Osheim</name>
        <url>https://github.com/non/</url>
      </developer>
      <developer>
        <id>LukaJCB</id>
        <name>LukaJCB</name>
        <url>https://github.com/LukaJCB/</url>
      </developer>
      <developer>
        <id>mpilquist</id>
        <name>Michael Pilquist</name>
        <url>https://github.com/mpilquist/</url>
      </developer>
      <developer>
        <id>milessabin</id>
        <name>Miles Sabin</name>
        <url>https://github.com/milessabin/</url>
      </developer>
      <developer>
        <id>djspiewak</id>
        <name>Daniel Spiewak</name>
        <url>https://github.com/djspiewak/</url>
      </developer>
      <developer>
        <id>fthomas</id>
        <name>Frank Thomas</name>
        <url>https://github.com/fthomas/</url>
      </developer>
      <developer>
        <id>julien-truffaut</id>
        <name>Julien Truffaut</name>
        <url>https://github.com/julien-truffaut/</url>
      </developer>
      <developer>
        <id>kailuowang</id>
        <name>Kailuo Wang</name>
        <url>https://github.com/kailuowang/</url>
      </developer>
    </developers>
) ++ sharedPublishSettings ++ sharedReleaseProcess

// Scalafmt
addCommandAlias("fmt", "; Compile / scalafmt; Test / scalafmt; scalafmtSbt")
addCommandAlias("fmtCheck", "; Compile / scalafmtCheck; Test / scalafmtCheck; scalafmtSbtCheck")

// These aliases serialise the build for the benefit of Travis-CI.
addCommandAlias("buildKernelJVM", ";kernelJVM/test;kernelLawsJVM/test")
addCommandAlias("buildCoreJVM", ";coreJVM/test")
addCommandAlias("buildTestsJVM", ";lawsJVM/test;testkitJVM/test;testsJVM/test;jvm/test")
addCommandAlias("buildFreeJVM", ";freeJVM/test")
addCommandAlias("buildAlleycatsJVM", ";alleycatsCoreJVM/test;alleycatsLawsJVM/test;alleycatsTestsJVM/test")
addCommandAlias("buildJVM", ";buildKernelJVM;buildCoreJVM;buildTestsJVM;buildFreeJVM;buildAlleycatsJVM")
addCommandAlias("validateBC", ";binCompatTest/test;catsJVM/mimaReportBinaryIssues")
addCommandAlias("validateJVM", ";fmtCheck;buildJVM;bench/test;validateBC;makeMicrosite")
addCommandAlias("validateJS", ";testsJS/test;js/test")
addCommandAlias("validateKernelJS", "kernelLawsJS/test")
addCommandAlias("validateFreeJS", "freeJS/test")
addCommandAlias("validateAlleycatsJS", "alleycatsTestsJS/test")
addCommandAlias("validateAllJS", "all testsJS/test js/test kernelLawsJS/test freeJS/test alleycatsTestsJS/test")
addCommandAlias("validateNative", ";testsNative/test;native/test")
addCommandAlias("validateKernelNative", "kernelLawsNative/test")
addCommandAlias("validateFreeNative", "freeNative/test")
addCommandAlias("validateAlleycatsNative", "alleycatsTestsNative/test")

val validateAllNativeAlias =
  "all testsNative/test native/test kernelLawsNative/test freeNative/test alleycatsTestsNative/test"
addCommandAlias("validateAllNative", validateAllNativeAlias)

addCommandAlias(
  "validate",
  ";clean;validateJS;validateKernelJS;validateFreeJS;validateNative;validateKernelNative;validateFreeNative;validateJVM"
)

addCommandAlias("prePR", "fmt")

////////////////////////////////////////////////////////////////////////////////////////////////////
// Base Build Settings - Should not need to edit below this line.
// These settings could also come from another file or a plugin.
// The only issue if coming from a plugin is that the Macro lib versions
// are hard coded, so an overided facility would be required.

addCommandAlias("gitSnapshots", ";set version in ThisBuild := git.gitDescribedVersion.value.get + \"-SNAPSHOT\"")

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val crossVersionSharedSources: Seq[Setting[_]] =
  Seq(Compile, Test).map { sc =>
    sc / unmanagedSourceDirectories ++= {
      (sc / unmanagedSourceDirectories).value.map { dir: File =>
        new File(dir.getPath + "_" + scalaBinaryVersion.value)
      }
    }
  }

def commonScalacOptions(scalaVersion: String, isDotty: Boolean) =
  Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-Xfatal-warnings",
    "-deprecation"
  ) ++ (if (priorTo2_13(scalaVersion))
          Seq(
            "-Yno-adapted-args",
            "-Ypartial-unification",
            "-Xfuture"
          )
        else
          Nil) ++ (if (isDotty)
                     Seq("-language:implicitConversions", "-Ykind-projector", "-Xignore-scala2-macros")
                   else
                     Seq(
                       "-language:existentials",
                       "-language:higherKinds",
                       "-language:implicitConversions",
                       "-Ywarn-dead-code",
                       "-Ywarn-numeric-widen",
                       "-Ywarn-value-discard",
                       "-Xlint:-unused,_"
                     ))

def priorTo2_13(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, minor)) if minor < 13 => true
    case _                              => false
  }

lazy val sharedPublishSettings = Seq(
  releaseTagName := tagName.value,
  releaseVcsSign := true,
  publishMavenStyle := true,
  Test / publishArtifact := false,
  pomIncludeRepository := Function.const(false),
  publishTo := {
    Some("mods".at("https://pkgs.dev.azure.com/Kotori316/minecraft/_packaging/mods/maven/v1"))
  },
  credentials += Credentials(Path.userHome / ".sbt" / "sbt.properties")
)

lazy val docs = project
  .in(file("site"))
  .enablePlugins(TypelevelSitePlugin)
  .settings(
    tlFatalWarnings := false,
    laikaConfig ~= { _.withRawContent },
    tlSiteRelatedProjects := Seq(
      TypelevelProject.CatsEffect,
      "Mouse" -> url("https://typelevel.org/mouse"),
      TypelevelProject.Discipline
    ),
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "discipline-munit" % disciplineMunitVersion
    )
  )
  .dependsOn(core.jvm, free.jvm, laws.jvm)

ThisBuild / licenses := List(License.MIT)
ThisBuild / startYear := Some(2015)
ThisBuild / developers ++= List(
  tlGitHubDev("ceedubs", "Cody Allen"),
  tlGitHubDev("rossabaker", "Ross Baker"),
  tlGitHubDev("armanbilge", "Arman Bilge"),
  tlGitHubDev("johnynek", "P. Oscar Boykin"),
  tlGitHubDev("travisbrown", "Travis Brown"),
  tlGitHubDev("adelbertc", "Adelbert Chang"),
  tlGitHubDev("danicheg", "Daniel Esik"),
  tlGitHubDev("peterneyens", "Peter Neyens"),
  tlGitHubDev("tpolecat", "Rob Norris"),
  tlGitHubDev("non", "Erik Osheim"),
  tlGitHubDev("LukaJCB", "LukaJCB"),
  tlGitHubDev("mpilquist", "Michael Pilquist"),
  tlGitHubDev("milessabin", "Miles Sabin"),
  tlGitHubDev("djspiewak", "Daniel Spiewak"),
  tlGitHubDev("fthomas", "Frank Thomas"),
  tlGitHubDev("satorg", "Sergey Torgashov"),
  tlGitHubDev("julien-truffaut", "Julien Truffaut"),
  tlGitHubDev("kailuowang", "Kailuo Wang")
)
