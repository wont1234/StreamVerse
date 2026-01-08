<template>
  <v-container>
    <NotLoginCard v-if="userInfo.userData == null"></NotLoginCard>
    <div v-else>
      <v-row align="center" class="mb-4">
        <v-col>
          <h2 class="text-h5 font-weight-bold">
            <v-icon icon="mdi-heart" color="red" class="mr-2"></v-icon>
            收藏
          </h2>
          <p class="text-body-2 text-grey">
            <v-chip color="primary" variant="tonal" size="small">
              共 {{ totalItems }} 个视频
            </v-chip>
          </p>
        </v-col>
      </v-row>

      <v-row class="mb-4" align="start" v-if="userInfo.userData">
        <v-col cols="12">
          <div class="d-flex flex-wrap align-start ga-4">
            <v-select
              class="input-align"
              v-model="selectedLabelId"
              :items="favoritesLabels"
              item-title="name"
              item-value="id"
              label="收藏夹"
              variant="outlined"
              density="comfortable"
              @update:model-value="onLabelChange"
              hide-details="auto"
              style="min-width: 320px; max-width: 420px;"
            />
            <v-text-field
              class="input-align"
              v-model="newFavoriteName"
              label="新建收藏夹名称"
              variant="outlined"
              density="comfortable"
              maxlength="30"
              clearable
              hide-details="auto"
              style="min-width: 320px; max-width: 420px;"
            />
            <div class="d-flex align-center ga-2">
              <v-btn color="primary" :loading="favoriteCreating" @click="createFavoriteLabel">新建</v-btn>
              <v-btn color="error" variant="tonal" :loading="favoriteDeleting" @click="deleteFavoriteLabel">删除</v-btn>
            </div>
          </div>
        </v-col>
      </v-row>
      <!-- 页面标题 -->

      <!-- 加载状态 -->
      <v-row v-if="loading" justify="center" class="mt-8">
        <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
      </v-row>

      <!-- 空状态 -->
      <v-row v-else-if="videoList.length === 0" justify="center" class="mt-8">
        <v-col cols="12" sm="8" md="6" class="text-center">
          <v-icon size="64" color="grey">mdi-playlist-play</v-icon>
          <h2 class="text-h5 mt-4">暂无收藏视频</h2>
          <p class="text-body-1 text-medium-emphasis mt-2">快去收藏一些喜欢的视频吧</p>
        </v-col>
      </v-row>

      <!-- 视频列表 -->
      <v-row v-else>
        <v-col v-for="item in videoList" :key="item.id" cols="12" sm="6" md="4" xl="3" class="pa-2">
          <FavoriteCard
            :video="item.articleEntity"
            :favorite="item.favorite"
            @favorite-removed="handleFavoriteRemoved"
          />
        </v-col>
      </v-row>

      <!-- 分页器 -->
      <v-row v-if="videoList.length > 0" justify="center" class="mt-6">
        <v-pagination
          rounded="circle"
          v-model="page"
          :length="length"
          :total-visible="7"
          color="primary"
          @update:model-value="pageChange"
          class="rounded-pill"
        ></v-pagination>
      </v-row>
    </div>
  </v-container>
</template>
  
<script>
import NotLoginCard from '@/components/card/NotLoginCard.vue'
import FavoriteCard from '@/components/card/FavoriteCard.vue'
import { useUserStore } from '@/stores/userStore'

export default {
  components: {
    NotLoginCard,
    FavoriteCard,
  },
  data() {
    return {
      userInfo: useUserStore(),
      page: 1,
      size: 24,
      videoList: [],
      length: 0,
      loading: false,
      totalItems: 0,
      favoritesLabels: [],
      selectedLabelId: null,
      newFavoriteName: '',
      favoriteCreating: false,
      favoriteDeleting: false,
    }
  },
  created() {
    if (this.$route.query.page === undefined) {
      this.page = 1
    } else {
      this.page = parseInt(this.$route.query.page)
    }

    if (this.$route.query.labelId !== undefined) {
      const parsed = parseInt(this.$route.query.labelId)
      this.selectedLabelId = Number.isNaN(parsed) ? null : parsed
    }

    if (this.userInfo.userData != null) {
      this.loadLabelsAndList()
    }
  },
  methods: {
    async loadLabelsAndList() {
      this.loading = true
      try {
        await this.httpGet(`/favorites/label/list`, (json) => {
          if (json && json.status === 200 && Array.isArray(json.data)) {
            this.favoritesLabels = json.data
            if (this.selectedLabelId == null && this.favoritesLabels.length) {
              this.selectedLabelId = this.favoritesLabels[0].id
            }
          }
        })
        await this.getFavoriteList()
      } finally {
        this.loading = false
      }
    },
    async getFavoriteList() {
      try {
        const labelQuery = this.selectedLabelId != null ? `&labelId=${this.selectedLabelId}` : ''
        await this.httpGet(`/favorites/user/list?limit=${this.size}&page=${this.page}${labelQuery}`, (json) => {
          this.videoList = json.data.list
          this.page = json.data.currPage
          this.length = json.data.totalPage
          this.totalItems = json.data.totalCount || 0
        })
      } catch (e) {
        console.error(e)
      }
    },
    onLabelChange() {
      this.page = 1
      this.$router.push({ query: { page: 1, labelId: this.selectedLabelId } })
      this.getFavoriteList()
    },
    async createFavoriteLabel() {
      const name = (this.newFavoriteName || '').trim()
      if (!name) {
        return
      }
      this.favoriteCreating = true
      try {
        await this.httpPost('/favorites/label/create', { name }, (json) => {
          if (json && json.status === 200 && json.data && json.data.id != null) {
            this.favoritesLabels.push(json.data)
            this.selectedLabelId = json.data.id
            this.newFavoriteName = ''
            this.onLabelChange()
          }
        })
      } finally {
        this.favoriteCreating = false
      }
    },
    async deleteFavoriteLabel() {
      if (this.favoriteDeleting) return
      if (this.selectedLabelId == null) return
      this.favoriteDeleting = true
      try {
        await this.httpPost('/favorites/label/delete', { labelId: this.selectedLabelId }, async (json) => {
          if (json && json.status === 200 && json.data === true) {
            // 移除本地列表
            this.favoritesLabels = this.favoritesLabels.filter((l) => l.id !== this.selectedLabelId)
            // 重新拉取列表，确保默认收藏夹存在并设为选中
            this.selectedLabelId = null
            await this.loadLabelsAndList()
          }
        })
      } finally {
        this.favoriteDeleting = false
      }
    },
    pageChange(value) {
      this.page = value
      this.$router.push({ query: { page: value, labelId: this.selectedLabelId } })
      this.getFavoriteList()
      window.scrollTo({
        top: 0,
        behavior: 'smooth',
      })
    },
    handleFavoriteRemoved(favoriteId) {
      this.videoList = this.videoList.filter((item) => item.favorite.id !== favoriteId)
      if (this.videoList.length === 0 && this.page > 1) {
        this.page--
        this.getFavoriteList()
      }
    },
  },
}
</script>
  
<style scoped>
.v-pagination {
  background: transparent;
}
</style>