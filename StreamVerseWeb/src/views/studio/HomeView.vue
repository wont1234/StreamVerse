<template>
  <v-container fluid>
    <h2 class="mb-4">创作者首页</h2>
    
    <!-- 数据概览卡片 -->
    <v-row>
      <v-col cols="12" md="3">
        <v-card class="pa-4" color="primary" variant="tonal">
          <div class="text-h4 font-weight-bold">{{ stats.totalViewCount || 0 }}</div>
          <div class="text-body-2 text-grey">总播放量</div>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card class="pa-4" color="success" variant="tonal">
          <div class="text-h4 font-weight-bold">{{ stats.totalFansCount || 0 }}</div>
          <div class="text-body-2 text-grey">粉丝数</div>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card class="pa-4" color="warning" variant="tonal">
          <div class="text-h4 font-weight-bold">{{ stats.totalLikeCount || 0 }}</div>
          <div class="text-body-2 text-grey">获赞数</div>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card class="pa-4" color="info" variant="tonal">
          <div class="text-h4 font-weight-bold">{{ stats.totalArticleCount || 0 }}</div>
          <div class="text-body-2 text-grey">稿件数</div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 待办事项 -->
    <v-row class="mt-4">
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>
            <v-icon class="mr-2">mdi-clipboard-check</v-icon>
            待办事项
          </v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item v-if="stats.pendingExamineCount > 0">
                <template v-slot:prepend>
                  <v-icon color="warning">mdi-clock-outline</v-icon>
                </template>
                <v-list-item-title>{{ stats.pendingExamineCount }} 个稿件待审核</v-list-item-title>
                <template v-slot:append>
                  <v-btn size="small" variant="text" color="primary" href="/studio/list">查看</v-btn>
                </template>
              </v-list-item>
              <v-list-item v-else>
                <template v-slot:prepend>
                  <v-icon color="success">mdi-check-circle</v-icon>
                </template>
                <v-list-item-title>暂无待办事项</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>
            <v-icon class="mr-2">mdi-chart-line</v-icon>
            快速入口
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="6">
                <v-btn block color="primary" variant="outlined" href="/studio/upload">
                  <v-icon class="mr-2">mdi-upload</v-icon>
                  上传稿件
                </v-btn>
              </v-col>
              <v-col cols="6">
                <v-btn block color="secondary" variant="outlined" href="/studio/list">
                  <v-icon class="mr-2">mdi-filmstrip-box-multiple</v-icon>
                  管理稿件
                </v-btn>
              </v-col>
              <v-col cols="6">
                <v-btn block color="info" variant="outlined" href="/studio/data">
                  <v-icon class="mr-2">mdi-database</v-icon>
                  数据分析
                </v-btn>
              </v-col>
              <v-col cols="6">
                <v-btn block color="success" variant="outlined" href="/studio/fans">
                  <v-icon class="mr-2">mdi-account-multiple</v-icon>
                  粉丝管理
                </v-btn>
              </v-col>
            </v-row>
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
      stats: {}
    }
  },
  created() {
    this.loadStats()
  },
  methods: {
    loadStats() {
      this.httpGet('/studio/stats', (json) => {
        if (json.status === 200) {
          this.stats = json.data
        }
      })
    }
  }
}
</script>

<style scoped>
</style>