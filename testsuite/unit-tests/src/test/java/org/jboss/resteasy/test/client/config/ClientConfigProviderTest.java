package org.jboss.resteasy.test.client.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.test.client.resource.RequestFilterAnnotation;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

public class ClientConfigProviderTest {

//    static Client client;
//
    String dummyUrl = "dummyUrl";
//
//    @BeforeClass
//    public static void setupClient() {
//        client = ClientBuilder.newClient();
//    }
//
//    @AfterClass
//    public static void close() {
//        client.close();
//    }

    /**
     * @tpTestDetails Test for removing property from WebTarget.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void propertyNullTest() throws Exception {
        String property = "property";

        ResteasyClient client = new ResteasyClientBuilderImpl().build();
        ClientWebTarget clientWebTarget = (ClientWebTarget) client.target(dummyUrl);

        Assert.assertTrue("Client properties should not be empty", client.getConfiguration().getProperties().isEmpty());

        clientWebTarget.property(property, property);

        Assert.assertEquals("Add of property faild", Collections.singletonMap(property, property), clientWebTarget.getConfiguration().getProperties());

        try {
            clientWebTarget.property(property, null);
        } catch (NullPointerException ex) {
            Assert.fail("Cannot remove property with null value.");
        }

        Object value = clientWebTarget.getConfiguration().getProperty(property);
        Assert.assertNull("Property from webTarget can not be removed", value);

        Entity<String> post = Entity.entity("test", MediaType.WILDCARD_TYPE,
                RequestFilterAnnotation.class.getAnnotations());
        Response response = client.target(dummyUrl).register(RequestFilterAnnotation.class).request().post(post);
        Assert.assertEquals("The response doesn't contain the expexted provider name",
                Provider.class.getName(), response.readEntity(String.class));
    }

    //create your authentication configuration
//      AuthenticationConfiguration adminConfig =
//              AuthenticationConfiguration.empty()
//                      .useName("bill")
//                      .usePassword("password1!");
//
////create your authentication context
//      AuthenticationContext context = AuthenticationContext.empty();
//      context = context.with(MatchRule.ALL, adminConfig);
//
//
////create your runnable for establishing a connection
//      Runnable runnable =
//              new Runnable() {
//                 public void run() {
//                    try {
//                       UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password1");
//                       CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//                       credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials);
//                       CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
//                       ApacheHttpClientEngine engine = ApacheHttpClientEngine.create(client);
//
//                       ResteasyClient authorizedClient = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).httpEngine(engine).build();
//                       Response response = authorizedClient.target(generateURL("/secured/deny")).request().get();
//                       Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
//                       Assert.assertEquals(ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class));
//                       authorizedClient.close();
//                    } catch (Exception e) {
//                       e.printStackTrace();
//                    }
//                 }
//              };
//
////use your authentication context to run your client
//      context.run(runnable);

    //    @Test
//    public void testWithClientRequestFilterAuthorizedUser() throws Exception{
//       AuthenticationContext previousAuthContext = AuthenticationContext.getContextManager().getGlobalDefault();
//       SSLContext previousDefaultSSLContext = SSLContext.getDefault();
//       try {
//        Response response = authorizedClientUsingRequestFilter.target(generateURL("/secured/authorized")).request().get();
//        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
//        Assert.assertEquals(WRONG_RESPONSE, "authorized", response.readEntity(String.class));
//       } finally {
//          AuthenticationContext.getContextManager().setGlobalDefault(previousAuthContext);
//          SSLContext.setDefault(previousDefaultSSLContext);
//       }
//    }

    //    @Test
//    public void testWithClientRequestFilterUnauthorizedUser() {
//
//       AuthenticationConfiguration adminConfig =
//               AuthenticationConfiguration.empty()
//                       .useName("se")
//                       .usePassword("se");
//
////create your authentication context
//       AuthenticationContext context = AuthenticationContext.empty();
//       context = context.with(MatchRule.ALL, adminConfig);
//
////create your runnable for establishing a connection
//       Runnable runnable =
//               new Runnable() {
//                  public void run() {
//                     try {
//                        Response response = unauthorizedClientUsingRequestFilter.target(generateURL("/secured/authorized")).request().get();
//                        Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
//                        Assert.assertEquals(WRONG_RESPONSE, ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class));
//
//                     } catch (Exception e) {
//                        e.printStackTrace();
//                     }
//                  }
//               };
////use your authentication context to run your client
//       context.run(runnable);
//    }
//
//    @Test
//    @RunAsClient
//    public void temp () throws Exception {
//       AuthenticationContext previousAuthContext = AuthenticationContext.getContextManager().getGlobalDefault();
//       SSLContext previousDefaultSSLContext = SSLContext.getDefault();
//       try {
//          ElytronClientTestUtils.setElytronClientConfig("/home/skylar/work/projects/Resteasy/testsuite/integration-tests/src/test/resources/wildfly-config-test.xml");
//
//          UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("username", "password1");
//          CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//          credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials);
//          CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
//          ApacheHttpClientEngine engine = ApacheHttpClientEngine.create(httpClient);
//          ResteasyClient client  = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).httpEngine(engine).build();
//
////          ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
//          client.target(generateURL("/secured/authorized")).request().get();
//          System.err.println("i am here");
//          ElytronClientTestUtils.setElytronClientConfig("/home/skylar/work/projects/Resteasy/testsuite/integration-tests/src/test/resources/wildfly-config-test.xml");
//       } finally {
//          AuthenticationContext.getContextManager().setGlobalDefault(previousAuthContext);
//          SSLContext.setDefault(previousDefaultSSLContext);
//       }
//    }
}
