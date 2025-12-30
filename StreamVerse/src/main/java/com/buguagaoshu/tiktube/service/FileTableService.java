package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.entity.FileTableEntity;

import java.util.List;
import java.util.Map;

/**
 * 鏂囦欢琛? *
 */
public interface FileTableService extends IService<FileTableEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 鑾峰彇褰撳墠瑙嗛涓嬬殑瑙嗛鏂囦欢
     * @param id 瑙嗛ID
     * @return 瑙嗛鍖呭惈鐨勬枃浠跺垪琛?     * */
    List<FileTableEntity> findArticleVideo(long id);

    List<FileTableEntity> findArticle(long id);


    FileTableEntity findFileByFilename(String fileName);


    /**
     * 鏇存柊鏂囦欢鐘舵€?     * @param userId 鏂囦欢鎵€灞炵敤鎴?     * @param fileId 鏂囦欢ID
     * @param fileType 鏂囦欢绫诲瀷
     * @param fileUrl 鏂囦欢璺緞
     * @return 鏇存柊缁撴灉
     * */
    boolean updateFileStatus(long userId, long fileId, int fileType, String fileUrl);


    /**
     * @param endTime 缁撴潫鏃堕棿
     * @param count 杩斿洖鏁伴噺
     * 宸插純鐢ㄦ枃浠跺垪琛?     * */
    List<FileTableEntity> deprecatedFileList(long endTime, int count);


    /**
     * 鏇存柊鏂囦欢淇℃伅
     * */
    boolean updateFileInfo(FileTableEntity fileTableEntity);
}

