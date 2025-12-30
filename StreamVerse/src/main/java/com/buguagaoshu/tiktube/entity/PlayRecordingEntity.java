package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 鎾斁璁板綍
 * 
 */
@Data
@TableName("play_recording")
public class PlayRecordingEntity {
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 瑙嗛ID
	 */
	private Long articleId;

	/**
	 * 瑙傜湅鍒扮鍑犱釜瑙嗛
	 */
	private Long fileId;

	/**
	 * 鏃堕棿鎴?	 */
	private Double videoTime;

	/**
	 * 鐢ㄦ埛ID
	 */
	private Long userId;

	/**
	 * 鍒涘缓鏃堕棿
	 */
	private Long createTime;

	/**
	 * 鏇存柊鏃堕棿
	 */
	private Long updateTime;


	private long videoId;

	private String ua;

}
