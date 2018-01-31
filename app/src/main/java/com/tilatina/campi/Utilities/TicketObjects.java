package com.tilatina.campi.Utilities;

/**
 * Derechos reservados tilatina.
 */

public class TicketObjects {
    private String date;
    private String task;
    private String user;
    private String eventId;
    private String eventCanAdd;
    private int elementType;
    private String linkPhoto;

    public TicketObjects() {}

    String getDate(){
        return date;
    }

    public TicketObjects setDate(String tDate) {
        this.date = tDate;
        return this;
    }

    String getTask() {
        return task;
    }

    public TicketObjects setTask(String tTask) {
        this.task = tTask;
        return this;
    }

    public String getUser() {
        return user;
    }

    public TicketObjects setUser(String tUser) {
        this.user = tUser;
        return this;
    }

    String getLinkPhoto() {return linkPhoto;}

    public TicketObjects setLinkPhoto(String photo) {
        this.linkPhoto = photo;
        return this;
    }

    int getElementType() {
        return elementType;
    }

    public TicketObjects setElementType(int element_type) {
        this.elementType = element_type;
        return this;
    }

    String getEventId() {
        return eventId;
    }

    public TicketObjects setEventId(String event_id) {
        this.eventId = event_id;
        return this;
    }

    String getEventCanAdd() {
        return eventCanAdd;
    }

    public TicketObjects setEventCanAdd(String canAdd) {
        this.eventCanAdd = canAdd;
        return this;
    }
}
