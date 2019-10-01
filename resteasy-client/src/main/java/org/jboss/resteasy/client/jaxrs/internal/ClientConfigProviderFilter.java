package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;
import org.jboss.resteasy.util.BasicAuthHelper;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.URI;
import java.util.ServiceLoader;

public class ClientConfigProviderFilter implements ClientRequestFilter {

   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      if (requestContext.getHeaderString(HttpHeaders.AUTHORIZATION) == null) {
         URI uri = requestContext.getUri();
         ClientConfigProvider serviceLoader = ServiceLoader.load(ClientConfigProvider.class).iterator().next();
         if (serviceLoader != null) {
            String token = serviceLoader.getBearerToken(uri);
            if (token != null) {
               requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            } else {
               String username = serviceLoader.getUsername(uri);
               String password = serviceLoader.getPassword(uri);
               if (username != null && password != null) {
                  requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, BasicAuthHelper.createHeader(username, password));
               }
            }
         }
      }
   }
}
