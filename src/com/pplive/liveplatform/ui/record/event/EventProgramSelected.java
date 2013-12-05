package com.pplive.liveplatform.ui.record.event;

import com.pplive.liveplatform.core.rest.model.Program;

public class EventProgramSelected extends Event<Program> {
    
    public EventProgramSelected(Program program) {
        super(program);
    }

}
