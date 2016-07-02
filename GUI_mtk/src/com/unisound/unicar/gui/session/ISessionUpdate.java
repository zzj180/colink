package com.unisound.unicar.gui.session;

import org.json.JSONObject;

public abstract interface ISessionUpdate {
    void updateSession(JSONObject jsonObject);

    void editSession();
}
