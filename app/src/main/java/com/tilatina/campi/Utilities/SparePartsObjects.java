package com.tilatina.campi.Utilities;

/**
 * Derechos reservados tilatina.
 */

public class SparePartsObjects {
    private String sparePartId;
    private String sparePartName;

    public SparePartsObjects(){}

    public String getSparePartsId() {
        return sparePartId;
    }

    public SparePartsObjects setSparePartsId(String spareparts_id) {
        this.sparePartId = spareparts_id;

        return this;
    }

    String getSparePartsName() {
        return sparePartName;
    }

    public SparePartsObjects setSparePartsName(String part_name) {
        this.sparePartName = part_name;

        return this;
    }
}
