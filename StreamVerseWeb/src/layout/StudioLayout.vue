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
          <v-list-item
            v-if="CheckPower.checkPower(userInfo.userData) == 'admin'"
            prepend-icon="mdi-security"
            title="管理中心"
            :to="'/admin'"
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
        <span style="cursor: pointer" @click="goToHome()">{{ webInfo.name }} Studio</span>
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
          <v-tabs class="sv-top-tabs" color="primary" density="compact" show-arrows align-tabs="center">
            <v-tab to="/studio" exact>信息中心</v-tab>
            <v-tab to="/studio/list">投稿列表</v-tab>
            <v-tab to="/studio/upload">投稿</v-tab>
            <v-tab to="/studio/data">数据分析</v-tab>
            <v-tab to="/studio/fans">粉丝管理</v-tab>
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
import CheckPower from '@/utils/check-power.vue'
import OpinionCard from '@/components/card/OpinionCard.vue'
export default {
  components: {
    AppBarHead,
    ThemeToggleButton,
    OpinionCard
  },
  data: () => ({
    CheckPower,
    webInfo: {},
    items: [
      { icon: 'mdi-application', text: '信息中心', link: '/studio' },
      { icon: 'mdi-filmstrip-box-multiple', text: '投稿列表', link: '/studio/list' },
      { icon: 'mdi-upload', text: '投稿', link: '/studio/upload' },
      { icon: 'mdi-database', text: '数据分析', link: '/studio/data' },
      { icon: 'mdi-account-multiple', text: '粉丝管理', link: '/studio/fans' },
    ],
    userInfo: useUserStore(),
    notificationCount: 0,
    chatUnreadCount: 0,
    unreadTimer: null,
    searchText: '',
    showOpinionDialog: false,
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
      this.$router.push('/studio')
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
.sv-top-tabs-wrap {
  width: 100%;
  display: flex;
  justify-content: center;
}

.sv-top-tabs {
  width: min(1200px, calc(100vw - 32px));
}

.sv-more-menu {
  min-width: 220px;
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
  