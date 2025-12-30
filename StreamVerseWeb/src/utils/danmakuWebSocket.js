/**
 * 弹幕 WebSocket 工具类
 * 用于接收实时弹幕推送
 */
export class DanmakuWebSocket {
  constructor(videoId, onDanmaku) {
    this.videoId = videoId
    this.onDanmaku = onDanmaku
    this.ws = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 3000
  }

  /**
   * 连接 WebSocket
   */
  connect() {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    // 开发环境直接连接后端 8080 端口，生产环境使用当前 host
    const wsHost = import.meta.env.DEV ? `${window.location.hostname}:8080` : window.location.host
    const url = `${protocol}//${wsHost}/ws/danmaku/${this.videoId}`

    console.log('准备连接 WebSocket:', url)

    try {
      this.ws = new WebSocket(url)

      this.ws.onopen = () => {
        console.log('弹幕 WebSocket 连接成功:', this.videoId)
        this.reconnectAttempts = 0
      }

      this.ws.onmessage = (event) => {
        try {
          const danmaku = JSON.parse(event.data)
          if (this.onDanmaku && typeof this.onDanmaku === 'function') {
            this.onDanmaku(danmaku)
          }
        } catch (e) {
          console.error('解析弹幕消息失败:', e)
        }
      }

      this.ws.onclose = (event) => {
        console.log('弹幕 WebSocket 连接关闭:', event.code, event.reason)
        this.tryReconnect()
      }

      this.ws.onerror = (error) => {
        console.error('弹幕 WebSocket 错误:', error)
      }
    } catch (e) {
      console.error('创建 WebSocket 失败:', e)
    }
  }

  /**
   * 尝试重连
   */
  tryReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`弹幕 WebSocket 尝试重连 (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`)
      setTimeout(() => {
        this.connect()
      }, this.reconnectDelay)
    } else {
      console.log('弹幕 WebSocket 重连次数已达上限，停止重连')
    }
  }

  /**
   * 断开连接
   */
  disconnect() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  /**
   * 发送心跳（可选）
   */
  sendHeartbeat() {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify({ type: 'heartbeat' }))
    }
  }
}
