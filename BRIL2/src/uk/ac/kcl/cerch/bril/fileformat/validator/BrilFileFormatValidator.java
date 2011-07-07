package uk.ac.kcl.cerch.bril.fileformat.validator;

import java.io.File;

import uk.ac.kcl.cerch.bril.fileformat.FormatValidation;
import uk.ac.kcl.cerch.soapi.fileformat.identifier.FileFormatIdentifierException;

public interface BrilFileFormatValidator {

	public FormatValidation validateBrilFileFormat(File file) throws FileFormatIdentifierException;
}
