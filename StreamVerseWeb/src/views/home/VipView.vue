<template>
  <v-container class="py-6">
    <v-row justify="center">
      <v-col cols="12" md="10" lg="8">
        <v-card elevation="2" rounded="lg">
          <v-toolbar color="amber-darken-2" density="comfortable">
            <v-toolbar-title class="text-white d-flex align-center">
              <v-icon class="mr-2">mdi-crown</v-icon>
              付费会员
            </v-toolbar-title>
          </v-toolbar>

          <v-card-text class="pt-6">
            <v-alert v-if="!isLoggedIn" type="info" variant="tonal" class="mb-4">
              登录后可查看你的会员状态。
              <template #append>
                <v-btn color="primary" variant="elevated" @click="$router.push('/login')">去登录</v-btn>
              </template>
            </v-alert>

            <v-row v-else>
              <v-col cols="12">
                <v-card variant="outlined" rounded="lg" class="pa-4">
                  <div class="d-flex align-center">
                    <v-avatar size="48" class="mr-3">
                      <v-img :src="user.avatarUrl" alt="avatar"></v-img>
                    </v-avatar>
                    <div class="flex-grow-1">
                      <div class="text-h6 font-weight-bold">{{ user.username }}</div>
                      <div class="text-caption text-medium-emphasis">{{ user.mail }}</div>
                    </div>

                    <v-chip
                      v-if="isVip"
                      color="amber-darken-2"
                      class="text-white"
                      variant="elevated"
                      prepend-icon="mdi-crown"
                    >
                      VIP 生效中
                    </v-chip>
                    <v-chip v-else color="grey" class="text-white" variant="elevated">
                      普通用户
                    </v-chip>
                  </div>

                  <v-divider class="my-4"></v-divider>

                  <v-row>
                    <v-col cols="12" sm="6">
                      <div class="text-caption text-medium-emphasis">会员有效期</div>
                      <div class="text-body-1" v-if="isVip">
                        <span v-if="isSuperAdmin">永久（超级管理员）</span>
                        <span v-else-if="isAdmin">永久（管理员）</span>
                        <span v-else>{{ formatDate(vipStartTime) }} - {{ formatDate(vipStopTime) }}</span>
                      </div>
                      <div class="text-body-1" v-else>未开通</div>
                    </v-col>
                    <v-col cols="12" sm="6">
                      <div class="text-caption text-medium-emphasis">当前权限</div>
                      <div class="text-body-1">
                        {{ roleText }}
                      </div>
                    </v-col>
                  </v-row>

                  <v-alert v-if="!isVip" type="warning" variant="tonal" class="mt-4">
                    本项目的 VIP 开通由管理员在后台为你设置（当前暂无在线支付）。
                    <template #append>
                      <v-btn
                        v-if="canGoAdmin"
                        color="primary"
                        variant="elevated"
                        @click="$router.push('/admin/userlist')"
                      >
                        去用户管理开通
                      </v-btn>
                    </template>
                  </v-alert>
                </v-card>
              </v-col>
            </v-row>

            <v-card variant="outlined" rounded="lg" class="mt-4">
              <v-card-title class="text-subtitle-1 font-weight-bold">
                VIP 权益
              </v-card-title>
              <v-card-text>
                <v-row>
                  <v-col cols="12" sm="6">
                    <v-list density="compact">
                      <v-list-item>
                        <template #prepend>
                          <v-icon color="success">mdi-check-circle</v-icon>
                        </template>
                        <v-list-item-title>解除播放次数限制</v-list-item-title>
                        <v-list-item-subtitle>VIP / 管理员默认生效</v-list-item-subtitle>
                      </v-list-item>
                      <v-list-item>
                        <template #prepend>
                          <v-icon color="success">mdi-check-circle</v-icon>
                        </template>
                        <v-list-item-title>更佳的观看体验</v-list-item-title>
                        <v-list-item-subtitle>预留：去广告/更高清晰度/专属功能等</v-list-item-subtitle>
                      </v-list-item>
                    </v-list>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-list density="compact">
                      <v-list-item>
                        <template #prepend>
                          <v-icon color="info">mdi-information</v-icon>
                        </template>
                        <v-list-item-title>开通方式</v-list-item-title>
                        <v-list-item-subtitle>由管理员在后台设置 VIP 有效期</v-list-item-subtitle>
                      </v-list-item>
                      <v-list-item>
                        <template #prepend>
                          <v-icon color="info">mdi-information</v-icon>
                        </template>
                        <v-list-item-title>续费/到期</v-list-item-title>
                        <v-list-item-subtitle>到期后将自动恢复为普通用户</v-list-item-subtitle>
                      </v-list-item>
                    </v-list>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { useUserStore } from '@/stores/userStore'
import checkPower from '@/utils/check-power.vue'

export default {
  name: 'VipView',
  data() {
    return {
      userInfo: useUserStore(),
    }
  },
  computed: {
    user() {
      return this.userInfo && this.userInfo.userData ? this.userInfo.userData : null
    },
    isLoggedIn() {
      return !!this.user
    },
    isSuperAdmin() {
      return !!(this.user && this.user.mail === '123@qq.com')
    },
    isAdmin() {
      if (!this.user || !this.user.userRoleEntity) {
        return false
      }
      return this.user.userRoleEntity.role === 'ROLE_ADMIN'
    },
    isVip() {
      if (!this.user) {
        return false
      }
      if (this.isSuperAdmin) {
        return true
      }
      try {
        return !!(this.user.userRoleEntity && checkPower.checkVip(this.user))
      } catch (e) {
        return false
      }
    },
    vipStartTime() {
      return this.user && this.user.userRoleEntity ? this.user.userRoleEntity.vipStartTime : null
    },
    vipStopTime() {
      return this.user && this.user.userRoleEntity ? this.user.userRoleEntity.vipStopTime : null
    },
    roleText() {
      if (!this.user) {
        return '未登录'
      }
      const role = this.user.userRoleEntity ? this.user.userRoleEntity.role : 'ROLE_USER'
      if (this.isSuperAdmin) {
        return '超级管理员（享受 VIP 权益）'
      }
      if (role === 'ROLE_ADMIN') {
        return '管理员（享受 VIP 权益）'
      }
      if (role === 'ROLE_VIP') {
        return this.isVip ? 'VIP' : 'VIP（已到期）'
      }
      return '普通用户'
    },
    canGoAdmin() {
      if (!this.user || !this.user.userRoleEntity) {
        return false
      }
      return this.user.userRoleEntity.role === 'ROLE_ADMIN'
    },
  },
  methods: {
    formatDate(timestamp) {
      if (!timestamp) {
        return '-'
      }
      const date = new Date(timestamp)
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${d}`
    },
  },
}
</script>

<style>

</style>