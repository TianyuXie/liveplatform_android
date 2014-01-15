package com.pplive.liveplatform.ui.live.event;

import com.pplive.liveplatform.core.service.live.model.Program;

public class EventProgramSelected extends Event<Program> {
    
    public EventProgramSelected(Program program) {
        super(program);
    }

}
