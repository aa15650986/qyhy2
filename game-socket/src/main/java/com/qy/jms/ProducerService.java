

package com.qy.jms;

import com.qy.model.Messages;

import javax.jms.Destination;

public interface ProducerService {
    void sendMessage(Destination var1, String var2);

    void sendMessage(String var1);

    void sendMessage(Destination var1, Object var2);

    void sendMessage(Destination var1, Messages var2);
}
