<template>
  <div>
    <v-app-bar
      app
      :color="isHome ? (isHomeTop ? 'transparent' : 'surface') : 'surface'"
      :flat="isHome ? isHomeTop : false"
      class="sv-home-appbar"
      :class="{ 'sv-home-appbar--home': isHomeTop }"
    >
      <v-menu location="bottom start" offset="6">
        <template #activator="{ props }">
          <v-btn v-bind="props" icon variant="text">
            <v-icon>mdi-menu</v-icon>
          </v-btn>
        </template>
        <v-list density="comfortable" class="sv-more-menu">
          <v-list-item
            v-for="item in items"
            :key="item.text"
            :prepend-icon="item.icon"
            :title="item.text"
            :to="item.link"
            rounded="xl"
          />
          <v-list-item
            prepend-icon="mdi-file-document-outline"
            title="文章"
            to="/articles"
            rounded="xl"
          />
          <v-divider class="my-2" />
          <v-list-item
            prepend-icon="mdi-message-alert"
            title="意见反馈"
            rounded="xl"
            @click="showOpinionDialog = true"
          />
        </v-list>
      </v-menu>
      <v-app-bar-title>
        <span style="cursor: pointer" @click="goToHome()">{{ webInfo.name }}</span>
      </v-app-bar-title>
      <!-- <v-container>
        <v-row>
          <v-col cols="5"> -->
      <v-text-field
        density="comfortable"
        prepend-inner-icon="mdi-magnify"
        variant="solo-filled"
        flat
        hide-details
        single-line
        label="搜索"
        color="#F44336"
        class="sv-home-search"
        v-model="searchText"
        @keydown="search"
      ></v-text-field>
      <!-- </v-col>
        </v-row>
      </v-container> -->
      <!-- 占位分割 -->
      <v-spacer />
      <!-- 发布 -->
      <v-tooltip location="bottom" text="发布">
        <template v-slot:activator="{ props }">
          <v-btn v-bind="props" icon="mdi-video-plus" @click="goToPublish()"></v-btn>
        </template>
      </v-tooltip>
      <!-- 通知 -->
      <v-tooltip location="bottom" text="通知">
        <template v-slot:activator="{ props }">
          <v-btn v-if="totalUnreadCount > 0" v-bind="props" stacked @click="goToNotification()">
            <v-badge color="error" :content="totalUnreadCount">
              <v-icon>mdi-bell</v-icon>
            </v-badge>
          </v-btn>
          <v-btn v-else v-bind="props" icon="mdi-bell" @click="goToNotification()"></v-btn>
        </template>
      </v-tooltip>
      <ThemeToggleButton />
      <AppBarHead v-if="userInfo.userData != null"></AppBarHead>
      <v-btn
        v-if="userInfo.userData == null"
        class="mr-2"
        prepend-icon="mdi-account-circle"
        variant="outlined"
        @click="goToLoginPage()"
        >登录</v-btn
      >

      <template v-slot:extension>
        <div class="sv-top-tabs-wrap">
          <v-tabs
            class="sv-top-tabs"
            color="primary"
            density="compact"
            show-arrows
            align-tabs="center"
          >
            <v-tab to="/" exact>首页</v-tab>
            <v-tab to="/hot">热门</v-tab>
            <v-tab to="/articles">文章</v-tab>
            <v-tab to="/subscribe">订阅</v-tab>
            <v-tab to="/history">历史</v-tab>
            <v-tab to="/playlist">收藏</v-tab>
            <v-tab to="/vip">VIP</v-tab>
            <v-tab to="/about">关于</v-tab>
          </v-tabs>
        </div>
      </template>
    </v-app-bar>

    <v-dialog v-model="showOpinionDialog" width="50vh">
        <OpinionCard 
          :targetId="-1" 
          :typeNum="10"
          :target-title="''"  
          :isReport="false"
          @close="showOpinionDialog = false"
        />
      </v-dialog>

    <v-main class="sv-main">
      <!--  fluid-->
      <div v-if="isHome" class="sv-hero">
        <v-carousel
          :height="heroHeight"
          hide-delimiter-background
          show-arrows="hover"
          cycle
          :interval="4500"
          class="sv-hero-carousel"
        >
          <v-carousel-item v-for="(item, idx) in heroSlides" :key="idx">
            <div class="sv-hero-slide" style="cursor: pointer" @click="goToVideo(item.id)">
              <v-img :src="item.src" cover height="100%">
                <div class="sv-hero-overlay">
                  <div class="sv-hero-title">{{ item.title }}</div>
                  <div class="sv-hero-subtitle">{{ item.subtitle }}</div>
                </div>
              </v-img>
            </div>
          </v-carousel-item>
        </v-carousel>
      </div>

      <router-view v-slot="{ Component }">
        <component :key="$route.fullPath" :is="Component" ref="childRef" />
      </router-view>
    </v-main>
  </div>
</template>

<script>
import { useWebInfoStore } from '@/stores/webInfoStore'
import { useUserStore } from '@/stores/userStore'
import AppBarHead from '@/components/user/AppBarHead.vue'
import ThemeToggleButton from '@/components/ThemeToggleButton.vue'
import OpinionCard from '@/components/card/OpinionCard.vue'
export default {
  components: {
    AppBarHead,
    ThemeToggleButton,
    OpinionCard
  },
  data: () => ({
    webInfo: {},
    scrollY: 0,
    heroSlides: [
      {
        src: '/images/banners/banner1.jpg',
        title: '发现正在流行的内容',
        subtitle: '一键进入时下流行 · 热门分区 · 精选推荐',
      },
      {
        src: '/images/banners/banner2.jpg',
        title: '更清爽的首页体验',
        subtitle: '顶部大图轮播 · 内容更聚焦',
      },
      {
        src: '/images/banners/banner3.jpg',
        title: '开始创作',
        subtitle: '上传视频 · 记录生活 · 分享知识',
      },
      {
        src: '/images/banners/banner4.jpg',
        title: '今日精选',
        subtitle: '看看大家都在看什么',
      },
      {
        src: '/images/banners/banner5.jpg',
        title: '关注你喜欢的创作者',
        subtitle: '订阅更新 · 第一时间获取新内容',
      },
    ],
    items: [
      { icon: 'mdi-home', text: '首页', link: '/' },
      { icon: 'mdi-trending-up', text: '时下流行', link: '/hot' },
      { icon: 'mdi-youtube-subscription', text: '订阅', link: '/subscribe' },
      { icon: 'mdi-history', text: '历史记录', link: '/history' },
      { icon: 'mdi-heart', text: '我的收藏', link: '/playlist' },
      // { icon: 'mdi-cog', text: '设置', link: '/settings' },
      { icon: 'mdi-help-circle', text: '关于', link: '/about' },
    ],
    userInfo: useUserStore(),
    notificationCount: 0,
    chatUnreadCount: 0,
    unreadTimer: null,
    searchText: '',
    showOpinionDialog: false,
  }),
  computed: {
    isHome() {
      return this.$route && this.$route.path === '/'
    },
    isHomeTop() {
      return this.isHome && this.scrollY < 40
    },
    heroHeight() {
      return this.isHome ? 360 : 0
    },
    totalUnreadCount() {
      const a = Number(this.notificationCount) || 0
      const b = Number(this.chatUnreadCount) || 0
      return a + b
    },
  },
  created() {
    this.webInfo = useWebInfoStore().webInfo
    this.getNotificationCount()
    this.getChatUnreadCount()
    this.unreadTimer = setInterval(() => {
      this.getNotificationCount()
      this.getChatUnreadCount()
    }, 10000)
    this.loadHeroSlides()
  },
  mounted() {
    this.onScroll()
    window.addEventListener('scroll', this.onScroll, { passive: true })
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.onScroll)
    if (this.unreadTimer) {
      clearInterval(this.unreadTimer)
      this.unreadTimer = null
    }
  },
  methods: {
    onScroll() {
      this.scrollY = window.scrollY || 0
    },
    goToLoginPage() {
      this.$router.push('/login')
    },
    loadHeroSlides() {
      const limit = 5
      const fallbackSlides = [
        {
          src: '/images/banners/banner1.jpg',
          title: '发现正在流行的内容',
          subtitle: '一键进入时下流行 · 热门分区 · 精选推荐',
        },
        {
          src: '/images/banners/banner2.jpg',
          title: '更清爽的首页体验',
          subtitle: '顶部大图轮播 · 内容更聚焦',
        },
        {
          src: '/images/banners/banner3.jpg',
          title: '开始创作',
          subtitle: '上传视频 · 记录生活 · 分享知识',
        },
        {
          src: '/images/banners/banner4.jpg',
          title: '今日精选',
          subtitle: '看看大家都在看什么',
        },
        {
          src: '/images/banners/banner5.jpg',
          title: '关注你喜欢的创作者',
          subtitle: '订阅更新 · 第一时间获取新内容',
        },
      ]

      const mapToSlide = (v) => {
        if (!v) return null
        const id = v.id
        const imgUrl = v.imgUrl || v.img || v.cover
        if (!id || !imgUrl) return null
        if (v.type != null && v.type !== 0) return null
        return {
          id,
          src: imgUrl,
          title: v.title || '视频',
          subtitle: v.describes || v.describe || '',
        }
      }

      const dedupeAndLimit = (arr) => {
        const seen = new Set()
        const out = []
        for (const it of arr) {
          if (!it || !it.id || seen.has(it.id)) continue
          seen.add(it.id)
          out.push(it)
          if (out.length >= limit) break
        }
        return out
      }

      this.httpGet('/article/hot', (json) => {
        let hotSlides = []
        if (json && json.status === 200 && Array.isArray(json.data)) {
          hotSlides = json.data.map(mapToSlide).filter(Boolean)
        }
        hotSlides = dedupeAndLimit(hotSlides)

        if (hotSlides.length >= limit) {
          this.heroSlides = hotSlides
          return
        }

        this.httpGet('/article/home/list?page=1&limit=10&type=0', (json2) => {
          let latestSlides = []
          if (json2 && json2.status === 200 && json2.data && Array.isArray(json2.data.list)) {
            latestSlides = json2.data.list.map(mapToSlide).filter(Boolean)
          }

          let merged = dedupeAndLimit(hotSlides.concat(latestSlides))
          if (merged.length < limit) {
            const need = limit - merged.length
            merged = merged.concat(fallbackSlides.slice(0, need))
          }
          if (merged.length) {
            this.heroSlides = merged
          }
        })
      })
    },
    goToVideo(id) {
      if (!id) return
      this.$router.push(`/video/${id}`)
    },
    goToHome() {
      this.$router.push('/')
    },
    goToPublish() {
      this.$router.push('/studio/upload')
    },
    goToNotification() {
      this.$router.push('/notification')
    },
    getNotificationCount() {
      if (!this.userInfo.userData) {
        return
      }
      this.httpGet('/notification/count', (json) => {
        this.notificationCount = json.data
      })
    },
    getChatUnreadCount() {
      if (!this.userInfo.userData) {
        this.chatUnreadCount = 0
        return
      }
      this.httpGet('/chat/unreadCount', (json) => {
        if (json && json.status === 200) {
          this.chatUnreadCount = json.data
        }
      })
    },
    search(e) {
      if (e.key === 'Enter') {
        if (this.searchText === '') {
          return
        }

        if (this.$route.path === '/search') {
          this.$router.push({
            path: this.$router.path,
            query: { key: this.searchText },
          })
          this.$refs.childRef.setSearchKey(this.searchText)
        } else {
          this.$router.push({ path: '/search', query: { key: this.searchText } })
        }
        this.searchText = ''
      }
    },
  },
  watch: {
    group() {
    },
  },
}
</script>

<style scoped>
.sv-home-appbar {
  color: rgb(var(--v-theme-on-surface));
}

.sv-home-appbar :deep(.v-toolbar__content) {
  backdrop-filter: blur(10px);
  background: rgba(15, 23, 42, 0.12);
}

.sv-home-appbar--home :deep(.v-toolbar__content) {
  backdrop-filter: none;
  background: linear-gradient(
    to bottom,
    rgba(15, 23, 42, 0.62) 0%,
    rgba(15, 23, 42, 0.28) 55%,
    rgba(15, 23, 42, 0.0) 100%
  );
}

.sv-home-appbar :deep(.v-btn),
.sv-home-appbar :deep(.v-app-bar-title) {
  color: inherit;
}

.sv-home-appbar--home :deep(.v-btn),
.sv-home-appbar--home :deep(.v-app-bar-title),
.sv-home-appbar--home :deep(.v-icon) {
  color: rgba(255, 255, 255, 0.95);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.45);
}

.sv-home-appbar--home :deep(.sv-home-search) {
  max-width: 520px;
  width: min(52vw, 520px);
}

.sv-home-appbar--home :deep(.sv-home-search .v-field) {
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22) !important;
  box-shadow: none !important;
}

.sv-home-appbar--home :deep(.sv-home-search .v-field__outline) {
  opacity: 0.4;
}

.sv-home-appbar--home :deep(.sv-home-search input) {
  color: rgba(255, 255, 255, 0.92);
}

.sv-home-appbar--home :deep(.sv-home-search .v-label) {
  color: rgba(255, 255, 255, 0.72);
}

.sv-home-appbar--home :deep(.sv-home-search .v-icon) {
  color: rgba(255, 255, 255, 0.9);
}

.sv-top-tabs-wrap {
  width: 100%;
  display: flex;
  justify-content: center;
}

.sv-top-tabs {
  width: min(1200px, calc(100vw - 32px));
}

.sv-home-appbar :deep(.sv-top-tabs .v-tab) {
  text-transform: none;
  letter-spacing: 0.1px;
  font-weight: 600;
}

.sv-home-appbar :deep(.sv-top-tabs .v-tab--selected) {
  color: rgb(var(--v-theme-primary));
}

.sv-home-appbar--home :deep(.sv-top-tabs .v-tab) {
  color: rgba(255, 255, 255, 0.92);
}

.sv-home-appbar--home :deep(.sv-top-tabs .v-tab--selected) {
  color: rgba(255, 255, 255, 0.98);
}

.sv-main {
  background: rgb(var(--v-theme-background));
  color: rgb(var(--v-theme-on-background));
}

.sv-more-menu {
  min-width: 220px;
}

@media (max-width: 600px) {
  .sv-home-appbar--home :deep(.sv-home-search) {
    width: 56vw;
  }
  .sv-top-tabs {
    width: calc(100vw - 24px);
  }
}

.sv-hero {
  margin-top: calc(-1 * var(--v-layout-top, 64px));
}

.sv-hero-carousel {
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 16px;
  overflow: hidden;
}

.sv-hero-slide {
  height: 100%;
  width: 100%;
  position: relative;
}

.sv-hero-overlay {
  position: absolute;
  left: 24px;
  bottom: 20px;
  right: 24px;
  color: rgba(255, 255, 255, 0.95);
}

.sv-hero-title {
  font-size: 26px;
  font-weight: 800;
  letter-spacing: 0.2px;
}

.sv-hero-subtitle {
  margin-top: 6px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
}

@media (max-width: 600px) {
  .sv-hero {
    margin-top: -56px;
  }
  .sv-hero-overlay {
    left: 16px;
    right: 16px;
  }
  .sv-hero-title {
    font-size: 20px;
  }
}
</style>
