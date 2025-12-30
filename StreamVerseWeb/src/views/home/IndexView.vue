<template>
  <v-container fluid>

    <v-banner @click="clickBanner()" class="my-4" color="warning" icon="$warning" lines="one" v-if="systemNotice != null">
      <v-banner-text>
        {{ systemNotice.content }}
      </v-banner-text>
    </v-banner>
    <!-- 分区 -->
    <v-row>
      <v-col>
        <v-menu v-for="item in categoryList" :key="item.id" open-on-hover offset-y>
          <template v-slot:activator="{ props }">
            <v-btn variant="text" color="inherit" class="category-btn" v-bind="props" @click="setCategory(item)">
              {{ item.name }}
            </v-btn>
          </template>
          <v-list>
            <v-list-item
              v-for="c in item.children"
              :key="c.id"
              :title="c.name"
              @click="setCategory(c)"
            >
            </v-list-item>
          </v-list>
        </v-menu>
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <v-col />
    <!-- 视频卡片 -->
    <v-row>
      <v-col cols="12" sm="6" md="4" lg="3" xxl="2" v-for="item in videoList" :key="item.id">
        <VideoCared :video="item" />
      </v-col>
    </v-row>
    <v-container>
      <v-row justify="center">
        <v-pagination
          rounded="circle"
          :total-visible="7"
          v-model="page"
          :length="length"
          color="red"
          @update:model-value="pageChange"
        />
      </v-row>
    </v-container>



    <!-- 引入弹窗广告组件 -->
    <NoticeDialogCard
      v-model="popupDialog" 
      :notice="popupNotice"
      :closed-popup-id="'closedHomePopupId'"
    />

    <v-divider class="mt-10"></v-divider>
    <v-container class="py-8">
      <div class="text-center">
        <div class="text-subtitle-1 font-weight-bold">{{ siteName }}</div>
        <div class="text-body-2 text-medium-emphasis mt-2 footer-desc">
          {{ siteDescribeSafe }}
        </div>
        <div class="text-body-2 text-medium-emphasis mt-3">
          version {{ appVersion }} &copy;2020 - {{ new Date().getFullYear() }}
        </div>
      </div>
    </v-container>
  </v-container>
</template>

<script>
import VideoCared from '@/components/card/VideoCard.vue'
import NoticeDialogCard from '@/components/card/NoticeDialogCard.vue'
import { useWebInfoStore } from '@/stores/webInfoStore'
import pkg from '../../../package.json'

export default {
  components: {
    VideoCared,
    NoticeDialogCard,
  },
  data() {
    return {
      webInfoStore: useWebInfoStore(),
      videoList: [],
      page: 1,
      size: 24,
      length: 0,
      categoryList: [],
      systemNotice: {},
      // 弹窗相关数据
      popupDialog: false,
      popupNotice: null,
      notice: null,
    }
  },
  computed: {
    siteName() {
      return this.webInfoStore && this.webInfoStore.webInfo && this.webInfoStore.webInfo.name
        ? this.webInfoStore.webInfo.name
        : 'StreamVerse'
    },
    siteDescribe() {
      return this.webInfoStore && this.webInfoStore.webInfo && this.webInfoStore.webInfo.webDescribe
        ? this.webInfoStore.webInfo.webDescribe
        : ''
    },
    siteDescribeSafe() {
      const s = (this.siteDescribe || '').trim()
      if (!s) {
        return '一个简单、流畅且功能丰富的视频观看与创作平台。'
      }
      const normalized = s
        .replaceAll('一个牛逼的网站', '')
        .replaceAll('牛逼', '')
        .replaceAll('一个的视频网站', '一个视频网站')
        .replaceAll('的视频网站', '视频网站')
        .trim()
      return normalized || '一个简单、流畅且功能丰富的视频观看与创作平台。'
    },
    appVersion() {
      return (pkg && pkg.version) ? pkg.version : '1.0.0'
    },
  },
  created() {
    if (this.$route.query.page === undefined) {
      this.page = 1
    } else {
      this.page = parseInt(this.$route.query.page)
    }
    this.getCategory()
    this.getVideoList()
    this.getSystemNotice()
    this.getPopupNotice()
  },
  methods: {
    getCategory() {
      this.httpGet(`/category/tree`, (json) => {
        this.categoryList = json.data
      })
    },
    getSystemNotice() {
      this.httpGet(`/web/notice?type=0`, (json) => {
        if (json.data != null && json.data.length != 0) {
          this.systemNotice = json.data[0]
        } else {
          this.systemNotice = null
        }
      })
    },
    // 获取弹窗公告
    getPopupNotice() {
      this.httpGet(`/web/notice?type=1`, (json) => {
        if (json.data != null && json.data.length > 0) {

          this.notice = json.data[0];
          
          // 从 localStorage 获取已关闭的弹窗ID
          const closedPopupId = localStorage.getItem('closedHomePopupId');
          
          // 判断是否需要显示弹窗
          // 1. 如果没有关闭过弹窗，或者关闭的弹窗ID与当前弹窗ID不同，则显示弹窗
          if (!closedPopupId || parseInt(closedPopupId) !== this.notice.id) {
            this.popupNotice = this.notice;
            this.popupDialog = true;
          }
        }
      })
    },
    clickBanner() {
      if (this.systemNotice != null) {
        this.popupNotice = this.systemNotice
        this.popupDialog = true
      }
    },
    setCategory(value) {
      this.$router.push(`/v/${value.id}`)
    },
    getVideoList() {
      this.httpGet(`/article/home/list?page=${this.page}&limit=${this.size}&type=0`, (json) => {
        this.videoList = json.data.list
        this.page = json.data.currPage
        this.length = json.data.totalPage
      })
    },
    pageChange(value) {
      this.page = value
      this.$router.push({ query: { page: this.page } })
      this.getVideoList()
      window.scrollTo({
        top: 0,
        behavior: 'smooth',
      })
    },
  },
}
</script>

<style scoped>
a {
  text-decoration: none;
}

.footer-desc {
  max-width: 720px;
  margin: 0 auto;
}

.category-btn {
  color: rgba(var(--v-theme-on-surface), 0.72);
}

.category-btn:hover {
  color: rgb(var(--v-theme-primary));
}
</style>
