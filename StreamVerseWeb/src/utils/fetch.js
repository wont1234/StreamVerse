import { useUserStore } from '@/stores/userStore';
import { useWebInfoStore } from '@/stores/webInfoStore';
export default {
  install: (app) => {
    const parseResponse = async function (response) {
      const status = response.status;
      const statusText = response.statusText;
      let text = '';
      try {
        text = await response.text();
      } catch (e) {
        text = '';
      }
      if (!text) {
        return { status, message: statusText };
      }
      try {
        const json = JSON.parse(text);
        if (json && typeof json === 'object' && json.status == null) {
          json.status = status;
        }
        return json;
      } catch (e) {
        return { status, message: text };
      }
    };

    // 检查未授权访问的函数
    const checkUnauthorized = function(json, context) {
      if (json && json.status === 0 && json.message === "登录过期或未登录") {
        const userStore = useUserStore();
        userStore.deleteUserData();
        
        // 使用Vue实例中的router导航
        if (context && context.$router) {
          context.$router.push('/login');
        } else {
          // 备用方案，直接使用window.location
          window.location.href = '/login';
        }
        return true;
      }
      return false;
    };
    
    app.config.globalProperties.httpGet = function (url, cb) {
      const webInfoStore = useWebInfoStore().webInfo;
      fetch(`${this.SERVER_API_URL}${url}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'App-Name': webInfoStore.name,
          //'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include',
      })
        .then((response) => parseResponse(response))
        .then((json) => {
          if (!checkUnauthorized(json, this)) {
            cb(json);
          }
        })
        .catch((error) => {
          console.error('HTTP Get Error:', error)
          return null
        })
    }

    app.config.globalProperties.httpPost = function (url, data, cb) {
      const webInfoStore = useWebInfoStore().webInfo;
      fetch(`${this.SERVER_API_URL}${url}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'App-Name': webInfoStore.name
          //'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(data),
      })
        .then((response) => parseResponse(response))
        .then((json) => {
          if (!checkUnauthorized(json, this)) {
            cb(json);
          }
        })
        .catch((error) => {
          console.error('HTTP Post Error:', error)
          return null
        })
    }

    app.config.globalProperties.uploadFiles = function (url, form, cb) {
      const webInfoStore = useWebInfoStore().webInfo;
      fetch(`${this.SERVER_API_URL}${url}`, {
        headers: {
          'App-Name': webInfoStore.name,
          //'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: form,
      })
        .then((response) => parseResponse(response))
        .then((json) => {
          if (!checkUnauthorized(json, this)) {
            cb(json);
          }
        })
        .catch((error) => {
          console.error('Upload Files Error:', error)
          return null
        })
    }
  },
}
