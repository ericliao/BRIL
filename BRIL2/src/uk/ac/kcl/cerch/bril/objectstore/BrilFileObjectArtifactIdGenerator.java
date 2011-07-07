package uk.ac.kcl.cerch.bril.objectstore;

import uk.ac.kcl.cerch.bril.service.uuid.IDGenerator;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifactIdGenerator;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifactIdGeneratorException;

/**
 * @author Shrija
 *
 */
public class BrilFileObjectArtifactIdGenerator implements ObjectArtifactIdGenerator{

	/* (non-Javadoc)
	 * Generates Id based on UUID generator
	 * @see uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifactIdGenerator#generateId()
	 */
	@Override
	public String generateId() throws ObjectArtifactIdGeneratorException {
		 String objectArtifactId = null;
		 objectArtifactId= IDGenerator.generateUUID();
		// TODO Auto-generated method stub
		 return objectArtifactId;
	}

}
