package com.example.runningservice.service.notification;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.TableType;
import java.util.List;

public interface NotificationManagerService {

    String getMessage(Long relatedId, TableType relatedType);

    List<MemberEntity> findSubscriber(Long relatedId, TableType relatedType);
}
