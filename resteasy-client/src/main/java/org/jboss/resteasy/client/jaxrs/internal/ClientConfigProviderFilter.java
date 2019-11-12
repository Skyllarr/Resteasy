package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;
import org.jboss.resteasy.util.BasicAuthHelper;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.URI;
import java.util.ServiceLoader;

/**
 * Client filter that will attach authorization header with either HTTP Basic auth or Bearer token auth.
 * Credentials and token are loaded from ClientConfigProvider implementation.
 *
 * @author dvilkola@redhat.com
 */
public class ClientConfigProviderFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (requestContext.getHeaderString(HttpHeaders.AUTHORIZATION) == null) {
            URI uri = requestContext.getUri();
            // TODO find a better place for clientConfigProvider instance so it is not loaded in filter during each request
            ClientConfigProvider clientConfigProvider = ServiceLoader.load(ClientConfigProvider.class).iterator().next();
            if (clientConfigProvider != null) {
                String token = clientConfigProvider.getBearerToken(uri);
                if (token != null) {
                    requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                } else {
                    String username = clientConfigProvider.getUsername(uri);
                    String password = clientConfigProvider.getPassword(uri);
                    if (username != null && password != null) {
                        requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, BasicAuthHelper.createHeader(username, password));
                    }
                }
            }
        }
    }
}
