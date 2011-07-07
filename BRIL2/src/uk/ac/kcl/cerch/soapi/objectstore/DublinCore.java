package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * <code>DublinCore</code> represents a Dublin Core record as defined by the Dublin
 * Core Metadata Element Set, Version 1.1 (http://www.dublincore.org/documents/dces).
 * 
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class DublinCore extends ObjectArtifact {
    private String contributor;
    private String coverage;
    private String creator;
    private String date;
    private String description;
    private String format;
    private String identifier;
    private String language;
    private String publisher;
    private String relation;
    private String rights;
    private String source;
    private String subject;
    private String title;
    private String type;
    private String relatedObjectArtifactId; // ObjectArtifact Id whose Dublin Core is represented.  
    
    public String getContributor() {
        return contributor;
    }
    
    public void setContributor(String contributor) {
        this.contributor = contributor;
    }
    
    public String getCoverage() {
        return coverage;
    }
    
    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }
    
    public String getCreator() {
        return creator;
    }
    
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
              
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public String getRelation() {
        return relation;
    }
    
    public void setRelation(String relation) {
        this.relation = relation;
    }
    
    public String getRights() {
        return rights;
    }
    
    public void setRights(String rights) {
        this.rights = rights;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

 
    /**
     * Returns the Id of the <code>ObjectArtifact</code> whose Dublin Core is represented.
     * 
     * @return Related <code>ObjectArtifact</code> Id.
     */
    public String getRelatedObjectArtifactId() {
        return relatedObjectArtifactId;
    }

    
    /**
     * Sets the Id of the <code>ObjectArtifact</code> whose Dublin Core is represented.
     * 
     * @param relatedObjectArtifactId Related <code>ObjectArtifact</code> Id.
     */
    public void setRelatedObjectArtifactId(String relatedObjectArtifactId) {
        this.relatedObjectArtifactId = relatedObjectArtifactId;
    }
}