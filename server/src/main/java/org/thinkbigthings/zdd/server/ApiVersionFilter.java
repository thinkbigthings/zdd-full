package org.thinkbigthings.zdd.server;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class ApiVersionFilter implements Filter {

    private Integer apiVersion;

    public static final String API_VERSION_HEADER_NAME = "X-Version";

    public ApiVersionFilter(AppProperties config) {
       apiVersion = config.getApiVersion();
    }

    // if something goes wrong: this example came from here https://www.baeldung.com/spring-response-header
    // filter order might matter, you can try changing that
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // TODO get client version into UI
        // this filter does not add the header to requests for static content
        // related to spring.resources.chain.strategy.fixed.version ?
        // https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html
        // related to ResourceHandlerRegistry?

        // would it be simpler to return it from API call to get application context on top level page load?
        // (Context, not local storage)
        // or is there a risk of resources call going to old server and api version call going to new server?
        // if that happened would it be bad?

        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        httpServletResponse.setHeader(API_VERSION_HEADER_NAME, apiVersion.toString());

        chain.doFilter(request, response);

    }

}
