package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;
import org.jboss.resteasy.util.BasicAuthHelper;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.URI;

public class ClientConfigProviderFilter implements ClientRequestFilter {

   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      if (requestContext.getHeaderString(HttpHeaders.AUTHORIZATION) == null) {
         URI uri = requestContext.getUri();
         if (requestContext.getProperty(ClientConfigProvider.CLIENT_CONFIG_PROVIDER) != null) {
            ClientConfigProvider clientConfigProvider = (ClientConfigProvider) requestContext.getProperty(ClientConfigProvider.CLIENT_CONFIG_PROVIDER);
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
