import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useChatStore = defineStore('chat', () => {
  const sessionId = ref<number>(0)
  const messages = ref<any[]>([])

  return { sessionId, messages }
})
