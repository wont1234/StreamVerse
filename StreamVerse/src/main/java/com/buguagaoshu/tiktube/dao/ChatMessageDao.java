package com.buguagaoshu.tiktube.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buguagaoshu.tiktube.entity.ChatMessageEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageDao extends BaseMapper<ChatMessageEntity> {
}
