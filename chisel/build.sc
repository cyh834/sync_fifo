import mill._, scalalib._
import coursier.maven.MavenRepository

object ivys {
  val scala = "2.13.14"
  val chisel = ivy"org.chipsalliance::chisel:6.5.0"
  val chiselPlugin = ivy"org.chipsalliance:::chisel-plugin:6.5.0"
  val chiseltest = ivy"edu.berkeley.cs::chiseltest:6.0.0"
}

trait CommonModule extends ScalaModule {
  override def scalaVersion = ivys.scala

  override def scalacOptions = Seq("-Ymacro-annotations")
}

trait HasChisel extends ScalaModule {
  override def ivyDeps = Agg(ivys.chisel)
  override def scalacPluginIvyDeps = Agg(ivys.chiselPlugin)
}

trait CommonNS extends SbtModule with CommonModule with HasChisel

//object difftest extends CommonNS {
//  override def millSourcePath = os.pwd / "difftest"
//}

object top extends CommonNS {

  override def millSourcePath = os.pwd

  override def moduleDeps = super.moduleDeps ++ Seq(
    //difftest
  )

  override def ivyDeps = super.ivyDeps() ++ Agg(
    ivys.chiseltest,
  )

  object test extends SbtModuleTests with TestModule.ScalaTest{
    override def forkArgs = Seq("-Xmx40G", "-Xss256m")

    override def ivyDeps = super.ivyDeps() ++ Agg(
      ivys.chiseltest,
    )

    override def scalacOptions = super.scalacOptions() ++ Agg("-deprecation", "-feature")
  }
}