package org.codigolibre.auditbpmn.wso2mediator;

import java.io.File;
import java.io.StringReader;
import java.net.InetAddress;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.config.SynapseConfigUtils;
import org.apache.synapse.mediators.TestUtils;
import org.codigolibre.auditbpmn.jaxb.BusinessProcessAudit;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.xml.sax.InputSource;

public abstract class AbstractAuditMediatorTestCase extends XMLTestCase {

	protected AuditMediatorFactory auditMediatorFactory;
	
	private String stringSoapBody = "   <p:echoInt xmlns:p=\"http://echo.services.core.carbon.wso2.org\">\n"
			+ "      <in>1</in>\n" + "   </p:echoInt>";

	
	
	
	public AbstractAuditMediatorTestCase() {
		super();
		org.apache.log4j.BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		
		// Change level to better debug: Level.TRACE
		Logger.getLogger("org.codigolibre.auditbpmn").setLevel(Level.INFO);
		
		auditMediatorFactory = new AuditMediatorFactory();
		
	}

	@BeforeClass
	public static void beforeClass() {
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setNormalizeWhitespace(true);
	}

	public void genericTest(String proxyTag, String serializeOKtoCompare) throws Exception {
		beforeClass();
		String proxyData = proxyTag;
		//String serializaOKCompare =serializaOKtoCompare;
		String host = 
		InetAddress.getLocalHost()
				.getHostName();
		
		String ip_local = 
		InetAddress.getLocalHost().getHostAddress();
				
		
		serializeOKtoCompare = serializeOKtoCompare.replaceAll("_HOST_", host);
		serializeOKtoCompare = serializeOKtoCompare.replaceAll("_IP_LOCAL_", ip_local);
		
		
		String serializationXML = validateAndGetSerialize(proxyTag);

		// validate transaction xml is correct comparing with a valid XML
		Diff myDiff = new Diff(serializationXML, serializeOKtoCompare);

		assertXMLEqual(myDiff.toString() + "\n Valid "
				+ serializeOKtoCompare + "\n Generated " + serializationXML,
				myDiff, true);

	}

	public  String validateAndGetSerialize(String proxyTag) throws Exception {
		beforeClass();
		String proxyData = proxyTag;
		
		Mediator auditMediator = auditMediatorFactory.createMediator(
				SynapseConfigUtils.stringToOM(proxyData),
				new Properties());

		MessageContext synCtx = TestUtils
				.createLightweightSynapseMessageContext("<empty/>");

		// uncoment to trace 
		//synCtx.setTracingState(SynapseConstants.TRACING_ON);
		
		synCtx.getEnvelope().getBody().getFirstElement().detach();
		synCtx.getEnvelope().getBody()
				.addChild(AXIOMUtil.stringToOM(stringSoapBody));
		
		
		// set ERROR...

		synCtx.setProperty("ERROR_CODE", "ERROR_CODE value");
		synCtx.setProperty("ERROR_MESSAGE", "ERROR_MESSAGE value");
		synCtx.setProperty("ERROR_DETAIL", "ERROR_DETAIL value");

		
		
		synCtx.setProperty("custom", "custom variable value");

		// perform mediation
		assertTrue(auditMediator.mediate(synCtx));
		// verify if auditXMLProxy contains a serialize tag with a target
		// variable, and get this variable
		String variable = null;
		if (proxyData.indexOf("target=\"") > 0) {
			variable = proxyData.substring(
					proxyData.indexOf("target=\"") + 8,
					proxyData.indexOf("\"",
							proxyData.indexOf("target=\"") + 8));

		} else
			return "";

		// get the transaction xml
		String serializationXML = (String) synCtx.getProperty(variable);

		//System.out.println("Serialize " + serializationXML);
		
		// validate transaction xml against its schema
		InputSource is = new InputSource(new StringReader(serializationXML));
		Validator v = new Validator(is);
		v.useXMLSchema(true);
		v.setJAXP12SchemaSource(new File(
				"./src/main/resources/businessProcessAudit.xsd"));

		Assert.assertTrue(v.toString(), v.isValid());

		return serializationXML;
	}
	

	
	public  MessageContext setBusinessProcessAuditObject(String businessProcessAuditXML) throws Exception {
		beforeClass();


		JAXBContext jaxbContext = JAXBContext
				.newInstance(BusinessProcessAudit.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		BusinessProcessAudit busines = (BusinessProcessAudit) jaxbUnmarshaller
				.unmarshal(IOUtils.toInputStream(businessProcessAuditXML));
		
		
		MessageContext synCtx = TestUtils
				.createLightweightSynapseMessageContext("<empty/>");
		
		synCtx
				.setProperty(AuditMediatorUtils.AUDIT_MEDIATOR_CONTEXT_PROPERTY,busines);
		return synCtx;
		

	}

}
