import java.io.File
import java.io.PrintWriter
import scala.io.Source
import edu.holycross.shot.cite._



// Run this script from root directory of repository.
//
// display codex pages in order with thumbnails of default image.
//

val srcFiles = Vector(
  "relations/ups1-1-pageToDefaultImage.cex",
  "relations/venB-pageToDefaultImage.cex"
)

// format two CEX columns as a row of markdown table
def formatLine(s: String) : String = {
  val cols = s.split("#")
  val width = 100
    val pg = cols(0)
    val img = cols(1)


    try  {
      val pgUrn = Cite2Urn(pg)
      val imgUrn = Cite2Urn(img)
      val thumb = s"http://www.homermultitext.org/iipsrv?OBJ=IIP,1.0&FIF=/project/homer/pyramidal/deepzoom/${imgUrn.namespace}/${imgUrn.collection}/${imgUrn.version}/${imgUrn.dropExtensions.objectComponent}.tif&WID=${width}&CVT=JPEG"
      val ict2 = "http://www.homermultitext.org/ict2?urn="

      s"| ![](${thumb}) | [${pgUrn.objectComponent}](${ict2}${imgUrn}) |"
    } catch {
      case t: Throwable => { println(s"Bad urn value: ${pg} or ${img}. Skipping"); ""}
    }

}

for (fName <- srcFiles) {
  val f = new File(fName)
  val base = f.getName.replaceAll(".cex", "")

  val lines = Source.fromFile(f).getLines.toVector.drop(1)
  val header = "| illustration    | page  |\n |:---------|:---------|\n"
  val markdown = for (l <- lines) yield {
    formatLine(l)
  }
  val viewFile = new File("views/" + base + ".md")
  new PrintWriter(viewFile) { write(header + markdown.filter(_.nonEmpty).mkString("\n")); close }
}
