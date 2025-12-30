package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import lombok.Data;

/**
 * 
 * 
 */
@Data
@TableName("invitation_code")
public class InvitationCodeEntity {
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 閭€璇风爜
	 */
	private String code;

	/**
	 * 鐢熸垚閭€璇风爜鐨勪汉
	 */
	private Long createUser;

	/**
	 * 浣跨敤閭€璇峰悧鐨勪汉
	 */
	private Long useUser;

	/**
	 * 銆? 鏈浣跨敤锛?0 宸茬粡琚娇鐢ㄣ€?	 */
	private Integer useStatus;

	/**
	 * 鐢熸垚鏃堕棿
	 */
	private Long createTime;

	/**
	 * 浣跨敤鏃堕棿
	 */
	private Long useTime;

}
