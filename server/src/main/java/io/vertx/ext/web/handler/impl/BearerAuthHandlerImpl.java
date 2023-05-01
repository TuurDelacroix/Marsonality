/*
 * You can ignore this file:
 * this is a missing class in the vert.x framework.
 * We need it to work with (simple) tokens as authentication.
 */
package io.vertx.ext.web.handler.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BearerAuthHandler;
import io.vertx.ext.web.handler.HttpException;

public class BearerAuthHandlerImpl extends HTTPAuthorizationHandler<AuthenticationProvider> implements BearerAuthHandler {

    public BearerAuthHandlerImpl(AuthenticationProvider authProvider, String realm) {
        super(authProvider, Type.BEARER, realm);
    }

    @Override
    public void authenticate(RoutingContext context, Handler<AsyncResult<User>> handler) {

        parseAuthorization(context, parseAuthorization -> {
            if (parseAuthorization.failed()) {
                handler.handle(Future.failedFuture(parseAuthorization.cause()));
                return;
            }

            authProvider.authenticate(new TokenCredentials(parseAuthorization.result()), authn -> {
                if (authn.failed()) {
                    handler.handle(Future.failedFuture(new HttpException(401, authn.cause())));
                } else {
                    handler.handle(authn);
                }
            });
        });
    }
}


