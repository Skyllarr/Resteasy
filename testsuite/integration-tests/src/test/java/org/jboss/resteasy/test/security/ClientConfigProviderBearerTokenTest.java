package org.jboss.resteasy.test.security;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.security.helperFilters.ClientConfigProviderBearerTokenAbortFilter;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Test;
import org.wildfly.security.auth.client.AuthenticationConfiguration;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.wildfly.security.auth.client.MatchRule;
import org.wildfly.security.credential.BearerTokenCredential;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientConfigProviderBearerTokenTest {

    String dummyUrl = "dummyUrl";

    /**
     * @tpTestDetails Test that bearer token is loaded from ClientConfigProvider implementation and is used in Authorization header.
     * This is done with registered filter that checks Authorization header.
     */
    @Test
    public void testClientWithBearerToken() {
        AuthenticationContext previousAuthContext = AuthenticationContext.getContextManager().getGlobalDefault();
        try {
            BearerTokenCredential bearerTokenCredential = new BearerTokenCredential("myTestToken");
            AuthenticationConfiguration adminConfig = AuthenticationConfiguration.empty().useBearerTokenCredential(bearerTokenCredential);
            AuthenticationContext context = AuthenticationContext.empty();
            context = context.with(MatchRule.ALL, adminConfig);
            AuthenticationContext.getContextManager().setGlobalDefault(context);
            Runnable runnable = new Runnable() {
                public void run() {
                    ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                    ResteasyClient client = builder.build();
                    Response response = client.target(dummyUrl)
                            .register(ClientConfigProviderBearerTokenAbortFilter.class).request().get();
                    Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                    client.close();
                }
            };
            context.run(runnable);
        } finally {
            AuthenticationContext.getContextManager().setGlobalDefault(previousAuthContext);
        }
    }

    /**
     * @tpTestDetails Test that credentials from ClientConfigProvider implementation are not used for HTTP Basic auth if Bearer token can be retrieved from ClientConfigProvider implementation.
     * This is done with registered filter that checks Authorization header.
     */
    @Test
    public void testClientWithBearerTokenAndCredentials() {
        AuthenticationContext previousAuthContext = AuthenticationContext.getContextManager().getGlobalDefault();
        try {
            BearerTokenCredential bearerTokenCredential = new BearerTokenCredential("myTestToken");
            AuthenticationConfiguration adminConfig = AuthenticationConfiguration.empty().useName("username").usePassword("password").useBearerTokenCredential(bearerTokenCredential);
            AuthenticationContext context = AuthenticationContext.empty();
            context = context.with(MatchRule.ALL, adminConfig);
            AuthenticationContext.getContextManager().setGlobalDefault(context);
            Runnable runnable = new Runnable() {
                public void run() {
                    ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                    ResteasyClient client = builder.build();
                    Response response = client.target(dummyUrl)
                            .register(ClientConfigProviderBearerTokenAbortFilter.class).request().get();
                    Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                    client.close();
                }
            };
            context.run(runnable);
        } finally {
            AuthenticationContext.getContextManager().setGlobalDefault(previousAuthContext);
        }
    }

    /**
     * @tpTestDetails Test that request does not contain Bearer token if none is retrieved from ClientConfigProvider implementation.
     * This is done with registered filter that checks Authorization header.
     */
    @Test
    public void testClientWithoutBearerToken() {
        AuthenticationContext previousAuthContext = AuthenticationContext.getContextManager().getGlobalDefault();
        try {
            AuthenticationConfiguration adminConfig = AuthenticationConfiguration.empty();
            AuthenticationContext context = AuthenticationContext.empty();
            context = context.with(MatchRule.ALL, adminConfig);
            AuthenticationContext.getContextManager().setGlobalDefault(context);
            Runnable runnable = new Runnable() {
                public void run() {
                    ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                    ResteasyClient client = builder.build();
                    try {
                        client.target(dummyUrl).register(ClientConfigProviderBearerTokenAbortFilter.class).request().get();
                        fail("Configuration not found ex should be thrown.");
                    } catch (Exception e) {
                        assertTrue(e.getMessage().contains("The request authorization header is not correct expected:<Bearer myTestToken> but was:<null>"));
                    } finally {
                        client.close();
                    }
                }
            };
            context.run(runnable);
        } finally {
            AuthenticationContext.getContextManager().setGlobalDefault(previousAuthContext);
        }
    }

    /**
     * @tpTestDetails Test that request does choose credentials based on destination of the request.
     */
    @Test
    public void testClientChooseCorrectBearerToken() {
        AuthenticationContext previousAuthContext = AuthenticationContext.getContextManager().getGlobalDefault();
        try {
            BearerTokenCredential bearerTokenCredential = new BearerTokenCredential("myTestToken");
            AuthenticationConfiguration adminConfig = AuthenticationConfiguration.empty().useBearerTokenCredential(bearerTokenCredential);
            AuthenticationContext context = AuthenticationContext.empty();
            context = context.with(MatchRule.ALL.matchHost("www.redhat.com"), adminConfig);
            AuthenticationContext.getContextManager().setGlobalDefault(context);
            Runnable runnable = new Runnable() {
                public void run() {
                    ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                    ResteasyClient client = builder.build();
                    try {
                        client.target(dummyUrl).register(ClientConfigProviderBearerTokenAbortFilter.class).request().get();
                        fail("Configuration not found ex should be thrown.");
                    } catch (Exception e) {
                        assertTrue(e.getMessage().contains("The request authorization header is not correct expected:<Bearer myTestToken> but was:<null>"));
                    } finally {
                        client.close();
                    }
                }
            };
            context.run(runnable);
        } finally {
            AuthenticationContext.getContextManager().setGlobalDefault(previousAuthContext);
        }
    }

    /**
     * @tpTestDetails Test that request does choose credentials based on destination of the request.
     */
    @Test
    public void testClientChooseCorrectBearerToken2() {
        BearerTokenCredential bearerTokenCredential = new BearerTokenCredential("myTestToken");
        AuthenticationConfiguration authenticationConfiguration = AuthenticationConfiguration.empty().useBearerTokenCredential(bearerTokenCredential);
        AuthenticationContext context = AuthenticationContext.empty();
        context = context.with(MatchRule.ALL.matchHost("127.0.0.1"), authenticationConfiguration);
        Runnable runnable = new Runnable() {
            public void run() {
                ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
                ResteasyClient client = builder.build();
                Response response = client.target("http://127.0.0.1").register(ClientConfigProviderBearerTokenAbortFilter.class).request().get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                client.close();
            }
        };
        context.run(runnable);
    }
}
