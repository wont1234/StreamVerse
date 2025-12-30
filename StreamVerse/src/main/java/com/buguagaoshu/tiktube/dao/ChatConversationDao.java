package com.buguagaoshu.tiktube.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buguagaoshu.tiktube.entity.ChatConversationEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatConversationDao extends BaseMapper<ChatConversationEntity> {
}
