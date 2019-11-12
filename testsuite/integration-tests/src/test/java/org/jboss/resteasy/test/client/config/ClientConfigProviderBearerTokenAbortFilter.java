package org.jboss.resteasy.test.client.config;

import org.junit.Assert;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class ClientConfigProviderBearerTokenAbortFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString("Authorization");
        Assert.assertEquals("The request authorization header is not correct", "Bearer myTestToken", authorizationHeader);
        Assert.assertTrue("The request authorization header should not contain both token and Basic credentials", !(authorizationHeader.contains("Basic") && authorizationHeader.contains("Bearer")));
        requestContext.abortWith(Response.ok().build());
    }
}
