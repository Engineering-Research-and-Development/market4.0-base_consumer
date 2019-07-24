package it.eng.idsa;

import java.net.URI;
import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonTypeName;

import de.fraunhofer.iais.eis.Token;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("ids:ArtifactRequestMessage")
public class ArtifactRequestMessage implements de.fraunhofer.iais.eis.ArtifactRequestMessage{
	String tokenValue;

	public ArtifactRequestMessage(String token) {
		// TODO Auto-generated constructor stub
		tokenValue=token;
	}
	
	@Override
	public URI getTransferContract() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getSenderAgent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Token getSecurityToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<? extends URI> getRecipientConnectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<? extends URI> getRecipientAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModelVersion() {
		// TODO Auto-generated method stub
		return "1.0.3-SNAPSHOT";
	}

	@Override
	public URI getIssuerConnector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLGregorianCalendar getIssued() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getCorrelationMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Token getAuthorizationToken() {
		return new it.eng.idsa.Token(tokenValue); 
	}

	@Override
	public @NotNull URI getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toRdf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getRequestedArtifact() {
		// TODO Auto-generated method stub
		return null;
	}

}