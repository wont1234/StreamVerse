<template>
  <div>
    <v-container fluid class="sv-video-page">
      <!-- 添加 Snackbar 组件 -->
      <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="2000" location="top">
        {{ snackbar.text }}
      </v-snackbar>

      <v-dialog v-model="favoriteDialog" max-width="520">
        <v-card>
          <v-card-title class="text-h6">选择收藏夹</v-card-title>
          <v-card-text>
            <v-select
              v-model="selectedFavoriteLabelId"
              :items="favoritesLabels"
              item-title="name"
              item-value="id"
              label="收藏夹"
              variant="outlined"
              density="comfortable"
              :loading="favoritesLabelsLoading"
            />
            <v-text-field
              v-model="newFavoriteName"
              label="新建收藏夹名称"
              variant="outlined"
              class="mt-3"
              density="comfortable"
              maxlength="30"
              clearable
            />
          </v-card-text>
          <v-card-actions class="justify-end">
            <v-btn variant="text" :loading="favoriteCreating" @click="createFavoriteLabel">新建</v-btn>
            <v-btn variant="text" @click="favoriteDialog = false">取消</v-btn>
            <v-btn color="primary" variant="elevated" :loading="favoriteSubmitting" @click="submitFavorite">确定</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- 固定层兜底，shareDialog=true 时必定显示 -->
      <div v-if="shareDialog" class="share-inline-fixed" @click.self="shareDialog = false">
        <div class="share-inline-card">
          <div class="d-flex justify-end mb-1">
            <v-btn icon="mdi-close" variant="text" size="small" @click="shareDialog = false"></v-btn>
          </div>
          <ShareCard :article="{ id: id, title: videoData?.title || '' }" />
        </div>
      </div>

      <v-row v-if="!showNotFound" style="padding-top: 12px; padding-bottom: 12px">
        <v-col :cols="colsWidth" style="padding-bottom: 0px">
          <div class="position-relative sv-video-player-wrap">
            <PlayerVideo
              v-if="playVideoData != null && adsInfoStatus"
              :article="parseInt(id)"
              :video="playVideoData"
              :picurl="videoData ? videoData.imgUrl : ''"
              :open-ads="openAds"
              :ads-info="adsInfo"
              @playback-error="onPlaybackError"
            />

            <v-overlay
              :model-value="playbackBlocked"
              class="align-center justify-center"
              scrim="rgba(0,0,0,0.72)"
              contained
            >
              <v-card rounded="lg" max-width="520" class="pa-4" elevation="0">
                <v-card-title class="text-h6 font-weight-bold">无法播放</v-card-title>
                <v-card-text class="text-body-2">{{ playbackBlockedMessage }}</v-card-text>
                <v-card-actions class="justify-end">
                  <v-btn variant="text" @click="playbackBlocked = false">关闭</v-btn>
                  <v-btn v-if="!userInfo.userData" color="primary" variant="elevated" @click="$router.push('/login')">
                    去登录
                  </v-btn>
                  <v-btn color="primary" variant="elevated" prepend-icon="mdi-crown" @click="goToVip">
                    开通VIP
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-overlay>
          </div>

          <!-- 宽屏：视频下方直接展示标题/简介/评论（保持右侧为推荐区） -->
          <div v-if="colsWidth === 8 && videoData != null" class="sv-video-under">
            <!-- 视频标题 -->
            <v-card flat>
              <v-card-title class="text-h5 px-2">
                {{ videoData.title }}
              </v-card-title>

              <v-card-text class="pt-0 pb-2 px-2">
                <div class="text-body-2">{{ videoData.describes }}</div>

                <div v-if="videoData.tag && videoData.tag.length" class="mt-2">
                  <v-chip-group>
                    <v-chip
                      v-for="item in videoData.tag"
                      :key="item"
                      size="small"
                      variant="tonal"
                      color="primary"
                      density="comfortable"
                      class="mr-1"
                    >
                      {{ item }}
                    </v-chip>
                  </v-chip-group>
                </div>
              </v-card-text>

              <!-- 视频交互工具栏 -->
              <v-card-actions class="px-0 py-2">
                <div class="d-flex flex-wrap align-center w-100">
                  <div class="d-flex flex-wrap">
                    <v-btn
                      prepend-icon="mdi-thumb-up"
                      :variant="isLiked ? 'flat' : 'tonal'"
                      color="primary"
                      @click="likeVideo"
                      class="ma-1"
                    >
                      {{ videoData.likeCount }}
                    </v-btn>
                    <v-btn
                      prepend-icon="mdi-thumb-down"
                      :variant="isDisliked ? 'flat' : 'tonal'"
                      color="error"
                      @click="dislikeVideo"
                      class="ma-1"
                    >
                      {{ videoData.dislikeCount }}
                    </v-btn>
                    <v-btn prepend-icon="mdi-share" variant="tonal" @click.stop="openShareDialog" class="ma-1">
                      分享
                    </v-btn>
                    <v-btn
                      prepend-icon="mdi-content-save"
                      color="orange-lighten-2"
                      :variant="isFavorited ? 'flat' : 'tonal'"
                      @click="favoritesClick"
                      class="ma-1"
                    >
                      <span v-if="isFavorited">取消收藏</span>
                      <span v-else>收藏</span>
                    </v-btn>
                  </div>

                  <v-spacer class="d-none d-sm-block"></v-spacer>

                  <div class="d-flex flex-wrap mt-2 mt-sm-0">
                    <v-chip variant="outlined" color="orange-lighten-2" class="ma-1">
                      {{ videoData.favoriteCount }} 次收藏
                    </v-chip>
                    <v-chip variant="outlined" color="blue" class="ma-1">
                      {{ videoData.viewCount }} 次观看
                    </v-chip>
                    <v-chip variant="outlined" class="ma-1">
                      {{ videoData.danmakuCount }} 弹幕
                    </v-chip>
                  </div>
                </div>
              </v-card-actions>

              <!-- 视频分类信息 -->
              <v-card-subtitle class="px-0 py-2">
                <router-link
                  v-if="videoData.childrenCategory.fatherId !== 0"
                  :to="`/v/${videoData.fatherCategory.id}`"
                  class="category-link text-decoration-none"
                >
                  {{ videoData.fatherCategory.name }}
                </router-link>
                /
                <router-link
                  :to="`/v/${videoData.childrenCategory.id}`"
                  class="category-link text-decoration-none"
                >
                  {{ videoData.childrenCategory.name }}
                </router-link>
                &nbsp;&nbsp;&nbsp;&nbsp; 发布于：
                {{ TimeUtil.renderTime(videoData.createTime) }}

                <v-btn class="ml-2" color="red" size="small" variant="text" @click="showReportDialog=true">
                  举报
                </v-btn>
              </v-card-subtitle>

              <v-divider class="my-2"></v-divider>
            </v-card>

            <v-card class="my-2" variant="elevated">
              <v-card-text class="pt-3">
                <Comment
                  :article="id"
                  :author-id="videoData.userId"
                  :typenum="1"
                  :count="videoData.commentCount"
                />
              </v-card-text>
            </v-card>
          </div>
        </v-col>

        <!-- 宽屏：播放器右侧作者信息 + 推荐视频 -->
        <v-col v-if="colsWidth === 8 && videoData != null" cols="4" style="padding-bottom: 0px">
          <!-- 上传用户信息（右侧固定显示） -->
          <v-card class="mb-4" variant="elevated">
            <v-card-text class="py-3">
              <div class="d-flex align-center">
                <router-link :to="`/user/${videoData.userId}`">
                  <v-avatar size="48" class="mr-2">
                    <v-img :src="videoData.avatarUrl" alt="用户头像"></v-img>
                  </v-avatar>
                </router-link>
                <div>
                  <router-link :to="`/user/${videoData.userId}`" class="text-decoration-none">
                    <div class="text-subtitle-1 font-weight-bold">{{ videoData.username }}</div>
                  </router-link>
                  <div class="text-caption text-medium-emphasis">个性签名：{{ videoData.introduction }}</div>
                </div>
              </div>

              <div class="d-flex align-center flex-wrap mt-3" style="gap: 8px">
                <v-chip variant="tonal" color="primary" size="small">
                  {{ videoData.fansCount }} 粉丝
                </v-chip>

                <v-btn
                  variant="tonal"
                  color="primary"
                  density="comfortable"
                  prepend-icon="mdi-account"
                  :to="`/user/${videoData.userId}`"
                >
                  进入主页
                </v-btn>

                <v-btn
                  v-if="videoData.userId !== userInfo.userData?.id"
                  :color="isFollowed ? 'grey' : 'primary'"
                  :variant="isFollowed ? 'flat' : 'outlined'"
                  density="comfortable"
                  :prepend-icon="!userInfo.userData ? 'mdi-login' : (isFollowed ? 'mdi-account-check' : 'mdi-account-plus')"
                  @click="userInfo.userData ? toggleFollow() : $router.push('/login')"
                >
                  {{ !userInfo.userData ? '登录后关注' : (isFollowed ? '已关注' : '关注') }}
                </v-btn>

                <v-btn
                  v-if="videoData.userId !== userInfo.userData?.id"
                  variant="tonal"
                  color="primary"
                  density="comfortable"
                  prepend-icon="mdi-message-text"
                  @click="userInfo.userData ? $router.push({ path: '/chat', query: { withUserId: videoData.userId } }) : $router.push('/login')"
                >
                  私信
                </v-btn>

                <v-btn
                  v-else
                  variant="outlined"
                  color="primary"
                  density="comfortable"
                  prepend-icon="mdi-account"
                  :to="`/user/${videoData.userId}`"
                >
                  我的主页
                </v-btn>
              </div>
            </v-card-text>
          </v-card>

          <!-- 推荐视频（右侧） -->
          <v-card class="mb-4">
            <v-card-title class="d-flex align-center">
              <v-icon class="mr-2">mdi-playlist-play</v-icon>
              推荐视频
            </v-card-title>
            <v-card-text>
              <p
                class="text-center text-body-2 text-grey mb-3"
                v-if="videoData.similar && videoData.similar.length === 0"
              >
                暂无推荐视频
              </p>
              <p
                class="text-center text-body-2 text-grey mb-3"
                v-else-if="videoData.similar && allItemsHaveSort"
              >
                暂未发现可以推荐的视频，为你推荐热门内容
              </p>
              <v-row>
                <v-col cols="12" v-for="item in videoData.similar" :key="item.id" class="py-2">
                  <div class="position-relative">
                    <VideoCared :video="item" />
                    <v-chip
                      v-if="item.sort !== null && item.sort !== undefined"
                      color="primary"
                      size="small"
                      class="position-absolute top-0 start-0 ma-2 pa-1"
                      variant="elevated"
                    >
                      <v-icon size="x-small" start>mdi-fire</v-icon>
                      热门
                    </v-chip>
                  </div>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <v-row v-else justify="center" align="center" style="padding-top: 12px; padding-bottom: 12px">
        <v-col cols="12" sm="10" md="8" lg="6">
          <v-card class="mx-auto" rounded="lg" elevation="0">
            <v-card-item class="text-center">
              <v-card-title class="text-h5 font-weight-bold my-4">视频不可用</v-card-title>
              <v-card-subtitle class="text-body-1 my-2">{{ notFoundMessage }}</v-card-subtitle>
            </v-card-item>
            <v-card-text class="text-center">
              <v-img
                src="/images/error.gif"
                alt="not found"
                class="mx-auto my-4"
                max-width="260"
                contain
              ></v-img>

              <div class="d-flex flex-column flex-sm-row justify-center gap-3 mt-5">
                <v-btn
                  color="red"
                  variant="elevated"
                  prepend-icon="mdi-history"
                  size="large"
                  rounded="lg"
                  @click="goToHistory"
                >
                  返回历史记录
                </v-btn>
                <span>&nbsp;&nbsp;&nbsp;&nbsp;</span>
                <v-btn
                  variant="outlined"
                  color="default"
                  prepend-icon="mdi-home"
                  size="large"
                  rounded="lg"
                  @click="$router.push('/')"
                >
                  返回首页
                </v-btn>
                <span>&nbsp;&nbsp;&nbsp;&nbsp;</span>
                <v-btn
                  color="primary"
                  variant="elevated"
                  prepend-icon="mdi-crown"
                  size="large"
                  rounded="lg"
                  @click="goToVip"
                >
                  开通VIP
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
    <!-- 视频详情 -->
    <v-container v-if="videoData != null && !showNotFound && colsWidth === 12" class="sv-video-detail" style="padding-top: 0px">
      <v-row>
        <v-col cols="12">
          <!-- 视频标题 -->
          <v-card flat>
            <v-card-title class="text-h5 px-2">
              {{ videoData.title }}
            </v-card-title>

            <v-card-text class="pt-0 pb-2 px-2">
              <div class="text-body-2">{{ videoData.describes }}</div>

              <div v-if="videoData.tag && videoData.tag.length" class="mt-2">
                <v-chip-group>
                  <v-chip
                    v-for="item in videoData.tag"
                    :key="item"
                    size="small"
                    variant="tonal"
                    color="primary"
                    density="comfortable"
                    class="mr-1"
                  >
                    {{ item }}
                  </v-chip>
                </v-chip-group>
              </div>
            </v-card-text>

            <!-- 视频交互工具栏 -->
            <v-card-actions class="px-0 py-2">
              <div class="d-flex flex-wrap align-center w-100">
                <div class="d-flex flex-wrap">
                  <v-btn
                    prepend-icon="mdi-thumb-up"
                    :variant="isLiked ? 'flat' : 'tonal'"
                    color="primary"
                    @click="likeVideo"
                    class="ma-1"
                  >
                    {{ videoData.likeCount }}
                  </v-btn>
                  <v-btn
                    prepend-icon="mdi-thumb-down"
                    :variant="isDisliked ? 'flat' : 'tonal'"
                    color="error"
                    @click="dislikeVideo"
                    class="ma-1"
                  >
                    {{ videoData.dislikeCount }}
                  </v-btn>
                  <v-btn prepend-icon="mdi-share" variant="tonal" @click.stop="openShareDialog" class="ma-1">
                    分享
                  </v-btn>
                  <v-btn
                    prepend-icon="mdi-content-save"
                    color="orange-lighten-2"
                    :variant="isFavorited ? 'flat' : 'tonal'"
                    @click="favoritesClick"
                    class="ma-1"
                  >
                    <span v-if="isFavorited">取消收藏</span>
                    <span v-else>收藏</span>
                  </v-btn>
                </div>
                
                <v-spacer class="d-none d-sm-block"></v-spacer>
                
                <div class="d-flex flex-wrap mt-2 mt-sm-0">
                  <v-chip variant="outlined" color="orange-lighten-2" class="ma-1">
                    {{ videoData.favoriteCount }} 次收藏
                  </v-chip>
                  <v-chip variant="outlined" color="blue" class="ma-1">
                    {{ videoData.viewCount }} 次观看
                  </v-chip>
                  <v-chip variant="outlined" class="ma-1">
                    {{ videoData.danmakuCount }} 弹幕
                  </v-chip>
                </div>
              </div>
            </v-card-actions>

            <!-- 视频分类信息 -->
            <v-card-subtitle class="px-0 py-2">
              <router-link
                v-if="videoData.childrenCategory.fatherId !== 0"
                :to="`/v/${videoData.fatherCategory.id}`"
                class="category-link text-decoration-none"
              >
                {{ videoData.fatherCategory.name }}
              </router-link>
              /
              <router-link
                :to="`/v/${videoData.childrenCategory.id}`"
                class="category-link text-decoration-none"
              >
                {{ videoData.childrenCategory.name }}
              </router-link>
              &nbsp;&nbsp;&nbsp;&nbsp; 发布于：
              {{ TimeUtil.renderTime(videoData.createTime) }}

              <v-btn class="ml-2" color="red" size="small" variant="text" @click="showReportDialog=true">
                举报
              </v-btn>
            </v-card-subtitle>
            <v-divider class="my-2"></v-divider>
          </v-card>

          <v-card class="my-2" variant="elevated">
            <v-card-text class="pt-3">
              <Comment
                :article="id"
                :author-id="videoData.userId"
                :typenum="1"
                :count="videoData.commentCount"
              />
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- 小屏幕下的推荐视频（在评论区后面显示） -->
      <v-row v-if="colsWidth === 12 && videoData != null">
        <v-col cols="12">
          <v-card class="mb-4">
            <v-card-title class="d-flex align-center">
              <v-icon class="mr-2">mdi-playlist-play</v-icon>
              推荐视频
            </v-card-title>
            <v-card-text>
              <p
                class="text-center text-body-2 text-grey mb-3"
                v-if="videoData.similar && videoData.similar.length === 0"
              >
                暂无推荐视频
              </p>
              <p
                class="text-center text-body-2 text-grey mb-3"
                v-else-if="videoData.similar && allItemsHaveSort"
              >
                暂未发现可以推荐的视频，为你推荐热门内容
              </p>
              <v-row>
                <v-col
                  cols="12"
                  sm="6"
                  md="4"
                  v-for="item in videoData.similar"
                  :key="item.id"
                  class="py-2"
                >
                  <div class="position-relative">
                    <VideoCared :video="item" />
                    <v-chip
                      v-if="item.sort !== null && item.sort !== undefined"
                      color="primary"
                      size="small"
                      class="position-absolute top-0 start-0 ma-2 pa-1"
                      variant="elevated"
                    >
                      <v-icon size="x-small" start>mdi-fire</v-icon>
                      热门
                    </v-chip>
                  </div>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <v-dialog v-model="showReportDialog" width="50vh">
        <OpinionCard 
          :targetId="id" 
          :typeNum="0"
          :target-title="videoData.title"  
          :isReport="true"
          @close="showReportDialog = false"
          @success="handleReportSuccess"
        />
      </v-dialog>

      <!-- 分享卡片弹窗 -->
      <v-dialog
        max-width="640"
        v-model="shareDialog"
        attach="body"
        persistent
        :retain-focus="false"
        scrim="rgba(0,0,0,0.75)"
        class="share-dialog"
        content-class="share-dialog-content"
        transition="dialog-bottom-transition"
      >
        <div class="share-card-wrapper">
          <ShareCard :article="{ id: id, title: videoData?.title || '' }" />
        </div>
      </v-dialog>

    </v-container>
  </div>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import PlayerVideo from '@/components/player/PlayerVideo.vue'
import Comment from '@/views/comment/VideoComment.vue'
import { useUserStore } from '@/stores/userStore'
import { useWebInfoStore } from '@/stores/webInfoStore'
import ShareCard from '@/components/card/ShareCard.vue'
import VideoCared from '@/components/card/VideoCard.vue'
import checkPower from '@/utils/check-power.vue'
import OpinionCard from '@/components/card/OpinionCard.vue'
export default {
  name: 'VideoView',
  components: {
    PlayerVideo,
    Comment,
    ShareCard,
    VideoCared,
    OpinionCard
  },
  data() {
    return {
      score: 0,
      TimeUtil,
      id: 0,
      videoData: null,
      playVideoData: null,
      showNotFound: false,
      notFoundMessage: '该视频不存在或已被删除。',
      windowSize: {},
      colsWidth: 8,
      detailTab: 'comment',
      isLiked: false,
      isDisliked: false,
      isFavorited: false,
      isFollowed: false,
      userInfo: useUserStore(),
      adsInfo: null,
      snackbar: {
        show: false,
        text: '',
        color: 'success',
      },
      adsInfoStatus: false,
      shareDialog: false,
      openAds: false,
      showReportDialog: false,
      playbackBlocked: false,
      playbackBlockedMessage: '',
      favoriteDialog: false,
      favoritesLabels: [],
      favoritesLabelsLoading: false,
      selectedFavoriteLabelId: null,
      favoriteSubmitting: false,
      currentFavoriteLabelId: null,
      newFavoriteName: '',
      favoriteCreating: false,
    }
  },
  computed: {
    allItemsHaveSort() {
      if (!this.videoData || !this.videoData.similar || this.videoData.similar.length === 0) {
        return false
      }
      return this.videoData.similar.every((item) => item.sort !== null && item.sort !== undefined)
    },
  },
  created() {
    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    })
    this.id = parseInt(this.$route.params.id)
    this.getAllVideoInfo()
    this.onResize()
    window.addEventListener('resize', this.onResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.onResize)
  },
  methods: {
    openShareDialog() {
      this.shareDialog = true
      this.showMessage('打开分享弹窗', 'info')
    },
    isVipSkipAds() {
      const user = this.userInfo && this.userInfo.userData ? this.userInfo.userData : null
      if (!user) {
        return false
      }

      // 超级管理员默认拥有 VIP 权限
      if (user.mail === '123@qq.com') {
        return true
      }

      // 兼容项目已有的 VIP 判定逻辑
      if (user.userRoleEntity) {
        try {
          return checkPower.checkVip(user)
        } catch (e) {
          return false
        }
      }
      return false
    },
    goToHistory() {
      this.$router.push('/history')
    },
    goToVip() {
      this.$router.push('/vip')
    },
    onPlaybackError() {
      const fallback = this.userInfo && this.userInfo.userData
        ? '视频加载失败：可能已达到今日播放次数限制，请开通VIP或明天再试。'
        : '视频加载失败：游客每日仅可播放 5 次，请登录或开通VIP。'

      const video = this.playVideoData
      const url = video && video.fileUrl && video.key ? `${video.fileUrl}?key=${encodeURIComponent(video.key)}` : null
      if (!url) {
        this.playbackBlockedMessage = fallback
        this.playbackBlocked = true
        this.showMessage(fallback, 'warning')
        return
      }

      fetch(url, {
        method: 'GET',
        credentials: 'include',
        headers: {
          Range: 'bytes=0-0',
        },
      })
        .then((res) => {
          if (res.status === 403) {
            this.playbackBlockedMessage = '今日播放次数已用完，请开通VIP或明天再试。'
          } else {
            this.playbackBlockedMessage = fallback
          }
          this.playbackBlocked = true
          this.showMessage(this.playbackBlockedMessage, 'warning')
        })
        .catch(() => {
          this.playbackBlockedMessage = fallback
          this.playbackBlocked = true
          this.showMessage(fallback, 'warning')
        })
    },
    setNotFound(message) {
      this.videoData = null
      this.playVideoData = null
      this.showNotFound = true
      this.notFoundMessage = message || '该视频不存在或已被删除。'
      document.title = '视频不可用'
    },
    handleReportSuccess() {
      // 处理举报成功的逻辑
      //this.showReportDialog = false
    },
    // 控制页面大小
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.colsWidth = 12
      } else {
        this.colsWidth = 8
      }
    },
    videoInfo() {
      const webInfo = useWebInfoStore().webInfo
      fetch(`${this.SERVER_API_URL}/article/video/${this.id}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'App-Name': webInfo?.name,
        },
        method: 'GET',
        credentials: 'include',
      })
        .then(async (response) => {
          try {
            return await response.json()
          } catch (e) {
            return { status: response.status, message: response.statusText }
          }
        })
        .then((json) => {
          if (json && json.status === 200 && json.data && json.data.isShow) {
            this.showNotFound = false
            this.videoData = json.data
            this.playVideoData = this.videoData.video.find((item) => item.type === 0)
            document.title = json.data.title
            // 假设数据中包含点赞和点踩数量，如果没有则使用默认值
            this.likeCount = json.data.likeCount || 0
            this.dislikeCount = json.data.dislikeCount || 0
            this.checkLike()
            this.checkDislike()
            this.checkFavorites()
            this.checkFollow()
          } else {
            this.setNotFound('该视频不存在或已被删除。')
          }
        })
        .catch(() => {
          this.setNotFound('该视频不存在或已被删除。')
        })
    },
    getAdsInfo() {
      const webInfo = useWebInfoStore().webInfo
      this.adsInfoStatus = false
      this.openAds = false
      this.adsInfo = null

      // VIP（以及超级管理员）跳过视频前贴片广告
      if (this.isVipSkipAds()) {
        this.adsInfoStatus = true
        return
      }

      fetch(`${this.SERVER_API_URL}/web/notice?type=2`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'App-Name': webInfo?.name,
        },
        method: 'GET',
        credentials: 'include',
      })
        .then(async (response) => {
          try {
            return await response.json()
          } catch (e) {
            return { status: response.status, message: response.statusText }
          }
        })
        .then((json) => {
          if (json && json.status === 200 && Array.isArray(json.data) && json.data.length > 0) {
            const list = json.data
            const idx = this.getRandomInt(0, list.length - 1)
            this.adsInfo = list[idx]
            this.openAds = !!(this.adsInfo && (this.adsInfo.videoUrl || this.adsInfo.imageUrl))
          } else {
            this.openAds = false
            this.adsInfo = null
          }
        })
        .catch(() => {
          this.openAds = false
          this.adsInfo = null
        })
        .finally(() => {
          this.adsInfoStatus = true
        })
    },
    getAllVideoInfo() {
      this.getAdsInfo()
      this.videoInfo()
    },
    getRandomInt(min, max) {
      if (min == max) {
        return min
      }
      min = Math.ceil(min)
      max = Math.floor(max)
      return Math.floor(Math.random() * (max - min + 1)) + min
    },
    requestGet(url) {
      return new Promise((resolve) => {
        this.httpGet(url, (json) => resolve(json))
      })
    },

    async createFavoriteLabel() {
      const name = (this.newFavoriteName || '').trim()
      if (!name) {
        this.showMessage('请输入收藏夹名称', 'warning')
        return
      }
      this.favoriteCreating = true
      try {
        const json = await this.requestPost('/favorites/label/create', { name })
        if (json && json.status === 200 && json.data && json.data.id != null) {
          this.showMessage('新建成功', 'success')
          this.favoritesLabels.push(json.data)
          this.selectedFavoriteLabelId = json.data.id
          this.newFavoriteName = ''
        } else {
          this.showMessage('新建失败', 'error')
        }
      } finally {
        this.favoriteCreating = false
      }
    },

    requestPost(url, data) {
      return new Promise((resolve) => {
        this.httpPost(url, data, (json) => resolve(json))
      })
    },

    loadFavoriteLabels() {
      this.favoritesLabelsLoading = true
      return this.requestGet('/favorites/label/list')
        .then((json) => {
          if (json && json.status === 200 && Array.isArray(json.data)) {
            this.favoritesLabels = json.data
          }
        })
        .finally(() => {
          this.favoritesLabelsLoading = false
        })
    },

    getFavoriteInfo() {
      return this.requestGet(`/favorites/info?articleId=${this.id}`).then((json) => {
        if (json && json.status === 200) {
          this.currentFavoriteLabelId = json.data ? json.data.favoritesLabelId : null
        }
        return json
      })
    },

    submitFavorite() {
      if (this.userInfo.userData == null) {
        this.showMessage('请先登录后再收藏', 'warning')
        return
      }
      if (this.selectedFavoriteLabelId == null) {
        this.showMessage('请选择收藏夹', 'warning')
        return
      }

      this.favoriteSubmitting = true
      this.requestPost('/favorites/toggle', {
        articeId: this.id,
        favoritesLabelId: this.selectedFavoriteLabelId,
      })
        .then((json) => {
          const id = json && json.data ? json.data.id : null

          if (id != null) {
            if (!this.isFavorited) {
              this.showMessage('收藏成功！', 'success')
              this.isFavorited = true
              this.videoData.favoriteCount += 1
            } else {
              this.showMessage('已移动到新的收藏夹', 'success')
            }
            this.currentFavoriteLabelId = this.selectedFavoriteLabelId
            return
          }

          if (this.isFavorited) {
            this.showMessage('取消收藏成功！', 'info')
            this.isFavorited = false
            this.videoData.favoriteCount -= 1
            this.currentFavoriteLabelId = null
            return
          }

          this.showMessage('操作失败', 'error')
        })
        .finally(() => {
          this.favoriteSubmitting = false
          this.favoriteDialog = false
        })
    },

    // 点赞功能
    likeVideo() {
      // 如果用户未登录
      if (this.userInfo.userData == null) {
        this.showMessage('请先登录后再点赞', 'warning')
        return
      }
      this.httpPost(`/like/toggle?likeObjId=${this.id}&type=0`, {}, (json) => {
        if (json.status === 200) {
          // 更新点赞状态
          if (json.data.like) {
            this.isLiked = !this.isLiked
            // 更新点赞数量
            this.videoData.likeCount += this.isLiked ? 1 : -1
            // 显示消息提示
            if (this.isLiked) {
              this.showMessage('点赞成功', 'success')
            } else {
              this.showMessage('取消点赞成功', 'info')
            }
          }
        } else {
          this.showMessage(json.data.info || '操作失败', 'error')
        }
      })
    },

    async favoritesClick() {
      if (this.userInfo.userData == null) {
        this.showMessage('请先登录后再收藏', 'warning')
        return
      }

      await this.loadFavoriteLabels()
      if (!this.favoritesLabels || this.favoritesLabels.length === 0) {
        this.showMessage('暂无收藏夹', 'warning')
        return
      }

      if (this.isFavorited) {
        await this.getFavoriteInfo()
        this.selectedFavoriteLabelId = this.currentFavoriteLabelId || this.favoritesLabels[0].id
      } else {
        this.selectedFavoriteLabelId = this.favoritesLabels[0].id
      }
      this.favoriteDialog = true
    },
    checkFavorites() {
      if (this.userInfo.userData == null) {
        this.isFavorited = false
        return
      }
      this.httpGet(`/favorites/check?articleId=${this.id}`, (json) => {
        if (json.status === 200) {
          this.isFavorited = json.data
        }
      })
    },
    // 关注功能
    toggleFollow() {
      if (this.userInfo.userData == null) {
        this.showMessage('请先登录后再关注', 'warning')
        return
      }

      if (this.isFollowed) {
        this.unfollowUser()
      } else {
        this.followUser()
      }
    },

    followUser() {
      if (this.userInfo.userData == null) {
        this.showMessage('请先登录后再关注', 'warning')
        return
      }

      const data = {
        followUser: this.videoData.userId,
        createUser: this.userInfo.userData.id,
      }

      this.httpPost('/follow/add', data, (json) => {
        if (json.data) {
          this.isFollowed = true
          if (this.videoData.fansCount !== undefined) {
            this.videoData.fansCount += 1
          }
          this.showMessage('关注成功', 'success')
        } else {
          this.showMessage('关注失败，请稍后重试', 'error')
        }
      })
    },

    unfollowUser() {
      if (this.userInfo.userData == null) {
        this.showMessage('请先登录', 'warning')
        return
      }

      const data = {
        followUser: this.videoData.userId,
        createUser: this.userInfo.userData.id,
      }

      this.httpPost('/follow/delete', data, (json) => {
        if (json.data) {
          this.isFollowed = false
          if (this.videoData.fansCount !== undefined && this.videoData.fansCount > 0) {
            this.videoData.fansCount -= 1
          }
          this.showMessage('已取消关注', 'info')
        } else {
          this.showMessage('取消关注失败，请稍后重试', 'error')
        }
      })
    },

    checkFollow() {
      if (this.userInfo.userData == null || this.videoData.userId === this.userInfo.userData.id) {
        this.isFollowed = false
        return
      }

      this.httpGet(`/follow/check?followId=${this.videoData.userId}`, (json) => {
        this.isFollowed = json.data
      })
    },

    checkLike() {
      if (this.userInfo.userData == null) {
        this.isLiked = false
        return
      }
      this.httpGet(`/like/status?likeObjId=${this.id}&type=0`, (json) => {
        if (json.status === 200) {
          this.isLiked = json.data
        }
      })
    },
    checkDislike() {
      if (this.userInfo.userData == null) {
        this.isDisliked = false
        return
      }
      this.httpGet(`/dislike/status?dislikeObjId=${this.id}&type=0`, (json) => {
        if (json.status === 200) {
          this.isDisliked = json.data
        }
      })
    },
    // 点踩功能
    dislikeVideo() {
      if (this.userInfo.userData == null) {
        this.showMessage('请先登录后再点踩', 'warning')
        return
      }
      this.httpPost(`/dislike/toggle?dislikeObjId=${this.id}&type=0`, {}, (json) => {
        if (json.status === 200) {
          // 更新点赞状态
          if (json.data.dislike) {
            this.isDisliked = !this.isDisliked
            // 更新点赞数量
            this.videoData.dislikeCount += this.isDisliked ? 1 : -1
            // 显示消息提示
            this.showMessage(json.data.info, 'success')
          }
        } else {
          this.showMessage(json.data.info || '操作失败', 'error')
        }
      })
    },
    // 显示消息提示
    showMessage(text, color = 'success') {
      this.snackbar.text = text
      this.snackbar.color = color
      this.snackbar.show = true
    },
  },
}
</script>

<style>
.sv-video-page {
  padding-left: 0;
  padding-right: 0;
}

.sv-video-player-wrap {
  max-width: 1560px;
  margin: 0 auto;
  padding: 0 12px;
}

.sv-video-under {
  padding: 0 12px;
}

.sv-video-detail {
  max-width: 1560px;
  padding: 0 12px;
}
.share-dialog .v-overlay__content {
  z-index: 6000 !important;
  top: 50% !important;
  left: 50% !important;
  transform: translate(-50%, -50%) !important;
}
.share-dialog .v-overlay__scrim {
  z-index: 5999 !important;
}
.share-card-wrapper {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.35);
  padding: 12px;
}
.share-inline-fixed {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  z-index: 7000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}
.share-inline-card {
  max-width: 640px;
  width: 100%;
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.35);
}
.share-inline-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 7000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
}
.share-inline-card {
  max-width: 640px;
  width: 100%;
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.35);
}

.category-link {
  color: #1867c0;
}

.position-relative {
  position: relative;
}

.position-absolute {
  position: absolute;
  z-index: 1;
}

.top-0 {
  top: 0;
}

.start-0 {
  left: 0;
}
</style>