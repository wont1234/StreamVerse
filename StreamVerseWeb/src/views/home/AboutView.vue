<template>
  <v-container class="py-8">
    <!-- 页面标题区域 -->
    <v-row align="center" class="mb-6">
      <v-col>
        <div class="d-flex align-center">
          <v-icon icon="mdi-help-circle" color="red" size="32" class="mr-3"></v-icon>
          <h1 class="text-h4 font-weight-bold mb-0">关于我们</h1>
        </div>
        <p class="text-body-1 text-medium-emphasis mt-2 ml-9">About StreamVerse</p>
      </v-col>
    </v-row>

    <!-- Logo和介绍区域 -->
    <v-card class="mb-8 rounded-lg" elevation="3">
      <v-card-text class="pa-6">
        <v-row align="center">
          <v-col cols="12" md="4" class="text-center">
            <v-img
              :height="220"
              src="logo.png"
              alt="StreamVerse Logo"
              class="mx-auto mb-4"
              contain
            ></v-img>
          </v-col>
          <v-col cols="12" md="8">
            <h2 class="text-h5 font-weight-bold mb-4">StreamVerse - 弹幕视频分享平台</h2>
            <p class="text-body-1 mb-4">
              StreamVerse是一个创新的视频分享平台，结合了短视频的趣味性和弹幕互动的社交体验。我们致力于为用户提供一个简单、流畅且功能丰富的视频观看和分享环境。
            </p>
            <p class="text-body-1 mb-4">
              通过StreamVerse，您可以发现有趣的视频内容，与其他用户通过弹幕实时互动，分享您的创意作品，并与志同道合的创作者建立联系。
            </p>
            <div class="d-flex flex-wrap gap-2 mt-4">
              <v-chip color="red" text-color="white" size="large">
                <v-icon start>mdi-video</v-icon>
                视频分享
              </v-chip>
              <v-chip color="blue" text-color="white" size="large">
                <v-icon start>mdi-message-text</v-icon>
                弹幕互动
              </v-chip>
              <v-chip color="green" text-color="white" size="large">
                <v-icon start>mdi-account-group</v-icon>
                社区交流
              </v-chip>
            </div>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 技术栈区域 -->
    <v-card class="mb-8 rounded-lg" elevation="2">
      <v-card-title class="text-h5 font-weight-bold py-4 px-6 bg-secondary text-white">
        <v-icon icon="mdi-code-tags" class="mr-2"></v-icon>
        技术栈
      </v-card-title>
      <v-card-text class="pa-6">
        <v-row>
          <v-col cols="12" md="6">
            <h3 class="text-subtitle-1 font-weight-bold mb-3">前端</h3>
            <v-chip-group>
              <v-chip color="primary" variant="outlined" class="ma-1">Vue 3</v-chip>
              <v-chip color="primary" variant="outlined" class="ma-1">Vuetify</v-chip>
              <v-chip color="primary" variant="outlined" class="ma-1">Pinia</v-chip>
              <v-chip color="primary" variant="outlined" class="ma-1">Vue Router</v-chip>
            </v-chip-group>
          </v-col>
          <v-col cols="12" md="6">
            <h3 class="text-subtitle-1 font-weight-bold mb-3">后端</h3>
            <v-chip-group>
              <v-chip color="success" variant="outlined" class="ma-1">Spring Boot</v-chip>
              <v-chip color="success" variant="outlined" class="ma-1">MyBatis</v-chip>
              <v-chip color="success" variant="outlined" class="ma-1">MySQL</v-chip>
            </v-chip-group>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 联系我们区域 -->
    <v-card class="rounded-lg" elevation="2">
      <v-card-title class="text-h5 font-weight-bold py-4 px-6 bg-info text-white">
        <v-icon icon="mdi-email" class="mr-2"></v-icon>
        联系我们
      </v-card-title>
      <v-card-text class="pa-6">
        <p class="text-body-1 mb-4">
          如果您有任何问题、建议或合作意向，欢迎通过以下方式与我们联系：
        </p>
        <div class="d-flex align-center">
          <v-icon color="primary" class="mr-3">mdi-email</v-icon>
          <span class="text-body-1">邮箱：</span>
          <a href="mailto:2644539131@qq.com" class="text-decoration-none ml-2">2644539131@qq.com</a>
        </div>
      </v-card-text>
    </v-card>

    <!-- 页脚版权信息 -->
    <div class="text-center mt-8 text-body-2 text-medium-emphasis">
      <p>
        <strong>StreamVerse</strong> &copy;2020 - {{ new Date().getFullYear() }}
      </p>
      <p>version 1.1 2025年12月15日</p>
    </div>
  </v-container>
</template>

<script>
export default {
  name: 'AboutView',
  created() {
    document.title = '关于我们 - StreamVerse'
  },
}
</script>

<style scoped>
.gap-2 {
  gap: 8px;
}

a {
  color: #1867c0;
  transition: color 0.2s ease;
}

a:hover {
  color: #5cbbf6;
}
</style>