<template>
  <v-container fluid>
    <h2 class="mb-4">数据分析</h2>

    <!-- 核心数据 -->
    <v-row>
      <v-col cols="12" sm="6" md="4" lg="2">
        <v-card class="pa-4 text-center" variant="outlined">
          <v-icon size="32" color="primary" class="mb-2">mdi-play-circle</v-icon>
          <div class="text-h5 font-weight-bold">{{ formatNumber(stats.totalViewCount) }}</div>
          <div class="text-body-2 text-grey">总播放量</div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="4" lg="2">
        <v-card class="pa-4 text-center" variant="outlined">
          <v-icon size="32" color="red" class="mb-2">mdi-thumb-up</v-icon>
          <div class="text-h5 font-weight-bold">{{ formatNumber(stats.totalLikeCount) }}</div>
          <div class="text-body-2 text-grey">总点赞数</div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="4" lg="2">
        <v-card class="pa-4 text-center" variant="outlined">
          <v-icon size="32" color="orange" class="mb-2">mdi-comment-multiple</v-icon>
          <div class="text-h5 font-weight-bold">{{ formatNumber(stats.totalCommentCount) }}</div>
          <div class="text-body-2 text-grey">总评论数</div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="4" lg="2">
        <v-card class="pa-4 text-center" variant="outlined">
          <v-icon size="32" color="purple" class="mb-2">mdi-star</v-icon>
          <div class="text-h5 font-weight-bold">{{ formatNumber(stats.totalFavoriteCount) }}</div>
          <div class="text-body-2 text-grey">总收藏数</div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="4" lg="2">
        <v-card class="pa-4 text-center" variant="outlined">
          <v-icon size="32" color="cyan" class="mb-2">mdi-message-text</v-icon>
          <div class="text-h5 font-weight-bold">{{ formatNumber(stats.totalDanmakuCount) }}</div>
          <div class="text-body-2 text-grey">总弹幕数</div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="4" lg="2">
        <v-card class="pa-4 text-center" variant="outlined">
          <v-icon size="32" color="green" class="mb-2">mdi-account-multiple</v-icon>
          <div class="text-h5 font-weight-bold">{{ formatNumber(stats.totalFansCount) }}</div>
          <div class="text-body-2 text-grey">粉丝数</div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 粉丝画像 -->
    <v-row class="mt-4">
      <v-col cols="12">
        <v-card>
          <v-card-title>
            <v-icon class="mr-2">mdi-account-group</v-icon>
            粉丝画像
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="6" sm="4" md="2">
                <div class="text-center">
                  <div class="text-h5 font-weight-bold text-primary">{{ fansProfile.totalFans || 0 }}</div>
                  <div class="text-body-2 text-grey">粉丝总数</div>
                </div>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <div class="text-center">
                  <div class="text-h5 font-weight-bold text-success">{{ fansProfile.mutualFans || 0 }}</div>
                  <div class="text-body-2 text-grey">互关粉丝</div>
                </div>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <div class="text-center">
                  <div class="text-h5 font-weight-bold text-info">{{ fansProfile.mutualRate || 0 }}%</div>
                  <div class="text-body-2 text-grey">互关率</div>
                </div>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <div class="text-center">
                  <div class="text-h5 font-weight-bold text-warning">{{ fansProfile.activeCreators || 0 }}</div>
                  <div class="text-body-2 text-grey">创作者粉丝</div>
                </div>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <div class="text-center">
                  <div class="text-h5 font-weight-bold text-purple">{{ fansProfile.highFansFans || 0 }}</div>
                  <div class="text-body-2 text-grey">高粉丝量</div>
                </div>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <div class="text-center">
                  <div class="text-h5 font-weight-bold text-red">{{ fansProfile.newFans || 0 }}</div>
                  <div class="text-body-2 text-grey">7天新增</div>
                </div>
              </v-col>

              <v-col cols="6" sm="4" md="2">
                <div class="text-center">
                  <div class="text-h5 font-weight-bold text-indigo">{{ fansProfile.newFans30d || 0 }}</div>
                  <div class="text-body-2 text-grey">30天新增</div>
                </div>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <div class="text-center">
                  <div class="text-h5 font-weight-bold text-teal">{{ fansProfile.newUserFans || 0 }}</div>
                  <div class="text-body-2 text-grey">30天新注册粉丝</div>
                </div>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <div class="text-center">
                  <div class="text-h5 font-weight-bold text-teal">{{ fansProfile.newUserFansRate || 0 }}%</div>
                  <div class="text-body-2 text-grey">新注册占比</div>
                </div>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <div class="text-center">
                  <div class="text-h5 font-weight-bold text-purple">{{ fansProfile.highFansRate || 0 }}%</div>
                  <div class="text-body-2 text-grey">高粉占比</div>
                </div>
              </v-col>
            </v-row>

            <v-row class="mt-4" v-if="newFansTrend7d.length">
              <v-col cols="12">
                <div class="text-subtitle-2 mb-2">近7天新增趋势</div>
                <v-row>
                  <v-col
                    v-for="p in newFansTrend7d"
                    :key="p.day"
                    cols="12"
                    sm="6"
                    md="3"
                  >
                    <div class="d-flex justify-space-between mb-1">
                      <span class="text-body-2 text-grey">{{ p.day }}</span>
                      <span class="text-body-2 font-weight-medium">{{ p.count }}</span>
                    </div>
                    <v-progress-linear
                      :model-value="(p.count * 100) / newFansTrendMax"
                      height="8"
                      rounded
                      color="primary"
                    />
                  </v-col>
                </v-row>
              </v-col>
            </v-row>

            <v-row class="mt-4" v-if="fansBucketEntries.length">
              <v-col cols="12">
                <div class="text-subtitle-2 mb-2">粉丝影响力分布</div>
                <v-row>
                  <v-col
                    v-for="item in fansBucketEntries"
                    :key="item.key"
                    cols="12"
                    sm="6"
                    md="3"
                  >
                    <v-card variant="outlined" class="pa-3">
                      <div class="d-flex justify-space-between align-center">
                        <div class="text-body-2 text-grey">{{ item.key }}</div>
                        <div class="text-body-1 font-weight-bold">{{ item.value }}</div>
                      </div>
                      <v-progress-linear
                        class="mt-2"
                        :model-value="(item.value * 100) / (fansProfile.totalFans || 1)"
                        height="8"
                        rounded
                        color="secondary"
                      />
                    </v-card>
                  </v-col>
                </v-row>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 稿件统计 -->
    <v-row class="mt-4">
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>
            <v-icon class="mr-2">mdi-filmstrip-box-multiple</v-icon>
            稿件统计
          </v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="primary">mdi-file-document-multiple</v-icon>
                </template>
                <v-list-item-title>总稿件数</v-list-item-title>
                <template v-slot:append>
                  <span class="text-h6">{{ stats.totalArticleCount || 0 }}</span>
                </template>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="warning">mdi-clock-outline</v-icon>
                </template>
                <v-list-item-title>待审核</v-list-item-title>
                <template v-slot:append>
                  <span class="text-h6">{{ stats.pendingExamineCount || 0 }}</span>
                </template>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="success">mdi-check-circle</v-icon>
                </template>
                <v-list-item-title>已通过</v-list-item-title>
                <template v-slot:append>
                  <span class="text-h6">{{ (stats.totalArticleCount || 0) - (stats.pendingExamineCount || 0) }}</span>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>
            <v-icon class="mr-2">mdi-chart-bar</v-icon>
            互动数据
          </v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="primary">mdi-play-circle</v-icon>
                </template>
                <v-list-item-title>平均播放量</v-list-item-title>
                <template v-slot:append>
                  <span class="text-h6">{{ avgViewCount }}</span>
                </template>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="red">mdi-thumb-up</v-icon>
                </template>
                <v-list-item-title>平均点赞数</v-list-item-title>
                <template v-slot:append>
                  <span class="text-h6">{{ avgLikeCount }}</span>
                </template>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="orange">mdi-comment-multiple</v-icon>
                </template>
                <v-list-item-title>平均评论数</v-list-item-title>
                <template v-slot:append>
                  <span class="text-h6">{{ avgCommentCount }}</span>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      stats: {},
      fansProfile: {}
    }
  },
  computed: {
    newFansTrend7d() {
      return Array.isArray(this.fansProfile.newFansTrend7d) ? this.fansProfile.newFansTrend7d : []
    },
    newFansTrendMax() {
      const max = Math.max(1, ...this.newFansTrend7d.map((p) => Number(p.count || 0)))
      return max
    },
    fansBucketEntries() {
      const buckets = this.fansProfile.fansCountBuckets || {}
      return Object.keys(buckets).map((k) => ({ key: k, value: Number(buckets[k] || 0) }))
    },
    avgViewCount() {
      if (!this.stats.totalArticleCount || this.stats.totalArticleCount === 0) return 0
      return Math.round(this.stats.totalViewCount / this.stats.totalArticleCount)
    },
    avgLikeCount() {
      if (!this.stats.totalArticleCount || this.stats.totalArticleCount === 0) return 0
      return Math.round(this.stats.totalLikeCount / this.stats.totalArticleCount)
    },
    avgCommentCount() {
      if (!this.stats.totalArticleCount || this.stats.totalArticleCount === 0) return 0
      return Math.round(this.stats.totalCommentCount / this.stats.totalArticleCount)
    }
  },
  created() {
    this.loadStats()
    this.loadFansProfile()
  },
  methods: {
    loadStats() {
      this.httpGet('/studio/stats', (json) => {
        if (json.status === 200) {
          this.stats = json.data
        }
      })
    },
    loadFansProfile() {
      this.httpGet('/studio/fans/profile', (json) => {
        if (json.status === 200) {
          this.fansProfile = json.data
        }
      })
    },
    formatNumber(num) {
      if (!num) return 0
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
      }
      return num
    }
  }
}
</script>

<style scoped>
</style>