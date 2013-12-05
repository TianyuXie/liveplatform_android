package com.pplive.liveplatform.ui.record.event;

import com.pplive.liveplatform.core.rest.model.Program;

public class EventProgramAdded extends Event<Program> {

    public EventProgramAdded(Program program) {
        super(program);
    }
}
