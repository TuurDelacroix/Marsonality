package be.howest.ti.mars.web.bridge;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.impl.UserImpl;

public class TokenManager implements AuthenticationProvider {

    static final String AUTH_VALUE = "39d9c89d822364d803feab9ae0171ec7d5b20ef4d558b088aeaec7ecbdefe23e";

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {
        TokenCredentials tokenCredentials = credentials.mapTo(TokenCredentials.class);
        String token = tokenCredentials.getToken();

        if (token.equals(AUTH_VALUE)) {
            resultHandler.handle(Future.succeededFuture(
                    new UserImpl()
            ));
        } else
        {
            resultHandler.handle(Future.failedFuture("This is a wrong token."));
        }
    }
}
