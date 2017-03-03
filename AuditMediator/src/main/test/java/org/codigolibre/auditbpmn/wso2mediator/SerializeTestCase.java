package org.codigolibre.auditbpmn.wso2mediator;

import java.io.StringWriter;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.config.SynapseConfigUtils;
import org.apache.synapse.mediators.TestUtils;
import org.codigolibre.auditbpmn.jaxb.BusinessProcessAudit;
import org.junit.Test;

public class SerializeTestCase extends AbstractAuditMediatorTestCase {

	private String serializeXMLProxyVariable = "<audit xmlns=\"http://ws.apache.org/ns/synapse\">\n"
			+ "	<serialize target=\"salidavariable\"/>\n" + "</audit>";

	private String serializeXMLProxyVariableCOMPARE = "<businessProcessAudit xmlns=\"urn:org:codigolibre:businessprocessaudit:type:v1.0.0\">\n" + 
			"	<id xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" + 
			"	<name xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" + 
			"	<startTime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" + 
			"	<endTime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" + 
			"	<status xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" + 
			"</businessProcessAudit>\n"
			;
	
	
	private String serializeJSON = "<audit xmlns=\"http://ws.apache.org/ns/synapse\">\n"
			+ "	<serialize target=\"salidavariable\" media-type=\"json\"/>\n" + "</audit>";
	
	private String serializeJSONCOMPARE = "{\"businessProcessAudit\":{\"id\":{\"@nil\":\"true\"},\"name\":{\"@nil\":\"true\"},\"startTime\":{\"@nil\":\"true\"},\"endTime\":{\"@nil\":\"true\"},\"status\":{\"@nil\":\"true\"}}}";
	
	private String serializeLOGGER = "<audit xmlns=\"http://ws.apache.org/ns/synapse\">\n" 
			+ "	<serialize logger=\"loggerOUT\" media-type=\"json\"/>\n" + "</audit>";
	
	
	private String serializeDetailLow = "<audit xmlns=\"http://ws.apache.org/ns/synapse\">\n" 
			+ "	<serialize target=\"salidavariable\" detail=\"low\"/>\n" + "</audit>";
	
	private String serializeDetailMEDIUM = "<audit xmlns=\"http://ws.apache.org/ns/synapse\">\n" 
			+ "	<serialize target=\"salidavariable\" detail=\"medium\"/>\n" + "</audit>";
	
	private String serializeDetailHIGH = "<audit xmlns=\"http://ws.apache.org/ns/synapse\">\n" 
			+ "	<serialize target=\"salidavariable\" detail=\"high\"/>\n" + "</audit>";
	
	
	

	private String serializeXMLBPOriginal="<businessProcessAudit xmlns=\"urn:org:codigolibre:businessprocessaudit:type:v1.0.0\"><id>SincronizacionAnuncios</id><name>Sincronizacion Anuncios OVESAE</name><correlationID>2124cf6b-470d-4e87-b226-2586b4c0cb26</correlationID><startTime>2017-03-03T09:46:00.024+01:00</startTime><endTime>2017-03-03T09:46:01.635+01:00</endTime><status>OK</status><activities><subProcessAudit><id>crearAnuncios</id><name>Crear Anuncios</name><startTime>2017-03-03T09:46:00.258+01:00</startTime><endTime>2017-03-03T09:46:00.798+01:00</endTime><status>OK</status><activities><sendTaskAudit><id>consultaAnunciosPendientes</id><name>Consulta Anuncios Pendientes</name><startTime>2017-03-03T09:46:00.303+01:00</startTime><endTime>2017-03-03T09:46:00.717+01:00</endTime><status>OK</status><inputParams><param name=\"numeroAnuncios\">0.0</param></inputParams><webServiceAudit><endPoint>gov:/intersaeEndpoints/SincronizacionAnunciosDSEP</endPoint><operation>SincronizacionCrearAnunciosDesdeTemporal</operation><type>SOAP</type><idMsg>urn:uuid:a6f97db3-49d5-4afd-8b9e-54cace72a1b8</idMsg><msgRequest>&lt;?xml version='1.0' encoding='utf-8'?&gt;&lt;soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;soapenv:Body&gt;&lt;SincronizacionCrearAnunciosDesdeTemporal xmlns=\"urn:juntadeandalucia:ceice:intersae:sincronizacioncrearanuncios:type:v1.0.0\"/&gt;&lt;/soapenv:Body&gt;&lt;/soapenv:Envelope&gt;</msgRequest><msgResponse>&lt;soapenv:Body xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;SincronizacionCrearAnunciosTemporal xmlns=\"urn:juntadeandalucia:ceice:intersae:sincronizacioncrearanuncios:type:v1.0.0\"/&gt;&lt;/soapenv:Body&gt;</msgResponse><ipClient>10.240.234.53</ipClient><hostClient>dessae03</hostClient><ipServer>10.240.234.203</ipServer><hostServer>dssdes.sae.junta-andalucia.es</hostServer></webServiceAudit></sendTaskAudit></activities></subProcessAudit><subProcessAudit><id>primerCandidato</id><name>Primer Candidato</name><startTime>2017-03-03T09:46:00.904+01:00</startTime><endTime>2017-03-03T09:46:01.091+01:00</endTime><status>OK</status><activities><sendTaskAudit><id>consultaPrimerCandidatoDesdeTemporal</id><name>Consulta Primer Candidato Desde BD</name><startTime>2017-03-03T09:46:00.956+01:00</startTime><endTime>2017-03-03T09:46:01.036+01:00</endTime><status>OK</status><inputParams><param name=\"numeroCandidatos\">0.0</param></inputParams><webServiceAudit><endPoint>gov:/intersaeEndpoints/SincronizacionAnunciosDSEPP</endPoint><operation>SincronizacionPrimerCandidatoDesdeTemporal</operation><type>SOAP</type><idMsg>urn:uuid:7efc0bce-5261-47e5-8805-a34e889de7df</idMsg><msgRequest>&lt;?xml version='1.0' encoding='utf-8'?&gt;&lt;soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;soapenv:Body&gt;&lt;p:SincronizacionPrimerCandidatoDesdeTemporal xmlns:p=\"es.juntadeandalucia.ceice.intersae.sincronizacionanuncios\"/&gt;&lt;/soapenv:Body&gt;&lt;/soapenv:Envelope&gt;</msgRequest><msgResponse>&lt;soapenv:Body xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;SincronizacionPrimerCandidatoAnuncios xmlns=\"urn:juntadeandalucia:ceice:intersae:sincronizacionprimercandidatoanuncios:type:v1.0.0\"/&gt;&lt;/soapenv:Body&gt;</msgResponse><ipClient>10.240.234.53</ipClient><hostClient>dessae03</hostClient><ipServer>10.240.234.203</ipServer><hostServer>dssdes.sae.junta-andalucia.es</hostServer></webServiceAudit></sendTaskAudit></activities></subProcessAudit><subProcessAudit><id>enviarCandidatos</id><name>Enviar Candidatos</name><startTime>2017-03-03T09:46:01.143+01:00</startTime><endTime>2017-03-03T09:46:01.337+01:00</endTime><status>OK</status><activities><taskAudit><id>busquedaCodigosAnunciosPendientes</id><name>Búsqueda Codigos Anuncios Pendientes</name><startTime>2017-03-03T09:46:01.211+01:00</startTime><endTime>2017-03-03T09:46:01.269+01:00</endTime><status>OK</status><inputParams><param name=\"codigosAnuncios\"/></inputParams></taskAudit></activities></subProcessAudit><subProcessAudit><id>cerrarAnuncios</id><name>Cerrar Anuncios</name><startTime>2017-03-03T09:46:01.392+01:00</startTime><endTime>2017-03-03T09:46:01.577+01:00</endTime><status>OK</status><activities><sendTaskAudit><id>busquedaCerrarAnunciosPendientes</id><name>Busqueda Cerrar Anuncios Pendientes</name><startTime>2017-03-03T09:46:01.441+01:00</startTime><endTime>2017-03-03T09:46:01.520+01:00</endTime><status>OK</status><inputParams><param name=\"numeroAnuncios\">0.0</param></inputParams><webServiceAudit><endPoint>gov:/intersaeEndpoints/SincronizacionAnunciosDSEP</endPoint><operation>SincronizacionCerrarAnunciosDesdeTemporal</operation><type>SOAP</type><idMsg>urn:uuid:b1368498-33aa-42a1-b80f-20b2032eb423</idMsg><msgRequest>&lt;?xml version='1.0' encoding='utf-8'?&gt;&lt;soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;soapenv:Body&gt;&lt;p:SincronizacionCerrarAnunciosDesdeTemporal xmlns:p=\"es.juntadeandalucia.ceice.intersae.sincronizacionanuncios\"/&gt;&lt;/soapenv:Body&gt;&lt;/soapenv:Envelope&gt;</msgRequest><msgResponse>&lt;soapenv:Body xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;SincronizacionCerrarAnuncios xmlns=\"urn:juntadeandalucia:ceice:intersae:sincronizacioncerraranuncios:type:v1.0.0\"/&gt;&lt;/soapenv:Body&gt;</msgResponse><ipClient>10.240.234.53</ipClient><hostClient>dessae03</hostClient><ipServer>10.240.234.203</ipServer><hostServer>dssdes.sae.junta-andalucia.es</hostServer></webServiceAudit></sendTaskAudit></activities></subProcessAudit></activities></businessProcessAudit>";
	
	private String serializeLOWDetailCOMPARE="<businessProcessAudit xmlns=\"urn:org:codigolibre:businessprocessaudit:type:v1.0.0\"><id>SincronizacionAnuncios</id><name>Sincronizacion Anuncios OVESAE</name><correlationID>2124cf6b-470d-4e87-b226-2586b4c0cb26</correlationID><startTime>2017-03-03T09:46:00.024+01:00</startTime><endTime>2017-03-03T09:46:01.635+01:00</endTime><status>OK</status></businessProcessAudit>";
	
	private String serializeMEDIUMDetailCOMPARE="<businessProcessAudit xmlns=\"urn:org:codigolibre:businessprocessaudit:type:v1.0.0\"><id>SincronizacionAnuncios</id><name>Sincronizacion Anuncios OVESAE</name><correlationID>2124cf6b-470d-4e87-b226-2586b4c0cb26</correlationID><startTime>2017-03-03T09:46:00.024+01:00</startTime><endTime>2017-03-03T09:46:01.635+01:00</endTime><status>OK</status><activities><subProcessAudit><id>crearAnuncios</id><name>Crear Anuncios</name><startTime>2017-03-03T09:46:00.258+01:00</startTime><endTime>2017-03-03T09:46:00.798+01:00</endTime><status>OK</status><activities><sendTaskAudit><id>consultaAnunciosPendientes</id><name>Consulta Anuncios Pendientes</name><startTime>2017-03-03T09:46:00.303+01:00</startTime><endTime>2017-03-03T09:46:00.717+01:00</endTime><status>OK</status><inputParams><param name=\"numeroAnuncios\">0.0</param></inputParams></sendTaskAudit></activities></subProcessAudit><subProcessAudit><id>primerCandidato</id><name>Primer Candidato</name><startTime>2017-03-03T09:46:00.904+01:00</startTime><endTime>2017-03-03T09:46:01.091+01:00</endTime><status>OK</status><activities><sendTaskAudit><id>consultaPrimerCandidatoDesdeTemporal</id><name>Consulta Primer Candidato Desde BD</name><startTime>2017-03-03T09:46:00.956+01:00</startTime><endTime>2017-03-03T09:46:01.036+01:00</endTime><status>OK</status><inputParams><param name=\"numeroCandidatos\">0.0</param></inputParams></sendTaskAudit></activities></subProcessAudit><subProcessAudit><id>enviarCandidatos</id><name>Enviar Candidatos</name><startTime>2017-03-03T09:46:01.143+01:00</startTime><endTime>2017-03-03T09:46:01.337+01:00</endTime><status>OK</status><activities><taskAudit><id>busquedaCodigosAnunciosPendientes</id><name>Búsqueda Codigos Anuncios Pendientes</name><startTime>2017-03-03T09:46:01.211+01:00</startTime><endTime>2017-03-03T09:46:01.269+01:00</endTime><status>OK</status><inputParams><param name=\"codigosAnuncios\"></param></inputParams></taskAudit></activities></subProcessAudit><subProcessAudit><id>cerrarAnuncios</id><name>Cerrar Anuncios</name><startTime>2017-03-03T09:46:01.392+01:00</startTime><endTime>2017-03-03T09:46:01.577+01:00</endTime><status>OK</status><activities><sendTaskAudit><id>busquedaCerrarAnunciosPendientes</id><name>Busqueda Cerrar Anuncios Pendientes</name><startTime>2017-03-03T09:46:01.441+01:00</startTime><endTime>2017-03-03T09:46:01.520+01:00</endTime><status>OK</status><inputParams><param name=\"numeroAnuncios\">0.0</param></inputParams></sendTaskAudit></activities></subProcessAudit></activities></businessProcessAudit>";
	
	private String serializeHIGHDetailCOMPARE="<businessProcessAudit xmlns=\"urn:org:codigolibre:businessprocessaudit:type:v1.0.0\"><id>SincronizacionAnuncios</id><name>Sincronizacion Anuncios OVESAE</name><correlationID>2124cf6b-470d-4e87-b226-2586b4c0cb26</correlationID><startTime>2017-03-03T09:46:00.024+01:00</startTime><endTime>2017-03-03T09:46:01.635+01:00</endTime><status>OK</status><activities><subProcessAudit><id>crearAnuncios</id><name>Crear Anuncios</name><startTime>2017-03-03T09:46:00.258+01:00</startTime><endTime>2017-03-03T09:46:00.798+01:00</endTime><status>OK</status><activities><sendTaskAudit><id>consultaAnunciosPendientes</id><name>Consulta Anuncios Pendientes</name><startTime>2017-03-03T09:46:00.303+01:00</startTime><endTime>2017-03-03T09:46:00.717+01:00</endTime><status>OK</status><inputParams><param name=\"numeroAnuncios\">0.0</param></inputParams><webServiceAudit><endPoint>gov:/intersaeEndpoints/SincronizacionAnunciosDSEP</endPoint><operation>SincronizacionCrearAnunciosDesdeTemporal</operation><type>SOAP</type><idMsg>urn:uuid:a6f97db3-49d5-4afd-8b9e-54cace72a1b8</idMsg><msgRequest>&lt;?xml version='1.0' encoding='utf-8'?&gt;&lt;soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;soapenv:Body&gt;&lt;SincronizacionCrearAnunciosDesdeTemporal xmlns=\"urn:juntadeandalucia:ceice:intersae:sincronizacioncrearanuncios:type:v1.0.0\"/&gt;&lt;/soapenv:Body&gt;&lt;/soapenv:Envelope&gt;</msgRequest><msgResponse>&lt;soapenv:Body xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;SincronizacionCrearAnunciosTemporal xmlns=\"urn:juntadeandalucia:ceice:intersae:sincronizacioncrearanuncios:type:v1.0.0\"/&gt;&lt;/soapenv:Body&gt;</msgResponse><ipClient>10.240.234.53</ipClient><hostClient>dessae03</hostClient><ipServer>10.240.234.203</ipServer><hostServer>dssdes.sae.junta-andalucia.es</hostServer></webServiceAudit></sendTaskAudit></activities></subProcessAudit><subProcessAudit><id>primerCandidato</id><name>Primer Candidato</name><startTime>2017-03-03T09:46:00.904+01:00</startTime><endTime>2017-03-03T09:46:01.091+01:00</endTime><status>OK</status><activities><sendTaskAudit><id>consultaPrimerCandidatoDesdeTemporal</id><name>Consulta Primer Candidato Desde BD</name><startTime>2017-03-03T09:46:00.956+01:00</startTime><endTime>2017-03-03T09:46:01.036+01:00</endTime><status>OK</status><inputParams><param name=\"numeroCandidatos\">0.0</param></inputParams><webServiceAudit><endPoint>gov:/intersaeEndpoints/SincronizacionAnunciosDSEPP</endPoint><operation>SincronizacionPrimerCandidatoDesdeTemporal</operation><type>SOAP</type><idMsg>urn:uuid:7efc0bce-5261-47e5-8805-a34e889de7df</idMsg><msgRequest>&lt;?xml version='1.0' encoding='utf-8'?&gt;&lt;soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;soapenv:Body&gt;&lt;p:SincronizacionPrimerCandidatoDesdeTemporal xmlns:p=\"es.juntadeandalucia.ceice.intersae.sincronizacionanuncios\"/&gt;&lt;/soapenv:Body&gt;&lt;/soapenv:Envelope&gt;</msgRequest><msgResponse>&lt;soapenv:Body xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;SincronizacionPrimerCandidatoAnuncios xmlns=\"urn:juntadeandalucia:ceice:intersae:sincronizacionprimercandidatoanuncios:type:v1.0.0\"/&gt;&lt;/soapenv:Body&gt;</msgResponse><ipClient>10.240.234.53</ipClient><hostClient>dessae03</hostClient><ipServer>10.240.234.203</ipServer><hostServer>dssdes.sae.junta-andalucia.es</hostServer></webServiceAudit></sendTaskAudit></activities></subProcessAudit><subProcessAudit><id>enviarCandidatos</id><name>Enviar Candidatos</name><startTime>2017-03-03T09:46:01.143+01:00</startTime><endTime>2017-03-03T09:46:01.337+01:00</endTime><status>OK</status><activities><taskAudit><id>busquedaCodigosAnunciosPendientes</id><name>Búsqueda Codigos Anuncios Pendientes</name><startTime>2017-03-03T09:46:01.211+01:00</startTime><endTime>2017-03-03T09:46:01.269+01:00</endTime><status>OK</status><inputParams><param name=\"codigosAnuncios\"></param></inputParams></taskAudit></activities></subProcessAudit><subProcessAudit><id>cerrarAnuncios</id><name>Cerrar Anuncios</name><startTime>2017-03-03T09:46:01.392+01:00</startTime><endTime>2017-03-03T09:46:01.577+01:00</endTime><status>OK</status><activities><sendTaskAudit><id>busquedaCerrarAnunciosPendientes</id><name>Busqueda Cerrar Anuncios Pendientes</name><startTime>2017-03-03T09:46:01.441+01:00</startTime><endTime>2017-03-03T09:46:01.520+01:00</endTime><status>OK</status><inputParams><param name=\"numeroAnuncios\">0.0</param></inputParams><webServiceAudit><endPoint>gov:/intersaeEndpoints/SincronizacionAnunciosDSEP</endPoint><operation>SincronizacionCerrarAnunciosDesdeTemporal</operation><type>SOAP</type><idMsg>urn:uuid:b1368498-33aa-42a1-b80f-20b2032eb423</idMsg><msgRequest>&lt;?xml version='1.0' encoding='utf-8'?&gt;&lt;soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;soapenv:Body&gt;&lt;p:SincronizacionCerrarAnunciosDesdeTemporal xmlns:p=\"es.juntadeandalucia.ceice.intersae.sincronizacionanuncios\"/&gt;&lt;/soapenv:Body&gt;&lt;/soapenv:Envelope&gt;</msgRequest><msgResponse>&lt;soapenv:Body xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"&gt;&lt;SincronizacionCerrarAnuncios xmlns=\"urn:juntadeandalucia:ceice:intersae:sincronizacioncerraranuncios:type:v1.0.0\"/&gt;&lt;/soapenv:Body&gt;</msgResponse><ipClient>10.240.234.53</ipClient><hostClient>dessae03</hostClient><ipServer>10.240.234.203</ipServer><hostServer>dssdes.sae.junta-andalucia.es</hostServer></webServiceAudit></sendTaskAudit></activities></subProcessAudit></activities></businessProcessAudit>";	

	
	
	@Test
	public void testXMLSerializeInsideVariable() throws Exception {
		genericTest(serializeXMLProxyVariable, serializeXMLProxyVariableCOMPARE);
	}
	
	
	@Test
	public void testVariable() throws Exception {
		beforeClass();
		String proxyData = serializeJSON;
		String serializaOKCompare =serializeJSONCOMPARE;
		
		Mediator auditMediator = auditMediatorFactory.createMediator(
				SynapseConfigUtils.stringToOM(serializeJSON),
				new Properties());

		
		
		MessageContext synCtx = TestUtils
				.createLightweightSynapseMessageContext("<empty/>");

		// perform mediation
		assertTrue(auditMediator.mediate(synCtx));

		// check if audit xml contents variable serialization and validate xml
		// transaccion
		String variable = null;
		if (proxyData.indexOf("target=\"") > 0) {
			variable = proxyData.substring(
					proxyData.indexOf("target=\"") + 8,
					proxyData
							.indexOf("\"", proxyData
									.indexOf("target=\"") + 8));

		} else
			return;

		String serializationXML = (String) synCtx.getProperty(variable);

		assertEquals("\n Valid "
				+ serializaOKCompare + "\n Generated "
				+ serializationXML,serializaOKCompare ,serializationXML);

	}
	
	@Test
	public void testLOGGER() throws Exception {
		beforeClass();
		String proxyData = serializeLOGGER;
		String serializaOKCompare =serializeJSONCOMPARE;
		
		// check if audit xml contents variable serialization and validate xml
		// transaccion
		String loggerName = null;
		if (proxyData.indexOf("logger=\"") > 0) {
			loggerName = proxyData.substring(
					proxyData.indexOf("logger=\"") + 8,
					proxyData
							.indexOf("\"", proxyData
									.indexOf("logger=\"") + 8));

		} else
			return;

		StringWriter sw = new StringWriter();
	    WriterAppender a = new WriterAppender();
	    a.setLayout(new PatternLayout("%m "));
	    a.setWriter(sw);
	    a.activateOptions();
	    BasicConfigurator.configure(a);
		
	    Logger logger = Logger.getLogger(loggerName);
	
		Mediator auditMediator = auditMediatorFactory.createMediator(
				SynapseConfigUtils.stringToOM(proxyData),
				new Properties());
		
		MessageContext synCtx = TestUtils
				.createLightweightSynapseMessageContext("<empty/>");

		// perform mediation
		assertTrue(auditMediator.mediate(synCtx));

		String serializationXML  = sw.getBuffer().toString().trim();

		assertEquals("\n Valid "
				+ serializaOKCompare + "\n Generated "
				+ serializationXML,serializaOKCompare ,serializationXML);
		

		
	}
	
	
	
	@Test
	public void testLOWDetailLevels() throws Exception {
		beforeClass();
		String businessProcessXML = serializeXMLBPOriginal;
		String proxyData = serializeDetailLow;
		String serializaOKCompare =serializeLOWDetailCOMPARE;

		// create a message context with audit object defined in XML 
		MessageContext synCtx = setBusinessProcessAuditObject(serializeXMLBPOriginal);

		// create audit command
		Mediator auditMediator = auditMediatorFactory.createMediator(
				SynapseConfigUtils.stringToOM(proxyData),
				new Properties());
		
		// perform mediation
		assertTrue(auditMediator.mediate(synCtx));

		// check if audit xml contents variable serialization and validate xml
		// transaccion
		String variable = null;
		if (proxyData.indexOf("target=\"") > 0) {
			variable = proxyData.substring(
					proxyData.indexOf("target=\"") + 8,
					proxyData
							.indexOf("\"", proxyData
									.indexOf("target=\"") + 8));

		} else
			return;

		String serializationXML = (String) synCtx.getProperty(variable);

		assertEquals("\n Valid "
				+ serializaOKCompare + "\n Generated "
				+ serializationXML,serializaOKCompare ,serializationXML);
	
		
	}

	
	@Test
	public void testMEDIUMDetailLevels() throws Exception {
		beforeClass();
		String businessProcessXML = serializeXMLBPOriginal;
		String proxyData = serializeDetailMEDIUM;
		String serializaOKCompare =serializeMEDIUMDetailCOMPARE;

		// create a message context with audit object defined in XML 
		MessageContext synCtx = setBusinessProcessAuditObject(serializeXMLBPOriginal);

		// create audit command
		Mediator auditMediator = auditMediatorFactory.createMediator(
				SynapseConfigUtils.stringToOM(proxyData),
				new Properties());
		
		// perform mediation
		assertTrue(auditMediator.mediate(synCtx));

		// check if audit xml contents variable serialization and validate xml
		// transaccion
		String variable = null;
		if (proxyData.indexOf("target=\"") > 0) {
			variable = proxyData.substring(
					proxyData.indexOf("target=\"") + 8,
					proxyData
							.indexOf("\"", proxyData
									.indexOf("target=\"") + 8));

		} else
			return;

		String serializationXML = (String) synCtx.getProperty(variable);

		assertEquals("\n Valid "
				+ serializaOKCompare + "\n Generated "
				+ serializationXML,serializaOKCompare ,serializationXML);
	
		
	}
	
	
	@Test
	public void testHIGHDetailLevels() throws Exception {
		beforeClass();
		String businessProcessXML = serializeXMLBPOriginal;
		String proxyData = serializeDetailHIGH;
		String serializaOKCompare =serializeHIGHDetailCOMPARE;

		// create a message context with audit object defined in XML 
		MessageContext synCtx = setBusinessProcessAuditObject(serializeXMLBPOriginal);

		// create audit command
		Mediator auditMediator = auditMediatorFactory.createMediator(
				SynapseConfigUtils.stringToOM(proxyData),
				new Properties());
		
		// perform mediation
		assertTrue(auditMediator.mediate(synCtx));

		// check if audit xml contents variable serialization and validate xml
		// transaccion
		String variable = null;
		if (proxyData.indexOf("target=\"") > 0) {
			variable = proxyData.substring(
					proxyData.indexOf("target=\"") + 8,
					proxyData
							.indexOf("\"", proxyData
									.indexOf("target=\"") + 8));

		} else
			return;

		String serializationXML = (String) synCtx.getProperty(variable);

		assertEquals("\n Valid "
				+ serializaOKCompare + "\n Generated "
				+ serializationXML,serializaOKCompare ,serializationXML);
	
		
	}
	
}
