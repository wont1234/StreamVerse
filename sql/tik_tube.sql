/*
 Navicat Premium Data Transfer

 Source Server         : MYSQL
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : localhost:3306
 Source Schema         : tik_tube

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 17/12/2025 21:12:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for advertisement_table
-- ----------------------------
DROP TABLE IF EXISTS `advertisement_table`;
CREATE TABLE `advertisement_table`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `url` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '链接地址',
  `image_url` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `content` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '细节描述',
  `video_url` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频广告地址',
  `create_time` bigint NULL DEFAULT NULL,
  `update_time` bigint NULL DEFAULT NULL,
  `create_user` bigint NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL,
  `type` int NULL DEFAULT NULL COMMENT '类型',
  `start_time` bigint NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` bigint NULL DEFAULT NULL COMMENT '结束时间',
  `view_count` bigint NULL DEFAULT NULL COMMENT '点击次数',
  `position` int NULL DEFAULT NULL COMMENT '投放位置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '广告以及系统公告' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of advertisement_table
-- ----------------------------

-- ----------------------------
-- Table structure for ai_config
-- ----------------------------
DROP TABLE IF EXISTS `ai_config`;
CREATE TABLE `ai_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `api_url` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '评论与弹幕审核使用暂时只能使用默认Prompt',
  `type` int NULL DEFAULT NULL COMMENT '0 默认， 1，评论弹幕审核',
  `create_time` bigint NOT NULL,
  `create_user` bigint NOT NULL,
  `update_time` bigint NOT NULL,
  `update_user` bigint NOT NULL,
  `status` int NOT NULL DEFAULT 0 COMMENT '启用状态，0未启用，1启用',
  `use_tokens` bigint NOT NULL DEFAULT 0 COMMENT '该配置已经使用的token数量',
  `max_tokens` bigint NOT NULL DEFAULT 0 COMMENT '最大可用token',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_config
-- ----------------------------

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片url',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `describes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '简介',
  `view_count` bigint NOT NULL DEFAULT 0 COMMENT '观看次数',
  `like_count` bigint NOT NULL DEFAULT 0 COMMENT '点赞次数',
  `favorite_count` bigint NOT NULL DEFAULT 0 COMMENT '收藏次数',
  `dislike_count` bigint NOT NULL DEFAULT 0 COMMENT '不喜欢次数',
  `examine_status` int NOT NULL COMMENT '审核情况 【0 暂未审核， 1 通过， 2 不通过】向西见枚举类',
  `examine_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '审核意见',
  `category` int NOT NULL COMMENT '分区',
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签',
  `user_id` bigint NOT NULL COMMENT '发帖人ID',
  `type` int NOT NULL COMMENT '类型 【0 视频， 1 图片  2 文章】',
  `create_time` bigint NOT NULL COMMENT '发布时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  `status` int NOT NULL DEFAULT 0 COMMENT '[0 正常， 1 删除]',
  `score` bigint NULL DEFAULT NULL COMMENT '评分总分',
  `score_count` bigint NULL DEFAULT NULL COMMENT '评分总人数',
  `comment_count` bigint NOT NULL DEFAULT 0 COMMENT '评论人数',
  `danmaku_count` bigint NOT NULL DEFAULT 0 COMMENT '弹幕数',
  `examine_user` bigint NULL DEFAULT NULL COMMENT '审核人id',
  `duration` double NULL DEFAULT NULL COMMENT '视频长度',
  `pixels_number` bigint NULL DEFAULT NULL COMMENT '像素数，超过1080*1920即为高清',
  `frame_rate` double NULL DEFAULT NULL COMMENT '帧率',
  `ua` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_video_by_category`(`category` ASC) USING BTREE,
  INDEX `find_video_by_title`(`title` ASC) USING BTREE,
  INDEX `find_video_by_user`(`user_id` ASC) USING BTREE,
  INDEX `find_video_by_type`(`type` ASC) USING BTREE,
  INDEX `find_video_by_tag`(`tag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '视频，图片，文章 发帖表\r\n\r\nTODO 回复消息可见，加密帖子，视频等' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article
-- ----------------------------

-- ----------------------------
-- Table structure for article_text
-- ----------------------------
DROP TABLE IF EXISTS `article_text`;
CREATE TABLE `article_text`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL COMMENT '关联的文章信息',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文本内容',
  `user_id` bigint NOT NULL COMMENT '作者',
  `type` int NOT NULL DEFAULT 0 COMMENT '文章类型0 普通文章，1回复可见，2加密文章',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '加密文章密码',
  `create_time` bigint NOT NULL COMMENT '发布时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  `sort` int NOT NULL DEFAULT 0 COMMENT '段落顺序，越小越靠前',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态0 正常，1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_article_text_by_article_id`(`article_id` ASC) USING BTREE,
  INDEX `find_article_text_by_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '保存与稿件关联的文章' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article_text
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分区名',
  `type` int NOT NULL COMMENT '分区级别【1 一级分区， 2 二级分区】',
  `father_id` int NULL DEFAULT NULL COMMENT '父级分区',
  `describes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '介绍',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` bigint NOT NULL,
  `update_time` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '动画', 1, 0, '动画', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (2, '音乐', 1, 0, '音乐', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (3, '舞蹈', 1, 0, '舞蹈', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (4, '知识', 1, 0, '知识', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (5, '生活', 1, 0, '生活', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (6, '时尚', 1, 0, '时尚', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (7, '娱乐', 1, 0, '娱乐', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (8, '放映厅', 1, 0, '放映厅', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (9, '数码', 1, 0, '数码', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (10, '番剧', 2, 1, '番剧', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (11, '原创音乐', 2, 2, '个人或团队制作以音乐为主要原创因素的歌曲或纯音乐', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (12, '翻唱', 2, 2, '一切非官方的人声再演绎歌曲作品', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (13, '舞蹈综合', 2, 3, '收录无法定义到其他舞蹈子分区的舞蹈视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (14, '舞蹈教程', 2, 3, '镜面慢速，动作分解，基础教程等具有教学意义的舞蹈视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (15, '宅舞', 2, 3, '与ACG相关的翻跳、原创舞蹈', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (16, 'MV', 2, 2, '音乐录影带，为搭配音乐而拍摄的短片', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (17, '演奏', 2, 2, '传统或非传统乐器及器材的演奏作品', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (18, '音乐现场', 2, 2, '音乐实况表演视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (19, '说唱', 2, 2, '说唱', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (20, 'VOCALOID·UTAU', 2, 2, '以雅马哈Vocaloid和UTAU引擎为基础，包含其他调教引擎，运用各类音源进行的歌曲创作内容', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (21, '电音', 2, 2, '以电子合成器、音乐软体等产生的电子声响制作的音乐', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (22, '音乐融合', 2, 2, '收录无法定义到其他音乐子分区的音乐视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (23, 'MAD·AMV', 2, 1, '具有一定制作程度的动画或静画的二次创作视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (24, 'MMD·3D', 2, 1, '使用MMD（MikuMikuDance）和其他3D建模类软件制作的视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (25, '短片·手书·配音', 2, 1, '追求创新并具有强烈特色的短片、手书（绘）及ACG相关配音', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (26, '手办·模玩', 2, 1, '手办模玩的测评、改造或其他衍生内容', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (27, '特摄', 2, 1, '特摄相关衍生视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (28, '综合', 2, 1, '以动画及动画相关内容为素材，包括但不仅限于音频替换、杂谈、排行榜等内容', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (29, '街舞', 2, 3, '收录街舞相关内容，包括赛事现场、舞室作品、个人翻跳、FREESTYLE等', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (30, '明星舞蹈', 2, 3, '国内外明星发布的官方舞蹈及其翻跳内容', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (31, '中国舞', 2, 3, '传承中国艺术文化的舞蹈内容，包括古典舞、民族民间舞、汉唐舞、古风舞等', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (32, '科学科普', 2, 4, '回答你的十万个为什么', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (33, '社科人文', 2, 4, '聊聊互联网社会法律，看看历史趣闻艺术，品品文化心理人物', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (34, '财经', 2, 4, '宏观经济分析，证券市场动态，商业帝国故事，知识与财富齐飞~', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (35, '校园学习', 2, 4, '老师很有趣，同学多人才，我们都爱搞学习', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (36, '职业职场', 2, 4, '职场加油站，成为最有料的职场人', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (37, '野生技术协会', 2, 4, '炫酷技能大集合，是时候展现真正的技术了', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (38, '搞笑', 2, 5, '各种沙雕有趣的搞笑剪辑，挑战，表演，配音等视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (39, '日常', 2, 5, '记录日常生活，分享生活故事', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (40, '动物圈', 2, 5, '萌萌的动物都在这里哦', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (41, '手工', 2, 5, '手工制品的制作过程或成品展示、教程、测评类视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (42, '绘画', 2, 5, '绘画过程或绘画教程，以及绘画相关的所有视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (43, '运动', 2, 5, '运动相关的记录、教程、装备评测和精彩瞬间剪辑视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (44, '汽车', 2, 5, '汽车相关资讯、体验、测评、记录和车主生活视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (45, '其它', 2, 5, '对分区归属不明的视频进行归纳整合的特定分区', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (46, '美妆', 2, 6, '涵盖妆容、发型、美甲等教程，彩妆、护肤相关产品测评、分享等', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (47, '服饰', 2, 6, '服饰风格、搭配技巧相关的展示和教程视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (48, '健身', 2, 6, '器械、有氧、拉伸运动等，以达到强身健体、减肥瘦身、形体塑造目的', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (49, 'T台', 2, 6, '发布会走秀现场及模特相关时尚片、采访、后台花絮', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (50, '风尚标', 2, 6, '时尚明星专访、街拍、时尚购物相关知识科普', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (51, '综艺', 2, 7, '国内外有趣的综艺和综艺相关精彩剪辑', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (52, '明星', 2, 7, '娱乐圈动态、明星资讯相关', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (53, '游戏', 1, 0, '游戏', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (54, '单机游戏', 2, 53, '以所有平台（PC、主机、移动端）的单机或联机游戏为主的视频内容，包括游戏预告、CG、实况解说及相关的评测、杂谈与视频剪辑等', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (55, '电子竞技', 2, 53, '具有高对抗性的电子竞技游戏项目，其相关的赛事、实况、攻略、解说、短剧等视频。', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (56, '手机游戏', 2, 53, '以手机及平板设备为主要平台的游戏，其相关的实况、攻略、解说、短剧、演示等视频。', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (57, '网络游戏', 2, 53, '由网络运营商运营的多人在线游戏，以及电子竞技的相关游戏内容。包括赛事、攻略、实况、解说等相关视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (58, '桌游棋牌', 2, 53, '桌游、棋牌、卡牌对战等及其相关电子版游戏的实况、攻略、解说、演示等视频。', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (59, 'GMV', 2, 53, '由游戏素材制作的MV视频。以游戏内容或CG为主制作的，具有一定创作程度的MV类型的视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (60, '音游', 2, 53, '各个平台上，通过配合音乐与节奏而进行的音乐类游戏视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (61, '手机平板', 2, 9, '手机平板、app 和产品教程等相关视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (62, '电脑装机', 2, 9, '电脑、笔记本、装机配件、外设和软件教程等相关视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (63, '摄影摄像', 2, 9, '摄影摄像器材、拍摄剪辑技巧、拍摄作品分享等相关视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (64, '影音智能', 2, 9, '影音设备、智能硬件、生活家电等相关视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (65, '纪录片', 2, 8, '纪录片', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (66, '电影', 2, 8, '电影', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (67, '电视剧', 2, 8, '电视剧', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (68, '综艺', 2, 8, '综艺', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (69, '影视杂谈', 2, 8, '影视评论、解说、吐槽、科普等', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (70, '影视剪辑', 2, 8, '对影视素材进行剪辑再创作的视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (71, '短片', 2, 8, '追求自我表达且具有特色的短片', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (72, '预告·资讯', 2, 8, '影视类相关资讯，预告，花絮等视频', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (73, '资讯', 1, 0, '新闻资讯', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (74, '热点', 2, 73, '全民关注的时政热门资讯', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (75, '环球', 2, 73, '全球范围内发生的具有重大影响力的事件动态', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (76, '社会', 2, 73, '日常生活的社会事件、社会问题、社会风貌的报道', NULL, 0, 0, 0);
INSERT INTO `category` VALUES (77, '综合', 2, 73, '除上述领域外其它垂直领域的综合资讯', NULL, 0, 0, 0);

-- ----------------------------
-- Table structure for chat_conversation
-- ----------------------------
DROP TABLE IF EXISTS `chat_conversation`;
CREATE TABLE `chat_conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user1_id` bigint NOT NULL,
  `user2_id` bigint NOT NULL,
  `last_message_id` bigint NULL DEFAULT NULL,
  `last_message_time` bigint NULL DEFAULT NULL,
  `create_time` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_pair`(`user1_id` ASC, `user2_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_conversation
-- ----------------------------

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `msg_type` tinyint NOT NULL DEFAULT 0,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` bigint NOT NULL,
  `read_status` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_conv_time`(`conversation_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_sender_receiver`(`sender_id` ASC, `receiver_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_message
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `comment` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论',
  `comment_id` bigint NULL DEFAULT NULL COMMENT '父级评论',
  `parent_comment_id` bigint NULL DEFAULT NULL COMMENT '父级评论',
  `parent_user_id` bigint NULL DEFAULT NULL COMMENT '评论对象',
  `like_count` bigint NOT NULL DEFAULT 0 COMMENT '喜欢数',
  `comment_count` bigint NOT NULL COMMENT '评论数',
  `dislike_count` bigint NOT NULL DEFAULT 0 COMMENT '不喜欢',
  `type` int NOT NULL COMMENT '【1 一级评论  2 二级评论】',
  `status` int NOT NULL DEFAULT 0 COMMENT '[0 正常  1 删除]',
  `create_time` bigint NOT NULL,
  `update_time` bigint NOT NULL,
  `ua` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ai_examine_message` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ai_examine_token` bigint NULL DEFAULT NULL,
  `ai_examine_id` int NULL DEFAULT NULL COMMENT '使用的模型ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_comment_by_artice`(`article_id` ASC) USING BTREE,
  INDEX `find_comment_by_user`(`user_id` ASC) USING BTREE,
  INDEX `find_comment_by_type`(`type` ASC) USING BTREE,
  INDEX `find_comment_by_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for danmaku
-- ----------------------------
DROP TABLE IF EXISTS `danmaku`;
CREATE TABLE `danmaku`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `video_id` bigint NOT NULL COMMENT '视频ID',
  `color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '弹幕颜色',
  `text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '弹幕内容',
  `time` double NOT NULL COMMENT '时间',
  `type` int NULL DEFAULT NULL COMMENT '类型',
  `author` bigint NOT NULL COMMENT '作者',
  `color_dec` bigint NOT NULL COMMENT '十进制颜色数据',
  `status` int NULL DEFAULT 0 COMMENT '类型',
  `create_time` bigint NOT NULL,
  `ua` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ai_examine_message` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ai_examine_token` bigint NULL DEFAULT NULL,
  `ai_examine_id` int NULL DEFAULT NULL COMMENT '使用的模型ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_danmaku_by_vido_id`(`video_id` ASC) USING BTREE,
  INDEX `find_danmaku_by_userID`(`video_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '弹幕表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of danmaku
-- ----------------------------

-- ----------------------------
-- Table structure for dislike_table
-- ----------------------------
DROP TABLE IF EXISTS `dislike_table`;
CREATE TABLE `dislike_table`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dislike_obj_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `create_time` bigint NOT NULL,
  `type` int NOT NULL COMMENT '【0 帖子视频图片， 1 评论】',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_like_by_user`(`user_id` ASC) USING BTREE,
  INDEX `find_like_bu_artice`(`dislike_obj_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '点踩' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dislike_table
-- ----------------------------

-- ----------------------------
-- Table structure for file_table
-- ----------------------------
DROP TABLE IF EXISTS `file_table`;
CREATE TABLE `file_table`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NULL DEFAULT NULL COMMENT '视频，图片，文章ID， 需要后期更新，没有此项的文件后期需要清楚',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务器保存的文件地址',
  `file_original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件原始名',
  `file_new_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '新的文件名',
  `size` bigint NOT NULL COMMENT '文件大小',
  `upload_time` bigint NOT NULL COMMENT '上传时间',
  `upload_user_id` bigint NOT NULL COMMENT '上传人',
  `type` int NOT NULL COMMENT '文件类型 【0 视频， 1 图片， 2 其它附件, 3 头像数据， 4 顶部大图数据】',
  `suffix_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '后缀名',
  `duration` double NULL DEFAULT NULL COMMENT '视频长度',
  `status` int NOT NULL DEFAULT 0 COMMENT '文件存储状态   0  未保存的临时文件，后期删除   1  保存成功并发布的文件',
  `height` int NULL DEFAULT NULL COMMENT '视频高度',
  `width` int NULL DEFAULT NULL COMMENT '视频宽度',
  `pixels_number` bigint NULL DEFAULT NULL COMMENT '像素数',
  `frame_rate` double NULL DEFAULT NULL COMMENT '帧率',
  `info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其他信息',
  `ua` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `save_location` int NOT NULL DEFAULT 0 COMMENT '存储位置 0 默认本地',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_file_by_artice_id`(`article_id` ASC) USING BTREE,
  INDEX `find_file_by_userid`(`upload_user_id` ASC) USING BTREE,
  INDEX `find_file_by_type`(`type` ASC) USING BTREE,
  INDEX `find_file_suffix`(`suffix_name` ASC) USING BTREE,
  INDEX `find_file_save_location`(`save_location` ASC) USING BTREE,
  INDEX `find_file_new_name`(`file_new_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file_table
-- ----------------------------

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `follow_user` bigint NOT NULL COMMENT '关注的用户ID',
  `create_user` bigint NOT NULL COMMENT '创建关注的用户ID',
  `create_time` bigint NULL DEFAULT NULL COMMENT '关注时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_follow_user`(`follow_user` ASC) USING BTREE,
  INDEX `find_create_user`(`create_user` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of follow
-- ----------------------------

-- ----------------------------
-- Table structure for invitation_code
-- ----------------------------
DROP TABLE IF EXISTS `invitation_code`;
CREATE TABLE `invitation_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邀请码',
  `create_user` bigint NOT NULL COMMENT '生成邀请码的人',
  `use_user` bigint NULL DEFAULT NULL COMMENT '使用邀请吗的人',
  `use_status` int NOT NULL DEFAULT 0 COMMENT '【1 未被使用， 0 已经被使用】',
  `create_time` bigint NOT NULL COMMENT '生成时间',
  `use_time` bigint NULL DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_code_by_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of invitation_code
-- ----------------------------

-- ----------------------------
-- Table structure for like_table
-- ----------------------------
DROP TABLE IF EXISTS `like_table`;
CREATE TABLE `like_table`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `like_obj_id` bigint NOT NULL COMMENT '喜欢的对象ID',
  `user_id` bigint NOT NULL,
  `create_time` bigint NOT NULL,
  `type` int NOT NULL COMMENT '【0 视频图片文章主帖子  1 评论】',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_like_by_user`(`user_id` ASC) USING BTREE,
  INDEX `find_like_bu_artice`(`like_obj_id` ASC) USING BTREE,
  INDEX `find_like_by_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '点赞' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of like_table
-- ----------------------------

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `login_time` bigint NOT NULL COMMENT '登录时间',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录IP',
  `ua` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '浏览器UA',
  `userId` bigint NOT NULL COMMENT '登录用户ID',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_user_log_by_id_index`(`userId` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of login_log
-- ----------------------------

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notifier` bigint NOT NULL COMMENT '通知发送人ID',
  `receiver` bigint NOT NULL COMMENT '通知接收人ID',
  `title` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `outer_id` bigint NOT NULL COMMENT '外部ID，如主帖子ID',
  `link_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息链接',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `comment_id` bigint NULL DEFAULT -1 COMMENT '评论目标ID',
  `type` int NOT NULL COMMENT '类型 【0 回复帖子， 1 回复评论，2 收到点赞】',
  `article_id` bigint NULL DEFAULT NULL COMMENT '文章ID',
  `create_time` bigint NOT NULL,
  `read_time` bigint NULL DEFAULT NULL,
  `status` int NOT NULL DEFAULT 0 COMMENT '【0 未读， 1 已读】',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_notification_by_notifier`(`notifier` ASC) USING BTREE,
  INDEX `find_notification_by_receiver`(`receiver` ASC) USING BTREE,
  INDEX `idx_receiver_status`(`receiver` ASC, `status` ASC) USING BTREE,
  INDEX `idx_receiver_create_time`(`receiver` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_outer_id`(`outer_id` ASC) USING BTREE,
  INDEX `idx_comment_id`(`comment_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 0, 2, '系统已收到您的举报', 1, '4', '你举报的评论：987\n，我们已经收到，稍后管理员将给你反馈', 4, 11, 4, 1764833660556, 1764833739841, 1);
INSERT INTO `notification` VALUES (2, 2, 3, '你的稿件已通过审核，现在所有人都能看见你的稿件了', 7, '<a href=\"/studio/upload?edit=7\">REPLACE_ME_VIDEO</a>', '你的稿件 《REPLACE_ME_VIDEO》 已通过审核，现在所有人都能看见你的稿件了', -1, 10, 7, 1765348101932, 1765348954010, 1);
INSERT INTO `notification` VALUES (3, 2, 3, '稿件《REPLACE_ME_VIDEO》收到新评论', 7, '', '今天天气不错\n', 16, 0, 7, 1765421753960, 1765424252377, 1);
INSERT INTO `notification` VALUES (4, 2, 3, '稿件《REPLACE_ME_VIDEO》收到新评论', 7, '', 'woshk文字\n', 17, 0, 7, 1765421794772, 1765424218076, 1);
INSERT INTO `notification` VALUES (5, 2, 3, '稿件《REPLACE_ME_VIDEO》收到新评论', 7, '', '我日你妈\n', 18, 0, 7, 1765421819243, 1765423615366, 1);
INSERT INTO `notification` VALUES (6, 2, 3, '稿件《REPLACE_ME_VIDEO》收到新评论', 7, '', '我日你妈\n', 19, 0, 7, 1765421930168, 1765423798794, 1);
INSERT INTO `notification` VALUES (7, 3, 2, '你在稿件《REPLACE_ME_VIDEO》下的评论：今天天气不错\n收到新回复了', 16, '', '真的不错\n', 22, 1, 7, 1765424263780, 1765424289733, 1);
INSERT INTO `notification` VALUES (8, 2, 3, '你的稿件已通过审核，现在所有人都能看见你的稿件了', 8, '<a href=\"/studio/upload?edit=8\">REPLACE_ME_VIDEO_2</a>', '你的稿件 《REPLACE_ME_VIDEO_2》 已通过审核，现在所有人都能看见你的稿件了', -1, 10, 8, 1765435516853, 1765444095472, 1);
INSERT INTO `notification` VALUES (9, 0, 2, '系统已收到您的举报', 2, '19', '你举报的评论：已屏蔽\n，我们已经收到，稍后管理员将给你反馈', 19, 11, 19, 1765506643464, 1765506766955, 1);
INSERT INTO `notification` VALUES (10, 0, 2, '系统已收到您的举报', 3, '18', '你举报的评论：已屏蔽\n，我们已经收到，稍后管理员将给你反馈', 18, 11, 18, 1765506652087, 1765506765449, 1);
INSERT INTO `notification` VALUES (11, 0, 2, '系统已收到您的举报', 4, '17', '你举报的评论：woshk文字\n，我们已经收到，稍后管理员将给你反馈', 17, 11, 17, 1765507288461, 1765507292612, 1);
INSERT INTO `notification` VALUES (12, 2, 3, '稿件《陶喆-二十二 (2003年soul power演唱会)(标清).mp4》收到新评论', 8, '', '今天星期6\n', 23, 0, 8, 1765507903087, 1765530095274, 1);
INSERT INTO `notification` VALUES (13, 0, 2, '系统已收到您的举报', 5, '23', '你举报的评论：今天星期6\n，我们已经收到，稍后管理员将给你反馈', 23, 11, 23, 1765507923023, 1765507926259, 1);
INSERT INTO `notification` VALUES (14, 2, 3, '稿件《陶喆-二十二 (2003年soul power演唱会)(标清).mp4》收到新评论', 8, '', '今天星期7\n', 24, 0, 8, 1765508020183, 1765529034220, 1);
INSERT INTO `notification` VALUES (15, 0, 2, '系统已收到您的举报', 6, '24', '你举报的评论：今天星期7\n，我们已经收到，稍后管理员将给你反馈', 24, 11, 24, 1765508026081, 1765508030728, 1);
INSERT INTO `notification` VALUES (16, 2, 1, '账号被封禁！', -1, '', '由于您近期多次违反社区规定，账号已被永久封禁！', -1, 10, -1, 1765526423519, NULL, 0);
INSERT INTO `notification` VALUES (17, 2, 1, '账号已被管理员解封！', -1, '', '由于您近期表现良好，管理员已提前解封您的账号！', -1, 10, -1, 1765526434901, NULL, 0);
INSERT INTO `notification` VALUES (18, 2, 3, '账号被封禁！', -1, '', '由于您近期多次违反社区规定，账号已被封禁到：2025-12-19 23:59', -1, 10, -1, 1765526586664, 1765530094865, 1);
INSERT INTO `notification` VALUES (19, 2, 3, '账号已被管理员解封！', -1, '', '由于您近期表现良好，管理员已提前解封您的账号！', -1, 10, -1, 1765526634260, 1765530094024, 1);
INSERT INTO `notification` VALUES (20, 2, 3, '恭喜你，已经成为尊贵的 VPI 用户', -1, '', '管理员已经将你设置为 VPI 用户，有效期为：2025-12-12 08:00 - 2026-12-12 08:00', -1, 10, -1, 1765529638627, 1765530093601, 1);
INSERT INTO `notification` VALUES (21, 3, 4, '恭喜你，已经成为尊贵的 VPI 用户', -1, '', '管理员已经将你设置为 VPI 用户，有效期为：2025-12-12 08:00 - 2026-12-12 08:00', -1, 10, -1, 1765530446429, NULL, 0);
INSERT INTO `notification` VALUES (22, 0, 3, '你已被任命为管理员', -1, '', '你的账号已被设置为管理员，现在可以访问管理后台并执行管理操作', -1, 10, -1, 1765530734268, 1765530746860, 1);
INSERT INTO `notification` VALUES (23, 0, 3, '你已被任命为管理员', -1, '', '你的账号已被设置为管理员，现在可以访问管理后台并执行管理操作', -1, 10, -1, 1765530886534, 1765530892923, 1);
INSERT INTO `notification` VALUES (24, 0, 3, '你已被任命为管理员', -1, '', '你的账号已被设置为管理员，现在可以访问管理后台并执行管理操作', -1, 10, -1, 1765531115932, 1765531132537, 1);
INSERT INTO `notification` VALUES (25, 0, 3, '你已被任命为管理员', -1, '', '你的账号已被设置为管理员，现在可以访问管理后台并执行管理操作', -1, 10, -1, 1765531347649, 1765531370128, 1);
INSERT INTO `notification` VALUES (26, 0, 3, '你的管理员权限已被取消', -1, '', '你的账号已被取消管理员权限，如有疑问请联系超级管理员', -1, 10, -1, 1765531439589, 1765531446066, 1);
INSERT INTO `notification` VALUES (27, 0, 3, '你已被任命为管理员', -1, '', '你的账号已被设置为管理员，现在可以访问管理后台并执行管理操作', -1, 10, -1, 1765533528965, 1765533534266, 1);

-- ----------------------------
-- Table structure for opinion_table
-- ----------------------------
DROP TABLE IF EXISTS `opinion_table`;
CREATE TABLE `opinion_table`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `target_id` bigint NOT NULL COMMENT '被举报的目标',
  `user_id` bigint NOT NULL COMMENT '举报人',
  `user_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户举报原因，意见建议',
  `type` int NOT NULL COMMENT '举报类型 0 稿件 1 评论 2 弹幕',
  `status` int NOT NULL COMMENT '状态 0 未处理，1已处理',
  `opinion` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理意见',
  `appeal_status` int NULL DEFAULT NULL COMMENT '申诉处理意见,0通过 1 不通过',
  `opinion_user` bigint NULL DEFAULT NULL COMMENT '处理人',
  `create_time` bigint NULL DEFAULT NULL COMMENT '举报时间',
  `opinion_time` bigint NULL DEFAULT NULL COMMENT '处理日期',
  `other_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '其它信息',
  `ua` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_opinion_target_id`(`target_id` ASC) USING BTREE,
  INDEX `find_opinion_user_id`(`user_id` ASC) USING BTREE,
  INDEX `find_opinion_type`(`type` ASC) USING BTREE,
  INDEX `find_opinion_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '举报以及意见反馈表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of opinion_table
-- ----------------------------
INSERT INTO `opinion_table` VALUES (1, 4, 2, '616165', 1, 1, '21\n', NULL, 2, 1764833660553, 1764833715151, '{\"id\":4,\"articleId\":5,\"userId\":2,\"comment\":\"987\\n\",\"commentId\":null,\"parentCommentId\":0,\"parentUserId\":0,\"likeCount\":0,\"commentCount\":0,\"dislikeCount\":0,\"type\":1,\"status\":-1,\"createTime\":1764833618023,\"updateTime\":1764833618023,\"ip\":\"127.0.0.1\",\"ua\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0\",\"city\":\"0|0|0|内网IP|内网IP\",\"aiExamineMessage\":null,\"aiExamineToken\":null,\"aiExamineId\":null}', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', '127.0.0.1', '0|0|0|内网IP|内网IP');
INSERT INTO `opinion_table` VALUES (2, 19, 2, '辱骂', 1, 1, '属实\n', NULL, 2, 1765506643457, 1765506750630, '{\"id\":19,\"articleId\":7,\"userId\":2,\"comment\":\"我日你妈\\n\",\"commentId\":null,\"parentCommentId\":0,\"parentUserId\":0,\"likeCount\":0,\"commentCount\":0,\"dislikeCount\":0,\"type\":1,\"status\":0,\"createTime\":1765421930166,\"updateTime\":1765421930166,\"ip\":\"127.0.0.1\",\"ua\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0\",\"city\":\"0|0|0|内网IP|内网IP\",\"aiExamineMessage\":null,\"aiExamineToken\":null,\"aiExamineId\":null}', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', '127.0.0.1', '0|0|0|内网IP|内网IP');
INSERT INTO `opinion_table` VALUES (3, 18, 2, '辱骂\n', 1, 1, '属实\n', NULL, 2, 1765506652085, 1765506727378, '{\"id\":18,\"articleId\":7,\"userId\":2,\"comment\":\"我日你妈\\n\",\"commentId\":null,\"parentCommentId\":0,\"parentUserId\":0,\"likeCount\":0,\"commentCount\":0,\"dislikeCount\":0,\"type\":1,\"status\":0,\"createTime\":1765421819241,\"updateTime\":1765421819241,\"ip\":\"127.0.0.1\",\"ua\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0\",\"city\":\"0|0|0|内网IP|内网IP\",\"aiExamineMessage\":null,\"aiExamineToken\":null,\"aiExamineId\":null}', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', '127.0.0.1', '0|0|0|内网IP|内网IP');
INSERT INTO `opinion_table` VALUES (4, 17, 2, '辱骂', 1, 1, ':doge:\n', NULL, 2, 1765507288460, 1765507320213, '{\"id\":17,\"articleId\":7,\"userId\":2,\"comment\":\"woshk文字\\n\",\"commentId\":null,\"parentCommentId\":0,\"parentUserId\":0,\"likeCount\":0,\"commentCount\":0,\"dislikeCount\":0,\"type\":1,\"status\":0,\"createTime\":1765421794768,\"updateTime\":1765421794768,\"ip\":\"127.0.0.1\",\"ua\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0\",\"city\":\"0|0|0|内网IP|内网IP\",\"aiExamineMessage\":null,\"aiExamineToken\":null,\"aiExamineId\":null}', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', '127.0.0.1', '0|0|0|内网IP|内网IP');
INSERT INTO `opinion_table` VALUES (5, 23, 2, '暗示', 1, 1, '?\n', NULL, 2, 1765507923022, 1765507973473, '{\"id\":23,\"articleId\":8,\"userId\":2,\"comment\":\"今天星期6\\n\",\"commentId\":null,\"parentCommentId\":0,\"parentUserId\":0,\"likeCount\":0,\"commentCount\":0,\"dislikeCount\":0,\"type\":1,\"status\":0,\"createTime\":1765507902926,\"updateTime\":1765507902926,\"ip\":\"127.0.0.1\",\"ua\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0\",\"city\":\"0|0|0|内网IP|内网IP\",\"aiExamineMessage\":null,\"aiExamineToken\":null,\"aiExamineId\":null}', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', '127.0.0.1', '0|0|0|内网IP|内网IP');
INSERT INTO `opinion_table` VALUES (6, 24, 2, '按时', 1, 1, '❤\n', NULL, 2, 1765508026079, 1765508063308, '{\"id\":24,\"articleId\":8,\"userId\":2,\"comment\":\"今天星期7\\n\",\"commentId\":null,\"parentCommentId\":0,\"parentUserId\":0,\"likeCount\":0,\"commentCount\":0,\"dislikeCount\":0,\"type\":1,\"status\":0,\"createTime\":1765508020017,\"updateTime\":1765508020017,\"ip\":\"127.0.0.1\",\"ua\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0\",\"city\":\"0|0|0|内网IP|内网IP\",\"aiExamineMessage\":null,\"aiExamineToken\":null,\"aiExamineId\":null}', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', '127.0.0.1', '0|0|0|内网IP|内网IP');

-- ----------------------------
-- Table structure for oss_config
-- ----------------------------
DROP TABLE IF EXISTS `oss_config`;
CREATE TABLE `oss_config`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `config_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置名称',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '存储桶名字',
  `endpoint` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '端点',
  `access_key` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `secret_key` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区',
  `url_prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义域名',
  `path_style_access` int(1) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT 'Endpoint 访问风格 0 path style，1Virtual Hosted Style',
  `other` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它参数配置',
  `create_time` bigint NULL DEFAULT NULL COMMENT '创建时间',
  `status` int(1) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '0 关闭，1启用',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `updater_id` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oss_config
-- ----------------------------
-- 注意：以下示例值不包含真实密钥，请在部署时改为环境变量或手工配置
INSERT INTO `oss_config` VALUES (1, 'tengxuncos', 'videostorage-1387215144', 'https://cos.ap-chongqing.myqcloud.com', 'REPLACE_ME_SECRET_ID', 'REPLACE_ME_SECRET_KEY', 'ap-chongqing', '', 1, '', 1765511974582, 1, 2, 2, 1765511974582);

-- ----------------------------
-- Table structure for play_recording
-- ----------------------------
DROP TABLE IF EXISTS `play_recording`;
CREATE TABLE `play_recording`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL COMMENT '视频ID',
  `file_id` bigint NULL DEFAULT NULL COMMENT '观看到第几个视频',
  `video_time` double NULL DEFAULT NULL COMMENT '时间戳',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  `video_id` bigint NOT NULL COMMENT '视频ID',
  `ua` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器ua',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_play_by_user_id`(`user_id` ASC) USING BTREE,
  INDEX `find_play_by_artice`(`article_id` ASC) USING BTREE,
  INDEX `find_play_by_vido`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '播放记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of play_recording
-- ----------------------------
INSERT INTO `play_recording` VALUES (1, 5, NULL, 5.016303, 2, 1764725816682, 1764834324022, 16, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0');
INSERT INTO `play_recording` VALUES (2, 5, NULL, NULL, -1, 1764821171366, 1764833232091, 16, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0');
INSERT INTO `play_recording` VALUES (3, 6, NULL, NULL, 2, 1765188668014, 1765188674102, 19, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0');
INSERT INTO `play_recording` VALUES (4, 7, NULL, 1.110692, 2, 1765348161925, 1765883902839, 20, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (5, 7, NULL, 5.013416, 3, 1765348854896, 1765869001258, 20, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (6, 8, NULL, 59.963108, 2, 1765435525570, 1765865931931, 23, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (7, 8, NULL, 259.921283, 3, 1765435608345, 1765868305054, 23, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (8, 8, NULL, 99.928345, 4, 1765442131636, 1765444021558, 23, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (9, 9, NULL, 35.020768, 2, 1765521825531, 1765955162325, 26, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (10, 9, NULL, NULL, -1, 1765523599444, 1765868098613, 26, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (11, 9, NULL, 25.914919, 3, 1765527151991, 1765939476120, 26, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:146.0) Gecko/20100101 Firefox/146.0');
INSERT INTO `play_recording` VALUES (12, 8, NULL, NULL, -1, 1765767210708, 1765868071869, 23, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (13, 7, NULL, NULL, -1, 1765767218274, 1765868079047, 20, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (14, 10, NULL, 10.011314, 2, 1765770713032, 1765882488800, 27, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (15, 10, NULL, NULL, -1, 1765849788874, 1765868041559, 27, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (16, 8, NULL, NULL, 5, 1765857537555, 1765857537555, 23, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (17, 7, NULL, NULL, 5, 1765857549226, 1765857549226, 20, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (18, 10, NULL, NULL, 5, 1765857987672, 1765877619167, 27, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (19, 8, NULL, NULL, 3, 1765868513393, 1765954033882, 29, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0');
INSERT INTO `play_recording` VALUES (20, 10, NULL, NULL, 3, 1765868596052, 1765869730679, 27, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (21, 9, NULL, NULL, 5, 1765877453582, 1765877521093, 26, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36');
INSERT INTO `play_recording` VALUES (22, 8, NULL, NULL, 2, 1765882494164, 1765955230101, 29, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0');

-- ----------------------------
-- Table structure for temp_file
-- ----------------------------
DROP TABLE IF EXISTS `temp_file`;
CREATE TABLE `temp_file`  (
  `ID` bigint NOT NULL,
  `CREATE_TIME` bigint NULL DEFAULT NULL,
  `DATE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `FILENAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `PATH` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `SIZE` bigint NULL DEFAULT NULL,
  `UPLOAD_FILENAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of temp_file
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `mail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `submit_count` bigint NOT NULL DEFAULT 0 COMMENT '提交视频，图片，文章数',
  `follow_count` bigint NOT NULL DEFAULT 0 COMMENT '关注数',
  `fans_count` bigint NOT NULL DEFAULT 0 COMMENT '粉丝数',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '/images/avatar/avatar.png' COMMENT '头像',
  `top_img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '/images/top.png' COMMENT '首页大图url',
  `introduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '简介',
  `last_publish_time` bigint NULL DEFAULT NULL COMMENT '上次投稿时间',
  `otp` int NULL DEFAULT 0 COMMENT '是否开启totp认证',
  `status` int NULL DEFAULT 0 COMMENT '状态',
  `block_end_time` bigint NULL DEFAULT 0 COMMENT '封禁截止日期',
  `otp_secret` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'otp密钥',
  `otp_recovery` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'otp回复密钥',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_user_by_mail_index`(`mail` ASC) USING BTREE COMMENT '使用邮箱查找用户',
  INDEX `find_user_by_phone_index`(`phone` ASC) USING BTREE COMMENT '使用手机号查找用户'
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '111@qq.com', 'admin', '15151515152', 15256156156, 0, 0, 0, '/images/avatar/avatar.png', '/images/top.png', NULL, NULL, 0, 0, 0, NULL, NULL);
INSERT INTO `user` VALUES (2, 'dbc', '123@qq.com', '$2a$10$7hKyInzTLx2qIEpYbch5pO3.gWKaChT3/R3QZ9NNtw7h0xW13yUmC', '15151515151', 1764665378349, 6, 6, 2, '/api/upload/file/2025/12/11/9d98cc0765da445890f199f8e354f055.JPG', '/images/top.png', NULL, 1765770511498, 0, 0, 0, NULL, NULL);
INSERT INTO `user` VALUES (3, 'wont', '2644539131@qq.com', '$2a$10$mY1.8KRi5PNM7rG/EZObYu8ArK0wORD3Hpd34sIVEHiW528h5.wti', '17262104857', 1765347790517, 3, 1, 1, '/images/head.png', '/images/top.png', NULL, 1765435477444, 0, 0, 0, NULL, NULL);
INSERT INTO `user` VALUES (4, 'test1', 'test@qq.com', '$2a$10$HjF009wzgDVIOmh9MdzrRul8Ev2qB3aE4CbFTwVj6zvCYDYGCR7k2', '15520165664', 1765442052888, 0, 0, 0, '/images/head.png', '/images/top.png', NULL, NULL, 0, 0, 0, NULL, NULL);
INSERT INTO `user` VALUES (5, 'dontwannaknow', '1598818827@qq.com', '$2a$10$vD0udbzfz.vkpRKuTKfmk.qaXiGFjRpTyYCNs1MqhbjZm95bC1GTm', '17823904857', 1765856079785, 0, 1, 1, '/images/head.png', '/images/top.png', NULL, NULL, 0, 0, 0, NULL, NULL);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NOT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色',
  `create_time` bigint NOT NULL,
  `update_time` bigint NOT NULL,
  `vip_start_time` bigint NULL DEFAULT NULL,
  `vip_stop_time` bigint NULL DEFAULT NULL,
  `modified` bigint NOT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 2, 'ROLE_ADMIN', 1764665378431, 1765529577347, 1765497600000, 1797033600000, 2);
INSERT INTO `user_role` VALUES (2, 3, 'ROLE_ADMIN', 1765347790590, 1765533528962, 1765497600000, 1797033600000, 2);
INSERT INTO `user_role` VALUES (3, 4, 'ROLE_USER', 1765442052964, 1765530451501, 1765497600000, 1797033600000, 3);
INSERT INTO `user_role` VALUES (4, 5, 'ROLE_USER', 1765856079861, 1765856079861, NULL, NULL, 0);

-- ----------------------------
-- Table structure for web_config
-- ----------------------------
DROP TABLE IF EXISTS `web_config`;
CREATE TABLE `web_config`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `json_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置json文件',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建用户',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '配置设置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of web_config
-- ----------------------------
INSERT INTO `web_config` VALUES (1, '{\"name\":\"TikTube\",\"baseUrl\":\"\",\"openNoVipLimit\":false,\"noVipViewCount\":false,\"logoUrl\":\"/favicon.jpg\",\"openInvitationRegister\":false,\"webDescribe\":\"一个牛逼的视频网站!\",\"openUploadVideoAddViewCount\":false,\"openExamine\":true,\"openCommentExam\":true,\"openDanmakuExam\":true,\"homeMaxVideoCount\":50,\"openEmail\":false,\"openAIConfig\":false,\"mailConfig\":{\"host\":null,\"port\":null,\"username\":null,\"password\":null,\"protocol\":null}}', 0, 1764664671731, 'SYSTEM_INIT');
INSERT INTO `web_config` VALUES (2, '{\"name\":\"StreamVerse\",\"baseUrl\":\"\",\"openNoVipLimit\":false,\"noVipViewCount\":false,\"logoUrl\":\"/favicon.jpg\",\"openInvitationRegister\":false,\"webDescribe\":\"一个牛逼的视频网站!\",\"openUploadVideoAddViewCount\":false,\"openExamine\":true,\"openCommentExam\":true,\"openDanmakuExam\":true,\"homeMaxVideoCount\":50,\"openEmail\":false,\"openAIConfig\":false,\"mailConfig\":{\"host\":null,\"port\":null,\"username\":null,\"password\":null,\"protocol\":null}}', 0, 1765505770639, 'SYSTEM_MIGRATION');
INSERT INTO `web_config` VALUES (3, '{\"name\":\"StreamVerse\",\"baseUrl\":\"\",\"openNoVipLimit\":false,\"noVipViewCount\":false,\"logoUrl\":\"/favicon.jpg\",\"openInvitationRegister\":false,\"webDescribe\":\"一个牛逼的视频网站!\",\"openUploadVideoAddViewCount\":false,\"openExamine\":true,\"openCommentExam\":true,\"openDanmakuExam\":true,\"homeMaxVideoCount\":50,\"openEmail\":true,\"openAIConfig\":false,\"mailConfig\":{\"host\":\"smtp.qq.com\",\"port\":465,\"username\":\"2644539131@qq.com\",\"password\":\"gttimwcgeuajebjb\",\"protocol\":\"smtp\"}}', 2, 1765855910361, '127.0.0.1');

-- ----------------------------
-- Table structure for web_setting
-- ----------------------------
DROP TABLE IF EXISTS `web_setting`;
CREATE TABLE `web_setting`  (
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '网站名',
  `open_no_vip_limit` int NOT NULL DEFAULT 1 COMMENT '是否开启非vip每日观看次数限制 [0 关闭， 1 开启]',
  `no_vip_view_count` int NOT NULL DEFAULT 5 COMMENT '非vip 每日观看次数',
  `logo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '/logo.png' COMMENT '网页logo地址',
  `open_invitation_register` int NOT NULL DEFAULT 1 COMMENT '是否开启邀请码注册 【0 关闭， 1开启】',
  `web_describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '网页简短的描述',
  `open_upload_video_add_view_count` int NOT NULL DEFAULT 1 COMMENT '是否开启每日上传视频增加非会员观看次数 【0 关闭， 1开启】',
  `open_examine` int NOT NULL DEFAULT 1 COMMENT '是否开启视频，文章，图片审核 【0 关闭， 1 开启】',
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `home_max_video_count` int NOT NULL DEFAULT 50 COMMENT '首页最大显示数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of web_setting
-- ----------------------------
INSERT INTO `web_setting` VALUES ('TikTube', 1, 5, '/favicon.jpg', 0, '一个牛逼的视频网站', 1, 1, 1, 0, 50);

SET FOREIGN_KEY_CHECKS = 1;
