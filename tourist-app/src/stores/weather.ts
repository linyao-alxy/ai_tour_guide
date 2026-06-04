import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useWeatherStore = defineStore('weather', () => {
  const weather = ref<any>({})

  async function fetchCurrent(scenicId: number) {
    try {
      const res = await uni.request({ url: `/api/v1/tourist/weather/current?scenicId=${scenicId}` })
      weather.value = (res.data as any).data
      return weather.value
    } catch (e) { return {} }
  }

  return { weather, fetchCurrent }
})
