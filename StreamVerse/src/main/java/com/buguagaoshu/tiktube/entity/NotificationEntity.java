package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import lombok.Data;

/**
 * 閫氱煡琛? * 
 */
@Data
@TableName("notification")
public class NotificationEntity {
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 閫氱煡鍙戦€佷汉ID
	 */
	private Long notifier;

	/**
	 * 閫氱煡鎺ユ敹浜篒D
	 */
	private Long receiver;

	/**
	 * 澶栭儴ID锛屽涓诲笘瀛怚D,璇勮ID
	 */
	private Long outerId;


	private long articleId;

	private String title;

	/**
	 * 閾炬帴淇℃伅
	 * */
	private String linkMessage;

	/**
	 * 鍐呭
	 */
	private String content;

	/**
	 * 璇勮鐩爣ID
	 */
	private Long commentId;

	/**
	 * 绫诲瀷 銆? 鍥炲甯栧瓙锛?1 鍥炲璇勮锛? 鏀跺埌鐐硅禐 銆?	 * 鏇村淇℃伅瑙?{@link  com.buguagaoshu.tiktube.enums.NotificationType} 鏋氫妇绫?	 */
	private Integer type;

	/**
	 * 
	 */
	private Long createTime;

	private Long readTime;

	/**
	 * 銆? 鏈锛?1 宸茶銆?	 */
	private Integer status;
}
