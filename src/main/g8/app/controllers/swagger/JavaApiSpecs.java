package controllers.swagger;

import com.github.ddth.commons.utils.TypesafeConfigUtils;
import com.iheart.playSwagger.SwaggerSpecGenerator;
import controllers.BaseController;
import play.api.libs.json.JsObject;
import play.api.libs.json.JsString;
import play.mvc.Http;
import play.mvc.Result;
import scala.Function1;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.util.Try;

import java.util.Arrays;
import java.util.List;

/**
 * Java controller that generate Swagger API specs file in JSON format.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.7.r1
 */
public class JavaApiSpecs extends BaseController {
    String domainPackage = "YOUR.DOMAIN.PACKAGE";
    String otherDomainPackage = "YOUR.OtherDOMAIN.PACKAGE";
    List<String> domains = Arrays.asList(domainPackage, otherDomainPackage);
    SwaggerSpecGenerator generator = SwaggerSpecGenerator
            .apply(true, JavaConverters.asScalaIterator(domains.iterator()).toSeq(),
                    JavaApiSpecs.class.getClassLoader());
    String swaggerRoutesFiles, swaggerBasePath;

    public Result specsJson(Http.Request request) {
        if (swaggerRoutesFiles == null) {
            swaggerRoutesFiles = TypesafeConfigUtils.getStringOptional(getAppConfig(), "swagger.routes.file")
                    .orElse("");
        }
        if (swaggerBasePath == null) {
            swaggerBasePath = TypesafeConfigUtils.getStringOptional(getAppConfig(), "swagger.api.basePath").orElse("/");
        }
        Try<JsObject> root = swaggerRoutesFiles.equals("") ?
                generator.generate(generator.defaultRoutesFile()) :
                generator.generate(swaggerRoutesFiles);
        if (swaggerRoutesFiles.equals("")) {
            //remove all "paths" if config [swagger.routes.file] is not defined
            root = root.map(node -> node.$minus("paths"));
        }
        return ok(root.map(
                (Function1<JsObject, Object>) node -> node.$plus(Tuple2.apply("host", JsString.apply(request.host())))
                        .$plus(Tuple2.apply("basePath", JsString.apply(swaggerBasePath)))).get().toString());
    }
}
