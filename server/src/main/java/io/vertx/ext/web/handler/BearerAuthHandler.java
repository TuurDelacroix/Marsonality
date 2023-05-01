/*
 * You can ignore this file:
 * this is a missing class in the vert.x framework.
 * We need it to work with (simple) tokens as authentication.
 */
package io.vertx.ext.web.handler;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.impl.BearerAuthHandlerImpl;


@VertxGen
public interface BearerAuthHandler extends AuthenticationHandler {

    /**
     * Create a basic auth handler
     *
     * @param authProvider the auth provider to use
     * @return the auth handler
     */
    static BearerAuthHandler create(AuthenticationProvider authProvider) {
        String defaultRealm = "vertx-web";
        return new BearerAuthHandlerImpl(authProvider, defaultRealm);
    }

    /**
     * Create a basic auth handler, specifying realm
     *
     * @param authProvider the auth service to use
     * @param realm        the realm to use
     * @return the auth handler
     */
    static BearerAuthHandler create(AuthenticationProvider authProvider, String realm) {
        return new BearerAuthHandlerImpl(authProvider, realm);
    }
}
