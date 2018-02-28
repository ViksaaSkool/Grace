package com.grace.app.model;

import retrofit2.Call;

/**
 * Created by varsovski on 15-Jun-17.
 */

public class GraceApiCall {

    public Call getGraceApicall() {
        return graceApicall;
    }

    public void setGraceApicall(Call graceApicall) {
        this.graceApicall = graceApicall;
    }

    private Call graceApicall;
}
