package com.dogpaws.backend.service.cys;

import com.dogpaws.backend.dto.ajy.DogDto;
import com.dogpaws.backend.repository.dao.cys.ChatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2025-02-11 by 최윤서
 */
@Service
public class ChatService {

    @Autowired
    private ChatDao chatDao;

    //profile 불러오기
    public List<DogDto> getChatProfile(List<Integer> otherParticipants) {
        List<DogDto> profiles = new ArrayList<>();

        for (Integer otherParticipant : otherParticipants) {
            DogDto profile = chatDao.getChatProfile(otherParticipant);
            if (profile != null) {
                profiles.add(profile);
            }
        }
        return profiles;
    };


}
