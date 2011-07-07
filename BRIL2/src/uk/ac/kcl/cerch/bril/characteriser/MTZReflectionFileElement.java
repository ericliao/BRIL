package uk.ac.kcl.cerch.bril.characteriser;

public enum MTZReflectionFileElement {
	MTZ_CRYSTAL_NAME("crystal_name"),
	MTZ_DATASET_NAME( "dataset_name"),
	MTZ_HISTORY( "history"),
	MTZ_FROM_PROGRAM( "from_program"),
    MTZ_REFLECTION_TYPE( "reflection_type"),
    MTZ_NUMBER_OF_BATCHES( "number_of_batches"),
    MTZ_NUMBER_OF_REFLECTION( "number_of_reflection"),
    MTZ_WAVELENGTH( "wavelength"),
    MTZ_CELL_DIMENSION( "cell_dimensions"),
    MTZ_CELL_UNIT( "cell_unit"),
    MTZ_SPACE_GROUP( "space_group"),
    MTZ_RESOLUTION_RANGE( "resolution_range"),
    MTZ_COLUMN_LABELS( "column_labels"),
    MTZ_R_FREE_FLAG_PERCENTAGE( "r-free_flag_percentage");
	
	private String localname;
	MTZReflectionFileElement( String localName )
    {
        this.localname = localName;
    }
 public String localName()
 {
     return this.localname;
 }
}
