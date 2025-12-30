<template>
  <v-app>
    <router-view />
  </v-app>
</template>

<script>
import { useWebInfoStore } from '@/stores/webInfoStore'
export default {
  name: 'App',

  data: () => ({
    //
  }),
  mounted() {
    this.httpGet('/web/info', (json) => {
      const webInfoStore = useWebInfoStore()
      const data = json.data || {}
      if (typeof data.name === 'string' && data.name.trim().toLowerCase() === 'tiktube') {
        data.name = 'StreamVerse'
      }
      webInfoStore.updateWebInfo(data)
      const metaDesc = document.querySelector('meta[name="description"]');
      if (metaDesc) {
        metaDesc.setAttribute('content', data.webDescribe);
      }
    })
  },
}
</script>

<style>
a {
  text-decoration: none;
}
</style>
