<template>
  <v-container fluid class="sv-chat-page">
    <v-row>
      <v-col cols="12" md="4" v-show="!isMobile || !selectedUserId">
        <v-card color="surface" class="sv-conversation-card">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-chat</v-icon>
            私信
            <v-spacer />
            <v-chip size="small" :color="wsConnected ? 'success' : 'warning'" variant="tonal">
              {{ wsConnected ? '已连接' : '连接中' }}
            </v-chip>
          </v-card-title>
          <v-divider />
          <v-list lines="two" density="comfortable" class="py-0 sv-conversation-list">
            <v-list-item
              v-for="c in conversations"
              :key="c.conversation.id"
              :active="selectedUserId === (c.otherUser && c.otherUser.id)"
              @click="selectConversation(c)"
            >
              <template #prepend>
                <v-avatar size="40">
                  <v-img :src="c.otherUser && c.otherUser.avatarUrl" />
                </v-avatar>
              </template>
              <v-list-item-title>
                {{ (c.otherUser && c.otherUser.username) || '用户' }}
              </v-list-item-title>
              <v-list-item-subtitle>
                {{ formatConversationPreview(c.lastMessage) }}
              </v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>

      <v-col cols="12" md="8" v-show="!isMobile || selectedUserId">
        <v-card class="sv-chat-shell" color="surface">
          <div class="sv-chat-header">
            <div class="d-flex align-center" style="gap: 10px; width: 100%;">
              <v-btn
                v-if="isMobile && selectedUserId"
                variant="text"
                icon="mdi-arrow-left"
                @click="backToList"
              />
              <v-avatar v-if="selectedUserId" size="34">
                <v-img :src="selectedUserAvatar" />
              </v-avatar>
              <div class="d-flex flex-column" style="min-width: 0;">
                <div class="text-subtitle-1 text-truncate">
                  {{ selectedUserName || '请选择会话' }}
                </div>
                <div class="text-caption text-medium-emphasis">
                  {{ wsConnected ? '实时连接中' : '网络较差，已启用轮询' }}
                </div>
              </div>
              <v-spacer />
              <v-btn
                v-if="selectedUserId"
                variant="tonal"
                size="small"
                prepend-icon="mdi-account"
                :to="`/user/${selectedUserId}`"
              >
                主页
              </v-btn>
            </div>
          </div>

          <div ref="msgScroll" class="sv-chat-messages">
            <div v-if="!selectedUserId" class="text-medium-emphasis">选择会话开始聊天</div>
            <div v-else>
              <div v-if="loadingMessages" class="text-medium-emphasis">加载中...</div>
              <div v-else>
                <div v-for="m in messages" :key="m.id" class="sv-msg-row" :class="m.senderId === myUserId ? 'sv-msg-row--me' : 'sv-msg-row--other'">
                  <router-link
                    v-if="m.senderId !== myUserId"
                    class="sv-msg-avatar-link sv-msg-avatar-link--left"
                    :to="`/user/${m.senderId}`"
                  >
                    <v-avatar size="30" class="sv-msg-avatar">
                      <v-img :src="selectedUserAvatar" />
                    </v-avatar>
                  </router-link>
                  <div class="sv-bubble" :class="isImageMessage(m) ? 'sv-bubble--image' : (m.senderId === myUserId ? 'sv-bubble--me' : 'sv-bubble--other')">
                    <template v-if="isImageMessage(m)">
                      <img
                        :src="resolveImageSrc(m.content)"
                        alt="image"
                        class="sv-msg-img"
                      />
                    </template>
                    <template v-else>
                      <div class="text-body-2" :class="m.senderId === myUserId ? 'text-white' : ''">
                        {{ m.content }}
                      </div>
                    </template>
                  </div>
                  <router-link
                    v-if="m.senderId === myUserId"
                    class="sv-msg-avatar-link sv-msg-avatar-link--right"
                    :to="`/user/${myUserId}`"
                  >
                    <v-avatar size="30" class="sv-msg-avatar">
                      <v-img :src="myAvatarUrl" />
                    </v-avatar>
                  </router-link>
                </div>
              </div>
            </div>
          </div>

          <div class="sv-chat-composer">
            <v-textarea
              v-model="draft"
              :disabled="!selectedUserId || sending"
              rows="1"
              auto-grow
              placeholder="输入消息..."
              hide-details
              class="sv-chat-input"
              @keydown.enter.exact.prevent="send"
              @keydown.enter.shift.exact.stop
            />
            <div class="d-flex justify-end mt-2" style="gap: 8px">
              <v-btn
                variant="tonal"
                :disabled="!selectedUserId || sending"
                icon="mdi-emoticon-outline"
                @click="emojiDialog = true"
              />

              <v-btn
                variant="tonal"
                icon="mdi-image-outline"
                :disabled="!selectedUserId || sending"
                title="发送图片"
                @click="triggerImagePick"
              />

              <v-btn
                color="primary"
                :disabled="!selectedUserId || sending || !draft.trim()"
                prepend-icon="mdi-send"
                @click="send"
              >
                发送
              </v-btn>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <input
      ref="imageInput"
      type="file"
      accept="image/*"
      style="display: none"
      @change="onImagePicked"
    />

    <v-dialog v-model="emojiDialog" max-width="520">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-2">mdi-emoticon</v-icon>
          表情
          <v-spacer />
          <v-btn variant="text" icon="mdi-close" @click="emojiDialog = false" />
        </v-card-title>
        <v-divider />
        <v-card-text>
          <div class="sv-emoji-grid">
            <v-btn
              v-for="e in emojis"
              :key="e"
              variant="text"
              class="sv-emoji-btn"
              @click="insertEmoji(e)"
            >
              {{ e }}
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000" location="top">
      {{ snackbar.text }}
      <template #actions>
        <v-btn variant="text" @click="snackbar.show = false">关闭</v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import { useUserStore } from '@/stores/userStore'

export default {
  name: 'ChatView',
  data() {
    return {
      userStore: useUserStore(),
      conversations: [],
      selectedUserId: null,
      selectedUserName: '',
      messages: [],
      loadingMessages: false,
      sending: false,
      pollingTimer: null,
      pollingInFlight: false,
      ws: null,
      wsConnected: false,
      wsReconnectTimer: null,
      emojiDialog: false,
      emojis: ['😀', '😁', '😂', '🤣', '😊', '😍', '😘', '😉', '😎', '🤔', '😴', '😭', '😡', '👍', '👎', '🙏', '🎉', '🔥', '❤️', '💯'],
      draft: '',
      snackbar: {
        show: false,
        text: '',
        color: 'success',
      },
    }
  },
  computed: {
    myUserId() {
      return this.userStore && this.userStore.userData ? this.userStore.userData.id : null
    },
    myAvatarUrl() {
      return this.userStore && this.userStore.userData ? this.userStore.userData.avatarUrl : ''
    },
    isMobile() {
      return !!(this.$vuetify && this.$vuetify.display && this.$vuetify.display.smAndDown)
    },
    selectedUserAvatar() {
      const hit = this.conversations.find((c) => c.otherUser && c.otherUser.id === this.selectedUserId)
      return hit && hit.otherUser ? hit.otherUser.avatarUrl : ''
    },
  },
  created() {
    if (typeof this.httpGet !== 'function' || typeof this.httpPost !== 'function') {
      if (typeof console !== 'undefined' && console && console.error) {
        console.error('ChatView: httpGet/httpPost not injected')
      }
      this.showMessage('页面初始化失败：网络请求方法未注入，请刷新页面', 'error')
      return
    }
    const q = this.$route && this.$route.query ? this.$route.query : {}
    const withUserId = q.withUserId != null ? parseInt(q.withUserId) : null
    if (withUserId) {
      this.selectedUserId = withUserId
    }
    this.loadConversations(() => {
      if (this.selectedUserId) {
        const hit = this.conversations.find((c) => c.otherUser && c.otherUser.id === this.selectedUserId)
        if (hit) {
          this.selectConversation(hit)
        } else {
          this.loadMessages()
        }
      }
    })

    this.connectWebSocket()
  },
  beforeUnmount() {
    this.stopPolling()
    this.disconnectWebSocket()
  },
  methods: {
    backToList() {
      this.selectedUserId = null
      this.selectedUserName = ''
      this.messages = []
    },
    showMessage(text, color = 'success') {
      this.snackbar.text = text
      this.snackbar.color = color
      this.snackbar.show = true
    },
    connectWebSocket() {
      this.disconnectWebSocket()
      if (!this.myUserId) {
        // 需要登录，路由已拦截，这里仅兜底
        this.startPolling()
        return
      }

      const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
      const wsUrl = `${protocol}://${window.location.host}/ws/chat`
      try {
        this.ws = new WebSocket(wsUrl)
      } catch (e) {
        this.ws = null
        this.startPolling()
        return
      }

      this.ws.onopen = () => {
        this.wsConnected = true
        this.stopPolling()
      }
      this.ws.onclose = () => {
        this.wsConnected = false
        this.ws = null
        this.startPolling()
        this.scheduleWsReconnect()
      }
      this.ws.onerror = () => {
        // onclose 会处理降级
      }
      this.ws.onmessage = (evt) => {
        this.handleWsMessage(evt.data)
      }
    },
    scheduleWsReconnect() {
      if (this.wsReconnectTimer) {
        return
      }
      this.wsReconnectTimer = setTimeout(() => {
        this.wsReconnectTimer = null
        this.connectWebSocket()
      }, 3000)
    },
    disconnectWebSocket() {
      if (this.wsReconnectTimer) {
        clearTimeout(this.wsReconnectTimer)
        this.wsReconnectTimer = null
      }
      if (this.ws) {
        try {
          this.ws.close()
        } catch (e) {
          // ignore
        }
      }
      this.ws = null
      this.wsConnected = false
    },
    handleWsMessage(raw) {
      if (!raw) {
        return
      }
      let json = null
      try {
        json = JSON.parse(raw)
      } catch (e) {
        return
      }
      if (!json || json.type !== 'chat_message' || !json.data) {
        return
      }

      const msg = json.data
      // 更新当前对话消息
      if (
        this.selectedUserId &&
        ((msg.senderId === this.myUserId && msg.receiverId === this.selectedUserId) ||
          (msg.senderId === this.selectedUserId && msg.receiverId === this.myUserId))
      ) {
        const exists = this.messages.some((m) => m.id === msg.id)
        if (!exists) {
          this.messages.push(msg)
          this.$nextTick(() => {
            this.scrollToBottom()
          })
        }
      }

      // 会话列表更新（简单做法：重新拉一下）
      this.loadConversations()
    },
    insertEmoji(e) {
      if (!e) {
        return
      }
      this.draft = `${this.draft || ''}${e}`
      this.emojiDialog = false
    },
    isImageMessage(m) {
      if (!m) {
        return false
      }
      if (Number(m.msgType) === 1) {
        return true
      }
      const c = m.content
      if (!c || typeof c !== 'string') {
        return false
      }
      // 兼容旧数据/后端未返回 msgType：如果内容本身就是图片地址，则按图片渲染
      return /\.(png|jpg|jpeg|gif|webp|bmp)(\?.*)?$/i.test(c) && (c.startsWith('/api/') || c.startsWith('http://') || c.startsWith('https://'))
    },
    resolveImageSrc(content) {
      if (!content) {
        return ''
      }
      if (typeof content !== 'string') {
        return ''
      }
      if (content.startsWith('http://') || content.startsWith('https://')) {
        return content
      }
      // 相对路径（如 /api/upload/...）
      if (content.startsWith('/')) {
        return `${window.location.origin}${content}`
      }
      return content
    },
    formatConversationPreview(lastMessage) {
      if (!lastMessage) {
        return ''
      }
      if (this.isImageMessage(lastMessage)) {
        return '[图片]'
      }
      return lastMessage.content || ''
    },
    triggerImagePick() {
      if (!this.selectedUserId || this.sending) {
        return
      }
      const el = this.$refs.imageInput
      if (el && el.click) {
        el.click()
      }
    },
    onImagePicked(e) {
      const input = e && e.target ? e.target : null
      const file = input && input.files && input.files.length > 0 ? input.files[0] : null
      if (!file) {
        return
      }

      if (file.size > 5 * 1024 * 1024) {
        this.showMessage('图片不能超过5MB', 'warning')
        input.value = ''
        return
      }

      this.uploadAndSendImage(file)
      input.value = ''
    },
    uploadAndSendImage(file) {
      if (!this.selectedUserId || !file) {
        return
      }
      if (!this.uploadFiles) {
        this.showMessage('当前页面未注入上传方法 uploadFiles', 'error')
        return
      }
      this.sending = true

      const formData = new FormData()
      formData.append('file[]', file)
      this.uploadFiles('/upload/photo', formData, (json) => {
        if (!json || json.status !== 200 || !json.data || !json.data[0] || !json.data[0].fileUrl) {
          this.sending = false
          this.showMessage((json && json.message) || '图片上传失败', 'error')
          return
        }

        const url = json.data[0].fileUrl
        this.sendMessage(1, url)
      })
    },
    sendMessage(msgType, content) {
      if (!this.selectedUserId) {
        this.sending = false
        return
      }
      this.httpPost('/chat/send', { toUserId: this.selectedUserId, msgType, content }, (json) => {
        this.sending = false
        if (json.status !== 200) {
          this.showMessage(json.message || '发送失败', 'warning')
          return
        }
        this.draft = ''
        this.loadConversations()
        this.loadMessages()
      })
    },
    startPolling() {
      this.stopPolling()
      if (typeof this.httpGet !== 'function') {
        return
      }
      this.pollingTimer = setInterval(() => {
        if (this.pollingInFlight) {
          return
        }
        if (!this.myUserId) {
          return
        }

        if (this.wsConnected) {
          return
        }

        this.pollingInFlight = true
        this.httpGet('/chat/conversations', (json) => {
          if (json.status === 200) {
            this.conversations = Array.isArray(json.data) ? json.data : []
          }

          if (this.selectedUserId) {
            this.httpGet(`/chat/messages?withUserId=${this.selectedUserId}&page=1&limit=50`, (json2) => {
              if (json2.status === 200) {
                const arr = Array.isArray(json2.data) ? json2.data : []
                this.messages = arr.slice().reverse()
              }
              this.pollingInFlight = false
            })
          } else {
            this.pollingInFlight = false
          }
        })
      }, 3000)
    },
    stopPolling() {
      if (this.pollingTimer) {
        clearInterval(this.pollingTimer)
        this.pollingTimer = null
      }
      this.pollingInFlight = false
    },
    loadConversations(done) {
      if (typeof this.httpGet !== 'function') {
        if (done) done()
        return
      }
      this.httpGet('/chat/conversations', (json) => {
        if (json.status !== 200) {
          this.showMessage(json.message || '加载会话失败', 'error')
          if (done) done()
          return
        }
        this.conversations = Array.isArray(json.data) ? json.data : []
        if (done) done()
      })
    },
    selectConversation(c) {
      if (!c || !c.otherUser) {
        return
      }
      this.selectedUserId = c.otherUser.id
      this.selectedUserName = c.otherUser.username
      this.loadMessages()
    },
    loadMessages() {
      if (!this.selectedUserId) {
        return
      }
      if (typeof this.httpGet !== 'function') {
        this.loadingMessages = false
        return
      }
      this.loadingMessages = true
      this.httpGet(`/chat/messages?withUserId=${this.selectedUserId}&page=1&limit=50`, (json) => {
        this.loadingMessages = false
        if (json.status !== 200) {
          this.showMessage(json.message || '加载消息失败', 'error')
          return
        }
        const arr = Array.isArray(json.data) ? json.data : []
        this.messages = arr.slice().reverse()
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      })
    },
    scrollToBottom() {
      const el = this.$refs.msgScroll
      if (el && el.scrollTop != null) {
        el.scrollTop = el.scrollHeight
      }
    },
    send() {
      if (!this.selectedUserId) {
        return
      }
      const content = (this.draft || '').trim()
      if (!content) {
        return
      }
      this.sending = true
      this.sendMessage(0, content)
    },
  },
}
</script>

<style scoped>
.sv-chat-page {
  height: calc(100dvh - var(--v-layout-top, 0px) - var(--v-layout-bottom, 0px));
  background: transparent;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sv-chat-page :deep(.v-row) {
  height: 100%;
  flex: 1;
  align-items: stretch;
  overflow: hidden;
  min-height: 0;
  flex-wrap: nowrap;
}

.sv-chat-page :deep(.v-col) {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
}

.sv-chat-page :deep(.v-card) {
  width: 100%;
  flex: 1;
  min-height: 0;
}

.sv-conversation-card {
  display: flex;
  flex-direction: column;
}

.sv-conversation-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.sv-chat-shell {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sv-chat-header {
  padding: 12px 16px;
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.12);
  background: rgba(var(--v-theme-surface), 0.92);
  backdrop-filter: blur(6px);
  flex: 0 0 auto;
}

.sv-chat-messages {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  padding: 14px 16px;
  background: linear-gradient(
    180deg,
    rgba(var(--v-theme-background), 1) 0%,
    rgba(var(--v-theme-surface), 1) 100%
  );
}

.sv-msg-row {
  display: flex;
  margin-bottom: 10px;
  align-items: flex-end;
}

.sv-msg-row--me {
  justify-content: flex-end;
}

.sv-msg-row--other {
  justify-content: flex-start;
}

.sv-bubble {
  max-width: min(78%, 520px);
  padding: 10px 12px;
  border-radius: 14px;
}

.sv-msg-avatar-link {
  display: inline-flex;
  text-decoration: none;
}

.sv-msg-avatar-link--left {
  margin-right: 8px;
}

.sv-msg-avatar-link--right {
  margin-left: 8px;
}

.sv-msg-avatar {
  border: 1px solid rgba(var(--v-theme-on-surface), 0.12);
}

.sv-bubble--me {
  background: rgb(var(--v-theme-primary));
}

.sv-bubble--other {
  background: rgba(var(--v-theme-on-surface), 0.08);
}

.sv-bubble--image {
  padding: 6px;
  background: rgba(var(--v-theme-on-surface), 0.08);
}

.sv-msg-img {
  display: block;
  width: 320px;
  height: 240px;
  max-width: 100%;
  object-fit: contain;
  border-radius: 10px;
  background: rgb(var(--v-theme-surface));
}

.sv-chat-composer {
  padding: 12px 16px;
  border-top: 1px solid rgba(var(--v-theme-on-surface), 0.12);
  background: rgba(var(--v-theme-surface), 0.92);
  backdrop-filter: blur(6px);
  flex: 0 0 auto;
}

.sv-chat-input :deep(textarea) {
  line-height: 1.4;
}

@media (max-width: 960px) {
  .sv-chat-shell {
    height: 100%;
    min-height: 0;
  }
}

.sv-emoji-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 6px;
}

.sv-emoji-btn {
  min-width: 0;
  padding: 6px;
  font-size: 18px;
}
</style>
