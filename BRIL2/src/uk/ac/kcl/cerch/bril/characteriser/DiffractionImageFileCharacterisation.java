package uk.ac.kcl.cerch.bril.characteriser;

import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;

public class DiffractionImageFileCharacterisation extends FileCharacterisation{

	private String diffImageSetIdentifier;
	private String diffImageIdentifier;
	
	public void setDiffImageSetIdentifier(String diffImageSetIdentifier){
		this.diffImageSetIdentifier=diffImageSetIdentifier;
	}
	
	public String getDiffImageSetIdentifier(){
		return diffImageSetIdentifier;
	}
	
	public void setDiffImageIdentifier(String diffImageIdentifier){
		this.diffImageIdentifier=diffImageIdentifier;
	}
	
	public String getDiffImageIdentifier(){
		return diffImageIdentifier;
	}
}
