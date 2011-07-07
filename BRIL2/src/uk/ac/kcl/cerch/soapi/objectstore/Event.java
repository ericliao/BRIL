package uk.ac.kcl.cerch.soapi.objectstore;

@SuppressWarnings("serial")
public class Event extends ObjectArtifact {

    private String event;
    private String relatedObjectArtifactId; // ObjectArtifact Id whose checksum is represented.
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
    public String getRelatedObjectArtifactId() {
        return relatedObjectArtifactId;
    }
    public void setRelatedObjectArtifactId(String relatedObjectArtifactId) {
        this.relatedObjectArtifactId = relatedObjectArtifactId;
    }
  
}
