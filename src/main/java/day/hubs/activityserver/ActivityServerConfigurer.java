package day.hubs.activityserver;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ActivityServerConfigurer extends WebMvcConfigurerAdapter {

    /////////
    // CORS
    /////////

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(
                "http://localhost:4200",
                "http://localhost:7000",
                "http://localhost:8080");
    }

    ////////////////////
    // API Documentation
    ////////////////////

    @Bean
    public Docket api() {

        // Creates the documentation at http://localhost:8181/swagger-ui.html
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("day.hubs.activityserver"))
                .paths(paths())
                .build()
                .apiInfo(apiEndPointsInfo());
    }

    // Only select apis that matches the given Predicates.
    private Predicate<String> paths() {
        return Predicates.and(
                PathSelectors.regex("/\\{.+Hub\\}/[Projects|Activities|Documents|Notices|Search].*"),
                Predicates.not(PathSelectors.regex("/\\{.+Hub\\}"))
        );
    }

    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder()
                .title("Activity Server REST API")
                .description("Activity Server REST API")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0-SNAPSHOT")
                .build();
    }
}
