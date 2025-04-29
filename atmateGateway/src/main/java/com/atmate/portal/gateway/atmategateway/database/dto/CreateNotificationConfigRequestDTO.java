package com.atmate.portal.gateway.atmategateway.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationConfigRequestDTO {

    List<Integer> clientsIDs; //Ids
    List<Integer> taxTypeIDs; //Ids
    List<Integer> notificationTypeList; //SMS ou EMAIL
    String frequency;
    boolean active;
    int startPeriod;
}
