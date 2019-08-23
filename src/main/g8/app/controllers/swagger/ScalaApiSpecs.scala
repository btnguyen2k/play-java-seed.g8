package controllers.swagger

import com.iheart.playSwagger.SwaggerSpecGenerator
import javax.inject.Inject
import play.api.Configuration
import play.api.libs.json.JsString
import play.api.mvc._

/**
 * Java controller that generate Swagger API specs file in JSON format.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.7.r1
 * @param cc
 * @param config
 */
class ScalaApiSpecs @Inject()(cc: ControllerComponents, @Inject config: Configuration) extends AbstractController(cc) {
  implicit val cl = getClass.getClassLoader

  val domainPackage = "YOUR.DOMAIN.PACKAGE"
  val otherDomainPackage = "YOUR.OtherDOMAIN.PACKAGE"
  lazy val generator = SwaggerSpecGenerator.apply(true, domainPackage, otherDomainPackage)
  val swaggerRoutesFile = config.getOptional[String]("swagger.routes.file").orNull
  val swaggerBasePath = config.getOptional[String]("swagger.api.basePath").getOrElse("/")

  lazy val swaggerJson = Action { request =>
    var root = if (swaggerRoutesFile != null) generator.generate(swaggerRoutesFile) else generator.generate(generator.defaultRoutesFile)
    if (swaggerRoutesFile == null) {
      //remove all "paths" if config [swagger.routes.file] is not defined
      root = root.map(_ - "paths")
    }
    root.map(_ + ("host" -> JsString(request.host)) + ("basePath" -> JsString(swaggerBasePath)))
      .fold(
        e => InternalServerError("Couldn't generate swagger."),
        s => Ok(s)
      )
  }

  def specsJson = swaggerJson
}
