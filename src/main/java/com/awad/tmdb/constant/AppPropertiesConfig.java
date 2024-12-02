package com.awad.tmdb.constant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;
import java.util.List;

@Getter
@Accessors(makeFinal = true)
@ConfigurationProperties(prefix = "app.config")
public final class AppPropertiesConfig {
    private Cors cors = new Cors();
    private SwaggerOpenAI swagger = new SwaggerOpenAI();
    private JWT jwt = new JWT();

    @Setter
    @Getter
    public static final class Cors {
        private String pathPattern;
        private String allowedOrigins;
        private boolean allowCredentials;
        private String[] allowedMethods;
        private String[] allowedHeaders;
        private String[] allowedPublicApis;
        private String exposedHeaders;
        private int maxAge;
    }

    @Getter
    @Setter
    public static final class SwaggerOpenAI{
        private Document document;
        private List<SupportedServer> servers;
        private AppLicense license;

        @Getter
        @Setter
        public static final class Document{
            private String title;
            private String version;
            private String description;
        }

        @Getter
        @Setter
        public static final class SupportedServer{
            private String url;
            private String description;
        }

        @Getter
        @Setter
        public static final class AppLicense{
            private String name;
            private String url;
        }
    }

    @Getter
    @Setter
    public static final class JWT {
        private String secret;
        private int expiration;
        private String tokenPrefix;
        private String tokenType;
        private int accessTokenExpiration;
        private int refreshTokenExpiration;
        private String macAlgorithm;

        public Date getAccessTokenExpirationDate(){
            long oneHour = 3600 * 1000L;
            return new Date(new Date().getTime() + this.accessTokenExpiration * oneHour);
        }

        public Date getRefreshTokenExpirationDate(){
            long oneHour = 3600 * 1000L;
            return new Date(new Date().getTime() + this.refreshTokenExpiration * oneHour);
        }
    }
}
