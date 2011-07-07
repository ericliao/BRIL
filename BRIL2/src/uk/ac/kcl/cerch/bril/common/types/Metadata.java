package uk.ac.kcl.cerch.bril.common.types;

import java.io.OutputStream;

public interface Metadata {
	  /**
    *
    *
    * @param out
    * @param identifier
    */
   public void serialize( OutputStream out, String identifier )throws BrilException;    

   public DataStreamType getType();
}
