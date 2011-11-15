package uk.ac.kcl.cerch.bril.characteriser;

public enum MTZReflectionFileElement {
	MTZ_CRYSTAL_NAME("CrystalName"),
	MTZ_DATASET_NAME( "DatasetName"),
	MTZ_HISTORY( "History"),
	MTZ_FROM_PROGRAM( "FromProgram"),
    MTZ_REFLECTION_TYPE( "ReflectionType"),
    MTZ_NUMBER_OF_BATCHES( "NumberOfBatches"),
    MTZ_NUMBER_OF_REFLECTION( "NumberOfReflections"),
    MTZ_WAVELENGTH( "Wavelength"),
    MTZ_CELL_DIMENSION( "CellDimensions"),
    MTZ_CELL_UNIT( "CellUnit"),
    MTZ_SPACE_GROUP( "SpaceGroup"),
    MTZ_RESOLUTION_RANGE( "ResolutionRange"),
    MTZ_COLUMN_LABELS( "ColumnLabels"),
    MTZ_R_FREE_FLAG_PERCENTAGE( "RFreePercentage");
	
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
