package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.gateway.TopicQuotaDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.JmxSwitchDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicDeletionDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.ConfigConstant;
import com.xiaojukeji.kafka.manager.web.config.CustomDataSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author xuguang
 * @Date 2022/2/21
 */
public class OpQuotaControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();

        String url = baseUrl + "/api/v1/op/topics";
        createCommonTopic(url);
    }

    @AfterClass
    public void afterTest() {
        // 删除Topic成功
        String url = baseUrl + "/api/v1/op/topics";
        deleteTopics(url);
    }

    private void createCommonTopic(String url) {
        // 创建Topic

        TopicCreationDTO creationDTO = CustomDataSource.getTopicCreationDTO(configMap);
        HttpEntity<TopicCreationDTO> httpEntity = new HttpEntity<>(creationDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteTopics(String url) {
        // 删除创建的topic
        TopicDeletionDTO topicDeletionDTO = CustomDataSource.getTopicDeletionDTO(configMap);
        HttpEntity<List<TopicDeletionDTO>> httpEntity2 = new HttpEntity<>(Arrays.asList(topicDeletionDTO), httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试配额调整")
    public void getTopicQuotas() {
        TopicQuotaDTO topicQuotaDTO = new TopicQuotaDTO();
        Long logicalClusterId = Long.parseLong(configMap.get(ConfigConstant.LOGICAL_CLUSTER_ID));
        topicQuotaDTO.setClusterId(logicalClusterId);
        topicQuotaDTO.setTopicName(configMap.get(ConfigConstant.TOPIC_NAME));
        topicQuotaDTO.setProduceQuota(1L);
        topicQuotaDTO.setConsumeQuota(1L);
        topicQuotaDTO.setAppId(configMap.get(ConfigConstant.APPID));

        String url = baseUrl + "/api/v1/op/topic-quotas";
        HttpEntity<TopicQuotaDTO> httpEntity2 = new HttpEntity<>(topicQuotaDTO, httpHeaders);
        ResponseEntity<Result> result2 = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity2, Result.class);
        Assert.assertEquals(result2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result2.getBody());
        Assert.assertEquals(result2.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
