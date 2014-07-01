package com.pplive.liveplatform.ui.live.event;

import com.pplive.liveplatform.core.api.live.model.Program;

public class EventProgramDeleted extends Event<Program> {

    public EventProgramDeleted(Program program) {
        super(program);
    }
}
