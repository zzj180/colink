package com.unisound.unicar.gui.model;

/**
 * 
 * @author xiaodong
 * @date 20150807
 */
public class SupportDomain {

    /**
     * SessionPreference.DOMAIN_XXX
     */
    public String type;

    /**
     * FUNCTION array res id
     */
    public int resourceId;

    public boolean hasNetwork;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public boolean isHasNetwork() {
        return hasNetwork;
    }

    public void setHasNetwork(boolean hasNetwork) {
        this.hasNetwork = hasNetwork;
    }

}
