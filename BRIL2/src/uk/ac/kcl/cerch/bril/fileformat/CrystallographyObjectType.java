package uk.ac.kcl.cerch.bril.fileformat;

import org.apache.log4j.Logger;

public enum CrystallographyObjectType {
	AlignmentFile("alignmentFile",".aln alignment file of protein"),	
	DiffractionImage("diffractionImage","diffraction image"),
	CompressedDiffractionImage("compressedDiffractionImage","compressed diffraction image"),	
	MTZReflectionFile("mtzReflectionFile","MTZ reflection file"),
	CoordinateFile("coordinateFile","PDB coordinate file"),
	COMFile("comFile","com script file"),
	LOGFile("logFile","a text log file that generated when running a script i.e., associated to a com file"),
	CCP4IDefFile("ccp4iDefFile","DEF file of CCP4I software"),
	PhenixDefFile("phenixDefFile","DEF file of Phenix software"),
	CootStateExeFile("cootStateExeFile","scm file of Coot software use to visualise PDB file"),
	MosflmLpFile("mosflmLpFile","mosflm.lp runnable file of Mosflm software"),
	MosflmSavFile("mosflmSavFile",".sav file of Mosflm software contains final parameter used to merge the diffraction images to create an MTZ file"),
	MosflmGenFile("mosflmGenFile",".gen file of Mosflm software file generated when as a snapshot task- the merging of diffraction"),
	MiscFile("miscFile","other misc file created during the experiment"),
	DOCFile("docFile","doc/docx/rtf file that contains the description of the available protein sequences"), 
	SEQFile("seqFile","sequence file that contains the chosen sequences that will be used to search for similar proteins");

	private final String objectType;
	private final String description;
	static Logger log = Logger.getLogger( CrystallographyObjectType.class );

	CrystallographyObjectType(String objectType, String description) {
		this.objectType = objectType;
        this.description = description;
	}
	
	public String getType() {
		return this.objectType;
	}

	public String getDescription() {
		return this.description;
	}
	
	
	/**
	 * 
	 * @param nameType
	 */
	public static boolean validCrystallographyObjectType(String objectTypeName) {
		CrystallographyObjectType COTName = CrystallographyObjectType.getCrystallographyObjectType(objectTypeName);
	       
        if( COTName == null )
        {
                return false;
        }
       
        return true;
	}

	/**
	 * 
	 * @param name
	 */
	public static CrystallographyObjectType getCrystallographyObjectType(String name) {
		CrystallographyObjectType COTName = null;
        for (CrystallographyObjectType cotn : CrystallographyObjectType.values() )
            
            if( name.toLowerCase().equals( cotn.getType().toLowerCase() ) )
            {
            	COTName = cotn;
            }
    
        return COTName;
	}

}
