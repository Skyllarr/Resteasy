package org.jboss.resteasy.test.client.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.RequestFilterTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wildfly.security.auth.client.AuthenticationConfiguration;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.wildfly.security.auth.client.MatchRule;
import org.wildfly.security.credential.BearerTokenCredential;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientConfigProviderBearerTokenTest {

   protected static final Logger logger = LogManager.getLogger(RequestFilterTest.class.getName());

   static Client client;
   String dummyUrl = "dummyUrl";

   @BeforeClass
   public static void setupClient() {
      ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
      client = builder.build();
   }

   @AfterClass
   public static void close() {
      client.close();
   }

   @Test
   public void testClientWithBearerToken() {
      BearerTokenCredential bearerTokenCredential = new BearerTokenCredential("myTestToken");
      AuthenticationConfiguration adminConfig = AuthenticationConfiguration.empty().useBearerTokenCredential(bearerTokenCredential);
      AuthenticationContext context = AuthenticationContext.empty();
      context = context.with(MatchRule.ALL, adminConfig);
      Runnable runnable = new Runnable() {
         public void run() {
            Response response = client.target(dummyUrl)
                  .register(ClientConfigProviderBearerTokenAbortFilter.class).request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         }
      };
      context.run(runnable);
   }

    @Test
    public void testClientWithBearerTokenAndCredentials() {
        BearerTokenCredential bearerTokenCredential = new BearerTokenCredential("myTestToken");
        AuthenticationConfiguration adminConfig = AuthenticationConfiguration.empty().useName("username").usePassword("password").useBearerTokenCredential(bearerTokenCredential);
        AuthenticationContext context = AuthenticationContext.empty();
        context = context.with(MatchRule.ALL, adminConfig);
        Runnable runnable = new Runnable() {
            public void run() {
                Response response = client.target(dummyUrl)
                        .register(ClientConfigProviderBearerTokenAbortFilter.class).request().get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            }
        };
        context.run(runnable);
    }

   @Test
   public void testClientWithoutBearerToken() {
      AuthenticationConfiguration adminConfig = AuthenticationConfiguration.empty();
      AuthenticationContext context = AuthenticationContext.empty();
      context = context.with(MatchRule.ALL, adminConfig);
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               client.target(dummyUrl).register(ClientConfigProviderBearerTokenAbortFilter.class).request().get();
               fail("Configuration not found ex should be thrown.");
            } catch (Exception e) {
               assertTrue(e.getMessage().contains("The request authorization header is not correct expected:<Bearer myTestToken> but was:<null>"));
            }
         }
      };
      context.run(runnable);
   }
}
