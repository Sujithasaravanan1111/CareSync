package com.caresync.config;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Filter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppInitializer
        extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
                RootConfig.class,
                JpaConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{
                WebConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{
                "/"
        };
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{
                new DelegatingFilterProxy("jwtAuthenticationFilter")
        };
    }
    @Override
protected void customizeRegistration(
        jakarta.servlet.ServletRegistration.Dynamic registration) {

    registration.setMultipartConfig(
            new MultipartConfigElement(
                    System.getProperty("java.io.tmpdir"),
                    10 * 1024 * 1024,
                    20 * 1024 * 1024,
                    1024 * 1024
            )
    );
}
}