package uk.ac.kcl.cerch.bril.common.fedora;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;

import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import org.fcrepo.server.types.gen.ComparisonOperator;
import org.fcrepo.server.types.gen.Condition;
import org.fcrepo.server.types.gen.Datastream;
import org.fcrepo.server.types.gen.FieldSearchQuery;
import org.fcrepo.server.types.gen.FieldSearchResult;
import org.fcrepo.server.types.gen.MIMETypedStream;
import org.fcrepo.server.types.gen.ObjectFields;
import org.fcrepo.server.types.gen.RelationshipTuple;

public class TestClass {

	/*
	 * xsd:string ingest( xsd:base64Binary XML, xsd:string format, xsd:string
	 * logMessage ) The valid formats are currently:
	 * "info:fedora/fedora-system:FOXML-1.1" and
	 * "info:fedora/fedora-system:METSFedoraExt-1.1"
	 */
	public static void main(String arg[]) throws BrilObjectRepositoryException, ConfigurationException, MalformedURLException, IOException, ServiceException {
		String metsdata = FileUtil
				.writeFileToString("c:/tmp/archive/mets-ingest-example.xml");
		String foxmldata = FileUtil
				.writeFileToString("c:/tmp/archive/foxml-ingest-example.xml");
		FedoraHandler handler;
	
			handler = new FedoraHandler();
		
		byte[] metsbyte = null;
		byte[] foxmlbyte = null;
		String pid = null;
		String property = "subject";
		String value = "test";

		NonNegativeInteger maxResults = new NonNegativeInteger("1000000");
		try {
			metsbyte = metsdata.getBytes("UTF-8");
			foxmlbyte = foxmldata.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// TEST ingest
	
			if (foxmlbyte != null) {
				// pid = FedoraHandler.getInstance().getAPIM().ingest(metsbyte,
				// "info:fedora/fedora-system:METSFedoraExt-1.1",
				// "Ingest of METS");
				// pid = FedoraHandler.getInstance().getAPIM().ingest(foxmlbyte,
				// "info:fedora/fedora-system:FOXML-1.1", "Ingest of FOXML");

			}
			System.out.println(pid);
			String[] resultFields = { "pid", "title" };

			// TEST soap client query to get PIDs:
			System.out
					.println("TEST soap client query to get PIDs--------------------");
			// \Todo: check needed on the operator
			// eq operator = assumes only a single object is returned from the
			// repository - only applicable to search for a specific date.
			ComparisonOperator comp = ComparisonOperator.fromString("has");
			Condition[] cond = { new Condition(property, comp, value) };
			FieldSearchQuery fsq = new FieldSearchQuery(cond, null);
			long timer = System.currentTimeMillis();
			FieldSearchResult fsr = handler.findObjects(resultFields,
					maxResults, fsq);

			ObjectFields[] objectFields = fsr.getResultList();

			int ofLength = objectFields.length;
			String[] pids = new String[ofLength];
			String[] title = new String[ofLength];

			for (int i = 0; i < ofLength; i++) {
				pids[i] = objectFields[i].getPid();
				// title[i] = objectFields[i].getTitle(i);
				// log.debug( "pid " + i + ": " + pids[i].toString() );
			}
			System.out.println("pids: " + pids[0] + ", " + pids[1]);

			// TEST get byte datastream with datastream ID like DC
			System.out
					.println("TEST get byte datastream with datastream ID--------------------");
			MIMETypedStream dstream = handler.getAPIA()
					.getDatastreamDissemination(pids[0], "DC", null);
			byte[] bytestream_DCXML = dstream.getStream();
			// DataStreamType.getDataStreamType( stream.getAttribute(
			// "streamNameType" )
			
			//a null value matches all predicates
			RelationshipTuple[] rt= handler.getRelationships("bril:333", null);
			System.out.println("------------------------------");
			//iterate
			for(int i=0;i<rt.length;i++){			
				System.out.println(rt[i].getSubject());
				System.out.println(rt[i].getPredicate());
				System.out.println(rt[i].getObject());
				System.out.println("------------------------------");
			}
			
			//Datastream get
			
			Datastream ds = handler.getDatastream("bril:333", "DC");
			//ds.
		/*	// TEST triple query
			System.out.println("TEST triple query--------------------");
			String query = "";
			String select = String.format("select $s $%s from <#ri> ", "p");
			String where = "where ";
			where += String.format("$s <dc:%s> '%s' ", "subject", "test");
			String relsNS = String.format("and $s <fedora-rels-ext:%s> $%s ",
					"isMemberOfCollection", "p");
			
			query = select + where + relsNS + "limil 1";
			System.out.println("query: " + query);

			Map<String, String> qparams = new HashMap<String, String>(4);

			qparams.put("lang", "itql");
			qparams.put("flush", "true");
			qparams.put("query", query);

			TupleIterator tuples = handler.getFC()
					.getTuples(qparams);
			if (tuples != null) {
				try {
					while (tuples.hasNext()) {
						// Map<String, Node> row = tuples.next();

					}

				} catch (Exception e) {

				}
			}
	*/
			// TEST hascode generation
			System.out.println("TEST hascode generation--------------------");
			String dataStreamName = "RELS-EXT";
			String cmt = "text/xml";
			String submitter = "stella";
			String data = "<dc/>";
			String language = "english";
			String format = "";
			String alias = "";
			long id = 0L;
			id += dataStreamName.hashCode();
			id += cmt.hashCode();
			id += language.hashCode();
			id += submitter.hashCode();
			id += format.hashCode();
			id += alias.hashCode();
			id += data.hashCode();

			System.out.println(id);

	

	}

}
