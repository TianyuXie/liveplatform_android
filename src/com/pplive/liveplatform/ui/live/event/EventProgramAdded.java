package com.pplive.liveplatform.ui.live.event;

import com.pplive.liveplatform.core.service.live.model.Program;

public class EventProgramAdded extends Event<Program> {

    public EventProgramAdded(Program program) {
        super(program);
    }
}
