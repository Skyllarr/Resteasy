package org.jboss.resteasy.test.security.helperFilters;

import org.junit.Assert;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class ClientConfigProviderNoBasicAuthorizationHeaderFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString("Authorization");
        Assert.assertTrue("There should be no Basic authorization header", authorizationHeader == null || !authorizationHeader.contains("Basic"));
    }
}
