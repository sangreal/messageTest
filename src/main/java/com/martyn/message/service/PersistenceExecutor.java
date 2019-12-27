package com.martyn.message.service;

import com.martyn.message.common.ThreadHelper;
import com.martyn.message.data.Message;
import com.martyn.message.data.Offset;
import com.martyn.message.data.repository.MessageRepository;
import com.martyn.message.data.repository.OffsetRepository;
import com.martyn.message.exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PersistenceExecutor {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private OffsetRepository offsetRepository;

    public void persistMessage(Message message) {
        messageRepository.save(message);
    }

    @Transactional(rollbackFor = Exception.class)
    public void persistOffset(Offset offset) {
        List<Offset> prevlist = offsetRepository.findByTopicAndUserId(offset.getTopic(), offset.getUserId());
        if (prevlist.size() == 0) {
            offsetRepository.save(offset);
        } else if (prevlist.size() == 1) {
            Offset prev = prevlist.get(0);
            prev.setOffset(offset.getOffset());
            if (prev.getOffset() < offset.getOffset()) {
                offsetRepository.save(prev);
            }
        }
    }

}
