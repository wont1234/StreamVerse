package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 
 * 
 *
 * 宸插純鐢? */
@Data
@TableName("web_setting")
public class WebSettingEntity {
	/**
	 * 缃戠珯鍚?	 */
	private String name;

	/**
	 * 鏄惁寮€鍚潪vip姣忔棩瑙傜湅娆℃暟闄愬埗 [0 鍏抽棴锛?1 寮€鍚痌
	 */
	private Integer openNoVipLimit;

	/**
	 * 闈瀡ip 姣忔棩瑙傜湅娆℃暟
	 */
	private Integer noVipViewCount;

	/**
	 * 缃戦〉logo鍦板潃
	 */
	private String logoUrl;

	/**
	 * 鏄惁寮€鍚個璇风爜娉ㄥ唽 銆? 鍏抽棴锛?1寮€鍚€?	 */
	private Integer openInvitationRegister;

	/**
	 * 缃戦〉绠€鐭殑鎻忚堪
	 */
	private String webDescribe;

	/**
	 * 鏄惁寮€鍚瘡鏃ヤ笂浼犺棰戝鍔犻潪浼氬憳瑙傜湅娆℃暟 銆? 鍏抽棴锛?1寮€鍚€?	 */
	private Integer openUploadVideoAddViewCount;

	/**
	 * 鏄惁寮€鍚棰戯紝鏂囩珷锛屽浘鐗囩洓鍜?銆? 鍏抽棴锛?1 寮€鍚€?	 */
	private Integer openExamine;

	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 鍒涘缓鏃堕棿
	 */
	private Long createTime;



	private Integer homeMaxVideoCount;

}
