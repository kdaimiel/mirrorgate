/*
 * Copyright 2017 Banco Bilbao Vizcaya Argentaria, S.A..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bbva.arq.devops.ae.mirrorgate.service;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Channel;
import allbegray.slack.webapi.SlackWebApiClient;
import com.bbva.arq.devops.ae.mirrorgate.dto.SlackDTO;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SlackServiceImpl implements SlackService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public SlackDTO getToken(final String team, final String clientId, final String clientSecret, final String code) {
        final UriComponents uri = UriComponentsBuilder.newInstance()
            .scheme("https")
            .host(team + ".slack.com")
            .path("/api/oauth.access")
            .queryParam("client_id", clientId)
            .queryParam("client_secret", clientSecret)
            .queryParam("code", code)
            .build()
            .encode();

        return restTemplate.getForObject(uri.toUriString(), SlackDTO.class);
    }

    @Override
    public SlackDTO getWebSocket(final String team, final String token) throws ResourceAccessException {
        final UriComponents uri = UriComponentsBuilder.newInstance()
            .scheme("https")
            .host(team + ".slack.com")
            .path("/api/rtm.connect")
            .queryParam("token", token)
            .build()
            .encode();

        return restTemplate.getForObject(uri.toUriString(), SlackDTO.class);
    }

    @Override
    public Map<String, String> getChannelList(final String slackToken) {
        final SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient(slackToken);
        final List<Channel> channelList = webApiClient.getChannelList();
        return channelList.stream().collect(Collectors.toMap(Channel::getId, Channel::getName));
    }

}
