package it.eng.idsa;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import it.eng.idsa.util.PropertiesConfig;
import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.TokenBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;

import org.apache.http.HttpEntity;

import org.apache.http.entity.mime.MultipartEntityBuilder;

import org.apache.http.util.EntityUtils;

public class ConsumerConnection {
	private static final PropertiesConfig CONFIG_PROPERTIES = PropertiesConfig.getInstance();
	private static Logger LOG = Logger.getLogger(ConsumerConnection.class.getName());
	private static String token = null;


	public static void main(String[] args) {
		// TODO Auto-generated method stub




		try {
			LOG.debug("ConsumerConnector starting...");

			Path targetDirectory=Paths.get(CONFIG_PROPERTIES.getProperty("targetDirectory"));
			String dapsUrl=CONFIG_PROPERTIES.getProperty("dapsUrl");
			String keyStoreName=CONFIG_PROPERTIES.getProperty("keyStoreName");
			String keyStorePassword=CONFIG_PROPERTIES.getProperty("keyStorePassword");
			String keystoreAliasName=CONFIG_PROPERTIES.getProperty("keystoreAliasName");
			String connectorUUID=CONFIG_PROPERTIES.getProperty("connectorUUID");

			DAPSInteraction dapsInteraction=new DAPSInteraction();
			token=dapsInteraction.acquireToken(targetDirectory, dapsUrl, keyStoreName, keyStorePassword, keystoreAliasName, connectorUUID);

			LOG.debug("TOKEN="+token);


			ClientConfig config = new ClientConfig();
			config.connectorProvider(new ApacheConnectorProvider());


			/*
			 * config.property(ClientProperties.PROXY_URI, "proxy_url");
			 * config.property(ClientProperties.PROXY_USERNAME,"user_name");
			 * config.property(ClientProperties.PROXY_PASSWORD,"password");
			 */
			Client client = ClientBuilder.newClient(config);		
			client.register(MultiPartFeature.class);

			WebTarget webTarget = client.target(CONFIG_PROPERTIES.getProperty("providerUri"));

			Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);



			URI uri=new URI (CONFIG_PROPERTIES.getProperty("uriSchema")+CONFIG_PROPERTIES.getProperty("uriAuthority")+CONFIG_PROPERTIES.getProperty("uriArtifact")+CONFIG_PROPERTIES.getProperty("artifactId"));
			HttpEntity entity = createArtifactRequest(uri);
			if (entity != null) {
				String retSrc = EntityUtils.toString(entity); 
				System.out.println("retSrc="+retSrc);
			}





			/**OLD
			Response response = invocationBuilder.post(Entity.json(new it.eng.idsa.ArtifactRequestMessage(token)));
			 **/
			Response response = invocationBuilder.post(Entity.entity(EntityUtils.toString(entity), "multipart/mixed"));
			//HttpResponse resp = sendArtifactRequest(entity,CONFIG_PROPERTIES.getProperty("providerUri"));

			
			if (response.getStatus()==Response.Status.UNAUTHORIZED.getStatusCode()) {
				LOG.debug("UNAUTHORIZED");
			}
			if (response.getStatus()==Response.Status.ACCEPTED.getStatusCode()) {
				LOG.debug("ACCEPTED");
				MessageConsumerApp messageConsumerApp=new MessageConsumerApp();
				messageConsumerApp.activateListening();
				LOG.debug("***Listening Activated***");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

	private static HttpEntity createArtifactRequest(URI requestedArtifact) throws IOException, DatatypeConfigurationException, ConstraintViolationException, URISyntaxException {
		GregorianCalendar gcal = new GregorianCalendar();
		XMLGregorianCalendar xgcal = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(gcal);
		Message msg = new ArtifactRequestMessageBuilder(new URI (CONFIG_PROPERTIES.getProperty("uriSchema")+CONFIG_PROPERTIES.getProperty("uriAuthority")+CONFIG_PROPERTIES.getProperty("uriPath")+UUID.randomUUID().toString()))
				._requestedArtifact_(requestedArtifact)
				._issuerConnector_(new URI (CONFIG_PROPERTIES.getProperty("uriSchema")+CONFIG_PROPERTIES.getProperty("uriAuthority")+CONFIG_PROPERTIES.getProperty("uriConnector")+UUID.randomUUID().toString()))
				._issued_(xgcal)
				._modelVersion_("1.0.3")
				._authorizationToken_(new TokenBuilder(null)
						._tokenFormat_(TokenFormat.JWT)
						._tokenValue_(token).build())
				.build();
		String msgSerialized = new Serializer().serializePlainJson(msg);
		return MultipartEntityBuilder
				.create()
				.addTextBody("header", msgSerialized, org.apache.http.entity.ContentType.APPLICATION_JSON)
				.build();
	}
/*
	private static HttpResponse sendArtifactRequest(HttpEntity multipartRequestBody, String providerUrl) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(providerUrl);
			httpPost.setEntity(multipartRequestBody);
			return httpclient.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
*/


}
