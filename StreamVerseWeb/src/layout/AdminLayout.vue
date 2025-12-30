<template>
  <div>
    <v-app-bar color="surface">
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
        </v-list>
      </v-menu>
      <v-app-bar-title>
        <span style="cursor: pointer" @click="goToHome()">{{ webInfo.name }} Admin</span>
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
        color="primary"
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
      <AppBarHead v-if="userInfo.userData != null" class="sv-admin-avatar"></AppBarHead>
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
          <v-tabs class="sv-top-tabs" color="primary" density="compact" show-arrows align-tabs="center">
            <v-tab to="/admin" exact>概览</v-tab>
            <v-tab to="/admin/userlist">用户</v-tab>
            <v-tab to="/admin/article/list">投稿</v-tab>
            <v-tab to="/admin/comment">评论</v-tab>
            <v-tab to="/admin/danmuku">弹幕</v-tab>
            <v-tab to="/admin/ads">广告公告</v-tab>
            <v-tab to="/admin/websetting">网页设置</v-tab>
          </v-tabs>
        </div>
      </template>
    </v-app-bar>

    <v-main class="sv-main">
      <!--  fluid-->
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
export default {
  components: {
    AppBarHead,
    ThemeToggleButton,
  },
  data: () => ({
    webInfo: {},
    items: [
      { icon: 'mdi-home', text: '首页', link: '/admin' },
      { icon: 'mdi-server', text: '运行状态', link: '/admin/runinfo' },
      { icon: 'mdi-application', text: '邀请码', link: '/admin/invitation' },
      { icon: 'mdi-video', text: '待审核', link: '/admin/examine' },
      { icon:'mdi-flag', text: '举报\\意见反馈\\申诉', link: '/admin/opinion' },
      { icon: 'mdi-filmstrip-box-multiple', text: '投稿列表', link: '/admin/article/list' },
      { icon: 'mdi-file', text: '文件列表', link: '/admin/file/list' },
      { icon: 'mdi-account-multiple', text: '用户列表', link: '/admin/userlist' },
      { icon: 'mdi-comment', text: '评论管理', link: '/admin/comment' },
      { icon: 'mdi-airplane', text: '弹幕管理', link: '/admin/danmuku' },
      { icon: 'mdi-advertisements', text: '广告公告管理', link: '/admin/ads' },
      { icon: 'mdi-playlist-edit', text: '分类管理', link: '/admin/category' },
      { icon: 'mdi-database', text: '存储管理', link: '/admin/oss' },
      { icon: 'mdi-robot', text: 'AI 模型管理', link: '/admin/ai' },
      { icon: 'mdi-square-edit-outline', text: '网页设置', link: '/admin/websetting' }
    ],
    userInfo: useUserStore(),
    notificationCount: 0,
    chatUnreadCount: 0,
    unreadTimer: null,
    searchText: '',
  }),
  computed: {
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
  },
  beforeUnmount() {
    if (this.unreadTimer) {
      clearInterval(this.unreadTimer)
      this.unreadTimer = null
    }
  },
  methods: {
    goToLoginPage() {
      this.$router.push('/login')
    },
    goToHome() {
      this.$router.push('/admin')
    },
    goToPublish() {
      this.$router.push('/studio/upload')
    },
    goToNotification() {
      this.$router.push('/notification')
    },
    getNotificationCount() {
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
}
</script>

<style scoped>
.sv-admin-avatar :deep(.v-btn) {
  background: rgba(56, 189, 248, 0.18) !important;
  border: 1px solid rgba(56, 189, 248, 0.35) !important;
}

.sv-admin-avatar :deep(.v-icon) {
  color: rgba(224, 242, 254, 0.95) !important;
}

.sv-top-tabs-wrap {
  width: 100%;
  display: flex;
  justify-content: center;
}

.sv-top-tabs {
  width: min(1200px, calc(100vw - 32px));
}

.sv-more-menu {
  min-width: 240px;
}

.sv-main {
  background: rgb(var(--v-theme-background));
  color: rgb(var(--v-theme-on-background));
}

@media (max-width: 600px) {
  .sv-top-tabs {
    width: calc(100vw - 24px);
  }
}
</style>
    