package org.xwalk.core.internal;

@XWalkAPI(createInternally = true)
public class XWalkHitTestResultInternal {
    private String mExtra;
    private int mType = 0;

    @XWalkAPI
    public enum type {
        UNKNOWN_TYPE,
        ANCHOR_TYPE,
        PHONE_TYPE,
        GEO_TYPE,
        EMAIL_TYPE,
        IMAGE_TYPE,
        IMAGE_ANCHOR_TYPE,
        SRC_ANCHOR_TYPE,
        SRC_IMAGE_ANCHOR_TYPE,
        EDIT_TEXT_TYPE
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setExtra(String extra) {
        this.mExtra = extra;
    }

    @XWalkAPI
    public type getType() {
        switch (this.mType) {
            case 0:
                return type.UNKNOWN_TYPE;
            case 1:
                return type.ANCHOR_TYPE;
            case 2:
                return type.PHONE_TYPE;
            case 3:
                return type.GEO_TYPE;
            case 4:
                return type.EMAIL_TYPE;
            case 5:
                return type.IMAGE_TYPE;
            case 6:
                return type.IMAGE_ANCHOR_TYPE;
            case 7:
                return type.SRC_ANCHOR_TYPE;
            case 8:
                return type.SRC_IMAGE_ANCHOR_TYPE;
            case 9:
                return type.EDIT_TEXT_TYPE;
            default:
                return type.UNKNOWN_TYPE;
        }
    }

    @XWalkAPI
    public String getExtra() {
        return this.mExtra;
    }
}
