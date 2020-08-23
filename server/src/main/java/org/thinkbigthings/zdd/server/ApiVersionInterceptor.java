package org.thinkbigthings.zdd.server;

import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Optional.ofNullable;

/**
 * This adds a Version header all requests including for static resources.
 * If during development the UI is being served by a UI dev environment (e.g. npm start, Express)
 * then you won't see that header in requests for static resources.
 */
@Component
public class ApiVersionInterceptor extends HandlerInterceptorAdapter {


    public static final String API_VERSION  = "X-Version";

    private final String apiVersion;

    public ApiVersionInterceptor(AppProperties config) {
        apiVersion = config.getApiVersion().toString();
    }

    /**
     *
     * @param request
     * @param response
     * @param handler
     * @return true if the execution chain should proceed with the next interceptor or the handler itself.
     * Else, DispatcherServlet assumes that this interceptor has already dealt with the response itself.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws HttpMediaTypeNotAcceptableException {

        response.setHeader(API_VERSION, apiVersion);

        // can be null, if it's missing then act as if it's the latest
        String requestApiVersion = ofNullable(request.getHeader(API_VERSION)).orElse(apiVersion);

        // since this is an Interceptor and not a Filter,
        // in theory we could inspect handler annotations for what version it can handle.
        if( ! requestApiVersion.equals(apiVersion)) {
            String message = "Request api " + requestApiVersion + " does not match server api " + apiVersion;
            System.out.println(message);
            throw new IncompatibleClientVersionException(message);
        }

        return true;
    }

}


