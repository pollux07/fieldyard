package com.tilatina.campi.Utilities;

/**
 * Derechos reservados tilatina.
 */
public class ServiceObject {
    private String id;
    private String name;
    private double lat;
    private double lng;
    private int element_type_id;
    private char color;
    private String ticketId;
    private String detail;

    public ServiceObject(){}

    public String getId() {
        return id;
    }

    public ServiceObject setId(String id) {
        this.id = id;

        return this;
    }

    public String getName() {
        return name;
    }

    public ServiceObject setName(String name) {
        this.name = name;

        return this;
    }

    public double getLat() {
        return lat;
    }

    public ServiceObject setLat(double lat) {
        this.lat = lat;

        return this;
    }

    public double getLng() {
        return lng;
    }

    public ServiceObject setLng(double lng) {
        this.lng = lng;

        return this;
    }

    public int getElementTypeId(){return element_type_id;}

    public ServiceObject setElementTypeId(int etID) {
        this.element_type_id = etID;
        return this;
    }

    public char getColor() {
        return color;
    }

    public ServiceObject setColor(char color) {
        this.color = color;

        return this;
    }

    public String getTicketID() {
        return ticketId;
    }

    public ServiceObject setTicketID(String ticketID) {
        this.ticketId = ticketID;
        return this;
    }

    public String getTicketDetail() {
        return  detail;
    }

    public ServiceObject setTicketDetail(String tDetail) {
        this.detail = tDetail;

        return this;
    }
}
