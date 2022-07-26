package com.ganbook.models;

import com.google.gson.annotations.SerializedName;

public class UserAttachmentModel {

    @SerializedName("id")
    private String documentId;

    @SerializedName("document_name")
    private String documentName;

    @SerializedName("document_title")
    private String documentTitle;

    @SerializedName("document_description")
    private String documentDescription;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }
}
