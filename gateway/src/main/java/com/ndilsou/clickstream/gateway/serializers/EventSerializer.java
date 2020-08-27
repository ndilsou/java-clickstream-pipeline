package com.ndilsou.clickstream.gateway.serializers;

import com.ndilsou.clickstream.common.events.Event;

import io.atlassian.fugue.Try;

public interface EventSerializer {

  Try<String> serialize(Event record);

}
