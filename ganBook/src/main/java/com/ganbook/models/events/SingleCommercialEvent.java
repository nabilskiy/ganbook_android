package com.ganbook.models.events;

import com.ganbook.models.Commercial;

public class SingleCommercialEvent {
    private Commercial commercial;

    public SingleCommercialEvent(Commercial commercial) {
        this.commercial = commercial;
    }

    public Commercial getCommercial() {
        return commercial;
    }

    public void setCommercial(Commercial commercial) {
        this.commercial = commercial;
    }
}
